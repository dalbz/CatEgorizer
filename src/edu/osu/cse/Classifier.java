package edu.osu.cse;

public class Classifier {

    /**
     * @param args
     */
    public static void main(String[] args) {

        CatImage testCatImage = new CatImage(
                "data/images/cats/00000001_000.jpg");

        testCatImage.save("data/images", "00000001_000.png");

        testCatImage = new CatImage("data/images/cats/00000001_005.jpg");

        testCatImage.save("data/images", "00000001_005.png");

        System.out.println("Completed");

    }

}
