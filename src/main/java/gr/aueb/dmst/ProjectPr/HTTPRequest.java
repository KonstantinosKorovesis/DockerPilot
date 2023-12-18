package gr.aueb.dmst.ProjectPr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest {
    public static void stop_container(String container_id) {
        String baseUrl = "http://localhost:8080";
        try {
            String endpoint = "/stop_container?container_id=" + container_id;

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            //get response code (should be 200)
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            //get the response
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Response: " + response.toString());
            }

            connection.disconnect();
        } catch (Exception e) {
            System.err.println("[ERROR] Something went wrong when using the stop_container method.");
            System.err.println(e);
        }
    }

    public static void start_container(String container_id) {
        String baseUrl = "http://localhost:8080";
        try {
            String endpoint = "/start_container?container_id=" + container_id;

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            //get response code (should be 200)
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            //get the response
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Response: " + response.toString());
            }

            connection.disconnect();
        } catch (Exception e) {
            System.err.println("[ERROR] Something went wrong when using the start_container method.");
            System.err.println(e);
        }
    }
}