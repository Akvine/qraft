package ru.akvine.qraft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConverterType {
    SVG("svg"),
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png");

    private final String type;
}
