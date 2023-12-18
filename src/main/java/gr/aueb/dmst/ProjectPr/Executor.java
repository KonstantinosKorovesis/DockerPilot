package gr.aueb.dmst.ProjectPr;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;

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
    public static void setUpExecutor(final DockerClient dockerClientMain) {
        Executor.dockerClient = dockerClientMain;
    }

    /** Method for Starting a Container.
     * If the Container is already running, only notifies the user that no action was taken.
     *
     * @param container The container to be started.
     */
    public static void startContainer(final Container container) {
        if ("running".equalsIgnoreCase(container.getState())) {
            System.out.printf("The container %s is already running. No action taken.\n", ContainerModel.getFormattedName(container));
        } else {
            System.out.printf("Starting container %s... \n", ContainerModel.getFormattedName(container));
            dockerClient.startContainerCmd(container.getId()).exec();
            //waiting until the container's state turns to running
            InspectContainerResponse containerInspect;
            String containerState = "exited";
            while ("exited".equalsIgnoreCase(containerState)) {
                containerInspect = dockerClient.inspectContainerCmd(container.getId()).exec();
                containerState = containerInspect.getState().getStatus();
            }
            System.out.printf("%s is now running. \n", ContainerModel.getFormattedName(container));
        }
    }

    /** Method for Starting a Container.
     *
     * @param containerId The ID of the container to be started.
     * @return An integer: 0 if the Container was already running or 1 if the Container was successfully started.
     */
    public static int startContainer(final String containerId) {
        InspectContainerResponse containerInspect = dockerClient.inspectContainerCmd(containerId).exec();
        String containerState = containerInspect.getState().getStatus();
        if ("running".equalsIgnoreCase(containerState)) {
            //System.out.printf("The container %.12s is already running. No action taken.\n", containerId);
            return 0;
        } else {
            //System.out.printf("Starting container %.12s... \n", containerId);
            dockerClient.startContainerCmd(containerId).exec();
            //waiting until the container's state turns to running
            containerState = "exited";
            while ("exited".equalsIgnoreCase(containerState)) {
                containerInspect = dockerClient.inspectContainerCmd(containerId).exec();
                containerState = containerInspect.getState().getStatus();
            }
            //System.out.printf("%.12s is now running. \n", containerId);
            return 1;
        }
    }

    /** Method for Stopping a Container.
     * If the Container is already exited, only notifies the user that no action was taken.
     *
     * @param container The container to be stopped.
     */
    public static void stopContainer(final Container container) {
        if ("exited".equalsIgnoreCase(container.getState())) {
            System.out.printf("The container %s is already stopped. No action taken.\n", ContainerModel.getFormattedName(container));
        } else {
            System.out.printf("Stopping container %s... \n", ContainerModel.getFormattedName(container));
            dockerClient.stopContainerCmd(container.getId()).exec();
            //waiting until the container's state turns to exited
            InspectContainerResponse containerInspect;
            String containerState = "running";
            while ("running".equalsIgnoreCase(containerState)) {
                containerInspect = dockerClient.inspectContainerCmd(container.getId()).exec();
                containerState = containerInspect.getState().getStatus();
            }
            System.out.printf("%s is no longer running. \n", ContainerModel.getFormattedName(container));
        }
    }

    /** Method for Stopping a Container.
     *
     * @param containerId The ID of the container to be stopped.
     * @return An integer: 0 if the Container was already exited or 1 if the Container was successfully stopped.
     */
    public static int stopContainer(final String containerId) {
        InspectContainerResponse containerInspect = dockerClient.inspectContainerCmd(containerId).exec();
        String containerState = containerInspect.getState().getStatus();
        if ("exited".equalsIgnoreCase(containerState)) {
            //System.out.printf("The container %.12s is already stopped. No action taken.\n", containerId);
            return 0;
        } else {
            //System.out.printf("Stopping container %.12s... \n", containerId);
            dockerClient.stopContainerCmd(containerId).exec();
            //waiting until the container's state turns to exited
            containerState = "running";
            while ("running".equalsIgnoreCase(containerState)) {
                containerInspect = dockerClient.inspectContainerCmd(containerId).exec();
                containerState = containerInspect.getState().getStatus();
            }
            //System.out.printf("%.12s is no longer running. \n", containerId);
            return 1;
        }
    }
}
