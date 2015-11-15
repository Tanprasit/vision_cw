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

        double[] xConvolution = getConvolution(sigma);
        double[] yConvolution = getConvolution(sigma);

        Image xConvolutedImage = getHorizontalConvolution(inputImage, xConvolution);
        Image yConvolutedImage = getVerticalConvolution(inputImage, yConvolution);

        return combineConvolution(xConvolutedImage, yConvolutedImage);
    }

    private static Image combineConvolution(Image xConvolutedImage, Image yConvolutedImage) {

        for (int y = 0; y < xConvolutedImage.height; y++) {
            for (int x = 0; x < xConvolutedImage.width; x++) {
                xConvolutedImage.pixels[x][y] = xConvolutedImage.pixels[x][y] + yConvolutedImage.pixels[x][y];
            }
        }

        return xConvolutedImage;
    }

    private static Image getVerticalConvolution(Image inputImage, double[] yConvolution) {
        Image outputImage = new Image(0, inputImage.width, inputImage.height);

        for (int y = 0; y < inputImage.height; y++) {
            for (int x = 0; x < inputImage.width; x++) {
                int total = 0;
                for (int z = 0; z < yConvolution.length; z++) {
                    int yPixel = y + z - (yConvolution.length /2);

                    if (yPixel < 0) {
                        yPixel = 0;
                    }

                    total += yConvolution[z] * inputImage.pixels[x][yPixel];
                }
                outputImage.pixels[x][y] = total;
            }
        }
        return outputImage;
    }

    private static Image getHorizontalConvolution(Image inputImage, double[] xConvolution) {

        Image outputImage = new Image(0, inputImage.width, inputImage.height);

        for (int y = 0; y < inputImage.height; y++) {
            for (int x = 0; x < inputImage.width; x++) {
                int total = 0;
                for (int z = 0; z < xConvolution.length; z++) {
                    int xPixel = x + z - (xConvolution.length /2);

                    if (xPixel < 0)
                        xPixel = 0;

                    total += xConvolution[z] * inputImage.pixels[xPixel][y];
                }
                outputImage.pixels[x][y] = total;
            }
        }
        return outputImage;
    }

    private static double[] getConvolution(int sigma) {
        int kernelSize = (6 * sigma) + 1;
        int relativeKernelSize = kernelSize / 2;

        double[] xConvolution = new double[kernelSize];
        double sum = 0.0;

        for (int x = -relativeKernelSize; x <= relativeKernelSize; x++) {
            xConvolution[x + relativeKernelSize] = d1Gaussian(sigma, x);
            sum += xConvolution[x + relativeKernelSize];
        }

        for (int index = 0; index < xConvolution.length; index++) {
            xConvolution[index] /= sum * 0.5;
        }

        return xConvolution;
    }

    private static double d1Gaussian(int sigma, int coordinate) {
        return (1.0 / (Math.sqrt(2.0 * Math.PI) * sigma)) *  Math.exp(-coordinate * coordinate / (2.0 * sigma * sigma));
    }

}

