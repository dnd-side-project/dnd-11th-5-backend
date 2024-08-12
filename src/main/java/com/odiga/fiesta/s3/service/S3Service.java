package com.odiga.fiesta.s3.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.odiga.fiesta.common.error.exception.s3.InvalidExtensionException;
import com.odiga.fiesta.common.error.exception.s3.InvalidImageException;
import com.odiga.fiesta.common.error.exception.s3.UploadFailException;

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

	public String uploadImage(final MultipartFile file, final String dirName) throws IOException {
		if (file.isEmpty()) {
			throw new UploadFailException();
		}

		final String fileName = file.getOriginalFilename();

		final String extension = getFileExtension(fileName);
		validateImageExtension(extension);

		return upload(file, dirName);
	}

	public String upload(final MultipartFile file, final String dirName) throws IOException {
		String fileName = dirName + "/" + UUID.randomUUID() + file.getOriginalFilename();
		return putS3(file, fileName);
	}

	public String putS3(final MultipartFile file, String fileName) throws IOException {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return String.valueOf(amazonS3Client.getUrl(bucket, fileName));
	}

	public void removeFile(String path, String dirName) {
		amazonS3Client.deleteObject(bucket, getKey(path, dirName));
	}

	private String getKey(String path, String dirName) {
		return path.substring(path.indexOf(dirName));
	}

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
