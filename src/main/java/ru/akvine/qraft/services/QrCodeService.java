package ru.akvine.qraft.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.qraft.core.QrCodeGenerator;
import ru.akvine.qraft.managers.ImageConverterManager;
import ru.akvine.qraft.services.dto.GenerateQrCode;
import ru.akvine.qraft.utils.QrCodeUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    private final ImageConverterManager imageConverterManager;

    public byte[] generateQrCode(GenerateQrCode generateQrCode) {
        QrCodeGenerator qrCodeGenerator = QrCodeGenerator.builder()
                .cornerBlocksAsCircles(generateQrCode.isCornerBlocksAsCircles())
                .roundInnerCorners(generateQrCode.isRoundInnerCorners())
                .roundOuterCorners(generateQrCode.isRoundOuterCorners())
                .cornerBlockRadiusFactor(generateQrCode.getCornerBlockRadiusFactor())
                .radiusFactor(generateQrCode.getRadiusFactor())
                .qrSize(generateQrCode.getQrSize())
                .errorCorrectionLevel(generateQrCode.getErrorCorrectionLevel())
                .build();
        List<String> paths = qrCodeGenerator.generate(generateQrCode.getUrl());
        StringBuilder sb = new StringBuilder();
        paths.forEach(sb::append);
        byte[] qrCode = imageConverterManager
                .getConverterServices()
                .get(generateQrCode.getImageType())
                .convert(sb.toString(), generateQrCode);
        return QrCodeUtils.addWhiteBoard(generateQrCode.getImageType(), qrCode, generateQrCode.getBorderSize());
    }
}
