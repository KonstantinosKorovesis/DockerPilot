package gr.aueb.dmst.ProjectPr;

import java.util.List;
import java.time.Duration;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

public class Main {
    private static DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                                                                .withDockerHost("tcp://localhost:2375")
                                                                .build();
    private static DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                                                                   .dockerHost(config.getDockerHost())
                                                                   .sslConfig(config.getSSLConfig())
                                                                   .maxConnections(100)
                                                                   .connectionTimeout(Duration.ofSeconds(30))
                                                                   .responseTimeout(Duration.ofSeconds(45))
                                                                   .build();
    private static DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
    public static void main(String[] args) {
        //set starting values of Monitor and Executor
        Monitor.setUpMonitor(dockerClient);
        Executor.setUpExecutor(dockerClient);

        List<Container> containers;

        containers = Monitor.getAllInstances();       
        Executor.startInstance(containers.get(0));  //start container 0

        containers = Monitor.getAllInstances();
        Executor.stopInstance(containers.get(0));   //stop container 0

        containers = Monitor.getAllInstances();
        Executor.stopInstance(containers.get(0));   //trying to stop container 0 again

        //print containers
        System.out.println("-- ALL CONTAINERS LIST --");
        containers = Monitor.getAllInstances();
        (containers).forEach(c -> System.out.println(c.getId() + "  " + c.getState()));
    }

    //updates Monitor and Executor thread with new instance of dockerClient to reflect current changes
    public static void updateDockerClient() {
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
        Monitor.updateMonitor(dockerClient);
        Executor.updateExecutor(dockerClient);
    }
}
