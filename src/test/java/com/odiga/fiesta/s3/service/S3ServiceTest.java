package com.odiga.fiesta.s3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.common.error.exception.s3.InvalidExtensionException;
import com.odiga.fiesta.common.error.exception.s3.InvalidImageException;
import com.odiga.fiesta.common.error.exception.s3.UploadFailException;

class S3ServiceTest extends MockTestSupport {

	@InjectMocks
	private S3Service s3Service;
	@Mock
	private AmazonS3Client amazonS3Client;

	private final byte[] imageData = new byte[] {(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A,
		(byte)0x1A,
		(byte)0x0A};

	@BeforeEach
	void beforeEach() {
		ReflectionTestUtils.setField(s3Service, "bucket", "fiesta-image");
	}

	@DisplayName("S3에 파일을 업로드한다.")
	@Test
	void upload() throws Exception {
		// given
		MultipartFile file = new MockMultipartFile("file", "testImage.png", "image/png", imageData);

		// when
		s3Service.uploadImage(file, "test");

		// then
		verify(amazonS3Client).putObject(any(PutObjectRequest.class));
	}

	@DisplayName("빈 파일이면 에러가 발생한다.")
	@Test
	void uploadImage_InvalidFile() {
		// given
		MultipartFile file = new MockMultipartFile("file", null, "image/png", new byte[] {});

		// when // then
		assertThrows(UploadFailException.class, () -> {
			s3Service.uploadImage(file, "test.txt");
		});
	}

	@DisplayName("파일을 삭제한다.")
	@Test
	void removeFile() {
		// given
		String filename = "testFile.txt";

		// when
		s3Service.removeFile(filename, "test");

		// then
		verify(amazonS3Client).deleteObject(any(String.class), eq(filename));
	}

	@DisplayName("파일에서 확장자를 추출할 수 있다.")
	@Test
	void getFileExtension() {
		String extension = s3Service.getFileExtension("test-image.png");

		assertEquals("png", extension);
	}

	@DisplayName("확장자를 추출할 수 없는 파일에서는 에러가 발생한다.")
	@Test
	void getFileExtension_invalidFileName() {
		assertThrows(InvalidExtensionException.class, () -> {
			s3Service.getFileExtension("invalidfile");
		});
	}

	@DisplayName("PNG 형식의 이미지 파일 확장자가 유효함을 검증한다")
	@Test
	void validateFileExtension() {
		assertDoesNotThrow(() -> s3Service.validateImageExtension("png"));
	}

	@DisplayName("잘못된 확장자의 이미지를 업로드하면 에러가 발생한다.")
	@Test
	void validateFileExtension_invalidImageExtension() {
		assertThrows(InvalidImageException.class, () -> s3Service.validateImageExtension("txt"));
	}
}
