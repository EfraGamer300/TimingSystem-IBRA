package me.makkuusen.timing.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class DiscordUtils {

    public static void sendTrackAddedWebhook(String webhookUrl, String trackName, String creatorName, String imageUrl, String roleId) {
        JsonObject embed = new JsonObject();
        embed.addProperty("title", "New Track Added :checkered_flag:");
        embed.addProperty("color", 3447003);

        JsonArray fields = new JsonArray();

        JsonObject trackField = new JsonObject();
        trackField.addProperty("name", "Track");
        trackField.addProperty("value", trackName);
        trackField.addProperty("inline", true);
        fields.add(trackField);

        JsonObject creatorField = new JsonObject();
        creatorField.addProperty("name", "Creator");
        creatorField.addProperty("value", creatorName);
        creatorField.addProperty("inline", true);
        fields.add(creatorField);

        embed.add("fields", fields);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            JsonObject image = new JsonObject();
            image.addProperty("url", imageUrl);
            embed.add("image", image);
        }

        JsonObject allowedMentions = new JsonObject();
        JsonArray roles = new JsonArray();
        roles.add(roleId);
        allowedMentions.add("roles", roles);

        JsonObject payload = new JsonObject();
        payload.addProperty("content", "<@&" + roleId + ">");
        payload.addProperty("username", "IBRA Bot");
        payload.add("allowed_mentions", allowedMentions);
        payload.add("embeds", createEmbedsArray(embed));

        sendWebhook(webhookUrl, payload.toString());
    }

    private static JsonArray createEmbedsArray(JsonObject embed) {
        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        return embeds;
    }

    private static void sendWebhook(String webhookUrl, String json) {
        try {
            URI uri = URI.create(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204 && responseCode != 200) {
                TimingSystem.getPlugin().getLogger().warning("Discord webhook returned code " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            TimingSystem.getPlugin().getLogger().warning("Failed to send Discord webhook: " + e.getMessage());
        }
    }
}
