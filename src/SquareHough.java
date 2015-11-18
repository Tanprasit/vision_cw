import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class SquareHough {

    // im1-s200.pgm 200 90 0.25 0.75 0.75 L
    // images/tshirt1-124.pgm 124 5 0.25 0.75 0.75 E

    private static int sqrLength;
    private static double changeOfTheta;
    private static double f1;
    private static double f2;
    private static double f3;
    private static String method;

    public SquareHough() {
    }

    public static void main(String[] args) {
        Image image = new Image();

        image.ReadPGM(args[0]);
        sqrLength = Integer.parseInt(args[1]);
        changeOfTheta = Integer.parseInt(args[2]);
        f1 = Double.parseDouble(args[3]);
        f2 = Double.parseDouble(args[4]);
        f3 = Double.parseDouble(args[5]);
        method = args[6];

        switch (method) {
            case "L":
                DoG(image);
                break;
            case "E":
                Sobel(image);
                break;
            default:
                DoG(image);
        }
    }

    private static void DoG(Image image) {
        Image image1 = GaussianFilter.blur(image, 1);
        Image image2 = GaussianFilter.blur(image, 2);

        Image imagePGM = takeAway(image1, image2);

        imagePGM.WritePGM("DoG.pgm");

        houghTransform(imagePGM, image);
    }

    private static void Sobel(Image image) {
        Image sobelImage = Sobel.applySobelKernel(image);

        Image image1 = GaussianFilter.blur(sobelImage, 1);
        Image image2 = GaussianFilter.blur(sobelImage, 2);

        Image imagePGM = takeAway(image1, image2);

        imagePGM.WritePGM("SobelDoG.pgm");

        houghTransform(imagePGM, image);
    }

    private static Image takeAway(Image im1, Image im2) {
        Image outputImage = new Image(0, im1.width, im1.height);

        // Iterate over every pixel. Start at 1  and end at -1 to prevent array out of bounds.
        for (int y = 0; y < im1.height; y++) {
            for (int x = 0; x < im1.width; x++) {
                int value = im2.pixels[x][y] - im1.pixels[x][y];

                outputImage.pixels[x][y] = clamp(value, 0, 255);
            }
        }

        return outputImage;
    }

    private static void houghTransform(Image imagePGM, Image originalImage) {

        // create a hough transform object with the right dimensions
        HoughTransform h = new HoughTransform(imagePGM);

        // add the points from the image (or call the addPoint method separately if your points are not in an image
        h.addPoints(imagePGM);

        // Accumulator
        Image im = h.getHoughArrayImage();

        im.WritePGM("accumulator.pgm");

        ImagePPM imagePPM = ImagePPM.PGMToPPM(originalImage);

        // get the lines out
        ArrayList<HoughLine> lines = h.getLines(f1);

//         draw the lines back onto the image
        for (int j = 0; j < lines.size(); j++) {
            HoughLine line = lines.get(j);
            line.draw(imagePPM, Color.GREEN.getRGB(), changeOfTheta);
        }
        imagePPM.WritePPM("lines.ppm");

        SquareConstructor squareConstructor = new SquareConstructor(lines, sqrLength, changeOfTheta);
        squareConstructor.drawCandidates(ImagePPM.PGMToPPM(originalImage));
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) value = min;
        if (value > max) value = max;
        return value;
    }
}
