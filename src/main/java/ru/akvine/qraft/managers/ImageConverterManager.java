package ru.akvine.qraft.managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.akvine.qraft.enums.ImageType;
import ru.akvine.qraft.services.image.ImageConverterService;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ImageConverterManager {
    private final Map<ImageType, ImageConverterService> converterServices;
}
