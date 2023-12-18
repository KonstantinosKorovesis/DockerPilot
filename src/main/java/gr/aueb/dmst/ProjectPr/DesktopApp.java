package gr.aueb.dmst.ProjectPr;

import java.util.List;
import java.util.ArrayList;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.awt.image.BufferedImage;

public class DesktopApp extends JFrame{
    private static final String[] dataColumns = {"ID", "Image", "Labels", "Name", "Date Created", "Status"};
    private static final String[] COMBO_BOX_OPTIONS = {"All", "Active Only", "Inactive Only"};
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 30;
    private static final int ROW_HEIGHT = 20;
    private static final int LIST_FONT_SIZE = 16;
    private static final int LABEL_FONT_SIZE = 14;
    private static DefaultTableModel tableModel = new DefaultTableModel() {
        @Override
        public int getColumnCount() {
            return dataColumns.length;
        }
        @Override
        public String getColumnName(int col) {
            return dataColumns[col];
        }
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private static List<String[]> tableItems = new ArrayList<>();
    private static JScrollBar verticalScrollBar;
    private static JComboBox<String> dropDownBox;
    private static String selectedOption;
    private static List<Container> containers = new ArrayList<Container>(); 


    public DesktopApp() {
        // 1A - THE JFRAME WINDOW SETTINGS
        setTitle("Docker Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        // 2 - THE MAIN LIST + LABEL ABOVE
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public int getColumnCount() {
                return dataColumns.length;
            }
            @Override
            public String getColumnName(int col) {
                return dataColumns[col];
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable mainTable = new JTable(tableModel);
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainTable.setFont(new Font("Arial", Font.PLAIN, LIST_FONT_SIZE));
        mainTable.setShowVerticalLines(false);
        mainTable.setShowHorizontalLines(false);
        mainTable.setShowGrid(false);
        mainTable.setRowHeight(ROW_HEIGHT);

        JScrollPane scrollPane = new JScrollPane(mainTable);
        verticalScrollBar = scrollPane.getVerticalScrollBar();

        /*ImageJPanel imagePanel = new ImageJPanel();
        mainTable.add(imagePanel, BorderLayout.CENTER); */
        
        /* 
        JFrame frame = new JFrame();
        BufferedImage backgroundImage;
        try { 
            backgroundImage = ImageIO.read(new File("\"C:\\Users\\Ξ§Ξ±ΟΞ¬\\Documents\\Ξ ΟΞΏΞ³ΟΞ±ΞΌΞΌΞ±Ο„ΞΉΟƒΞΌΟΟ‚ 2\\TeamLogo.img"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        public void paint(Graphics g) {
            // drawImage(Image img, int x, int y, ImageObserver observer)
            g.drawImage(backgroundImage, 0, 0, frame); }*/

        // 3 - THE BUTTONS
        //button 1
        JButton button1 = new JButton("Run");
        button1.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button1.setBackground(Color.BLACK);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedRow = String.valueOf(tableModel.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedRow != null) {
                        //TODO: ADD FUNCTIONALITY HERE
                        System.out.println("Action 1 performed on: " + selectedRow);

                        updateContainerTableModel(tableModel);
                    }
                }
            }
        });

