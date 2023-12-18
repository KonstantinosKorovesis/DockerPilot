package gr.aueb.dmst.ProjectPr;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.Duration;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

/** The Main Class. */
public class Main {
    static Scanner scanner = new Scanner(System.in);
    /** The main method and entry point of the application.
     *
     * @param args Command-line arguments, currently not used in any way.
     */
    public static void main(String[] args) {
        DockerClientConfig config = null;
        DockerHttpClient httpClient = null;
        DockerClient dockerClient = null;
        try {
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

            //test dockerClient usability
            String version = dockerClient.versionCmd().exec().getVersion();
            System.out.println("Docker version: " + version);
            System.out.println("Docker Manager version: 1.2");

            //set up Monitor and Executor with the dockerClient object
            Monitor.setUpMonitor(dockerClient);
            Executor.setUpExecutor(dockerClient);

            API.start();

            //Main Method Body

            //first time set up
            //Database.setUpDatabase();

            //insert into both measurement and container tables (more detailed finalized version down)
            //Database.insertContainers(Database.insertMeasurement());

            //open window for desktop application
            DesktopApp.runWindow();

        } catch (RuntimeException e) {
            System.err.println("[ERROR]"
            + " Make sure Docker Desktop is open and you've"
            + " exposed Docker daemon on tcp://localhost:2375 without TLS.");
            System.err.println(e);  
            System.exit(1);
        } finally {
            try {
                API.stop();
                dockerClient.close();
                scanner.close();
                System.out.println("Successfully exited Docker Manager.");
            } catch (IOException e) {
                System.err.println("[ERROR] Something went wrong while trying to exit the program.");
                System.err.println(e);
                System.exit(1);
            }
        }
        /*
        //set up directory + database + 2 tables
        //if they all already exist, this just sets the setUpComplete variable to true
        Database.setUpDatabase();
        if (Database.setUpComplete) {
            //make a new pack in table_1 and insert the pack container data in table_2
            int pack_id = Database.insertMeasurement();
            if (pack_id != -1) {
                System.out.println(pack_id);
                Database.insertContainers(pack_id);
            }
        }
        */

        /*
        //select all
        //get column number for each table, get column names
        String sqlSelectAll = "SELECT * FROM ";
        try (Connection conn = DriverManager.getConnection(Database.JDBC_URL);
            Statement stmt1 = conn.createStatement();
            Statement stmt2 = conn.createStatement();
            Statement stmt3 = conn.createStatement();
            ResultSet containerPacks = stmt1.executeQuery(sqlSelectAll + Database.TABLE_NAME_1);
            ResultSet containerData = stmt2.executeQuery(sqlSelectAll + Database.TABLE_NAME_2);
            ResultSet sqlite_sequence = stmt3.executeQuery(sqlSelectAll + "sqlite_sequence");) {
            
            ResultSetMetaData metaData;
            int columnCount;

            metaData = containerPacks.getMetaData();
            columnCount = metaData.getColumnCount();
            System.out.println(columnCount);
            System.out.println(metaData.getColumnName(1));  //starts from 1

            metaData = containerData.getMetaData();
            columnCount = metaData.getColumnCount();
            System.out.println(columnCount);
            System.out.println(metaData.getColumnName(1));

            metaData = sqlite_sequence.getMetaData();
            columnCount = metaData.getColumnCount();
            System.out.println(columnCount);
            System.out.println(metaData.getColumnName(1));
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get queries from the database tables.");
            System.err.println(e);
        }
        */

        /* understanding container labels
            for (Container c : Monitor.getAllContainers()) {
                Map<String, String> labels = c.getLabels();
                for (String key : labels.keySet()) {
                    System.out.printf("[%s - %s] ", key, labels.get(key));
                }
                System.out.println();
            }
            */
    }
    /** Method for running a Command Line Docker Manager application. */
    protected static void commandLineDockerManager() {
        int choice;
        do {
            System.out.println("\n [DOCKER MANAGER MENU]");
            System.out.println("[1] Print All Containers");
            System.out.println("[2] Show Table of All Containers");
            System.out.println("[3] Print All Images");
            System.out.println("[4] Show Table of All Images");
            System.out.println("[5] Manage Containers");
            System.out.println("[-1] Exit Docker Manager\n");  
            choice = getInput(1, 5, "Select an Option: ");

            switch (choice) {
                case 1:
                    System.out.println("\n-- ALL CONTAINERS LIST --");
                    ContainerModel.printContainers(Monitor.getAllContainers());
                    break;
                case 2:
                    System.out.println("\nDisplaying Containers table...");
                    ContainerModel.showTable(Monitor.getAllContainers(), "All Containers");
                    break;
                case 3:
                    System.out.println("\n-- ALL IMAGES LIST --");
                    ImageModel.printImages(Monitor.getImages());
                    break;
                case 4:
                    System.out.println("\nDisplaying Images table...");
                    ImageModel.showTable(Monitor.getImages(), "All Images");
                    break;
                case 5:
                    manageContainers();
                    break;
            }
        } while (choice != -1);
        System.out.println("\nExiting Docker Manager...");
        scanner.close();
    }
    /** Method for handling the user inputs.
     *
     * @param low The lowest integer value that is accepted as the user input.
     * @param high The highest integer value that is accepted as the user input.
     * @param optionMessage Custom message printed when prompting the user to input their option choice.
     * @return An integer representing the user's choice. Can be between (and including) low and high OR -1.
     */
    protected static int getInput(int low, int high, String optionMessage) {
        scanner = new Scanner(System.in);
        String option;
        int choice;
        do {
            choice = 0;
            System.out.print(optionMessage);
            try {
                while (scanner.hasNextLine()) {
                    option = scanner.nextLine();
                    choice = Integer.parseInt(option);
                    if (choice != -1 && choice < low || choice > high) {
                        System.out.println("Input must be an integer from the available options. Try again.");
                        System.out.print(optionMessage);
                    } else {
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Input must be an integer from the available options. Try again.");
            }
        } while (choice != -1 && choice < low || choice > high);
        return choice;
    }
    /** Method for handling Containers when the user picks the option Manage Containers. */
    protected static void manageContainers() {
        int choice;
        do {
            System.out.println("\n [CONTAINER MANAGER MENU]");
            System.out.println("[1] Start a container");
            System.out.println("[2] Stop a container");
            System.out.println("[3] Show Table of All Containers");
            System.out.println("[4] Show Table of Active Containers");
            System.out.println("[5] Show Table of Inactive Containers");
            System.out.println("[-1] Exit Container Manager\n");
            choice = getInput(1, 5, "Select an Option: ");

            switch (choice) {
                case 1:
                    if (Monitor.getInactiveContainers().size() >= 1) {
                        System.out.println("\n-- ALL INACTIVE CONTAINERS --");
                        ContainerModel.printContainers(Monitor.getInactiveContainers());
                        System.out.println();
                        choice = getInput(1, Monitor.getInactiveContainers().size(), "Enter Container Number to Start (-1 to cancel): ");
                        if (choice != -1) {
                            System.out.println();
                            Executor.startContainer(Monitor.getInactiveContainers().get(choice - 1));
                        } else {
                            choice = 0;
                        }
                    } else {
                        System.out.println("\nThere are no Inactive Containers to start.");
                    }
                    break;
                case 2:
                    if (Monitor.getActiveContainers().size() >= 1) {
                        System.out.println("\n-- ALL ACTIVE CONTAINERS --");
                        ContainerModel.printContainers(Monitor.getActiveContainers());
                        System.out.println();
                        choice = getInput(1, Monitor.getActiveContainers().size(), "Enter Container Number to Stop (-1 to cancel): ");
                        if (choice != -1) {
                            System.out.println();
                            Executor.stopContainer(Monitor.getActiveContainers().get(choice - 1));
                        } else {
                            choice = 0;
                        }
                    } else {
                        System.out.println("\nThere are no Active Containers to stop.");
                    }
                    break;
                case 3:
                    ContainerModel.showTable(Monitor.getAllContainers(), "All Containers");
                    break;
                case 4:
                    ContainerModel.showTable(Monitor.getActiveContainers(), "Active Containers");
                    break;
                case 5:
                    ContainerModel.showTable(Monitor.getInactiveContainers(), "Inactive Containers");
                    break;
            }
        } while (choice != -1);
    }
}
