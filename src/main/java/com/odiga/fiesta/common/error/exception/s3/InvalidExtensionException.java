package com.odiga.fiesta.common.error.exception.s3;

import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;

public class InvalidExtensionException extends CustomException {

	public InvalidExtensionException() {
		super(ErrorCode.INVALID_EXTENSION_TYPE);
	}
}
