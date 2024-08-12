package com.odiga.fiesta.common.error.exception.s3;

import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;

public class UploadFailException extends CustomException {

	public UploadFailException() {
		super(ErrorCode.UPLOAD_FAIL);
	}

}
