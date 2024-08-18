package com.odiga.fiesta.log.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import com.odiga.fiesta.MockTestSupport;
import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.log.domain.LogImage;
import com.odiga.fiesta.log.dto.request.LogCreateRequest;
import com.odiga.fiesta.log.repository.LogImageRepository;
import com.odiga.fiesta.s3.service.S3Service;
import com.odiga.fiesta.user.domain.User;
import com.odiga.fiesta.user.domain.repository.UserRepository;

class LogMockServiceTest extends MockTestSupport {

	@Mock
	private S3Service s3Service;

	@Mock
	private LogImageRepository logImageRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private LogService logService;

	@Mock
	private MultipartFile multipartFile;

	private final Long logId = 1L;

	@DisplayName("방문 일지 생성 - 방문일지 이미지 생성 성공")
	@Test
	void createLogImages_success() throws Exception {
		// Given
		String imageUrl = "https://s3.example.com/image.jpg";
		when(s3Service.upload(any(MultipartFile.class), anyString())).thenReturn(imageUrl);

		// When
		logService.createLogImages(logId, List.of(multipartFile));

		// Then
		verify(s3Service, times(1)).upload(any(MultipartFile.class), anyString());
		verify(logImageRepository, times(1)).save(any(LogImage.class));
	}

	@DisplayName("방문 일지 생성 - 방문일지 이미지 생성 실패")
	@Test
	void createLogImages_uploadFail() throws Exception {
		// Given
		when(s3Service.upload(any(MultipartFile.class), anyString())).thenThrow(new IOException("Upload failed"));

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			logService.createLogImages(logId, List.of(multipartFile));
		});

		assertEquals(ErrorCode.UPLOAD_FAIL, exception.getErrorCode());
		verify(logImageRepository, never()).save(any(LogImage.class)); // save가 호출되지 않아야 함
	}

	@DisplayName("방문 일지 생성 - 존재하지 않는 유저")
	@Test
	void createLog_userNotFound() {
		// Given
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		LogCreateRequest request = LogCreateRequest.builder()
			.keywordIds(List.of(1L))
			.title("title")
			.sido("sido")
			.sigungu("sigungu")
			.address("address")
			.content("content")
			.build();

		List<MultipartFile> files = List.of(mock(MultipartFile.class));

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			logService.createLog(1L, request, files);
		});

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
		verify(userRepository, times(1)).findById(1L); // userRepository 호출 확인
	}
}
