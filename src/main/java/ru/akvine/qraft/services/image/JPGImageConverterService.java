package ru.akvine.qraft.services.image;

import org.springframework.stereotype.Service;
import ru.akvine.qraft.enums.ImageType;
import ru.akvine.qraft.services.dto.GenerateQrCode;

@Service
public class JPGImageConverterService extends JPEGImageConverterService {
    @Override
    public byte[] convert(String svg, GenerateQrCode generateQrCode) {
        return super.convert(svg, generateQrCode);
    }

    @Override
    public ImageType getType() {
        return ImageType.JPG;
    }
}
