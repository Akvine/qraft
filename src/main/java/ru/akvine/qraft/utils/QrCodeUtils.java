package ru.akvine.qraft.utils;

import lombok.experimental.UtilityClass;
import ru.akvine.qraft.enums.ImageType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@UtilityClass
public class QrCodeUtils {
    public byte[] addWhiteBoard(ImageType imageType, byte[] qrcode, int borderSize) {
        ByteArrayInputStream qrCodeInputStream = new ByteArrayInputStream(qrcode);
        BufferedImage qrImage = ImageIOUtils.read(qrCodeInputStream);

        int newWidth = qrImage.getWidth() + borderSize * 2;
        int newHeight = qrImage.getHeight() + borderSize * 2;

        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        // Заполнить все изображение белым цветом
        Graphics2D g2d = newImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newWidth, newHeight);

        // Нарисовать исходное изображение в центре
        g2d.drawImage(qrImage, borderSize, borderSize, null);
        g2d.dispose();

        ByteArrayOutputStream outputStreamWithBorder = new ByteArrayOutputStream();
        ImageIOUtils.write(newImage, imageType.getType(), outputStreamWithBorder);
        return outputStreamWithBorder.toByteArray();
    }
}
