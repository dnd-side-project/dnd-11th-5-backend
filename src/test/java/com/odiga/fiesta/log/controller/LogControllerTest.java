package com.odiga.fiesta.log.controller;

import static com.odiga.fiesta.common.error.ErrorCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odiga.fiesta.ControllerTestSupport;
import com.odiga.fiesta.common.error.exception.CustomException;
import com.odiga.fiesta.common.util.FileUtils;
import com.odiga.fiesta.log.dto.request.LogCreateRequest;
import com.odiga.fiesta.log.dto.response.LogDetailResponse;
import com.odiga.fiesta.log.dto.response.LogIdResponse;
import com.odiga.fiesta.log.dto.response.LogKeywordResponse;
import com.odiga.fiesta.log.service.LogService;
import com.odiga.fiesta.user.domain.User;

class LogControllerTest extends ControllerTestSupport {

	@Autowired
	private LogService logService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private FileUtils fileUtils;

	// @BeforeEach
	// void beforeEach() {
	// 	User mockUser = Mockito.mock(User.class);
	// 	Mockito.when(mockUser.getId()).thenReturn(1L);
	// 	SecurityContextHolder.getContext()
	// 		.setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null,
	// 			List.of(new SimpleGrantedAuthority("ROLE_USER"))));
	// }

	@DisplayName("방문일지 키워드들을 조회한다.")
	@Test
	void getAllLogKeywords() throws Exception {
		// given
		String message = "방문일지 키워드 조회 성공";

		List<LogKeywordResponse> mockLogKeywords = List.of();
		when(logService.getAllLogKeywords()).thenReturn(mockLogKeywords);

		// when // then
		mockMvc.perform(
				get("/api/v1/logs/keywords")
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(message))
			.andExpect(jsonPath("$.data").isArray());
	}

	@DisplayName("방문일지 ID를 이용해 방문일지를 상세 조회한다.")
	@Test
	void getLogDetail() throws Exception {
		// given
		String message = "방문일지 상세 조회 성공";

		LogDetailResponse logDetailResponse = LogDetailResponse.builder().build();
		Long logId = 1L;

		when(logService.getLogDetail(logId)).thenReturn(logDetailResponse);

		// when // then
		mockMvc.perform(
				get("/api/v1/logs/" + logId)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(message));
	}

	@DisplayName("존재하지 않는 방문일지 ID로 조회하면 404 에러가 발생한다.")
	@Test
	void getLogDetail_NotFound() throws Exception {
		// given
		Long logId = 17L;
		CustomException logNotFoundException = new CustomException(LOG_NOT_FOUND);
		when(logService.getLogDetail(logId)).thenThrow(logNotFoundException);

		// when // then
		mockMvc.perform(
				get("/api/v1/logs/" + logId)
			).andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void createLog_success() throws Exception {
		// given
		LogCreateRequest request = LogCreateRequest.builder()
			.title("제목")
			.sido("서울특별시")
			.sigungu("강남구")
			.address("어디 어디")
			.content("방문일지 내용")
			.keywordIds(List.of(1L, 2L))
			.build();

		MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg", "image/jpeg", "image data".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpg", "image/jpeg", "image data".getBytes());

		when(fileUtils.getFileExtension(any())).thenReturn("jpg");
		doNothing().when(fileUtils).validateImageExtension(any());

		LogIdResponse logIdResponse = LogIdResponse.of(1L);
		when(logService.createLog(anyLong(), any(LogCreateRequest.class), any(List.class))).thenReturn(logIdResponse);

		MockMultipartFile data = getMockMultipartFile(request);

		// when // then
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/logs")
				.file(data)
				.file(file1)
				.file(file2)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.with(csrf())
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("방문일지 생성 완료"))
			.andExpect(jsonPath("$.data.logId").value(1L));
	}

	@Test
	@WithMockUser
	void createLog_fileCountExceedsLimit() throws Exception {
		// Given
		LogCreateRequest request = LogCreateRequest.builder()
			.title("제목")
			.sido("서울특별시")
			.sigungu("강남구")
			.address("어디 어디")
			.content("방문일지 내용")
			.keywordIds(List.of(1L, 2L))
			.build();

		MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg", "image/jpeg", "image data".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpg", "image/jpeg", "image data".getBytes());
		MockMultipartFile file3 = new MockMultipartFile("files", "image3.jpg", "image/jpeg", "image data".getBytes());
		MockMultipartFile file4 = new MockMultipartFile("files", "image4.jpg", "image/jpeg", "image data".getBytes());

		MockMultipartFile data = getMockMultipartFile(request);

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/logs")
				.file(data)
				.file(file1)
				.file(file2)
				.file(file3)
				.file(file4) // 4개의 파일 업로드
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.with(csrf())
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(LOG_IMAGE_COUNT_EXCEEDED.getMessage()));
	}

	@Test
	@WithMockUser
	void createLog_invalidFileExtension() throws Exception {
		// Given
		LogCreateRequest request = LogCreateRequest.builder()
			.title("제목")
			.sido("서울특별시")
			.sigungu("강남구")
			.address("어디 어디")
			.content("방문일지 내용")
			.keywordIds(List.of(1L, 2L))
			.build();

		MockMultipartFile file1 = new MockMultipartFile("files", "image1.exe", "application/octet-stream",
			"binary data".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("files", "image2.exe", "application/octet-stream",
			"binary data".getBytes());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);

		when(fileUtils.getFileExtension("image1.exe")).thenReturn("exe");
		when(fileUtils.getFileExtension("image2.exe")).thenReturn("exe");

		// Mocking the file extension validation to throw an exception
		doThrow(new CustomException(INVALID_EXTENSION_TYPE)).when(fileUtils).validateImageExtension("exe");

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/logs")
				.file(data)
				.file(file1)
				.file(file2)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.with(csrf())
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(INVALID_EXTENSION_TYPE.getMessage()));
	}

	@Test
	@WithMockUser
	void createLog_invalidRequestData() throws Exception {
		// Given
		LogCreateRequest request = LogCreateRequest.builder()
			.title("") // 제목이 비어있음 (유효성 검사 실패)
			.sido("서울특별시")
			.sigungu("강남구")
			.address("어디 어디")
			.content("방문일지 내용")
			.keywordIds(List.of(1L, 2L))
			.build();

		MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg", "image/jpeg", "image data".getBytes());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/logs")
				.file(data)
				.file(file1)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.with(csrf())
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(INVALID_INPUT_VALUE.getMessage())); // 예상되는 오류 메시지
	}

	private MockMultipartFile getMockMultipartFile(LogCreateRequest request) throws JsonProcessingException {
		return new MockMultipartFile(
			"data",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);
	}

}
