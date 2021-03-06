public class HoughLine {

    protected double theta;
    protected double r;

    /**
     * Initialises the hough line
     */
    public HoughLine(double theta, double r) {
        this.theta = theta;
        this.r = r;
    }

    /**
     * Draws the line on the image of your choice with the RGB colour of your choice.
     */
    public void draw(ImagePPM image, int color, double changeOfTheta) {

        int height = image.height;
        int width = image.width;

        // During processing h_h is doubled so that -ve r values
        int houghHeight = (int) Math.sqrt((height * height) + (width * width)) / 2;

        // Find edge points and vote in array
        float centerX = width / 2;
        float centerY = height / 2;

        // Draw edges in output array
        double tsin = Math.sin(theta);
        double tcos = Math.cos(theta);

        double changeOfThetaRad = Math.toRadians(changeOfTheta);

        if (theta < changeOfThetaRad || theta > (changeOfThetaRad + Math.toRadians(90))) {
            // Draw vertical-ish lines

            for (int y = 0; y < height; y++) {
                int x = (int) ((((r - houghHeight) - ((y - centerY) * tsin)) / tcos) + centerX);
                if (x < width && x >= 0) {
                    image.setRGB(x, y, color);
                }
            }
        } else {
            // Draw horizontal-sh lines
            for (int x = 0; x < width; x++) {
                int y = (int) ((((r - houghHeight) - ((x - centerX) * tcos)) / tsin) + centerY);
                if (y < height && y >= 0) {
                    image.setRGB(x, y, color);
                }
            }
        }
    }
}