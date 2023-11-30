package gr.aueb.dmst.ProjectPr;

import java.util.List;
import java.util.ArrayList;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;

/** The Monitor Thread Class. */
public class Monitor {
    protected static DockerClient dockerClient;
    protected static List<Container> activeContainers = new ArrayList<>();
    protected static List<Container> inactiveContainers = new ArrayList<>();
    protected static List<Container> allContainers = new ArrayList<>();
    protected static List<Image> images = new ArrayList<>();

    /** Method that is used before interacting with Containers and Images.
     * Sets up the Monitor Thread with the instance of the dockerClient and initializes all Lists.
     * 
     * @param dockerClient The main dockerClient used to execute Docker commands.
     */
    public static void setUpMonitor(DockerClient dockerClient) {
        Monitor.dockerClient = dockerClient;
        activeContainers = getActiveContainers();
        inactiveContainers = getInactiveContainers();
        allContainers = getAllContainers();
        images = getImages();
    }

    /** Method for getting all Active Containers.
     * 
     * @return A {@link List} of {@link Container} objects representing active containers.
     * @see Container 
     */
    public static List<Container> getActiveContainers() {
        activeContainers = dockerClient.listContainersCmd()
                                      .withShowAll(false)
                                      .exec();
        return activeContainers;
    }

    /** Method for getting all Inactive Containers.
     * 
     * @return A {@link List} of {@link Container} objects representing inactive containers.
     * @see Container 
     */
    public static List<Container> getInactiveContainers() {
        inactiveContainers.clear();
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
        allContainers = dockerClient.listContainersCmd()
                                   .withShowAll(true)
                                   .exec();
        return allContainers;
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
