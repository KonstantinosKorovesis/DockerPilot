package gr.aueb.dmst.ProjectPr;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;

/** The Container Model Class
 * used for getting and displaying information regarding Container objects.
 * @see Container
 */
public class ContainerModel {
    /** The columns of the Container Table displayed. */
    private final static String[] dataColumns = {"Number", "Name", "ID", "Image", "Status", "Date Created"};

    protected static String getFormattedName(Container container) {
        String name = (container.getNames()[0]).split("/")[1];
        return name;
    }
    
    protected static String getID(Container container) {
        String id = container.getId();
        return id;
    }

    protected static String getFormattedID(Container container) {
        String id = container.getId();
        id = String.format("%.12s", id);
        return id;
    }

    protected static String getState(Container container) {
        String state = container.getState();
        return state;
    }

    protected static String getStatus(Container container) {
        String status = container.getStatus();
        return status;
    }

    protected static String getFormattedDateTime(Container container) {
        Instant instant = Instant.ofEpochSecond(container.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = instant.atZone(zoneId).format(formatter);
        return formattedDateTime;
    }

    protected static String getFormattedDate(Container container) {
        Instant instant = Instant.ofEpochSecond(container.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

    protected static String getLabelsString(Container container) {
        String label = String.valueOf(container.getLabels());
        return label;
    }

    /** Method that is used to print the basic information about the given Container in a single line.
     *
     * @param container The passed Container.
     */
    public static void printContainer(Container container) {
        String id = getFormattedID(container);
        String state = container.getState();
        String name = getFormattedName(container);
        String formattedDate = getFormattedDateTime(container);
        System.out.printf("[ID: %.12s]  [State: %s]  [Name: %s]  [Created: %s]\n", id, state, name, formattedDate);
    }
    
    /** Method that is used to get the data needed in the SQLite Database table about the given Container.
     *
     * @param container The passed Container.
     */
    public static String[] getContainerEntry(Container container) {
        String[] entry = new String[6];
        int i = 0;
        entry[i++] = ContainerModel.getID(container);
        entry[i++] = ContainerModel.getFormattedImageName(container);
        entry[i++] = ContainerModel.getLabelsString(container);
        entry[i++] = ContainerModel.getFormattedName(container);
        entry[i++] = ContainerModel.getFormattedDate(container);
        entry[i++] = ContainerModel.getStatus(container);
        return entry;
    }

    /** Method that is used to print the basic information about the given Container objects.
     * Calls the printContainer method for each of the given Container objects.
     *
     * @param containers The passed Container Objects.
     */
    public static void printContainers(List<Container> containers) {
        int i = 1;
        for (Container container : containers) {
            System.out.print("[" + i++ + "] ");
            printContainer(container);
        }
    }

    /** Method for creating a 2D String Array with each row representing an entry of data about a single Container.
     * Row Number: The number of the passed Container Objects in the List.
     * Column Number: The size of dataColumns = {"Number", "Name", "ID", "Image", "Status", "Date Created"}
     *
     * @param containers The passed {@link List} of {@link Container} Objects.
     * @return The 2D String Array of data regarding the passed {@link Container} Objects.
     */
    public static String[][] getContainerTable(List<Container> containers) {
        String[][] containerModel = new String[containers.size()][dataColumns.length];
        int i = 0;
        int n = 1;
        for (Container container : containers) {
            containerModel[i][0] = String.valueOf(n++);
            containerModel[i][1] = getFormattedName(container);
            containerModel[i][2] = getFormattedID(container);
            containerModel[i][3] = getFormattedImageName(container);
            containerModel[i][4] = getStatus(container);
            containerModel[i][5] = getFormattedDateTime(container);
            i++;
        }
        return containerModel;
    }

    /** Method for creating the {@link JFrame} and displaying the passed Container Objects in the List.
     *
     * @param containers The passed {@link List} of {@link Container} Objects.
     * @param title The title of the display Window.
     */
    public static void showTable(List<Container> containers, String title) {
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
        JScrollPane sPane = new JScrollPane(table);
        JFrame frame = new JFrame();

        String[][] containerModel = getContainerTable(containers);
        for (String[] row : containerModel) {
            model.addRow(row);
        }

        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setSize(800,600);
        frame.add(sPane);
        frame.setVisible(true);
    }
}
