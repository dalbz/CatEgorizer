package edu.osu.cse;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class CatImage {

    BufferedImage mImage;

    HashMap<Feature, BufferedImage> mSubImages = new HashMap<CatImage.Feature, BufferedImage>();

    int[] mCoordinates = new int[18];

    static enum Feature {
        L_EAR, R_EAR, L_EYE, R_EYE, NOSE
    }

    public CatImage(String filepath) {

        // load the image

        try {
            mImage = ImageIO.read(new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // parse the coordinate file

        // 1 - Left eye
        // 2 - Right eye
        // 3 - Mouth
        // 4 - Left ear-1
        // 5 - Left ear-2
        // 6 - Left ear-3
        // 7 - Right ear-1
        // 8 - Right ear-2
        // 9 - Right ear-3

        File coordinateFile = new File(filepath + ".cat");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    coordinateFile));

            String input = reader.readLine();
            String[] coordinateStrings = input.split(" ", 19);

            if (coordinateStrings.length == 19) {
                for (int i = 1; i < 19; i += 2) {
                    mCoordinates[i - 1] = Integer.parseInt(coordinateStrings[i]
                            .trim());
                    mCoordinates[i] = Integer.parseInt(coordinateStrings[i + 1]
                            .trim());
                }
            } else {
                // handle error
            }

            reader.close();

            // get sub-images

            extractSubImages();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void extractSubImages() {

        int imgWidth;
        int leftX;
        int topY;

        // nose

        imgWidth = (int) (euclideanDistance(mCoordinates[0], mCoordinates[1],
                mCoordinates[2], mCoordinates[3]) / 2);

        leftX = mCoordinates[4] - imgWidth / 2;
        topY = mCoordinates[5] - imgWidth / 2;

        BufferedImage nose = mImage
                .getSubimage(leftX, topY, imgWidth, imgWidth);

        mSubImages.put(Feature.NOSE, nose);


        // eyes


        // ears
    }

    private static double euclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public void save(String dataFolder, String fileName) {
        try {

            // save raw image

            BufferedImage nose = mSubImages.get(Feature.NOSE);
            File outputfile = new File(dataFolder + "/noses/raw/" + fileName);
            ImageIO.write(nose, "png", outputfile);

            // save processed image

            CannyEdgeDetector detector = new CannyEdgeDetector();
            detector.setLowThreshold(0.5f);
            detector.setHighThreshold(1f);
            // apply it to an image
            detector.setSourceImage(nose);
            detector.process();
            BufferedImage edges = detector.getEdgesImage();
            outputfile = new File(dataFolder + "/noses/processed/"
                    + fileName);
            ImageIO.write(edges, "png", outputfile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
