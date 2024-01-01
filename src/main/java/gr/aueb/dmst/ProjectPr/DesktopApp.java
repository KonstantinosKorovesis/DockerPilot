package gr.aueb.dmst.ProjectPr;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesktopApp extends JFrame {
    private static final String[] containerDataColumnNames = {"Name", "ID", "Image", "Date Created", "State"};
    private static final String[] imageDataColumnNames = {"Name", "ID", "Size", "Container No."};
    private static final String[] COMBO_BOX_OPTIONS = {"All", "Active Only", "Inactive Only"};
    private static final Color START_COLOR = new Color(34,61,254);
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 30;
    private static final int ROW_HEIGHT = 20;
    private static final int FONT_SIZE = 16;
    private static final Font FONT = new Font("Arial", Font.PLAIN, FONT_SIZE);
    private JPanel startPanel = new JPanel(new GridBagLayout());
    private JProgressBar progressBar = new JProgressBar();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private List<String[]> tableItems = new ArrayList<>();
    private List<Container> containers = new ArrayList<Container>();
    private DefaultTableModel containerTabDefaultTableModel;
    private JComboBox<String> containerTabDropDownBox;
    private boolean containerTabTipShown = false;
    private boolean imageTabTipShown = false;
    private boolean createMeasurementTipShown = false;

    public DesktopApp() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/icon.png"));
        setIconImage(icon.getImage());
        setTitle("DockerPilot");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        pack();
        setVisible(true);
    }
    
    private JPanel getContainersPanel() {
        containerTabDefaultTableModel = new DefaultTableModel() {
            @Override
            public int getColumnCount() {
                return containerDataColumnNames.length;
            }
            @Override
            public String getColumnName(int col) {
                return containerDataColumnNames[col];
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable mainTable = new JTable(containerTabDefaultTableModel);
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainTable.setFont(FONT);
        mainTable.setShowVerticalLines(false);
        mainTable.setShowHorizontalLines(false);
        mainTable.setShowGrid(false);
        mainTable.setRowHeight(ROW_HEIGHT);

        JScrollPane scrollPane = new JScrollPane(mainTable);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();

        containerTabDropDownBox = new JComboBox<>(COMBO_BOX_OPTIONS);
        containerTabDropDownBox.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        containerTabDropDownBox.setSelectedItem(COMBO_BOX_OPTIONS[0]);
        containerTabDropDownBox.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateContainerTableModel(String.valueOf(containerTabDropDownBox.getSelectedItem()));
                moveToTop(verticalScrollBar);
            }
        });

        JButton runButton = new JButton("Run");
        runButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainTable.getSelectedRow() != -1) {
                    String selectedContainerID = String.valueOf(mainTable.getValueAt(mainTable.getSelectedRow(), 1));
                    if (selectedContainerID != null) {
                        HTTPRequest.start_container(selectedContainerID);
                        updateContainerTableModel(String.valueOf(containerTabDropDownBox.getSelectedItem()));
                    }
                } else {
                    if (!containerTabTipShown) {
                        containerTabTipShown = true;
                        displayMessage("You must select a Container first.");
                    }
                }
            }
        });

        JButton stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainTable.getSelectedRow() != -1) {
                    String selectedContainerID = String.valueOf(containerTabDefaultTableModel.getValueAt(mainTable.getSelectedRow(), 1));
                    if (selectedContainerID != null) {
                        HTTPRequest.stop_container(selectedContainerID);
                        updateContainerTableModel(String.valueOf(containerTabDropDownBox.getSelectedItem()));
                    }
                } else {
                    if (!containerTabTipShown) {
                        containerTabTipShown = true;
                        displayMessage("You must select a Container first.");
                    }
                }
            }
        });

        JButton inspectButton = new JButton("Inspect");
        inspectButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        inspectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainTable.getSelectedRow() != -1) {
                    String selectedContainerID = String.valueOf(containerTabDefaultTableModel.getValueAt(mainTable.getSelectedRow(), 1));
                    if (selectedContainerID != null) {
                        openInspectContainerDialog(selectedContainerID);
                    }
                } else {
                    if (!containerTabTipShown) {
                        containerTabTipShown = true;
                        displayMessage("You must select a Container first.");
                    }
                }
            }
        });
        
        JButton histogramButton = new JButton("Create Measurement");
        histogramButton.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
        histogramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Database.setUpComplete) {
                    Database.insertContainers(Database.insertMeasurement());
                    if (!createMeasurementTipShown) {
                        createMeasurementTipShown = true;
                        displayMessage("Added a new Measurement in the Database. "
                        + "\nOpen the Container History tab to view all created Measurements.");
                    } else {
                        displayMessage("Added a new Measurement in the Database.");
                    }
                }
            }
        });

        JPanel buttonAndComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonAndComboBoxPanel.add(runButton);
        buttonAndComboBoxPanel.add(stopButton);
        buttonAndComboBoxPanel.add(inspectButton);
        buttonAndComboBoxPanel.add(containerTabDropDownBox);
        JPanel histogramPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        histogramPanel.add(histogramButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonAndComboBoxPanel, BorderLayout.WEST);
        southPanel.add(histogramPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        updateContainerTableModel(String.valueOf(containerTabDropDownBox.getSelectedItem()));
        
        return mainPanel;
    }

    private JPanel getImagesPanel() {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public int getColumnCount() {
                return imageDataColumnNames.length;
            }
            @Override
            public String getColumnName(int col) {
                return imageDataColumnNames[col];
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable mainTable = new JTable(tableModel);
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainTable.setFont(FONT);
        mainTable.setShowVerticalLines(false);
        mainTable.setShowHorizontalLines(false);
        mainTable.setShowGrid(false);
        mainTable.setRowHeight(ROW_HEIGHT);

        JScrollPane scrollPane = new JScrollPane(mainTable);

        JButton createButton = new JButton("Create New Container");
        createButton.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainTable.getSelectedRow() != -1) {
                    String selectedImageName = String.valueOf(mainTable.getValueAt(mainTable.getSelectedRow(), 0));
                    if (selectedImageName != null) {
                        openCreateContainerDialog(selectedImageName);
                        updateImageTableModel(tableModel);
                        updateContainerTableModel(String.valueOf(containerTabDropDownBox.getSelectedItem()));
                    }
                } else {
                    if (!imageTabTipShown) {
                        imageTabTipShown = true;
                        displayMessage("You must select an Image first.");
                    }
                }
            }
        });

        JButton inspectButton = new JButton("Inspect");
        inspectButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        inspectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainTable.getSelectedRow() != -1) {
                    String selectedImageID = String.valueOf(tableModel.getValueAt(mainTable.getSelectedRow(), 1));
                    if (selectedImageID != null) {
                        openInspectImageDialog(selectedImageID);
                    }
                } else {
                    if (!imageTabTipShown) {
                        imageTabTipShown = true;
                        displayMessage("You must select an Image first.");
                    }
                }
            }
        });

        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeftPanel.add(createButton);
        bottomLeftPanel.add(inspectButton);
        //JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //bottomRightPanel.add(downloadButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(bottomLeftPanel, BorderLayout.WEST);
        //southPanel.add(bottomRightPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        updateImageTableModel(tableModel);

        return mainPanel;
    }

    private JPanel getContainerHistoryPanel() {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public int getColumnCount() {
                return containerDataColumnNames.length;
            }
            @Override
            public String getColumnName(int col) {
                return containerDataColumnNames[col];
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable mainTable = new JTable(tableModel);
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainTable.setFont(FONT);
        mainTable.setShowVerticalLines(false);
        mainTable.setShowHorizontalLines(false);
        mainTable.setShowGrid(false);
        mainTable.setRowHeight(ROW_HEIGHT);

        JScrollPane scrollPane = new JScrollPane(mainTable);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        
        JLabel dateInfoLabel = new JLabel("Measurement Date: -");
        JLabel idInfoLabel = new JLabel("Measurement ID: -");
        JLabel containerInfoLabel = new JLabel("Running Containers: -");
        
        JButton chooseButton = new JButton("Choose Measurement");
        chooseButton.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_HEIGHT));
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = openMeasurementInputDialog();
                if (options != null) {
                    updateContainerHistoryTable(tableModel, dateInfoLabel, idInfoLabel, containerInfoLabel, options);
                    moveToTop(verticalScrollBar);
                }
            }
        });

        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeftPanel.add(chooseButton);
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        bottomRightPanel.add(dateInfoLabel);
        bottomRightPanel.add(idInfoLabel);
        bottomRightPanel.add(containerInfoLabel);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(bottomLeftPanel, BorderLayout.WEST);
        southPanel.add(bottomRightPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        return mainPanel;
    }

    private void openInspectContainerDialog(String containerID) {
        Map<String, String> containerMap = ContainerModel.getContainerDataMap(containerID);
        
        JPanel dialogPanel = new JPanel(new GridLayout(containerMap.size(), 1, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogPanel.setFont(FONT);
        for (String key : containerMap.keySet()) {
            dialogPanel.add(new JLabel(key + ": " + containerMap.get(key)));
        }

        JDialog inspectDialog = new JDialog();
        inspectDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        inspectDialog.setTitle("Inspecting Container " + containerMap.get("Name"));
        inspectDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        inspectDialog.add(dialogPanel);
        inspectDialog.pack();
        inspectDialog.setVisible(true);
    }

    private void openInspectImageDialog(String imageID) {
        Map<String, String> imageMap = ImageModel.getImageDataMap(imageID);
        
        JPanel dialogPanel = new JPanel(new GridLayout(imageMap.size(), 1, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogPanel.setFont(FONT);
        for (String key : imageMap.keySet()) {
            dialogPanel.add(new JLabel(key + ": " + imageMap.get(key)));
        }

        JDialog inspectDialog = new JDialog();
        inspectDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        inspectDialog.setTitle("Inspecting Image " + imageMap.get("Name"));
        inspectDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        inspectDialog.add(dialogPanel);
        inspectDialog.pack();
        inspectDialog.setVisible(true);
    }

    private void openCreateContainerDialog(String imageName) {
        JTextField inputNameField = new JTextField(3);
        JCheckBox checkBox = new JCheckBox("Use Default Container Name");
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    inputNameField.setEnabled(false);
                } else {
                    inputNameField.setEnabled(true);
                }
            }
        });
        
        JPanel dialogPanel = new JPanel(new GridLayout(3, 3, 20, 5));
        dialogPanel.add(new JLabel("New Container Image:"));
        dialogPanel.add(new JLabel(imageName + ":latest"));
        dialogPanel.add(new JLabel("New Container Name:"));
        dialogPanel.add(inputNameField);
        dialogPanel.add(checkBox);
        dialogPanel.add(Box.createRigidArea(new Dimension(0, 0)));

        int option = JOptionPane.showConfirmDialog(this, dialogPanel, "Create New Container", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            if (checkBox.isSelected()) {
                String result = Executor.createContainer(imageName);
                displayMessage(result);
            } else {
                String result = Executor.createContainer(imageName, inputNameField.getText());
                displayMessage(result);
            }
        }
    }

    private String[] openMeasurementInputDialog() {
        String[] dates = HTTPRequest.available_dates();
        if (dates.length == 0) {
            displayMessage("You must Create a Measurement in the Containers tab first.");
            return null;
        }
        JComboBox<String> dateComboBox = new JComboBox<>(dates);
        dateComboBox.setSelectedIndex(dates.length - 1);
        
        String[] measurements = HTTPRequest.available_measurements(dates[dates.length - 1]);
        String[] measurementOptions = new String[measurements.length];
        for (int i = 0; i < measurementOptions.length; i++) {
            measurementOptions[i] = String.format("<html><font color='black'>%s</font><font color='gray'> [%s / %s]</font></html>",
                measurements[i],
                HTTPRequest.running_container_count(measurements[i]),
                HTTPRequest.all_container_count(measurements[i]));
        }
        JComboBox<String> measurementComboBox = new JComboBox<>(measurementOptions);

        dateComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDate = (String) dateComboBox.getSelectedItem();
                String[] updatedMeasurements = HTTPRequest.available_measurements(selectedDate);
                String [] updatedMeasurementOptions = new String[updatedMeasurements.length];
                for (int i = 0; i < updatedMeasurementOptions.length; i++) {
                    updatedMeasurementOptions[i] = String.format("<html><font color='black'>%s</font><font color='gray'> [%s / %s]</font></html>",
                        updatedMeasurements[i],
                        HTTPRequest.running_container_count(updatedMeasurements[i]),
                        HTTPRequest.all_container_count(updatedMeasurements[i]));
                }
                measurementComboBox.setModel(new DefaultComboBoxModel<>(updatedMeasurementOptions));
            }
        });

        JPanel dialogPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        dialogPanel.add(new JLabel("Select Date:"));
        dialogPanel.add(dateComboBox);
        dialogPanel.add(new JLabel("Select Measurement ID:"));
        dialogPanel.add(measurementComboBox);

        int option = JOptionPane.showConfirmDialog(this, dialogPanel, "Select A Specific Measurement", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String selectedDate = (String) dateComboBox.getSelectedItem();
            String selectedMeasurement = HTTPRequest.available_measurements(selectedDate)[measurementComboBox.getSelectedIndex()];
            String[] options = {selectedDate, selectedMeasurement};
            return options;
        } else {
            return null;
        }
    }

    private List<String[]> getContainerTableItems(String selectedOption) {
        tableItems.clear();
        int i = 0;
        if (selectedOption.equals(COMBO_BOX_OPTIONS[0])) {
            containers = Monitor.getAllContainers();
            for (Container container : containers) {
                Map<String, String> containerMap = ContainerModel.getContainerDataMap(container);
                String[] row = new String[5];
                int j = 0;
                String[] containerDataColumnKeys = {"Name", "ID_12", "Image", "Date Created", "State"};
                for (String col : containerDataColumnKeys) {
                    row[j++] = containerMap.get(col);
                }
                tableItems.add(i++, row);
            }
        } else if (selectedOption.equals(COMBO_BOX_OPTIONS[1])) {
            containers = Monitor.getActiveContainers();
            for (Container container : containers) {
                Map<String, String> containerMap = ContainerModel.getContainerDataMap(container);
                String[] row = new String[5];
                int j = 0;
                String[] containerDataColumnKeys = {"Name", "ID_12", "Image", "Date Created", "State"};
                for (String col : containerDataColumnKeys) {
                    row[j++] = containerMap.get(col);
                }
                tableItems.add(i++, row);
            }
        } else if (selectedOption.equals(COMBO_BOX_OPTIONS[2])) {
            containers = Monitor.getInactiveContainers();
            for (Container container : containers) {
                Map<String, String> containerMap = ContainerModel.getContainerDataMap(container);
                String[] row = new String[5];
                int j = 0;
                String[] containerDataColumnKeys = {"Name", "ID_12", "Image", "Date Created", "State"};
                for (String col : containerDataColumnKeys) {
                    row[j++] = containerMap.get(col);
                }
                tableItems.add(i++, row);
            }
        }
        return tableItems;
    }

    private void updateContainerTableModel(String selectedOption) {
        while (containerTabDefaultTableModel.getRowCount() > 0) {
            containerTabDefaultTableModel.removeRow(0);
        }
        for (String[] row : getContainerTableItems(selectedOption)) {
            containerTabDefaultTableModel.addRow(row);
        }
    }

    private List<String[]> getImageTableItems() {
        tableItems.clear();
        int i = 0;
        for (Image image : Monitor.getImages()) {
            Map<String, String> imageMap = ImageModel.getImageDataMap(image);
            String[] row = new String[4];
                int j = 0;
                String[] imageDataColumnKeys = {"Name", "ID_12", "Size", "Container Number"};
                for (String col : imageDataColumnKeys) {
                    row[j++] = imageMap.get(col);
                }
                tableItems.add(i++, row);
        }
        return tableItems;
    }

    private void updateImageTableModel(DefaultTableModel tableModel) {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        for (String[] row : getImageTableItems()) {
            tableModel.addRow(row);
        }
    }

    private List<String[]> getContainerHistoryItems(String[] options) {
        tableItems.clear();
        String selectedMeasurement = options[1];
        tableItems = HTTPRequest.container_entries(selectedMeasurement);
        return tableItems;
    }

    private void updateContainerHistoryTable(DefaultTableModel tableModel,
                                            JLabel dateInfoLabel, JLabel idInfoLabel, JLabel containerInfoLabel,
                                            String[] options) {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        for (String[] row : getContainerHistoryItems(options)) {
            tableModel.addRow(row);
        }

        dateInfoLabel.setText("Measurement Date: " + options[0]);
        idInfoLabel.setText("Measurement ID: " + options[1]);
        String containerInfoText = String.format("Running Containers: %s / %s", 
            HTTPRequest.running_container_count(options[1]), HTTPRequest.all_container_count(options[1]));
        containerInfoLabel.setText(containerInfoText);
    }

    private void moveToTop(JScrollBar verticalScrollBar) {
        verticalScrollBar.setValue(verticalScrollBar.getMinimum());
    }

    private void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void openStartScreen() {
        startPanel.setBackground(START_COLOR);

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/start_image.png"));
        java.awt.Image originalImage = originalIcon.getImage();
        java.awt.Image scaledImage = originalImage.getScaledInstance(750, 562, java.awt.Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(scaledIcon);

        progressBar.setPreferredSize(new Dimension(600, progressBar.getPreferredSize().height));
        progressBar.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                JProgressBar progressBar = (JProgressBar) c;
                int min = progressBar.getMinimum();
                int max = progressBar.getMaximum();
                int value = progressBar.getValue();

                double percent = (double) (value - min) / (max - min);

                Rectangle bar = getBounds();
                g.setColor(Color.WHITE);
                g.fillRect(bar.x, bar.y, bar.width, bar.height);

                int progress = (int) (bar.width * percent);
                g.setColor(START_COLOR);
                g.fillRect(bar.x, bar.y, progress, bar.height);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(-150, 0, 0, 0);
        gbc.anchor = GridBagConstraints.PAGE_START;
        startPanel.add(logoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        startPanel.add(progressBar, gbc);


        add(startPanel);
        revalidate();
        repaint();
    }

    public void setProgress(int progress) {
        progressBar.setValue(progress);
    }

    public void openMainApplication() {
        remove(startPanel);

        tabbedPane.addTab("Containers", getContainersPanel());
        tabbedPane.addTab("Images", getImagesPanel());
        tabbedPane.addTab("Container History", getContainerHistoryPanel());

        add(tabbedPane);
        revalidate();
        repaint();
    }
}
