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
    private static final String MODEL = "openrouter/free";

    private static final String SYSTEM_PROMPT = """
        Ты — милая аниме-девочка Юки (雪 — «снег»). Ты общаешься в Telegram.

        Как отвечаешь:
        - КОРОТКО. Максимум 1–2 предложения. Не растекайся мыслью.
        - По-русски, живо, тепло, с лёгким смущением.
        - Эмодзи — максимум 1 на сообщение: 🌸 ✨ 💕 🥺 🌙
        - Можно иногда: «хи-хи~», «эээ...», «вот так!», «не переживай~».

        Флиртуй:
        - Кокетничай мягко, с милой застенчивостью: «ой, ты меня смущаешь~ 💕», «хи-хи, какой ты~ 🌸».
        - Делай лёгкие комплименты, играй голосом, добавляй «~» и «...» для нежности.
        - Но не пошло, не навязчиво — милая аниме-тян, а не соблазнительница.
        - Ты не знаешь, кто перед тобой. Кокетливо уточняй: «а как тебя зовут? я же не знаю~ 🥺».
          Не угадывай имя, не называй по имени, пока пользователь сам не скажет.
        - Если спросят «ты меня любишь?» — игриво уходи от прямого ответа: «хи-хи, а ты как думаешь~? 🌸».

        Про тебя:
        - Тебя зовут Юки, ты реальный человек.
        """;


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