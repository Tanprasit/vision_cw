import javax.xml.ws.soap.MTOM;

public class GaussianFilter {


//    Pseudo code for applying Gaussian (or any separable filter):
//
//    calculate 1D Gaussian (or other separable filter) coefficients
//    for each row
//      for each col
//          apply 1D coefficients in horizontal axis
//    for each row
//      for each col
//          apply 1D coefficients in vertical axis

    public static Image blur(Image inputImage, int sigma) {
        Image outputImage = new Image(0, inputImage.width, inputImage.height);

        for (int x = 0; x < inputImage.width; x++) {
            for (int y = 0; y < inputImage.height; y++ ) {
                outputImage.pixels[x][y] = getAverageIntensity(x, y, inputImage, sigma);
            }
        }

        return outputImage;
    }

    private static int getAverageIntensity(int row, int column, Image inputImage, int sigma) {
        int kernelSize = (6 * sigma) + 1;

        int[][] imagePixels = getImagePixels( row, column, inputImage, kernelSize, sigma);

        int total = 0;

        for (int y = 0; y < kernelSize; y++)
            for (int x = 0; x < kernelSize; x++)
//                total +=  (1 / Math.sqrt(2 * Math.PI * sigma)) * (d1Gaussian(x, sigma) * d1Gaussian(y, sigma)) ;
                total += imagePixels[x][y];

        return total;
    }

    private static int[][] getImagePixels(int row, int column, Image inputImage, int kernelSize, int sigma) {

        int[][] kernel = new int[kernelSize][kernelSize];

        for (int x = 0; x < kernelSize; x++) {
            int xPixel = clamp(row - kernelSize/2 + x, 0, inputImage.width);
            int relativeX = x - kernelSize/2;
            for (int y = 0; y < kernelSize; y++) {
                int yPixel = clamp(column -   kernelSize/2 + y, 0, inputImage.height);
                int relativeY = y - kernelSize/2;
                kernel[x][y] =  (int) (clamp(inputImage.pixels[xPixel][yPixel], 0, 255) * asd(sigma, relativeX,relativeY ));
            }
        }

        return kernel;
    }

    private static double d1Gaussian(int coordinate, int sigma) {
        return (Math.exp(Math.pow(coordinate, 2) / Math.pow(2 * sigma, 2)));
    }

    private static int clamp(int value, int min, int max) {
        if (value > max) value = max;
        if (value < min) value = min;
        return value;
    }

    private static double asd(int sigma, int x, int y) {
        double lhs = 1 / (2 * Math.PI * Math.pow(sigma, 2));
        double rhs = Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
        return lhs * rhs;
    }

}

