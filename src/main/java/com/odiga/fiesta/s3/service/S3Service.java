package com.odiga.fiesta.s3.service;

import static com.odiga.fiesta.common.error.ErrorCode.*;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.odiga.fiesta.common.error.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Profile("!test")
@Service
@RequiredArgsConstructor
public class S3Service {

	private static final List<String> PERMISSION_IMG_EXTENSIONS = List.of("png", "jpg", "jpeg", "gif", "tif", "ico",
		"svg", "bmp", "webp", "tiff", "jfif");

	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private String uploadImage(final MultipartFile file) throws IOException {
		final String fileName = getFileName(file);
		final String extension = getFileExtension(fileName);
		validateImageExtension(extension);

		return upload(file);
	}

	private String upload(final MultipartFile file) throws IOException {
		String fileName = getFileName(file);
		return putS3(file, fileName);
	}

	private String putS3(final MultipartFile file, String fileName) throws IOException {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return String.valueOf(amazonS3Client.getUrl(bucket, fileName));
	}

	private String getFileName(final MultipartFile file) {
		return file.getOriginalFilename();
	}

	private String getFileExtension(final String fileName) {
		final int index = fileName.lastIndexOf(".");
		if (index > 0 && fileName.length() > index + 1) {
			return fileName.substring(index + 1);
		} else {
			throw new CustomException(INVALID_EXTENSION_TYPE);
		}
	}

	private void validateImageExtension(final String extension) {
		if (!PERMISSION_IMG_EXTENSIONS.contains(extension)) {
			throw new CustomException(INVALID_IMAGE_TYPE);
		}
	}
}
