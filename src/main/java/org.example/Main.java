package org.example;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Main {
    @Test
    public void GetStepikStats() {
        List<Object> list = (RestAssured
                .given()
                .contentType("application/json")
                .get("https://stepik.org/api/user-activity-summaries/472513762")
                .then().assertThat().statusCode(200)
                .extract().response().jsonPath().getList("user-activity-summaries"));

        HashMap<String, Object> hashMap = (HashMap<String, Object>) list.get(0);

        String solvedToday = hashMap.get("solved_today") != null ? hashMap.get("solved_today").toString() : "Нет данных";
        String pins = hashMap.get("pins") != null ? hashMap.get("pins").toString() : "Нет данных";
        String solved = hashMap.get("solved") != null ? hashMap.get("solved").toString() : "Нет данных";
        String recentStrike = hashMap.get("recent_strike") != null ? hashMap.get("recent_strike").toString() : "Нет данных";
        String maxStrike = hashMap.get("max_strike") != null ? hashMap.get("max_strike").toString() : "Нет данных";

        String newStatsSection = "# Stepik Tasks Stats\n" +
                "Solved Today: " + solvedToday + "\n" +
                "Activity: " + pins + "\n" +
                "Total Solved Tasks: " + solved + "\n" +
                "Problems solved for days in a row: " + recentStrike + "\n" +
                "The maximum number of consecutive days of solved tasks: " + maxStrike + "\n";

        StringBuilder existingContent = new StringBuilder();
        boolean inOldStatsSection = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("README.md"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("# Stepik Tasks Stats")) {
                    inOldStatsSection = true;
                }

                if (inOldStatsSection && line.isBlank()) {
                    inOldStatsSection = false;
                    continue;
                }

                if (!inOldStatsSection) {
                    existingContent.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        existingContent.append("\n").append(newStatsSection);

        try (FileWriter writer = new FileWriter("README.md")) {
            writer.write(existingContent.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



