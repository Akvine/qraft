package ru.akvine.qraft.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.qraft.rest.converters.QrCodeConverter;
import ru.akvine.qraft.rest.dto.qrcode.GenerateQrCodeRequest;
import ru.akvine.qraft.rest.validators.QrCodeValidator;
import ru.akvine.qraft.services.QrCodeService;
import ru.akvine.qraft.services.dto.GenerateQrCode;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/qr-code/")
public class QrCodeController {
    private final QrCodeValidator qrCodeValidator;
    private final QrCodeConverter qrCodeConverter;
    private final QrCodeService qrCodeService;

    @PostMapping(value = "/generate")
    public ResponseEntity<?> generate(@Valid @RequestBody GenerateQrCodeRequest request) {
        qrCodeValidator.verifyGenerateQrCodeRequest(request);
        GenerateQrCode generateQrCode = qrCodeConverter.convertToGenerateQrCode(request);
        byte[] qrCode = qrCodeService.generateQrCode(generateQrCode);
        return qrCodeConverter.convertToGenerateQrCodeResponse(generateQrCode.getImageType(), qrCode);
    }
}
