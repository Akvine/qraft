package ru.akvine.qraft.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GenerateQrCodeRequest {
    @NotBlank
    private String url;

    @NotBlank
    private String errorCorrectionLevel;

    @NotNull
    @Min(0)
    @Max(3)
    private Double cornerBlockRadiusFactor;

    @NotNull
    @Min(0)
    @Max(1)
    private Integer radiusFactor;

    @NotNull
    @Min(0)
    private Integer qrSize;

    private boolean roundInnerCorners;

    private boolean roundOuterCorners;

    private boolean cornerBlocksAsCircles;

}
