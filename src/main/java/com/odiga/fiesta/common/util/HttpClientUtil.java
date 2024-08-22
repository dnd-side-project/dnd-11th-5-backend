package com.odiga.fiesta.common.util;

import static com.odiga.fiesta.common.error.ErrorCode.*;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odiga.fiesta.common.error.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

// TODO: 이 후에 WebClient 를 사용하도록 수정
@Component
@Slf4j
public class HttpClientUtil {

	public static <T> T sendRequest(String url, HttpMethod method, HttpHeaders headers, Map<String, String> body,
		Class<T> responseType) {
		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				url,
				method,
				requestEntity,
				String.class
			);
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(response.getBody(), responseType);
		} catch (HttpClientErrorException e) {
			log.error("HTTP error occurred while sending request: {}, {}", e.getMessage(), e);
			throw new CustomException(INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("An error occurred while processing the request: {}, {}", e.getMessage(), e);
			throw new CustomException(JSON_PARSING_ERROR);
		}
	}
}
