package gr.aueb.dmst.ProjectPr;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Container;

/** The Container Model Class
 * used for getting and displaying information regarding Container objects.
 * @see Container
 */
public class ContainerModel {
    protected static String getName(Container container) {
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

    protected static String getFormattedTime(Container container) {
        Instant instant = Instant.ofEpochSecond(container.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = instant.atZone(zoneId).format(formatter);
        return formattedTime;
    }

    protected static String getFormattedDate(Container container) {
        Instant instant = Instant.ofEpochSecond(container.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = instant.atZone(zoneId).format(formatter);
        return formattedDate;
    }

    protected static String getImageID(Container container) {
        return (container.getImageId()).split(":")[1];
    }

    protected static String getImageName(Container container) {
        for (Image image : Monitor.getImages()) {
            if ((image.getId()).equals(container.getImageId())) {
                return (image.getRepoTags()[0]).split(":")[0];
            }
        }
        return "";
    }

    protected static String getLabelsString(Container container) {
        String labelsString = "";
        Map<String, String> labels = container.getLabels();
        if (labels != null) {
            for (String key : labels.keySet()) {
                labelsString += "[" + key + " - " + labels.get(key) + "] ";
            }
        }
        return labelsString;
    }

    /** Method that is used to get the data needed in the SQLite Database table about the given Container.
     *
     * @param container The passed Container.
     * @return A String Array with the Container data needed for the Database entry.
     */
    public static String[] getContainerEntry(Container container) {
        String[] entry = new String[5];
        int i = 0;
        entry[i++] = ContainerModel.getFormattedID(container);
        entry[i++] = ContainerModel.getImageName(container);
        entry[i++] = ContainerModel.getName(container);
        entry[i++] = ContainerModel.getFormattedDate(container);
        entry[i++] = ContainerModel.getState(container);
        return entry;
    }

    public static Map<String, String> getContainerDataMap(Container container) {
        Map<String, String> containerMap = new LinkedHashMap<>();
        containerMap.put("ID", ContainerModel.getID(container));
        containerMap.put("ID_12", ContainerModel.getFormattedID(container));
        containerMap.put("Image", ContainerModel.getImageName(container));
        containerMap.put("ImageID", ContainerModel.getImageID(container));
        containerMap.put("Labels", ContainerModel.getLabelsString(container));
        containerMap.put("Name", ContainerModel.getName(container));
        containerMap.put("Date Created", ContainerModel.getFormattedDate(container));
        containerMap.put("Time Created", ContainerModel.getFormattedTime(container));
        containerMap.put("Status", ContainerModel.getStatus(container));
        containerMap.put("State", ContainerModel.getState(container));
        return containerMap;
    }

    public static Map<String, String> getContainerDataMap(String containerID) {
        Map<String, String> containerMap = new HashMap<>();
        for (Container container : Monitor.getAllContainers()) {
            if (ContainerModel.getID(container).equals(containerID) || ContainerModel.getID(container).startsWith(containerID)) {
                containerMap = getContainerDataMap(container);
                break;
            }
        }
        return containerMap;
    }
}
