package ru.akvine.qraft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageType {
    SVG("svg"),
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png");

    private final String type;

    public static ImageType safeValueOf(String value) {
        if (value.equals(SVG.getType())) {
            return SVG;
        } else if (value.equals(JPG.getType())) {
            return JPG;
        } else if (value.equals(JPEG.getType())) {
            return JPEG;
        } else if (value.equals(PNG.getType())) {
            return PNG;
        } else {
            throw new IllegalArgumentException("Image type = [" + value + "] is not supported");
        }
    }
}
