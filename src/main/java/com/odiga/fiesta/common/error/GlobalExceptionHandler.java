package com.odiga.fiesta.common.error;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 500 : Internal Server Error
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleServerException(Exception e) {
		log.warn(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}

	// 405 : Method Not Allowed
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException e) {
		log.error(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}

	// 400 : MethodArgumentNotValidException
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		log.warn(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}

	// 400 : MethodArgumentType
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		log.error(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}

	// 400 : Bad Request, ModelAttribute
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
		log.warn(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException e) {
		log.warn(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
		log.warn(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
		log.warn(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e) {
		log.warn(e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER);
		return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse);
	}
}
