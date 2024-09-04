package ru.akvine.qraft.services.image;

import ru.akvine.qraft.enums.ImageType;
import ru.akvine.qraft.services.dto.GenerateQrCode;

public interface ImageConverterService {
    byte[] convert(String svg, GenerateQrCode generateQrCode);

    ImageType getType();
}
