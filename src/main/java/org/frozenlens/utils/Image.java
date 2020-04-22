package org.frozenlens.utils;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
    private static File sourceImage;
    private static BufferedImage bufferedImage;
    private static Graphics2D g2d = null;

    public Image(File image) throws IOException {
        this.sourceImage = image;
        this.bufferedImage = ImageIO.read(this.sourceImage);
    }

    public Image(String fileName) throws IOException {
        this(new File(fileName));
    }

    public void resize(int width, int height) {
        this.bufferedImage = Scalr.resize(this.bufferedImage, Scalr.Method.QUALITY, width, height);
    }

    public void resize(int size) {
        this.resize(size, size);
    }

    public void applyTextWatermark(WaterMarkTypes type, String text, Float alpha, Color color, String fontFamily, int fontSize)
            throws UnknownWatermarkTypeException {
        g2d = (Graphics2D)bufferedImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaChannel);
        g2d.setColor(color);
        g2d.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(text, g2d);
        int x = 0;
        int y = 0;
        int rectWidth = (int)rect.getWidth();
        int rectHeight = (int)rect.getHeight();
        int imgWidth = bufferedImage.getWidth();
        int imgHeight = bufferedImage.getHeight();
        int top = 0; int left = 0; int bottom = imgHeight - rectHeight; int right = imgWidth - rectWidth;
        switch(type) {
            case BOTTOMLEFT:
                y = bottom; x = left;
                break;
            case BOTTOMRIGHT:
                y = bottom; x = right;
                break;
            case TOPLEFT:
                y = top; x = left;
                break;
            case TOPRIGHT:
                y = top; x = right;
                break;
            case BIGANDCENTER:
                y = bottom/2; x = right/2;
                break;
            default:
                throw new UnknownWatermarkTypeException("Unknown watermark type");
        }
        g2d.drawString(text, x, y);
    }

    public void save(String filename, String type) throws IOException {
        save(new File(filename), type);
    }

    public void save(File file, String type) throws IOException {
        ImageIO.write(bufferedImage, type, file);

        if (g2d != null) {
            g2d.dispose();
            g2d = null;
        }
    }
}

enum WaterMarkTypes {
    BIGANDCENTER,
    BOTTOMRIGHT,
    BOTTOMLEFT,
    TOPLEFT,
    TOPRIGHT
}
