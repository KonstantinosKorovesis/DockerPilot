package gr.aueb.dmst.ProjectPr;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.BadRequestException;
import com.github.dockerjava.api.exception.ConflictException;

/** The Executor Thread Class. */
public final class Executor {
    /** The {@link DockerClient} object used for executing Docker commands in the Executor Thread. */
    private static DockerClient dockerClient;

    private Executor() {
        throw new UnsupportedOperationException(
        "The Monitor Thread is a utility class and cannot be instantiated.");
    }

    /** Method that is used before interacting with Containers.
     * Sets up the Executor Thread with the instance of the dockerClient.
     *
     * @param dockerClientMain The main dockerClient used to execute Docker commands.
     */
    public static void setUpExecutor(DockerClient dockerClientMain) {
        Executor.dockerClient = dockerClientMain;
    }

    /** Method for Starting a Container.
     *
     * @param containerId The ID of the container to be started.
     * @return An integer: 0 if the Container was started successfully, 1 otherwise.
     */
    public static int startContainer(String containerId) {
        InspectContainerResponse containerInspect = dockerClient.inspectContainerCmd(containerId).exec();
        String containerState = containerInspect.getState().getStatus();
        if ("running".equalsIgnoreCase(containerState)) {
            return 1;
        } else {
            try {
                dockerClient.startContainerCmd(containerId).exec();
            } catch (Exception e) {
                return 1;
            }
            
            containerState = "exited";
            while ("exited".equalsIgnoreCase(containerState)) {
                containerInspect = dockerClient.inspectContainerCmd(containerId).exec();
                containerState = containerInspect.getState().getStatus();
            }
            return 0;
        }
    }

    /** Method for Stopping a Container.
     *
     * @param containerId The ID of the container to be stopped.
     * @return An integer: 0 if the Container was stopped successfully, 1 otherwise.
     */
    public static int stopContainer(String containerId) {
        InspectContainerResponse containerInspect = dockerClient.inspectContainerCmd(containerId).exec();
        String containerState = containerInspect.getState().getStatus();
        if ("exited".equalsIgnoreCase(containerState)) {
            return 1;
        } else {
            try {
                dockerClient.stopContainerCmd(containerId).exec();
            } catch (Exception e) {
                return 1;
            }
            
            containerState = "running";
            while ("running".equalsIgnoreCase(containerState)) {
                containerInspect = dockerClient.inspectContainerCmd(containerId).exec();
                containerState = containerInspect.getState().getStatus();
            }
            return 0;
        }
    }

    public static String createContainer(String imageName, String containerName) {
        try {
            dockerClient.createContainerCmd(imageName+":latest")
                        .withName(containerName)
                        .exec();
            return "Created new container called " + containerName + " with Image " + imageName + ".";
        } catch (BadRequestException e) {
            return "Invalid container name. Only [a-zA-Z0-9][a-zA-Z0-9_.-] are allowed.";
        } catch (ConflictException e) {
            return "Invalid container name. The container name " + containerName + " is already in use.";
        } catch (Exception e) {
            return "Something went wrong when attempting to create a new container.";
        }
    }

    public static String createContainer(String imageName) {
        try {
            dockerClient.createContainerCmd(imageName+":latest")
                        .exec();
            return "Created new container with Image " + imageName + ".";
        } catch (Exception e) {
            return "Something went wrong when attempting to create a new container.";
        }
    } 
}
