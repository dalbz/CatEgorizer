package edu.osu.cse;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.ResampleOp;

public class CatImage {

    BufferedImage mImage;

    String mFilename = "";

    HashMap<Feature, BufferedImage> mSubImages = new HashMap<CatImage.Feature, BufferedImage>();

    int[] mCoordinates = new int[18];

    static enum Feature {
        L_EAR, R_EAR, L_EYE, R_EYE, NOSE
    }

    public CatImage(String folderpath, File imageFile) {

        mFilename = imageFile.getName();

        // load the image

        try {
            mImage = ImageIO.read(imageFile);
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

        File coordinateFile = new File(folderpath + mFilename + ".cat");

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

        leftX = mCoordinates[0] - imgWidth / 2;
        topY = mCoordinates[1] - imgWidth / 2;

        BufferedImage l_eye = mImage.getSubimage(leftX, topY, imgWidth,
                imgWidth);

        mSubImages.put(Feature.L_EYE, l_eye);

        leftX = mCoordinates[2] - imgWidth / 2;
        topY = mCoordinates[3] - imgWidth / 2;

        BufferedImage r_eye = mImage.getSubimage(leftX, topY, imgWidth,
                imgWidth);

        mSubImages.put(Feature.R_EYE, r_eye);

        // ears
    }

    private static double euclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public void save(String dataFolder) {
        for (Feature feature : mSubImages.keySet()) {
            saveSubImage(dataFolder, feature);
        }

    }

    private void saveSubImage(String dataFolder, Feature feature) {

        String featureFolder = "";
        switch (feature) {
        case NOSE:
            featureFolder = "noses";
                break;
        case L_EYE:
            featureFolder = "eyes_left";
                break;
        case R_EYE:
            featureFolder = "eyes_right";
                break;
        case L_EAR:
            featureFolder = "ears_left";
            break;
        case R_EAR:
            featureFolder = "ears_right";
            break;
        }

        BufferedImage rawImage = mSubImages.get(feature);
        File outputfile = new File(dataFolder + "/" + featureFolder + "/raw/"
                + mFilename);
        try {
            ImageIO.write(rawImage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputfile = new File(dataFolder + "/" + featureFolder + "/processed/"
                + mFilename);
        try {
            ImageIO.write(processImage(rawImage), "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static BufferedImage processImage(BufferedImage rawImage) {

        CannyEdgeDetector detector = new CannyEdgeDetector();
        detector.setLowThreshold(0.2f);
        detector.setHighThreshold(1f);

        ResampleOp resampleOp = new ResampleOp(32, 32);
        BufferedImage rescaledImage = resampleOp.filter(rawImage, null);

        detector.setSourceImage(rescaledImage);
        detector.process();

        BufferedImage edges = detector.getEdgesImage();
        return edges;
    }


}
