package gr.aueb.dmst.ProjectPr;

import java.util.List;
import java.util.ArrayList;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;

/** The Monitor Thread Class. */
public final class Monitor {
    /** The {@link DockerClient} object used for executing Docker commands in the Monitor Thread. */
    protected static DockerClient dockerClient;

    /** Constructor not meant to be used, only added for clarification. */
    private Monitor() {
        throw new UnsupportedOperationException(
        "The Monitor Thread is a utility class and cannot be instantiated.");
    }

    /** Method that is used before interacting with Containers and Images.
     * Sets up the Monitor Thread with the instance of the dockerClient and initializes all Lists.
     *
     * @param dockerClientMain The main dockerClient used to execute Docker commands.
     */
    public static void setUpMonitor(final DockerClient dockerClientMain) {
        Monitor.dockerClient = dockerClientMain;
    }

    /** Method for getting all Active Containers.
     *
     * @return A {@link List} of {@link Container} objects representing active containers.
     * @see Container
     */
    public static List<Container> getActiveContainers() {
        return dockerClient.listContainersCmd()
                           .withShowAll(false)
                           .exec();
    }

    /** Method for getting all Inactive Containers.
     *
     * @return A {@link List} of {@link Container} objects representing inactive containers.
     * @see Container
     */
    public static List<Container> getInactiveContainers() {
        List<Container> inactiveContainers = new ArrayList<>();
        for (Container container : getAllContainers()) {
            if (container.getState().equals("exited")) {
                inactiveContainers.add(container);
            }
        }
        return inactiveContainers;
    }

    /** Method for getting All Containers regardless of their current state.
     *
     * @return A {@link List} of {@link Container} objects representing all containers.
     * @see Container
     */
    public static List<Container> getAllContainers() {
        return dockerClient.listContainersCmd()
                           .withShowAll(true)
                           .exec();
    }

    /** Method for getting All Images.
     *
     * @return A {@link List} of {@link Image} objects representing all Images.
     * @see Image
     */
    public static List<Image> getImages() {
        return dockerClient.listImagesCmd().exec();
    }
}
