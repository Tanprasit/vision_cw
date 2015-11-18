import java.util.*;

public class HoughTransform {

    private int maxTheta = 180;

    // The size of the local window
    final int windowSize = 19;

    // the coordinates of the centre of the image
    private float centerX, centerY;

    // The accumulator for all possible r and thetas.
    private int[][] accumulator;

    // the max r of accumulator
    private int houghWidth;

    // double the hough height (allows for negative numbers)
    private int doubleHoughWidth;

    // the number of points that have been added
    private int numPoints = 0;

    private double stepSize = Math.PI / maxTheta;

    // cache of values of sin and cos for different theta values. Has a significant performance improvement.
    private double[] sinTable;
    private double[] cosTable;

    public HoughTransform(Image image) {

        int width = image.width;
        int height = image.height;

        // Calculate the maximum r for all possible coordinate
        houghWidth =  (int) Math.sqrt((height * height) + (width * width)) / 2;

        // Double the height of the hough array to cope with negative r values
        doubleHoughWidth = 2 * houghWidth;

        // Create the hough array
        accumulator = new int[maxTheta][doubleHoughWidth];

        // Find edge points and vote in array
        centerX = width / 2;
        centerY = height / 2;

        // Count how many points there are
        numPoints = 0;

        sinTable = new double[maxTheta];
        cosTable = sinTable.clone();

        for (int theta = 0; theta < maxTheta; theta++) {
            double thetaRadians = theta * stepSize;
            sinTable[theta] = Math.sin(thetaRadians);
            cosTable[theta] = Math.cos(thetaRadians);
        }
    }

    /**
     * Adds points from an image. The image is assumed to be greyscale black and white, so all pixels that are
     * not black are counted as edges. The image should have the same dimensions as the one passed to the constructor.
     */
    public void addPoints(Image image) {

        // Now find edge points and update the hough array
        for (int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                // Find white pixels or anything that isn't black. 0 == black.
                if (image.pixels[x][y] != 0) {
                    addPoint(x, y);
                }
            }
        }
    }

    /**
     * Adds a single point to the hough transform. You can use this method directly
     * if your data isn't represented as a buffered image.
     */
    public void addPoint(int x, int y) {

        // Go through each value of theta from -r to r
        for (int t = 0; t < maxTheta; t++) {

            //Work out the r values for each theta step
            int rScaled = (int) (((x - centerX) * cosTable[t]) + ((y - centerY) * sinTable[t]));

            // this copes with negative values of r
            rScaled += houghWidth;

            if (rScaled < 0 || rScaled >= doubleHoughWidth) continue;

            // Increment the hough array
            accumulator[t][rScaled]++;

        }

        numPoints++;
    }

    /**
     * Once points have been added in some way this method extracts the lines and returns them as a Vector
     * of HoughLine objects, which can be used to draw on the
     *
     * @param threshold The percentage threshold above which lines are determined from the hough array
     */
    public ArrayList<HoughLine> getLines(double threshold) {

        // Initialise the vector of lines that we'll return
        ArrayList<HoughLine> lines = new ArrayList<>();

        // Break out if there are not points.
        if (numPoints == 0) return lines;

        // Search for local peaks above specified threshold.
        for (int curTheta = 0; curTheta < maxTheta; curTheta++) {
            loop:
            for (int r = windowSize; r < doubleHoughWidth - windowSize; r++) {

                // Only consider points above threshold
                if (accumulator[curTheta][r] > threshold * getHighestValue()) {

                    //Is this point a local maxima (7x7)
                    int peak = accumulator[curTheta][r];

                    // Check that this peak is indeed the local maxima
                    for (int dx = -windowSize; dx <= windowSize; dx++) {
                        for (int dy = -windowSize; dy <= windowSize; dy++) {
                            int dt = curTheta + dx;
                            int dr = r + dy;
                            if (dt < 0) dt = dt + maxTheta;
                            else if (dt >= maxTheta) dt = dt - maxTheta;
                            if (accumulator[dt][dr] > peak) {
                                // found a bigger point nearby, skip
                                continue loop;
                            }
                        }
                    }

                    // calculate the true value of theta
                    double theta = curTheta * stepSize;

                    // add the line to the vector
                    lines.add(new HoughLine(theta, r));
                }
            }
        }

        return lines;
    }

    /**
     * Gets the highest value in the accumulator.
     */
    public int getHighestValue() {
        int max = 0;
        for (int currTheta = 0; currTheta < maxTheta; currTheta++) {
            for (int r = 0; r < doubleHoughWidth; r++) {
                if (accumulator[currTheta][r] > max) {
                    max = accumulator[currTheta][r];
                }
            }
        }
        return max;
    }

    /**
     * Gets the hough array as an image, in case you want to have a look at it.
     */
    public Image getHoughArrayImage() {
        int max = getHighestValue();
        Image image = new Image(0, doubleHoughWidth, maxTheta);
        for (int currTheta = 0; currTheta < maxTheta; currTheta++) {
            for (int r = 0; r < doubleHoughWidth; r++) {

                if (r == doubleHoughWidth / 2) {
                    image.pixels[r][currTheta] =  0;
                } else {
                    image.pixels[r][currTheta] = accumulator[currTheta][r];
                }


            }
        }
        return image;
    }
}
