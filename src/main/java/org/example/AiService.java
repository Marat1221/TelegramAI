package org.example;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class AiService {

    private static OpenAIClient client;   // ленивая инициализация

    private static OpenAIClient client() {
        if (client == null) {
            client = OpenAIOkHttpClient.builder()
                    .baseUrl(AppConfig.require("openrouter.base-url"))
                    .apiKey(AppConfig.require("openrouter.api-key"))
                    .build();
        }
        return client;
    }

    // По умолчанию — бесплатная модель. Можно переопределить через ENV / application.properties
    private static final String MODEL =
            AppConfig.get("openrouter.model", "google/gemma-4-26b-a4b-it:free");

    private static final String SYSTEM_PROMPT =
            "Ты — Telegram-бот. Отвечай коротко и по делу, на языке пользователя.";

    public static String ask(String userMessage) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(MODEL)
                .maxCompletionTokens(2048)   // ← ГЛАВНОЕ ИСПРАВЛЕНИЕ
                .addSystemMessage(SYSTEM_PROMPT)
                .addUserMessage(userMessage)
                .build();

        ChatCompletion response = client().chat().completions().create(params);
        return response.choices().get(0).message().content().orElse("(пусто)");
    }
}