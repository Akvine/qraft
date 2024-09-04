package ru.akvine.qraft.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.akvine.qraft.constants.ApiErrorCodes;
import ru.akvine.qraft.rest.dto.common.ErrorResponse;

import java.util.List;

import static ru.akvine.qraft.constants.ApiErrorCodes.GENERAL_ERROR;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(GENERAL_ERROR, exception.getMessage(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info("Url is incorrect");
        String url = ((ServletWebRequest) request).getRequest().getRequestURI();
        String message = String.format("Resource by url = [%s] not exists", url);
        ErrorResponse errorResponse = new ErrorResponse(
                ApiErrorCodes.RESOURCE_NOT_FOUND_ERROR,
                message,
                message
        );
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info("Method argument is not presented", exception);
        List<ErrorField> errorFields = ErrorMessageHelper.extractErrorField(exception);
        String errorMessage = ErrorMessageHelper.toErrorMessage(errorFields);
        ErrorResponse errorResponse = new ErrorResponse(
                ApiErrorCodes.Validation.FIELD_NOT_PRESENTED_ERROR,
                errorMessage,
                errorMessage);
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info("Error while parsing json body");
        String errorMessage = "Error while parsing json body";
        ErrorResponse errorResponse = new ErrorResponse(
                ApiErrorCodes.JSON_BODY_INVALID_ERROR,
                errorMessage,
                errorMessage
        );
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
