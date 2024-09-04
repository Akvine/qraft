package ru.akvine.qraft.constants;

public final class ApiErrorCodes {
    private ApiErrorCodes() throws IllegalAccessException {
        throw new IllegalAccessException("Calling " + getClass().getSimpleName() + " constructor is prohibited!");
    }

    public final static String GENERAL_ERROR = "general.error";

    public final static String RESOURCE_NOT_FOUND_ERROR = "resource.notFound.error";
    public final static String JSON_BODY_INVALID_ERROR = "json.body.invalid.error";

    public interface Validation {
        String IMAGE_TYPE_BLANK_ERROR = "imageType.blank.error";
        String IMAGE_TYPE_INVALID_ERROR = "imageType.invalid.error";

        String FIELD_NOT_PRESENTED_ERROR = "field.not.presented.error";

        String ECL_BLANK_ERROR = "ecl.blank.error";
        String ECL_INVALID_ERROR = "ecl.invalid.error";
    }
}
