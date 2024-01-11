package gr.aueb.dmst.ProjectPr;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.time.Duration;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;


/** The Main Class. */
public class Main {
    /** The main method and entry point of the application.
     *
     * @param args Command-line arguments, currently not used in any way.
     */
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        try {
            DesktopApp appWindow = new DesktopApp();

            //start screen
            appWindow.openStartScreen();

            //get the dockerClient
            DockerClient dockerClient = getDockerClient();

            appWindow.setProgress(20);

            //test dockerClient usability, print Docker and DockerPilot versions
            String version = dockerClient.versionCmd().exec().getVersion();
            System.out.println("Docker version: " + version);
            System.out.println("DockerPilot version: 1.3");

            appWindow.setProgress(40);

            //set up Monitor and Executor with the dockerClient object
            Monitor.setUpMonitor(dockerClient);
            Executor.setUpExecutor(dockerClient);

            appWindow.setProgress(60);

            //set up Database and if all necessary files are already set up, just sets class variable setUpComplete to true
            Database.setUpDatabase();

            appWindow.setProgress(80);

            //start the API server
            API.start();

            appWindow.setProgress(100);

            //the main application
            appWindow.openMainApplication();

            //wait for window to close and close all connections
            appWindow.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    try {
                        API.stop();
                        dockerClient.close();
                        appWindow.dispose();
                        System.out.println("Successfully exited DockerPilot.");
                    } catch (IOException exitException) {
                        System.err.println("[ERROR] Something went wrong while trying to exit the program.");
                        System.err.println(exitException);
                        System.exit(1);
                    }
                }
            });
        } catch (RuntimeException mainException) {
            System.err.println("[ERROR]"
            + " Make sure the Docker Engine is running.");
            System.err.println(mainException);
            System.exit(1);
        }
    }

    /** Method that sets up and returns the dockerClient for executing the various Docker commands.
     *
     * @return The {@link DockerClient} dockerClient object.
     * @throws RuntimeException if setting up the dockerClient is unsuccessful
     */
    protected static DockerClient getDockerClient() throws RuntimeException {
        //set up and configure the dockerClient used for executing Docker commands.
        DockerClientConfig config = null;
        DockerHttpClient httpClient = null;
        DockerClient dockerClient = null;
        config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                                        .withDockerHost("tcp://localhost:2375")
                                        .build();
        httpClient = new ApacheDockerHttpClient.Builder()
                                            .dockerHost(config.getDockerHost())
                                            .sslConfig(config.getSSLConfig())
                                            .maxConnections(100)
                                            .connectionTimeout(Duration.ofSeconds(30))
                                            .responseTimeout(Duration.ofSeconds(45))
                                            .build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
        return dockerClient;
    }
}
