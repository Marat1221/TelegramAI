package org.example;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class OpenRouterExample {
    public static void main(String[] args) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .baseUrl(AppConfig.require("openrouter.base-url"))
                .apiKey(AppConfig.require("openrouter.api-key"))
                .build();

        String model = AppConfig.get("openrouter.model", "deepseek/deepseek-r1:free");

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .addUserMessage("Привет! Ответь одним предложением, кто ты.")
                .build();

        ChatCompletion chatResponse = client.chat().completions().create(params);

        String text = chatResponse.choices().get(0).message().content().orElse("<пусто>");
        System.out.println("Модель: " + model);
        System.out.println("Ответ:  " + text);
    }
}