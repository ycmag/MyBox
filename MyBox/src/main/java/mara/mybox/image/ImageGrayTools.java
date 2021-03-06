package mara.mybox.image;

import mara.mybox.image.ImageColorTools;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author Mara
 * @CreateDate 2018-6-4 16:07:27
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageGrayTools {

    private static final Logger logger = LogManager.getLogger();

    public static BufferedImage color2Gray(BufferedImage source) {
        try {
            int width = source.getWidth();
            int height = source.getHeight();
            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D graphics = grayImage.createGraphics();
            graphics.drawImage(source, 0, 0, null);
            return grayImage;
        } catch (Exception e) {
            logger.error(e.toString());
            return source;
        }
    }

    public static BufferedImage color2Gray(BufferedImage source, boolean preserveChannels) {
        try {
            int width = source.getWidth();
            int height = source.getHeight();
            BufferedImage grayImage;
            if (preserveChannels) {
                grayImage = new BufferedImage(width, height, source.getType());
            } else {
                grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            }
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp theOp = new ColorConvertOp(cs, null);
            theOp.filter(source, grayImage);
            return grayImage;
        } catch (Exception e) {
            logger.error(e.toString());
            return source;
        }
    }

    public static int calculateThreshold(File file) {
        try {
            BufferedImage bufferImage = ImageIO.read(file);
            return OTSU(color2Gray(bufferImage));
        } catch (Exception e) {
            logger.error(e.toString());
            return -1;
        }

    }

    //  OTSU algorithm: ICV=PA∗(MA−M)2+PB∗(MB−M)2
    // https://blog.csdn.net/taoyanbian1022/article/details/9030825
    // https://blog.csdn.net/liyuanbhu/article/details/49387483
    public static int OTSU(BufferedImage grayImage) {
        try {
            int width = grayImage.getWidth();
            int height = grayImage.getHeight();

            int[] grayNumber = new int[256];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
//                    int r = 0xFF & grayImage.getRGB(i, j);
                    int gray = ImageColorTools.grayPixel2GrayValue(grayImage.getRGB(i, j));
                    grayNumber[gray]++;
                }
            }

            float pixelTotal = width * height;
            float[] grayRadio = new float[256];
            for (int i = 0; i < 256; i++) {
                grayRadio[i] = grayNumber[i] / pixelTotal;
            }

            float backgroundNumber, foregroundNumber, backgoundValue, foregroundValue;
            float backgoundAverage, foregroundAverage, imageAverage, delta, deltaMax = 0;
            int threshold = 0;
            for (int gray = 0; gray < 256; gray++) {
                backgroundNumber = 0;
                foregroundNumber = 0;
                backgoundValue = 0;
                foregroundValue = 0;
                for (int i = 0; i < 256; i++) {
                    if (i <= gray) {
                        backgroundNumber += grayRadio[i];
                        backgoundValue += i * grayRadio[i];
                    } else {
                        foregroundNumber += grayRadio[i];
                        foregroundValue += i * grayRadio[i];
                    }
                }

                backgoundAverage = backgoundValue / backgroundNumber;
                foregroundAverage = foregroundValue / foregroundNumber;
                imageAverage = backgoundValue + foregroundValue;

                delta = backgroundNumber * (backgoundAverage - imageAverage) * (backgoundAverage - imageAverage)
                        + foregroundNumber * (foregroundAverage - imageAverage) * (foregroundAverage - imageAverage);

                if (delta > deltaMax) {
                    deltaMax = delta;
                    threshold = gray;
                }
            }
//            logger.debug("threshold:" + threshold);
            return threshold;

        } catch (Exception e) {
            logger.error(e.toString());
            return -1;
        }
    }

    public static int getIterationBinary(BufferedImage grayImage) {
        try {
            int width = grayImage.getWidth();
            int height = grayImage.getHeight();

            int Threshold = 128;
            int preThreshold = 256;

            while (Math.abs(Threshold - preThreshold) > 4) {
                int s1 = 0;
                int s2 = 0;
                int f1 = 0;
                int f2 = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int gray = grayImage.getRGB(x, y);
                        if (gray < Threshold) {
                            s1 += gray;
                            f1++;
                        } else {
                            s2 += gray;
                            f2++;
                        }
                    }
                }
                preThreshold = Threshold;
                Threshold = (int) ((s1 / f1 + s2 / f2) / 2);
            }
            return Threshold;
        } catch (Exception e) {
            logger.error(e.toString());
            return -1;
        }
    }

    public static BufferedImage color2BinaryWithPercentage(BufferedImage image, int percentage) {
        int threshold = 256 * percentage / 100;
        return color2BinaryWithThreshold(image, threshold);
    }

    public static BufferedImage color2BinaryWithThreshold(BufferedImage image, int threshold) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            int white = Color.WHITE.getRGB();
            int black = Color.BLACK.getRGB();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixel = image.getRGB(i, j);
                    int gray = ImageColorTools.pixel2GrayValue(pixel);
                    if (gray < threshold) {
                        binaryImage.setRGB(i, j, black);
                    } else {
                        binaryImage.setRGB(i, j, white);
                    }
                }
            }
            return binaryImage;
        } catch (Exception e) {
            logger.error(e.toString());
            return image;
        }
    }

    public static BufferedImage color2BinaryByCalculation(BufferedImage image) {
        try {
            BufferedImage grayImage = color2Gray(image);
            return gray2BinaryByCalculation(grayImage);
        } catch (Exception e) {
            logger.error(e.toString());
            return image;
        }
    }

    public static BufferedImage color2Binary(BufferedImage source) {
        try {
            int width = source.getWidth();
            int height = source.getHeight();
            BufferedImage binImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D graphics = binImage.createGraphics();
            graphics.drawImage(source, 0, 0, null);
            return binImage;
        } catch (Exception e) {
            logger.error(e.toString());
            return source;
        }
    }

    public static BufferedImage gray2BinaryWithPercentage(BufferedImage grayImage, int percentage) {
        int threshold = 256 * percentage / 100;
        return gray2BinaryWithThreshold(grayImage, threshold);
    }

    public static BufferedImage gray2BinaryWithThreshold(BufferedImage grayImage, int threshold) {
        try {
            int width = grayImage.getWidth();
            int height = grayImage.getHeight();
            BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            int white = Color.WHITE.getRGB();
            int black = Color.BLACK.getRGB();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixel = grayImage.getRGB(i, j);
                    int gray = ImageColorTools.grayPixel2GrayValue(pixel);
                    if (gray < threshold) {
                        binaryImage.setRGB(i, j, black);
                    } else {
                        binaryImage.setRGB(i, j, white);
                    }
                }
            }
            return binaryImage;
        } catch (Exception e) {
            logger.error(e.toString());
            return grayImage;
        }
    }

    public static BufferedImage gray2BinaryByCalculation(BufferedImage grayImage) {
        int threshold = OTSU(grayImage);
        return gray2BinaryWithThreshold(grayImage, threshold);
    }

}
