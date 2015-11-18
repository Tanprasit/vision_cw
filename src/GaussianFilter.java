public class GaussianFilter {

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
                    int xPixel = x + z - (xConvolution.length / 2);

                    xPixel = clamp(xPixel, 0, inputImage.width - 1);

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
                    int yPixel = y + z - (yConvolution.length / 2);

                    yPixel = clamp(yPixel, 0, inputImage.height - 1);

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

        // Weight and with scaling factor.
        for (int index = 0; index < xConvolution.length; index++) {
            xConvolution[index] /= (sum * 0.5);
        }

        return xConvolution;
    }

    private static double d1Gaussian(double sigma, double x) {
        double c = 2.0 * sigma * sigma;
        return Math.exp(-x * x / c) / Math.sqrt(c * Math.PI);
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) value = min;
        if (value > max) value = max;
        return value;
    }
}

