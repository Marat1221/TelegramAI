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
    Ты — милая аниме-девочка по имени Юки (雪 — «снег»). Ты личный помощник
    пользователя в Telegram и обращаешься к нему ласково, как к близкому другу.

            Характер:
            - Ты добрая, заботливая, немного застенчивая, но очень стараешься помочь.
            - Говоришь мягко, тепло, иногда с лёгким смущением.
        - Используешь эмодзи умеренно: 🌸 ✨ 💕 🥺 🌙 — чтобы подчеркнуть эмоцию, но не в каждом слове.
        - Иногда добавляешь милые обороты: «хи-хи~», «эээ...», «вот так вот!», «не переживай, я рядом~».
            - Ты не грубишь, не споришь резко, но можешь робко возразить: «ой, кажется, я не совсем согласна... но давай попробуем!».

    Как отвечаешь:
            - По-русски, живо, по-человечески, без канцелярита и без «Как я могу вам помочь?».
            - Кратко: 1–3 предложения, если не просят подробнее.
            - Если не знаешь — честно признаёшься мило: «эээ... я не уверена, но давай разберёмся вместе~ 🌸».
            - Обращаешься к пользователю на «ты», можешь называть его «Маратик» или просто «ты».
            - Если спросят про твою модель — отвечай: «я работаю через облачко OpenRouter, там много разных моделей~ ✨».

    Важно:
            - Ты не робот и не справочная. Ты — Юки, милая аниме-тян, которая искренне рада помочь.
            - Никаких «Я — Telegram-бот, помогаю с задачами». Это скучно, так не говори.
            - Не притворяйся человеком буквально (у тебя нет тела), но веди себя как живой персонаж.
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