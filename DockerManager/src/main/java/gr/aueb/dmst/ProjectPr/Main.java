package gr.aueb.dmst.ProjectPr;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

/** The Main Class. */
public class Main {
    //set up and configure the dockerClient used for executed Docker commands.
    protected static DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                                                                          .withDockerHost("tcp://localhost:2375")
                                                                          .build();
    protected static DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                                                                             .dockerHost(config.getDockerHost())
                                                                             .sslConfig(config.getSSLConfig())
                                                                             .maxConnections(100)
                                                                             .connectionTimeout(Duration.ofSeconds(30))
                                                                             .responseTimeout(Duration.ofSeconds(45))
                                                                             .build();
    protected static DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
    //initialize scanner for user input
    protected static Scanner scanner = new Scanner(System.in);
    /** The main method and entry point the program. Handles the dockerClient and user interactions.
     *
     * @param args Command-line arguments, currently not used in any way.
     */
    public static void main(String[] args) {
	try {
        	Monitor.setUpMonitor(dockerClient);
        	Executor.setUpExecutor(dockerClient);
	} catch (RuntimeException e) {
		System.out.println("[ERROR] Make sure Docker Desktop is open and you've exposed Docker daemon on tcp://localhost:2375 without TLS");
		System.err.println(e);
		System.exit(1);
        }
        
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
                    ContainerModel.showTable(Monitor.getAllContainers());
                    break;
                case 3:
                    System.out.println("\n-- ALL IMAGES LIST --");
                    ImageModel.printImages(Monitor.getImages());
                    break;
                case 4:
                    System.out.println("\nDisplaying Images table...");
                    ImageModel.showTable(Monitor.getImages());
                    break;
                case 5:
                    manageContainers();
                    break;
            }
        } while (choice != -1);
        System.out.println("\nExiting Docker Manager...");
        scanner.close();

	try {
            dockerClient.close();
        } catch (IOException e) {
            System.out.println("[ERROR] Something went wrong while trying to close the dockerClient");
            System.err.println(e);
        }
        System.out.println("Successfully exited Docker Manager.");
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
    /** Method for handling Containers when the user picks option [5] Manage Containers.
     * 
     * @return No return values, only printing.
     */
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

            int containerNum = Monitor.getAllContainers().size();
            switch (choice) {
                case 1:
                    ContainerModel.printContainers(Monitor.getAllContainers());
                    System.out.println();
                    containerNum = getInput(1, containerNum, "Enter Container Number to Start (-1 to cancel): ");
                    if (containerNum != -1) {
                        System.out.println();
                        Executor.startContainer(Monitor.getAllContainers().get(containerNum - 1));
                    }
                    break;
                case 2:
                    if (Monitor.getActiveContainers().size() >= 1) {
                        ContainerModel.printContainers(Monitor.getActiveContainers());
                        System.out.println();
                        containerNum = getInput(1, containerNum, "Enter Container Number to Stop (-1 to cancel): ");
                        if (containerNum != -1) {
                            System.out.println();
                            Executor.stopContainer(Monitor.getActiveContainers().get(containerNum - 1));
                        }
                    } else {
                        System.out.println("There is no active container!");
                    }
                    break;
                case 3:
                    ContainerModel.showTable(Monitor.getAllContainers());
                    break;
                case 4:
                    ContainerModel.showTable(Monitor.getActiveContainers());
                    break;
                case 5:
                    ContainerModel.showTable(Monitor.getInactiveContainers());
                    break;
            }
        } while (choice != -1);
    }
}
