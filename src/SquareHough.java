import java.awt.*;
import java.io.IOException;
import java.util.Vector;

public class SquareHough {

    public SquareHough() {
    }

    public static void main(String[] args) {
//        Image image = new Image();
//        image.ReadPGM(args[0]);
//        int sqrLength = Integer.getInteger(args[1]);
//        int chgToTheta = Integer.getInteger(args[2]);
//        int f1 = Integer.getInteger(args[3]);
//        int f2 = Integer.getInteger(args[4]);
//        int f3 = Integer.getInteger(args[5]);

        String fileNameIn = "./images/im1-s200.pgm";
        Image image = new Image();
        image.ReadPGM(fileNameIn);
        DoG(image);
    }

    private static void DoG(Image image) {
        Image image1 = GaussianFilter.blur(image, 1);
        Image image2 = GaussianFilter.blur(image, 2);
//        image2.WritePGM("dog2.pgm");
//        image1.WritePGM("dog1.pgm");

        Image imagePGM = takeAway(image1, image2);

        imagePGM.WritePGM("DoG.pgm");

        houghTransform(imagePGM);
    }

    private static Image takeAway(Image im1, Image im2) {
        Image outputImage = im1;

        // Iterate over every pixel. Start at 1 and end at -1 to prevent array out of bounds.
        for (int y = 1; y < im1.height - 1; y++) {
            for (int x = 1; x < im1.width - 1; x++) {
                outputImage.pixels[x][y] = im1.pixels[x][y] - im2.pixels[x][y];
            }
        }

        return outputImage;
    }

    private static void houghTransform(Image imagePGM) {
        // create a hough transform object with the right dimensions
        HoughTransform h = new HoughTransform(imagePGM);

        // add the points from the image (or call the addPoint method separately if your points are not in an image
        h.addPoints(imagePGM);

        // Accumulator
        Image im = h.getHoughArrayImage();

        im.WritePGM("houghSpace.pgm");

        ImagePPM imagePPM = ImagePPM.PGMToPPM(imagePGM);

        // get the lines out
        Vector<HoughLine> lines = h.getLines(10);

        // draw the lines back onto the image
        for (int j = 0; j < lines.size(); j++) {
            HoughLine line = lines.elementAt(j);
            line.draw(imagePPM, Color.GREEN.getRGB());
        }

        imagePPM.WritePPM("houghLines.ppm");
    }

}
