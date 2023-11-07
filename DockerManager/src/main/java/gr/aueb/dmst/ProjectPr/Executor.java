package gr.aueb.dmst.ProjectPr;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;

public class Executor {
    private static DockerClient dockerClient;

    //set up Executor Thread with starting isntance of dockerClient
    public static void setUpExecutor(DockerClient dockerClient) {
        Executor.dockerClient = dockerClient;
    }

    //update Executor Thread with current isntance of dockerClient
    public static void updateExecutor(DockerClient dockerClient) {
        Executor.dockerClient = dockerClient;     
    }

    //receives and starts Container (if it's not already running)
    public static void startInstance(Container c) {
        if (c.getState().equals("running")) {
            System.err.printf("The container %s is already running. \n", c.getId());
        } else {
            System.out.printf("Starting container %s \n", c.getId());
            dockerClient.startContainerCmd(c.getId()).exec();
            //pause for 1 second to allow Container to start
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
            //update instance of dockerClient
            Main.updateDockerClient();
        }
    }

    //receives and stops Container (if it's not already stopped)
    public static void stopInstance(Container c) {
        if (c.getState().equals("exited")) {
            System.err.printf("The container %s is already stopped. \n", c.getId());
        } else {
            System.out.printf("Stopping container %s \n", c.getId());
            dockerClient.stopContainerCmd(c.getId()).exec();
            //pause for 1 second to allow Container to stop
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
            //update instance of dockerClient
            Main.updateDockerClient();
        }
    }
}
