package ru.akvine.qraft.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.qraft.constants.ApiErrorCodes;
import ru.akvine.qraft.enums.ImageType;
import ru.akvine.qraft.exceptions.ValidationException;

@Component
public class ImageTypeValidator implements Validator<String> {
    @Override
    public void validate(String imageType) {
        if (StringUtils.isBlank(imageType)) {
            throw new ValidationException(
                    ApiErrorCodes.Validation.IMAGE_TYPE_BLANK_ERROR,
                    "Image type is blank"
            );
        }

        try {
            ImageType.safeValueOf(imageType);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(
                    ApiErrorCodes.Validation.IMAGE_TYPE_INVALID_ERROR,
                    exception.getMessage()
            );
        }
    }
}
