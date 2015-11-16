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

        double[] xKernel = generateGaussianKernel(sigma);
        double[] yKernel = generateGaussianKernel(sigma);

        Image horizontalConvolutedImage = applyHorizontalConvolution(inputImage, xKernel);
        return applyVerticalConvolution(horizontalConvolutedImage, yKernel);
    }

    private static Image applyHorizontalConvolution(Image inputImage, double[] xConvolution) {

        Image outputImage = new Image(0, inputImage.width, inputImage.height);

        for (int y = 0; y < inputImage.height; y++) {
            for (int x = 0; x < inputImage.width; x++) {
                int total = 0;
                for (int z = 0; z < xConvolution.length; z++) {
                    int xPixel = x + z - (xConvolution.length /2);

                    if (xPixel < 0)
                        xPixel = 0;
                    else if (xPixel >= inputImage.width) {
                        xPixel = inputImage.width - 1;
                    }

                    total += xConvolution[z] * inputImage.pixels[xPixel][y];
                }
                outputImage.pixels[x][y] = total;
            }
        }
        return outputImage;
    }

    private static Image applyVerticalConvolution(Image inputImage, double[] yConvolution) {
        Image outputImage = new Image(0, inputImage.width, inputImage.height);

        for (int y = 0; y < inputImage.height; y++) {
            for (int x = 0; x < inputImage.width; x++) {
                int total = 0;
                for (int z = 0; z < yConvolution.length; z++) {
                    int yPixel = y + z - (yConvolution.length /2);

                    if (yPixel < 0) {
                        yPixel = 0;
                    } else if (yPixel >= inputImage.height) {
                        yPixel = inputImage.height - 1;
                    }

                    total += yConvolution[z] * inputImage.pixels[x][yPixel];
                }
                outputImage.pixels[x][y] = total;
            }
        }
        return outputImage;
    }

    private static double[] generateGaussianKernel(int sigma) {
        int kernelSize = (6 * sigma) + 1;
        int relativeKernelSize = kernelSize / 2;

        double[] xConvolution = new double[kernelSize];
        double sum = 0.0;

        for (int x = 0; x < kernelSize; ++x) {
            xConvolution[x] = d1Gaussian(sigma, x - relativeKernelSize);
        }

        for (double value : xConvolution)
                sum += value;

        // Weight scaling
        for (int index = 0; index < xConvolution.length; index++) {
            xConvolution[index] /= sum * 0.49;
        }

        return xConvolution;
    }

    private static double d1Gaussian(double sigma, double x) {
        double c = 2.0 * sigma * sigma;
        return Math.exp(-x * x / c) / Math.sqrt(c * Math.PI);
    }

}

