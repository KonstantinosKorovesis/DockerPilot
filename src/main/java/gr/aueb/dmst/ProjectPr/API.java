package gr.aueb.dmst.ProjectPr;

import static spark.Spark.*;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import java.time.Duration;

public class API {
    public static void start() {
        //http://localhost:8080
        //staticFileLocation("/api_resources");
        port(8080);
        initExceptionHandler((e) -> System.err.println("[ERROR] Something went wrong when initializing the server."));
        init();

        get("/info", (request, response) -> {
            return "<html><body><h1>This is the info page of the application.</h1></body></html>";
        });

        get("/testing", (request, response) -> {
            System.out.println("[REQUEST] " + request.toString());
            System.out.println("[RESPONSE] " + response.toString());
            return "<html><body><h1>Testing... Watch command-line.</h1></body></html>";
        });

        get("/container_states", (request, response) -> {
            String date = request.queryParams("date");
            //TODO: use date for queries, return running/stopped containers
            return "param: " + date;
        });

        get("/stop_container", (request, response) -> {
            String container_id = request.queryParams("container_id");
            int result = Executor.stopContainer(container_id);
            if (result == 0) {
                return String.format("Container %12s was already exited. No action taken.", container_id);
            } else {
                return String.format("Container %12s is now stopped. ", container_id);
            }
        });

        get("/start_container", (request, response) -> {
            String container_id = request.queryParams("container_id");
            int result = Executor.startContainer(container_id);
            if (result == 0) {
                return String.format("Container %12s was already running. No action taken.", container_id);
            } else {
                return String.format("Container %12s is now running. ", container_id);
            }
        });

        post("/submit", (request, response) -> {
            String body = request.body();

            //use body in program
            System.out.println("[Received POST data] " + body);
            
            response.status(200);
            return body;
        });

        /*
        try {
            DockerClientConfig config = null;
            DockerHttpClient httpClient = null;
            DockerClient dockerClient = null;
            //set up and configure the dockerClient used for executing Docker commands.
            config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                                              .withDockerHost("tcp://localhost:2375")
                                              .build();
            httpClient = new ApacheDockerHttpClient.Builder()
                                                   .dockerHost(config.getDockerHost())
                                                   .sslConfig(config.getSSLConfig())
                                                   .maxConnections(100)
                                                   .connectionTimeout(Duration.ofSeconds(30))
                                                   .responseTimeout(Duration.ofSeconds(45))
                                                   .build();
            dockerClient = DockerClientImpl.getInstance(config, httpClient);

            //set up Monitor and Executor with the dockerClient object
            Monitor.setUpMonitor(dockerClient);
            Executor.setUpExecutor(dockerClient);



            stop_container("8d171c6c0ba6");
            //System.out.println("[Server will close in 60 seconds...]");
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("[Server is closing...]");
            stop();
            System.out.println("[Server closed.]");
        }
         */
    }

    public static void stop() {
        System.out.println("Server is closing...");
        spark.Spark.stop();
    }
    
    public static void post_testing() {
        try {
            String urlString = "http://localhost:8080/submit";

            String postData = "key1=value1&key2=value2";

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            //write POST data to the connection's output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
                os.write(postDataBytes);
                os.flush();
            }

            //get response code (should be 200)
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            //get the response body
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("POST Response: " + response.toString());
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Something went wrong when using the POST method.");
            System.err.println(e);
        }
    }

    public static void GET_container_states(String dateRequest) {
        String baseUrl = "http://localhost:8080";
        try {
            //FOR STOPPING/ STARTING
            //String endpoint = "/stop_container?container_id=" + container_id;
            String endpoint = "/container_states?date=" + dateRequest;

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
                System.out.println("GET Response: " + response.toString());
            }

            connection.disconnect();
        } catch (Exception e) {
            System.err.println("[ERROR] Something went wrong when using the GET method.");
            System.err.println(e);
        }
    }
}