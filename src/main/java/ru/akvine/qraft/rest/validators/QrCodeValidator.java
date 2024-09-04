package ru.akvine.qraft.rest.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.akvine.qraft.rest.dto.qrcode.GenerateQrCodeRequest;
import ru.akvine.qraft.validators.ImageTypeValidator;

@Component
@RequiredArgsConstructor
public class QrCodeValidator {
    private final ImageTypeValidator imageTypeValidator;

    public void verifyGenerateQrCodeRequest(GenerateQrCodeRequest request) {
        imageTypeValidator.validate(request.getImageType());
    }
}
