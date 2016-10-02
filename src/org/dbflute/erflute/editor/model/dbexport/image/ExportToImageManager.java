package org.dbflute.erflute.editor.model.dbexport.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class ExportToImageManager {

    protected Image img;
    private final int format;
    private final String saveFilePath;
    private String formatName;

    public ExportToImageManager(Image img, int format, String saveFilePath) {
        this.img = img;
        this.format = format;
        this.saveFilePath = saveFilePath;
    }

    public void doProcess() throws IOException, InterruptedException {
        if (format == SWT.IMAGE_JPEG || format == SWT.IMAGE_BMP) {
            writeJPGorBMP(img, saveFilePath, format);

        } else if (format == SWT.IMAGE_PNG || format == SWT.IMAGE_GIF) {
            writePNGorGIF(img, saveFilePath, formatName);
        }
    }

    private void writeJPGorBMP(Image image, String saveFilePath, int format) throws IOException {
        final ImageData[] imgData = new ImageData[1];
        imgData[0] = image.getImageData();

        final ImageLoader imgLoader = new ImageLoader();
        imgLoader.data = imgData;
        imgLoader.save(saveFilePath, format);
    }

    private void writePNGorGIF(Image image, String saveFilePath, String formatName) throws IOException, InterruptedException {
        try {
            final ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { image.getImageData() };
            loader.save(saveFilePath, format);
        } catch (final SWTException e) {
            e.printStackTrace();
            final BufferedImage bufferedImage =
                    new BufferedImage(image.getBounds().width, image.getBounds().height, BufferedImage.TYPE_INT_RGB);
            drawAtBufferedImage(bufferedImage, image, 0, 0);
            ImageIO.write(bufferedImage, formatName, new File(saveFilePath));
        }
    }

    private void drawAtBufferedImage(BufferedImage bimg, Image image, int x, int y) throws InterruptedException {
        final ImageData data = image.getImageData();
        for (int i = 0; i < image.getBounds().width; i++) {
            for (int j = 0; j < image.getBounds().height; j++) {
                final int tmp = 4 * (j * image.getBounds().width + i);
                if (data.data.length > tmp + 2) {
                    final int r = 0xff & data.data[tmp + 2];
                    final int g = 0xff & data.data[tmp + 1];
                    final int b = 0xff & data.data[tmp];

                    bimg.setRGB(i + x, j + y, 0xFF << 24 | r << 16 | g << 8 | b << 0);
                }
                this.doPostTask();
            }
        }
    }

    protected void doPostTask() throws InterruptedException {
    }
}
