package gr.aueb.dmst.ProjectPr;

import java.util.List;
import java.util.ArrayList;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class DesktopApp extends JFrame{
    private static final String[] containerDataColumns = {"ID", "Image", "Labels", "Name", "Date Created", "Status"};
    private static final String[] imageDataColumns = {"Name", "Container No."};
    private static final String[] COMBO_BOX_OPTIONS = {"All", "Active Only", "Inactive Only"};
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 30;
    private static final int ROW_HEIGHT = 20;
    private static final int LIST_FONT_SIZE = 16;
    private static List<String[]> tableItems = new ArrayList<>();
    private static List<Container> containers = new ArrayList<Container>(); 

    public DesktopApp() {
        setTitle("Docker Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/dockerpiloticon.png"));
        setIconImage(icon.getImage());

        //TODO: ADD GRAPHICS/ LOGO AND MODIFY START PANEL
        //TODO: POSITION START BUTTON CORRECTLY IN THE START PANEL

        JPanel startPanel = new JPanel(new BorderLayout());

        JButton startButton = new JButton("START DOCKERPILOT");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(startPanel);

                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("Containers", getContainersPanel());
                tabbedPane.addTab("Images", getImagesPanel());
                tabbedPane.addTab("Container History", getContainerHistoryPanel());

                add(tabbedPane);
                revalidate();
                repaint();
            }
        });

        startPanel.add(startButton, BorderLayout.EAST);

        add(startPanel);
        pack();
        setVisible(true);
    }
    
    private JPanel getContainersPanel() {
        // 1 - MAIN TABLE
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public int getColumnCount() {
                return containerDataColumns.length;
            }
            @Override
            public String getColumnName(int col) {
                return containerDataColumns[col];
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
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();

        // 2 - THE DROP DOWN BOX WITH OPTIONS ABOUT THE TABLE
        JComboBox<String> dropDownBox = new JComboBox<>(COMBO_BOX_OPTIONS);
        dropDownBox.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        dropDownBox.setSelectedItem(COMBO_BOX_OPTIONS[0]);
        dropDownBox.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {

                updateContainerTableModel(tableModel, String.valueOf(dropDownBox.getSelectedItem()));
                moveToTop(verticalScrollBar);

                System.out.println("Selected Status: " + dropDownBox.getSelectedItem());
            }
        });

        // 3 - THE BUTTONS
        //button 1
        JButton runButton = new JButton("Run");
        runButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedContainerID = String.valueOf(mainTable.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedContainerID != null) {
                        HTTPRequest.start_container(selectedContainerID);
                        updateContainerTableModel(tableModel, String.valueOf(dropDownBox.getSelectedItem()));
                    }
                }
            }
        });

        //button 2
        JButton stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedContainerID = String.valueOf(tableModel.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedContainerID != null) {
                        HTTPRequest.stop_container(selectedContainerID);
                        updateContainerTableModel(tableModel, String.valueOf(dropDownBox.getSelectedItem()));
                    }
                }
            }
        });

        //button 3
        JButton restartButton = new JButton("Restart");
        restartButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedContainerID = String.valueOf(tableModel.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedContainerID != null) {
                        //TODO: CALL API TO RESTART CONTAINER USING selectedContainerID CONTAINER NAME
                        System.out.println("Action 3 performed on: " + selectedContainerID);

                        updateContainerTableModel(tableModel, String.valueOf(dropDownBox.getSelectedItem()));
                    }
                }
            }
        });
        
        //button 4
        JButton histogramButton = new JButton("Histogram");
        histogramButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        histogramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: DISPLAY WINDOW WITH HISTOGRAM OF CONTAINERS AND HOURS RUNNING
                System.out.println("DISPLAYING HISTOGRAM");
            }
        });

        // 4 - CREATING THE FINAL mainPanel USING ALL THE COMPONENTS
        JPanel buttonAndComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonAndComboBoxPanel.add(runButton);
        buttonAndComboBoxPanel.add(stopButton);
        buttonAndComboBoxPanel.add(restartButton);
        buttonAndComboBoxPanel.add(dropDownBox);
        JPanel histogramPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        histogramPanel.add(histogramButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonAndComboBoxPanel, BorderLayout.WEST);
        southPanel.add(histogramPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        //initialize table model and set up a timer to update it every 1 second
        updateContainerTableModel(tableModel, String.valueOf(dropDownBox.getSelectedItem()));
        
        return mainPanel;
    }

    private JPanel getImagesPanel() {
        // 1 - MAIN TABLE
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public int getColumnCount() {
                return imageDataColumns.length;
            }
            @Override
            public String getColumnName(int col) {
                return imageDataColumns[col];
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
        //JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();

        // 3 - THE BUTTONS
        //button 1
        JButton createButton = new JButton("Create New Container");
        createButton.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedImageName = String.valueOf(mainTable.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedImageName != null) {
                        //TODO: CREATE NEW CONTAINER USING selectedImageName IMAGE NAME
                        System.out.println("Action 1 performed on: " + selectedImageName);

                        updateImageTableModel(tableModel);
                    }
                }
            }
        });

        //button 2
        JButton downloadButton = new JButton("Download New Image");
        downloadButton.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: TAKE USER INPUT AND DOWNLOAD NEW IMAGE
                System.out.println("Action 2 performed");

                updateImageTableModel(tableModel);
            }
        });
        /*
        //button 2
        JButton button2 = new JButton("Download New Image");
        button2.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedRow = String.valueOf(tableModel.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedRow != null) {
                        System.out.println("Action 2 performed on: " + selectedRow);

                        updateImageTableModel(tableModel);
                    }
                }
            }
        });

        //button 3
        JButton button3 = new JButton("Restart");
        button3.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //selected value is the whole item on that row as a string
                if (mainTable.getSelectedRow() != -1) {
                    String selectedRow = String.valueOf(tableModel.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedRow != null) {
                        System.out.println("Action 3 performed on: " + selectedRow);

                        updateImageTableModel(tableModel);
                    }
                }
            }
        });
        */

        // 4 - CREATING THE FINAL mainPanel USING ALL THE COMPONENTS
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeftPanel.add(createButton);
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRightPanel.add(downloadButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(bottomLeftPanel, BorderLayout.WEST);
        southPanel.add(bottomRightPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        //initialize image table and set up a timer to update it every 1 second
        updateImageTableModel(tableModel);

        return mainPanel;
    }

    private JPanel getContainerHistoryPanel() {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public int getColumnCount() {
                return containerDataColumns.length;
            }
            @Override
            public String getColumnName(int col) {
                return containerDataColumns[col];
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
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        
        JLabel measurementInfoLabel = new JLabel("Running Containers: -");
        
        JButton chooseButton = new JButton("Choose Measurement");
        chooseButton.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = openDialogInputWindow();
                if (options != null) {
                    //TODO: MAKE updateContainerHistoryTable(tableModel, String[] options)
                    //TODO: UPDATE LABEL WITH RUNNING CONTAINER COUNT
                    System.out.println("UPDATE TABLE WITH" + options.toString());

                    moveToTop(verticalScrollBar);
                } else {
                    System.out.println("DO NOTHING?");
                }
                
            }
        });

        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeftPanel.add(chooseButton);
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRightPanel.add(measurementInfoLabel);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(bottomLeftPanel, BorderLayout.WEST);
        southPanel.add(bottomRightPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        //chooseButton.doClick();
        return mainPanel;
    }

    private String[] openDialogInputWindow() {
        //TODO: ADD COMBOBOX ACCURATE INFO
        String[] dates = {"DATE 1", "DATE 2", "DATE 3"};
        String[] measurements = new String[100];
        for (int i = 0; i < 100; i++) {
            measurements[i] = String.valueOf(i);
        }

        JComboBox<String> dateComboBox = new JComboBox<>(dates);
        JComboBox<String> measurementComboBox = new JComboBox<>(measurements);

        JPanel dialogPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        dialogPanel.add(new JLabel("Select Date:"));
        dialogPanel.add(dateComboBox);
        dialogPanel.add(new JLabel("Select Measurement ID:"));
        dialogPanel.add(measurementComboBox);

        int option = JOptionPane.showConfirmDialog(this, dialogPanel, "Select A Specific Measurement", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            //TODO: USE / RETURN SELECTED OPTIONS TO BE USED
            String selectedDate = (String) dateComboBox.getSelectedItem();
            String selectedMeasurement = (String) measurementComboBox.getSelectedItem();
            System.out.println("Option 1: " + selectedDate);
            System.out.println("Option 2: " + selectedMeasurement);
            String[] options = {selectedDate, selectedMeasurement};
            return options;
        } else {
            System.out.println("Dialog canceled or closed");
            return null;
        }
    }

    //FOR CONTAINER TAB
    //TODO: USE API AND SELECTS FOR CONTAINERS
    private List<String[]> getContainerTableItems(String selectedOption) {
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

    //FOR CONTAINER TAB
    private void updateContainerTableModel(DefaultTableModel tableModel, String selectedOption) {
        //update table model based on the current selected option from the drop down box
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        for (String[] row : getContainerTableItems(selectedOption)) {
            tableModel.addRow(row);
        }
    }

    //FOR IMAGE TAB
    //TODO: USE API AND SELECTS FOR USED IMAGES
    private List<String[]> getImageTableItems() {
        tableItems.clear();
        int i = 0;
        for (Image image : Monitor.getImages()) {
            String[] row = {ImageModel.getFormattedName(image), String.valueOf(ImageModel.getContainerNumber(image))};
            tableItems.add(i++, row);
        }
        return tableItems;
    }

    //FOR IMAGE TAB
    private void updateImageTableModel(DefaultTableModel tableModel) {
        //update table model based on the current selected option from the drop down box
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        for (String[] row : getImageTableItems()) {
            tableModel.addRow(row);
        }
    }
    //FOR ALL
    private void moveToTop(JScrollBar verticalScrollBar) {
        //move to the top of the scroll pane
        verticalScrollBar.setValue(verticalScrollBar.getMinimum());
    }

    public static void runWindow() {
        //native look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        Object lock = new Object();

        SwingUtilities.invokeLater(() -> {
            DesktopApp app = new DesktopApp();
            app.setVisible(true);
            app.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
        });

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}