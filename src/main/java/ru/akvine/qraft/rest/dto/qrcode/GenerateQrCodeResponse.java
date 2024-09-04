package ru.akvine.qraft.rest.dto.qrcode;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.qraft.rest.dto.common.SuccessfulResponse;

@Data
@Accessors(chain = true)
public class GenerateQrCodeResponse extends SuccessfulResponse {
    private byte[] qrcode;
}
