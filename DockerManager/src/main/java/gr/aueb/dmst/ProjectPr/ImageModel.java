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

/** The Image Model Class 
 * used for getting and displaying information regarding Image objects.
 * @see Image
 */
public class ImageModel {
    protected final static String[] dataColumns = {"Number", "Name", "ID", "Size", "Container No.", "Date Created"};
    
    protected static String getFormattedName(Image image) {
        String name = (image.getRepoTags()[0]).split(":")[0];
        return name;
    }

    protected static String getID(Image image) {
        String id = (image.getId()).split(":")[1];
        return id;
    }

    protected static String getFormattedID(Image image) {
        String id = (image.getId()).split(":")[1];
        id = String.format("%.12s", id);
        return id;
    }
    
    protected static String getFormattedSize(Image image) {
        double sizeInMB = (double) image.getSize() / 1000000;
        String formattedSize = String.format("%.2f", sizeInMB);
        return formattedSize + " MB";
    }

    protected static String getFormattedDateTime(Image image) {
        Instant instant = Instant.ofEpochSecond(image.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = instant.atZone(zoneId).format(formatter);
        return formattedDate;
    }

    protected static int getContainerNumber(Image image) {
        int c = 0;
        for (Container container : Monitor.getAllContainers()) {
            if (image.getId().equals(container.getImageId())) {
                c++;
            }
        }
        return c;
    }

    /** Method that is used to print the basic information about the given Image in a single line.
     * 
     * @param image The passed Image.
     * @return Does not return anything.
     */
    public static void printImage(Image image) {
        String id = getFormattedID(image);
        String name = getFormattedName(image);
        String formattedSize = getFormattedSize(image);
        String formattedDate = getFormattedDateTime(image);
        System.out.printf("[ID: %.12s]  [Name: %s]  [Size: %s]  [Created: %s]\n", id, name, formattedSize, formattedDate);
    }

    /** Method that is used to print the basic information about the given Image objects.
     * Calls the printContainer method for each of the given Image objects.
     * 
     * @param containers The passed Image Objects.
     * @return Does not return anything.
     */
    public static void printImages(List<Image> images) {
        int i = 1;
        for (Image image : images) {
            System.out.print("[" + i++ + "] ");
            printImage(image);
        }
    }

    /** Method for creating a 2D String Array with each row representing an entry of data about a single Image.
     * Row Number: The number of the passed Image Objects in the List.
     * Column Number: The size of dataColumns = {"Number", "Name", "ID", "Size", "Container No.", "Date Created"}
     * 
     * @param images The passed {@link List} of {@link Image} Objects.
     * @return The 2D String Array of data regarding the passed {@link Image} Objects.
     */
    public static String[][] getImageModel(List<Image> images) {
        String[][] imageModel = new String[images.size()][dataColumns.length];
        int i = 0;
        int n = 1;
        for (Image image : images) {
            imageModel[i][0] = String.valueOf(n++);
            imageModel[i][1] = getFormattedName(image);
            imageModel[i][2] = getFormattedID(image);
            imageModel[i][3] = getFormattedSize(image);
            imageModel[i][4] = String.valueOf(getContainerNumber(image));
            imageModel[i][5] = getFormattedDateTime(image);
            i++;
        }
        return imageModel;
    }

    /** Method for creating the {@link JFrame} and displaying the passed Image Objects in the List.
     * 
     * @param images The passed {@link List} of {@link Image} Objects.
     * @param title The title of the display Window.
     * @return Does not return anything.
     */
    public static void showTable(List<Image> images, String title) {
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

        String[][] imageModel = getImageModel(images);
        for (String[] row : imageModel) {
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
