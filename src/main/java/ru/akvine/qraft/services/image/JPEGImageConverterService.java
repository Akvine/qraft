package ru.akvine.qraft.services.image;

import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.stereotype.Service;
import ru.akvine.qraft.enums.ImageType;
import ru.akvine.qraft.exceptions.QrCodeConvertException;
import ru.akvine.qraft.services.dto.GenerateQrCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@Slf4j
public class JPEGImageConverterService implements ImageConverterService {
    @Override
    public byte[] convert(String svg, GenerateQrCode generateQrCode) {
        InputStream targetStream = new ByteArrayInputStream(svg.getBytes());
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) generateQrCode.getQrSize());
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) generateQrCode.getQrSize());
        TranscoderInput input = new TranscoderInput(targetStream);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try {
            TranscoderOutput output = new TranscoderOutput(bytes);
            transcoder.transcode(input, output);
        } catch (TranscoderException exception) {
            logger.error("Error while convert svg to {}. Message = [{}]", getType(), exception.getMessage());
            throw new QrCodeConvertException("Error while converting svg to " + getType());
        }

        return bytes.toByteArray();
    }

    @Override
    public ImageType getType() {
        return ImageType.JPEG;
    }
}
