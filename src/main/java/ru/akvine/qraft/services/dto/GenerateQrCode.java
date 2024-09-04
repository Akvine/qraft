package ru.akvine.qraft.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.qraft.enums.ImageType;

@Data
@Accessors(chain = true)
public class GenerateQrCode {
    private String url;
    private String errorCorrectionLevel;

    private Double cornerBlockRadiusFactor;

    private Integer radiusFactor;
    private Integer qrSize;
    private Integer borderSize;

    private boolean roundInnerCorners;
    private boolean roundOuterCorners;
    private boolean cornerBlocksAsCircles;

    private ImageType imageType;
}
