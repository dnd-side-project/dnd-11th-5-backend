package com.odiga.fiesta.common.error.exception.s3;

import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;

public class InvalidImageException extends CustomException {

	public InvalidImageException() {
		super(ErrorCode.INVALID_IMAGE_TYPE);
	}
}
