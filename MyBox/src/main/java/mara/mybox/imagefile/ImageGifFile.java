package mara.mybox.imagefile;

import com.github.jaiimageio.impl.plugins.gif.GIFImageMetadata;
import com.github.jaiimageio.impl.plugins.gif.GIFImageWriter;
import com.github.jaiimageio.impl.plugins.gif.GIFImageWriterSpi;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
//import com.sun.imageio.plugins.gif.GIFImageMetadata;
//import com.sun.imageio.plugins.gif.GIFImageReader;
//import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import mara.mybox.objects.ImageAttributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author Mara
 * @CreateDate 2018-6-24
 *
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageGifFile {

    private static final Logger logger = LogManager.getLogger();

    public static GIFImageMetadata getGifMetadata(File file) {
        try {

            GIFImageReaderSpi spi = new GIFImageReaderSpi();
            GIFImageReader reader = new GIFImageReader(spi);
            try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
                reader.setInput(iis, false);
                GIFImageMetadata metadata = (GIFImageMetadata) reader.getImageMetadata(0);
                return metadata;
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

//    public static void writeGifFile(File file) {
//        try {
//
//
//
//            FileImageInputStream in = null;
//            FileImageOutputStream out = null;
//
//            in = new FileImageInputStream(file);
//            ImageReaderSpi readerSpi = new GIFImageReaderSpi();
//            GIFImageReader gifReader = (GIFImageReader) readerSpi.createReaderInstance();
//            gifReader.setInput(in);
//            int num = gifReader.getNumImages(true);
//            int frame = 10;
//// 要取的帧数要小于总帧数
//            if (num > frame) {
//                ImageWriterSpi writerSpi = new GIFImageWriterSpi();
//                GIFImageWriter writer = (GIFImageWriter) writerSpi.createWriterInstance();
//                for (int i = 0; i < num; i++) {
//                    if (i == frame) {
//                        String target;
//                        File newfile = new File(target);
//                        out = new FileImageOutputStream(newfile);
//                        writer.setOutput(out);
////    读取读取帧的图片
//                        writer.write(gifReader.read(i));
//
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error(e.toString());
//            return;
//        }
//    }
//
//    public static int gifFrameCount(byte data[]) throws IOException {
//        ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
//        ImageInputStream ciis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));
//        reader.setInput(ciis, false);
//        int noi = reader.getNumImages(true);
//        ciis.close();
//        return noi;
//    }
    // https://docs.oracle.com/javase/10/docs/api/javax/imageio/metadata/doc-files/gif_metadata.html#image
    public static void writeGifImageFile(BufferedImage image,
            ImageAttributes attributes, String outFile) {
        try {

            GIFImageWriterSpi tiffspi = new GIFImageWriterSpi();
            GIFImageWriter writer = new GIFImageWriter(tiffspi);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (attributes.getCompressionType() != null) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                logger.error(attributes.getCompressionType());
                param.setCompressionType(attributes.getCompressionType());
                param.setCompressionQuality(attributes.getQuality() / 100.0f);
            }

            GIFImageMetadata metaData;
            try {
                metaData = (GIFImageMetadata) writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
            } catch (Exception e) {
                logger.error(e.toString());
                metaData = null;
            }

            if (attributes.getDensity() > 0) {
                // Have not found the way to set density data in meta data of GIF format.
            }

            try (ImageOutputStream out = ImageIO.createImageOutputStream(new File(outFile))) {
                writer.setOutput(out);
                writer.write(metaData, new IIOImage(image, null, metaData), param);
                out.flush();
            }
            writer.dispose();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

}
