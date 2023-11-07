package gr.aueb.dmst.ProjectPr;

import java.util.List;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;

public class Monitor {
    private static DockerClient dockerClient;
    private static List<Container> activeInstances;
    private static List<Container> allInstances;

    //set up Monitor Thread with starting instance of dockerClient 
    //and Container Lists by calling updateLists()
    public static void setUpMonitor(DockerClient dockerClient) {
        Monitor.dockerClient = dockerClient;
        updateLists();
    }

    //update Monitor Thread with current instance of dockerClient
    public static void updateMonitor(DockerClient dockerClient) {
        Monitor.dockerClient = dockerClient;
        updateLists();
    }

    //returns a Container List of active instances (only running)
    public static List<Container> getActiveInstances() {
        return activeInstances;
    }

    //returns a Container List of all instances (running and exited)
    public static List<Container> getAllInstances() {
        return allInstances;
    }

    //update Monitor using the current instance of dockerClient
    private static void updateLists() {
        activeInstances = dockerClient.listContainersCmd()
                                      .withShowAll(false)
                                      .exec();
        allInstances = dockerClient.listContainersCmd()
                                   .withShowAll(true)
                                   .exec();
    }
}
