package org.example;

import com.sun.net.httpserver.HttpServer;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        // Health-check сервер для Amvera (порт 8080)
        startHealthServer();

        // Токен из переменных окружения или application.properties
        String token = AppConfig.require("telegram.bot-token");

        // Создаём приложение и регистрируем бота
        var app = new TelegramBotsLongPollingApplication();
        app.registerBot(token, new MyBot());

        System.out.println("Bot started (long polling)");
    }

    private static void startHealthServer() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            byte[] body = "OK".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        System.out.println("Health-check сервер запущен на порту 8080");
    }
}