package ru.akvine.qraft.rest.converters;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.akvine.qraft.enums.ImageType;
import ru.akvine.qraft.rest.dto.qrcode.GenerateQrCodeRequest;
import ru.akvine.qraft.services.dto.GenerateQrCode;

@Component
public class QrCodeConverter {
    public GenerateQrCode convertToGenerateQrCode(GenerateQrCodeRequest request) {
        Preconditions.checkNotNull(request, "GenerateQrCodeRequest is null");
        return new GenerateQrCode()
                .setUrl(request.getUrl())
                .setQrSize(request.getQrSize())
                .setErrorCorrectionLevel(request.getErrorCorrectionLevel())
                .setRadiusFactor(request.getRadiusFactor())
                .setCornerBlockRadiusFactor(request.getCornerBlockRadiusFactor())
                .setRoundInnerCorners(request.isRoundInnerCorners())
                .setRoundOuterCorners(request.isRoundOuterCorners())
                .setCornerBlocksAsCircles(request.isCornerBlocksAsCircles())
                .setBorderSize(request.getBorderSize())
                .setImageType(StringUtils.isNotBlank(request.getImageType()) ? ImageType.safeValueOf(request.getImageType()) : ImageType.PNG);
    }

    public ResponseEntity<?> convertToGenerateQrCodeResponse(ImageType imageType, byte[] qrcode) {
        Preconditions.checkNotNull(qrcode, "qrcode is null");

        String type;
        if (imageType.equals(ImageType.JPEG) || imageType.equals(ImageType.JPG)) {
            type = MediaType.IMAGE_JPEG_VALUE;
        } else {
            type = MediaType.IMAGE_PNG_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, type)
                .body(qrcode);
    }
}
