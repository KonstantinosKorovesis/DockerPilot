package gr.aueb.dmst.ProjectPr;

import java.util.List;
import com.google.gson.Gson;
import static spark.Spark.*;

public class API {
    public static void start() {      
        port(8080); //http://localhost:8080
        initExceptionHandler((e) -> System.err.println("[ERROR API] Something went wrong when initializing the server."));
        init();

        post("/stop_container", (request, response) -> {
            String container_id = request.queryParams("container_id");
            int result = Executor.stopContainer(container_id);
            if (result == 0) {
                return String.format("[API] Container %12s is now stopped. ", container_id);
            } else {
                return String.format("[API] Something went wrong when attempting to stop the container. No action taken.", container_id);
            }
        });

        post("/start_container", (request, response) -> {
            String container_id = request.queryParams("container_id");
            int result = Executor.startContainer(container_id);
            if (result == 0) {
                return String.format("[API] Container %12s is now running. ", container_id);
            } else {
                return String.format("[API] Something went wrong when attempting to start the container. No action taken.", container_id);
            }
        });

        get("/running_container_count", (request, response) -> {
            String measurementId = request.queryParams("measurementId");
            int count = Database.selectRunningContainerCount(measurementId);
            return String.valueOf(count);
        });

        get("/all_container_count", (request, response) -> {
            String measurementId = request.queryParams("measurementId");
            int count = Database.selectTotalContainerCount(measurementId);
            return String.valueOf(count);
        });

        get("/available_dates", (request, response) -> {
            String[] dates = Database.selectAllDates();
            Gson gson = new Gson();
            String jsonDates = gson.toJson(dates);
            response.type("application/json");
            return jsonDates;
        });

        get("/available_measurements", (request, response) -> {
            String date = request.queryParams("date");
            String[] measurementIds = Database.selectMeasurementIdsOnDate(date);
            Gson gson = new Gson();
            String jsonIds = gson.toJson(measurementIds);
            response.type("application/json");
            return jsonIds;
        });

        get("/container_entries", (request, response) -> {
            String measurementId = request.queryParams("measurementId");
            List<String[]> containerEntries = Database.selectContainerEntries(measurementId);
            Gson gson = new Gson();
            String jsonEntries = gson.toJson(containerEntries);
            response.type("application/json");
            return jsonEntries;
        });
    }

    public static void stop() {
        System.out.println("Server is closing...");
        spark.Spark.stop();
    }
}