        //button 2
        JButton button2 = new JButton("Stop");
        button2.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button2.setBackground(Color.BLACK);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedRow = String.valueOf(tableModel.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedRow != null) {
                        //TODO: ADD FUNCTIONALITY HERE
                        System.out.println("Action 2 performed on: " + selectedRow);

                        updateContainerTableModel(tableModel);
                    }
                }
            }
        });

        //button 3
        JButton button3 = new JButton("Restart");
        button3.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button3.setBackground(Color.BLACK);
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedRow = String.valueOf(tableModel.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedRow != null) {
                        //TODO: ADD FUNCTIONALITY HERE
                        System.out.println("Action 3 performed on: " + selectedRow);

                        updateContainerTableModel(tableModel);
                    }
                }
            }
        });

        //button 4
        JButton button4 = new JButton("Histogram");
        button4.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button4.setBackground(Color.BLACK);
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: ADD FUNCTIONALITY HERE
                System.out.println("DISPLAYING HISTOGRAM");
            }
        });

        JButton button5 = new JButton("DOWNLOAD");
        button5.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button5.setBackground(Color.BLACK);
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: ADD FUNCTIONALITY HERE
            }
        });

        JButton button6 = new JButton("SEARCH");
        button6.setBackground(Color.BLACK);
        button6.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        JTextField searchField = new JTextField(0);
        //searchField.setHorizontalAlignment(2);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                //TODO: ADD FUNCTIONALITY HERE 
                System.out.println("Searching for: " + searchTerm);
            }
        });

        // 4 - THE DROP DOWN BOX WITH OPTIONS ABOUT THE LIST
        dropDownBox = new JComboBox<>(COMBO_BOX_OPTIONS);
        dropDownBox.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        dropDownBox.setBackground(Color.BLACK);
        dropDownBox.setSelectedItem(COMBO_BOX_OPTIONS[0]);
        //initialize list model
        updateContainerTableModel(tableModel);
        dropDownBox.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {

                updateContainerTableModel(tableModel);
                moveToTop();

                System.out.println("Selected Status: " + selectedOption);
            }
        });

        // 1B - THE FINAL SETTINGS
        JPanel buttonAndComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonAndComboBoxPanel.add(button1);
        buttonAndComboBoxPanel.add(button2);
        buttonAndComboBoxPanel.add(button3);
        buttonAndComboBoxPanel.add(dropDownBox);

        JPanel histogramPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        histogramPanel.add(button4);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonAndComboBoxPanel, BorderLayout.WEST);
        southPanel.add(histogramPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        //mainPanel.add(titleLabel, BorderLayout.NORTH);

        /* 
        ImageIcon backgroundImage = new ImageIcon("\"C:\\Users\\Ξ§Ξ±ΟΞ¬\\Documents\\Ξ ΟΞΏΞ³ΟΞ±ΞΌΞΌΞ±Ο„ΞΉΟƒΞΌΟΟ‚ 2\\TeamLogo.img");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setHorizontalAlignment(JLabel.CENTER);
        backgroundLabel.setVerticalAlignment(JLabel.CENTER); */

        JPanel secondPanel = new JPanel(new BorderLayout());
        secondPanel.add(button5, BorderLayout.EAST);
        secondPanel.add(button6, BorderLayout.SOUTH);
        //secondPanel.add(backgroundLabel, BorderLayout.CENTER);
        //secondPanel.add(searchField, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Containers", mainPanel);
        tabbedPane.addTab("Images", secondPanel);
        tabbedPane.addTab("History", searchField);

        getContentPane().add(tabbedPane);

        pack();
        setVisible(true);
    }

    private static List<String[]> getTableItems(String selectedOption) {
        tableItems.clear();
        int i = 0;
        if (selectedOption.equals(COMBO_BOX_OPTIONS[0])) {
            containers = Monitor.getAllContainers();
            for (Container container : containers) {
                tableItems.add(i++, ContainerModel.getContainerEntry(container));
            }
        } else if (selectedOption.equals(COMBO_BOX_OPTIONS[1])) {
            containers = Monitor.getActiveContainers();
            for (Container container : containers) {
                tableItems.add(i++, ContainerModel.getContainerEntry(container));
            }
        } else if (selectedOption.equals(COMBO_BOX_OPTIONS[2])) {
            containers = Monitor.getInactiveContainers();
            for (Container container : containers) {
                tableItems.add(i++, ContainerModel.getContainerEntry(container));
            }
        }
        return tableItems;
    }

    private static void moveToTop() {
        //move to the top of the scroll pane
        verticalScrollBar.setValue(verticalScrollBar.getMinimum());
    }

    private static void updateContainerTableModel(DefaultTableModel tableModel) {
        //update list model based on the current selected option from the drop down box
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        selectedOption = dropDownBox.getSelectedItem().toString();
        for (String[] row : getTableItems(selectedOption)) {
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        DockerClientConfig config = null;
        DockerHttpClient httpClient = null;
        DockerClient dockerClient = null;
        try {
            //set up and configure the dockerClient used for executing Docker commands.
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

            //set up Monitor and Executor with the dockerClient object
            Monitor.setUpMonitor(dockerClient);
            Executor.setUpExecutor(dockerClient);

            //native look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            //run the JFrame and add it to the EDT (Event Dispatch Thread) to be initialized and updated
            SwingUtilities.invokeLater(() -> {
                new DesktopApp1();
            });


        } catch (Exception e) {
            System.err.println("[ERROR]"
            + " Make sure Docker Desktop is open and you've"
            + " exposed Docker daemon on tcp://localhost:2375 without TLS.");
            System.err.println(e);
            System.exit(1);
        }
    }
}

