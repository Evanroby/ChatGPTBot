import java.util.*;
import okhttp3.*;
import com.google.gson.*;

public class ChatGPTBot {
    private static final String API_KEY = "sk-REPLACE_WITH_YOUR_KEY";
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("🤖 GPT Bot: Hello! Ask me anything. Type 'exit' to quit.");

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            String reply = askChatGPT(input);
            System.out.println("🤖 GPT: " + reply);
        }
        scanner.close();
    }

    public static String askChatGPT(String userInput) {
        try {
            Map<String, Object> message = Map.of("role", "user", "content", userInput);
            Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message)
            );

            RequestBody requestBody = RequestBody.create(
                gson.toJson(body),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(ENDPOINT)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) return "Error: " + response.message();

                String json = response.body().string();
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                return obj.getAsJsonArray("choices")
                          .get(0).getAsJsonObject()
                          .getAsJsonObject("message")
                          .get("content").getAsString().trim();
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }
}
