package ru.akvine.qraft.rest.dto.qrcode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GenerateQrCodeRequest {
    @NotBlank
    private String url;

    @NotBlank
    private String errorCorrectionLevel;

    @Min(0)
    @Max(3)
    private double cornerBlockRadiusFactor;

    @Min(0)
    @Max(1)
    private int radiusFactor;

    @Min(0)
    private int qrSize;

    @Min(0)
    private int borderSize;

    private boolean roundInnerCorners;

    private boolean roundOuterCorners;

    private boolean cornerBlocksAsCircles;

    private String imageType;
}
