package gr.aueb.dmst.ProjectPr;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;

/** The Executor Thread Class. */
public class Executor {
    protected static DockerClient dockerClient;

    /** Method that is used before interacting with Containers.
     * Sets up the Executor Thread with the instance of the dockerClient.
     * 
     * @param dockerClient The main dockerClient used to execute Docker commands.
     */
    public static void setUpExecutor(DockerClient dockerClient) {
        Executor.dockerClient = dockerClient;
    }

    /** Method for Starting a Container.
     * If the Container is already running, only notifies the user that no action was taken.
     * 
     * @param container The container to be started.
     * @return Does not return anything. Only prints the process of attempting to start the given Container.
     * @see Container
     */
    public static void startContainer(Container container) {
        if ("running".equalsIgnoreCase(container.getState())) {
            System.err.printf("The container %s is already running. No action taken.\n", ContainerModel.getFormattedName(container));
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

    /** Method for Stopping a Container.
     * If the Container is already exited, only notifies the user that no action was taken.
     * 
     * @param container The container to be started.
     * @return Does not return anything. Only prints the process of attempting to stop the given Container.
     * @see Container
     */
    public static void stopContainer(Container container) {
        if ("exited".equalsIgnoreCase(container.getState())) {
            System.err.printf("The container %s is already stopped. No action taken.\n", ContainerModel.getFormattedName(container));
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
}
