package ru.akvine.qraft.services.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.akvine.qraft.enums.ImageType;
import ru.akvine.qraft.services.dto.GenerateQrCode;

@Service
@Slf4j
public class SVGImageConverterService implements ImageConverterService {
    @Override
    public byte[] convert(String svg, GenerateQrCode generateQrCode) {
        return svg.getBytes();
    }

    @Override
    public ImageType getType() {
        return ImageType.SVG;
    }
}
