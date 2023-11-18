package gr.aueb.dmst.ProjectPr;

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
    public static void main(String[] args) {
        //set starting values of Monitor and Executor
        Monitor.setUpMonitor(dockerClient);
        Executor.setUpExecutor(dockerClient);

        List<Container> containers;

        containers = Monitor.getAllContainers();       
        Executor.startContainer(containers.get(0));  //start container 0
        ContainerModel.showTable(Monitor.getAllContainers());
        containers = Monitor.getAllContainers();
        Executor.stopContainer(containers.get(0));   //stop container 0

        containers = Monitor.getAllContainers();
        Executor.stopContainer(containers.get(0));   //trying to stop container 0 again

        //print containers
        System.out.println("-- ALL CONTAINERS LIST --");
        containers = Monitor.getAllContainers();
        (containers).forEach(c -> System.out.println(c.getId() + "  " + c.getState()));
    }
}