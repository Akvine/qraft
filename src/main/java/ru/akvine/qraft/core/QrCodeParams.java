package ru.akvine.qraft.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QrCodeParams {
    private boolean cornerBlocksAsCircles;
    private int radiusFactor;
    private double blockRadiusFactor;
    private int qrSize;
    private ErrorCorrectionLevel errorCorrectionLevel;
}
