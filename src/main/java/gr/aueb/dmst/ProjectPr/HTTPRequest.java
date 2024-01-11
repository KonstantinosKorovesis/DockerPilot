package gr.aueb.dmst.ProjectPr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

/** The HTTPRequest Class which handles the HTTP POST and GET Requests to the API server. */
public class HTTPRequest {
    /** The URL of the API server. */
    protected static final String baseUrl = "http://localhost:8080";

    /** Sends a POST Request to the server's stop_container route with the given container_id as a paremeter.
     *
     * @param container_id The passed container_id to be stopped.
     */
    public static void stop_container(String container_id) {
        try {
            String endpoint = "/stop_container?container_id=" + container_id;

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            if (connection.getResponseCode() != 200) {
                System.out.println("[HTTPRequest ERROR] Response Code: " + connection.getResponseCode());
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                System.out.println(response);
            }

            connection.disconnect();
        } catch (Exception e) {
            System.err.println("[HTTPRequest ERROR] Something went wrong when using the stop_container method.");
            System.err.println(e);
        }
    }

    /** Sends a POST Request to the server's start_container route with the given container_id as a paremeter.
     *
     * @param container_id The passed container_id to be started.
     */
    public static void start_container(String container_id) {
        try {
            String endpoint = "/start_container?container_id=" + container_id;

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            if (connection.getResponseCode() != 200) {
                System.out.println("[HTTPRequest ERROR] Response Code: " + connection.getResponseCode());
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                System.out.println(response);
            }

            connection.disconnect();
        } catch (Exception e) {
            System.err.println("[HTTPRequest ERROR] Something went wrong when using the start_container method.");
            System.err.println(e);
        }
    }

    /** Sends a GET Request to the server's running_container_count route with the given measurementId as a paremeter.
     *
     * @param measurementId The passed measurementId specifying the set of entries to be included in the count.
     * @return The count of the running containers out of the total number of containers for the specific measurementId.
     */
    public static int running_container_count(String measurementId) {
        try {
            String endpoint = "/running_container_count?measurementId=" + measurementId;

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) {
                System.out.println("[HTTPRequest ERROR] Response Code: " + connection.getResponseCode());
            }

            int count;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                count = Integer.valueOf(response.toString());
            }
            connection.disconnect();
            return count;
        } catch (Exception e) {
            System.err.println("[HTTPRequest ERROR] Something went wrong when using the running_container_count method.");
            System.err.println(e);
        }
        return 0;
    }

    /** Sends a GET Request to the server's all_container_count route with the given measurementId as a paremeter.
     *
     * @param measurementId The passed measurementId specifying the set of entries to be included in the count.
     * @return The total number of available containers for the specific measurementId.
     */
    public static int all_container_count(String measurementId) {
        try {
            String endpoint = "/all_container_count?measurementId=" + measurementId;

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) {
                System.out.println("[HTTPRequest ERROR] Response Code: " + connection.getResponseCode());
            }

            int count;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                count = Integer.valueOf(response.toString());
            }
            connection.disconnect();
            return count;
        } catch (Exception e) {
            System.err.println("[HTTPRequest ERROR] Something went wrong when using the all_container_count method.");
            System.err.println(e);
        }
        return 0;
    }

    /** Sends a GET Request to the server's available_dates.
     *
     * @return A String[] of all the available dates in the database.
     */
    public static String[] available_dates() {
        try {
            String endpoint = "/available_dates";

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) {
                System.out.println("[HTTPRequest ERROR] Response Code: " + connection.getResponseCode());
            }

            String[] dates;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                Gson gson = new Gson();
                dates = gson.fromJson(response.toString(), String[].class);
            }
            connection.disconnect();
            return dates;
        } catch (Exception e) {
            System.err.println("[HTTPRequest ERROR] Something went wrong when using the available_dates method.");
            System.err.println(e);
        }
        return new String[0];
    }

    /** Sends a GET Request to the server's available_dates with the given date as a parameter.
     *
     * @param date The passed date specifying which entries are to be included.
     * @return A String[] of all the available measurements in the database on the specific passed date.
     */
    public static String[] available_measurements(String date) {
        try {
            String endpoint = "/available_measurements?date=" + date;

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) {
                System.out.println("[HTTPRequest ERROR] Response Code: " + connection.getResponseCode());
            }

            String[] measurementIds;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                Gson gson = new Gson();
                measurementIds = gson.fromJson(response.toString(), String[].class);
            }
            connection.disconnect();
            return measurementIds;
        } catch (Exception e) {
            System.err.println("[HTTPRequest ERROR] Something went wrong when using the available_measurements method.");
            System.err.println(e);
        }
        return new String[0];
    }

    /** Sends a GET Request to the server's container_entries with the given measurementId as a parameter.
     *
     * @param measurementId The passed measurementId which points to a specific set of entries.
     * @return A {@link List} of String[] arrays, with each one containing the data of a single container from the specific measurement.
     */
    public static List<String[]> container_entries(String measurementId) {
        List<String[]> containerEntries = new ArrayList<String[]>();
        try {
            String endpoint = "/container_entries?measurementId=" + measurementId;

            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) {
                System.out.println("[HTTPRequest ERROR] Response Code: " + connection.getResponseCode());
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                Gson gson = new Gson();
                Type listType = new TypeToken<List<String[]>>() {}.getType();
                containerEntries = gson.fromJson(response.toString(), listType);
            }
            connection.disconnect();
            return containerEntries;
        } catch (Exception e) {
            System.err.println("[HTTPRequest ERROR] Something went wrong when using the container_entries method.");
            System.err.println(e);
        }
        return containerEntries;
    }
}
