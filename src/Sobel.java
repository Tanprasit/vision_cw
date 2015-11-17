public class Sobel {

    public static Image applySobelKernel(Image image) {
        Image outputImage = new Image(0, image.width, image.height);

        int[][] xSobel = {{-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}};

        int[][] ySobel = {{-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}};

        for (int y = 1; y < image.height - 2; y++) {
            for (int x = 1; x < image.width - 2; x++) {
                int xPixel = (xSobel[0][0] * image.pixels[x - 1][y - 1]) + (xSobel[0][1] * image.pixels[x][y - 1]) + (xSobel[0][2] * image.pixels[x + 1][y - 1])
                        + (xSobel[1][0] * image.pixels[x - 1][y]) + (xSobel[1][1] * image.pixels[x][y]) + (xSobel[1][2] * image.pixels[x + 1][y])
                        + (xSobel[2][0] * image.pixels[x - 1][y + 1]) + (xSobel[2][1] * image.pixels[x][y + 1]) + (xSobel[2][2] * image.pixels[x + 1][y + 1]);

                int yPixel = (ySobel[0][0] * image.pixels[x - 1][y - 1]) + (ySobel[0][1] * image.pixels[x][y - 1]) + (ySobel[0][2] * image.pixels[x + 1][y - 1])
                        + (ySobel[1][0] * image.pixels[x - 1][y]) + (ySobel[1][1] * image.pixels[x][y]) + (ySobel[1][2] * image.pixels[x + 1][y])
                        + (ySobel[2][0] * image.pixels[x - 1][y + 1]) + (ySobel[2][1] * image.pixels[x][y + 1]) + (ySobel[2][2] * image.pixels[x + 1][y + 1]);

                int magnitude = Math.abs(xPixel) + (Math.abs(yPixel));

                magnitude = 255 - clamp(magnitude / 6, 0, 255);
                outputImage.pixels[x][y] = magnitude;
            }
        }

        return outputImage;
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) value = min;
        if (value > max) value = max;
        return value;
    }
}
