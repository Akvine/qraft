package ru.akvine.qraft.utils;

import lombok.experimental.UtilityClass;
import ru.akvine.qraft.exceptions.TechnicalException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@UtilityClass
public class ImageIOUtils {

    public BufferedImage read(InputStream inputStream) {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException exception) {
            throw new TechnicalException("Some error was occurred. Message = [" + exception.getMessage() + "]");
        }
    }

    public void write(RenderedImage image,
                      String formatName,
                      OutputStream output) {
        try {
           ImageIO.write(image, formatName, output);
        } catch (IOException exception) {
            throw new TechnicalException("Some error was occurred. Message = [" + exception.getMessage() + "]");
        }
    }
}
