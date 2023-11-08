package gr.aueb.dmst.ProjectPr;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.github.dockerjava.api.model.Image;

public class ImageModel {
    public static void printImage(Image image) {
        double sizeInMB = (double) image.getSize() / 1000000;
        String formattedNumber = String.format("%.2f", sizeInMB);
        Instant instant = Instant.ofEpochSecond(image.getCreated());
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = instant.atZone(zoneId).format(formatter);
        System.out.println("The image's ID is: " + image.getId() + " with size " + formattedNumber + " MB, and was created on " + formattedDate);
    }

    public static void printImages(List<Image> images) {
        System.out.println("ALL IMAGES");
        for (Image image : images) {
            printImage(image);
        }
    }
}
