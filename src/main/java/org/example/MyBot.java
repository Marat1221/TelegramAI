package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient client;
    private final String botUsername;

    public MyBot(String botToken, String botUsername) {
        this.client = new OkHttpTelegramClient(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage()) return;
        Message msg = update.getMessage();
        if (!msg.hasText()) return;

        // 1. Отсекаем бэклог — старше 60 секунд игнорируем
        long now = System.currentTimeMillis() / 1000;
        if (now - msg.getDate() > 60) return;

        String text = msg.getText();
        long chatId = msg.getChatId();
        boolean isGroup = chatId < 0;

        // 2. В группах — только если это команда или упоминание бота
        if (isGroup) {
            boolean isCommand = text.startsWith("/");
            boolean isMention = text.toLowerCase().contains("@" + botUsername.toLowerCase());
            boolean isReplyToBot = msg.getReplyToMessage() != null
                    && msg.getReplyToMessage().getFrom() != null
                    && msg.getReplyToMessage().getFrom().getUserName() != null
                    && msg.getReplyToMessage().getFrom().getUserName().equals(botUsername);
            if (!isCommand && !isMention && !isReplyToBot) return;
        }

        System.out.println("[" + chatId + "] " + text);

        try {
            String reply = AiService.ask(text);
            client.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(reply)
                    .replyToMessageId(msg.getMessageId())
                    .build());
        } catch (Exception e) {
            System.err.println("Ошибка ответа: " + e.getMessage());
            try {
                client.execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("⚠️ Ошибка: " + e.getMessage())
                        .build());
            } catch (Exception ignored) {}
        }
    }
}