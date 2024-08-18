package com.odiga.fiesta.common.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.odiga.fiesta.common.error.exception.s3.InvalidExtensionException;
import com.odiga.fiesta.common.error.exception.s3.InvalidImageException;

@Component
public class FileUtils {

	private static final List<String> PERMISSION_IMG_EXTENSIONS = List.of("png", "jpg", "jpeg", "gif", "tif", "ico",
		"svg", "bmp", "webp", "tiff", "jfif");

	public String getFileExtension(final String fileName) {
		final int index = fileName.lastIndexOf(".");
		if (index > 0 && fileName.length() > index + 1) {
			return fileName.substring(index + 1);
		} else {
			throw new InvalidExtensionException();
		}
	}

	public void validateImageExtension(final String extension) {
		if (!PERMISSION_IMG_EXTENSIONS.contains(extension)) {
			throw new InvalidImageException();
		}
	}
}
