package gr.aueb.dmst.ProjectPr;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;

public class ContainerModel {
    protected final static String[] dataColumns = {"Number", "Name", "ID", "Image", "Status", "Date Created"};

    protected static String getFormattedName(Container container) {
        String name = (container.getNames()[0]).split("/")[1];
        return name;
    }

    protected static String getFormattedID(Container container) {
        String id = (container.getId()).split(":")[0];
        id = String.format("%.12s", id);
        return id;
    }
    
    protected static String getFormattedDate(Container container) {
        Instant instant = Instant.ofEpochSecond(container.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = instant.atZone(zoneId).format(formatter);
        return formattedDate;
    }

    protected static String getFormattedImageID(Container container) {
        return (container.getImageId()).split(":")[1];
    }

    protected static String getFormattedImageName(Container container) {
        for (Image image : Monitor.getImages()) {
            if ((image.getId()).equals(container.getImageId())) {
                return (image.getRepoTags()[0]).split(":")[0];
            }
        }
        return "";
    }

    //prints the basic information about the given Container in one line
    public static void printContainer(Container container) {
        String id = getFormattedID(container);
        String state = container.getState();
        String name = getFormattedName(container);
        String formattedDate = getFormattedDate(container); 
        System.out.printf("[ID: %.12s]  [State: %s]  [Name: %s]  [Created: %s]\n", id, state, name, formattedDate);
    }

    //prints and numbers all given Containers
    public static void printContainers(List<Container> containers) {
        int i = 1;
        for (Container container : containers) {
            System.out.print("[" + i++ + "] ");
            printContainer(container);
        }
    }

    //returns a 2D array with each row representing an entry of information about a single Container
    //rows: the size of the dataColumns {"Number", "Name", "ID", "Image", "Status", "Date Created"}
    //columns: the number of containers
    public static String[][] getContainerModel(List<Container> containers) {
        String[][] containerModel = new String[containers.size()][dataColumns.length];
        int i = 0;
        int n = 1;
        for (Container container : containers) {
            containerModel[i][0] = String.valueOf(n++);
            containerModel[i][1] = getFormattedName(container);
            containerModel[i][2] = getFormattedID(container);
            containerModel[i][3] = getFormattedImageName(container);
            containerModel[i][4] = container.getStatus();
            containerModel[i][5] = getFormattedDate(container);
            i++;
        }
        return containerModel;
    }

    //creates a frame (window) and displays the given Containers List
    public static void showTable(List<Container> containers) {
        DefaultTableModel model = new DefaultTableModel() {
            String[] columns = dataColumns;
            @Override 
            public int getColumnCount() { 
                return columns.length; 
            }
            @Override 
            public String getColumnName(int col) { 
                return columns[col]; 
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane spane = new JScrollPane(table);
        JFrame frame = new JFrame();

        String[][] containerModel = getContainerModel(containers);
        for (String[] row : containerModel) {
            model.addRow(row);
        }
        
        frame.setTitle("All Containers");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setSize(800,600);
        frame.add(spane);
        frame.setVisible(true);
    }
}
