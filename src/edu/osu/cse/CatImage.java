package edu.osu.cse;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CatImage {

    BufferedImage mImage;

    ArrayList<BufferedImage> mSubImages;

    int[] mCoordinates = new int[18];

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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
