package ru.akvine.qraft.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.qraft.core.QrCodeGenerator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/qr-code/")
public class QrCodeController {

    @GetMapping(value = "/generate")
    public ResponseEntity<String> generate(@Valid @RequestBody GenerateQrCodeRequest request) {
        QrCodeGenerator qrCodeGenerator = QrCodeGenerator.builder()
                .cornerBlocksAsCircles(request.isCornerBlocksAsCircles())
                .roundInnerCorners(request.isRoundInnerCorners())
                .roundOuterCorners(request.isRoundOuterCorners())
                .cornerBlockRadiusFactor(request.getCornerBlockRadiusFactor())
                .radiusFactor(request.getRadiusFactor())
                .qrSize(request.getQrSize())
                .errorCorrectionLevel(request.getErrorCorrectionLevel())
                .build();
        List<String> paths = qrCodeGenerator.generate(request.getUrl());
        StringBuilder sb = new StringBuilder();
        paths.forEach(sb::append);
        return ResponseEntity.ok(sb.toString());
    }
}
