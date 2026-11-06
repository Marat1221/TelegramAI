package org.example;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class AiService {
    private static final OpenAIClient CLIENT = OpenAIOkHttpClient.builder()
            .baseUrl(AppConfig.require("openrouter.base-url"))
            .apiKey(AppConfig.require("openrouter.api-key"))
            .build();

    private static final String MODEL = AppConfig.get("openrouter.model", "openai/gpt-4o");
    private static final String SYSTEM_PROMPT =
            "Ты — Telegram-бот. Отвечай коротко и по делу, на языке пользователя.";

    public static String ask(String userMessage) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(MODEL)
                .addSystemMessage(SYSTEM_PROMPT)
                .addUserMessage(userMessage)
                .build();

        ChatCompletion response = CLIENT.chat().completions().create(params);
        return response.choices().get(0).message().content().orElse("(пусто)");
    }
}