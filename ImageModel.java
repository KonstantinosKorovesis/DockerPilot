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

public class ImageModel {
    protected final static String[] dataColumns = {"Number", "Name", "ID", "Size", "Container No.", "Date Created"};
    
    protected static String getFormattedName(Image image) {
        String name = (image.getRepoTags()[0]).split(":")[0];
        return name;
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

    protected static String getFormattedDate(Image image) {
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

    //prints the basic information about the given Image in one line
    public static void printImage(Image image) {
        String id = getFormattedID(image);
        String name = getFormattedName(image);
        String formattedSize = getFormattedSize(image);
        String formattedDate = getFormattedDate(image);
        System.out.printf("[ID: %.12s]  [Name: %s]  [Size: %s]  [Created: %s]\n", id, name, formattedSize, formattedDate);
    }

    //prints and numbers all given Images
    public static void printImages(List<Image> images) {
        int i = 1;
        for (Image image : images) {
            System.out.print("[" + i++ + "] ");
            printImage(image);
        }
    }

    //returns a 2D array with each row representing an entry of information about a single Image
    //rows: the size of the dataColumns {"Number", "Name", "ID", "Size", "Container No.", "Date Created"};
    //columns: the number of images
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
            imageModel[i][5] = getFormattedDate(image);
            i++;
        }
        return imageModel;
    }

    //creates a frame (window) and displays the given Images List
    public static void showTable(List<Image> images) {
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

        String[][] imageModel = getImageModel(images);
        for (String[] row : imageModel) {
            model.addRow(row);
        }
        
        frame.setTitle("All Images");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setSize(800,600);
        frame.add(spane);
        frame.setVisible(true);
    }
}
