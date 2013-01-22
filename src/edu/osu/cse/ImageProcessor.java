package edu.osu.cse;

import java.io.File;

public class ImageProcessor {

    public static void processImages() {

        File folder = new File("data/images/cats/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.getName().endsWith(".jpg")) {
                CatImage testCatImage = new CatImage("data/images/cats/", file);

                testCatImage.save("data/images");
            }
        }

    }

}
