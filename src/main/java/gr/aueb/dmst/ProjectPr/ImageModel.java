package gr.aueb.dmst.ProjectPr;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;

/** The Image Model Class
 * used for getting information regarding Image objects.
 * @see Image
 */
public class ImageModel {
    /** Constructor not meant to be used, only added for clarification. */
    private ImageModel() {
        throw new UnsupportedOperationException(
        "The ImageModel Class is a utility class and cannot be instantiated.");
    }

    protected static String getName(Image image) {
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

    protected static String getFormattedTime(Image image) {
        Instant instant = Instant.ofEpochSecond(image.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = instant.atZone(zoneId).format(formatter);
        return formattedTime;
    }

    protected static String getFormattedDate(Image image) {
        Instant instant = Instant.ofEpochSecond(image.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

    protected static String getLabelsString(Image image) {
        String labelsString = "";
        Map<String, String> labels = image.getLabels();
        if (labels != null) {
            for (String key : labels.keySet()) {
                labelsString += "[" + key + " - " + labels.get(key) + "] ";
            }
        }
        return labelsString;
    }

    /** Method that creates and returns a Map containing all the available information of a given Image.
     *
     * @param image The passed Image object.
     * @return A {@link Map} with String key and value parameters.
     * The following keys correspond to data related to the passed Image:
     * ID, ID_12, Labels, Size, Name, Date Created, Time Created, Container Number
     */
    public static Map<String, String> getImageDataMap(Image image) {
        Map<String, String> imageMap = new LinkedHashMap<>();
        imageMap.put("ID", ImageModel.getID(image));
        imageMap.put("ID_12", ImageModel.getFormattedID(image));
        imageMap.put("Labels", ImageModel.getLabelsString(image));
        imageMap.put("Size", ImageModel.getFormattedSize(image));
        imageMap.put("Name", ImageModel.getName(image));
        imageMap.put("Date Created", ImageModel.getFormattedDate(image));
        imageMap.put("Time Created", ImageModel.getFormattedTime(image));
        imageMap.put("Container Number", String.valueOf(ImageModel.getContainerNumber(image)));
        return imageMap;
    }

    /** Method that creates and returns a Map containing all the available information of an Image given its ID.
     *
     * @param imageID The passed Image ID.
     * @return A {@link Map} with String key and value parameters.
     * The following keys which correspond to data related to the Image:
     * ID, ID_12, Labels, Size, Name, Date Created, Time Created, Container Number
     */
    public static Map<String, String> getImageDataMap(String imageID) {
        Map<String, String> imageMap = new LinkedHashMap<>();
        for (Image image : Monitor.getImages()) {
            if (ImageModel.getID(image).equals(imageID) || ImageModel.getID(image).startsWith(imageID)) {
                imageMap = getImageDataMap(image);
                break;
            }
        }
        return imageMap;
    }
}
