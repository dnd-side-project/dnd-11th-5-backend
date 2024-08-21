package com.odiga.fiesta.festival.util;

import java.util.HashMap;
import java.util.Map;

import com.odiga.fiesta.common.error.ErrorCode;
import com.odiga.fiesta.common.error.exception.CustomException;

public class CityMapper {
	private static final Map<String, Long> cityToIdMap = new HashMap<>();

	static {
		cityToIdMap.put("서울", 1L);
		cityToIdMap.put("서울특별시", 1L);
		cityToIdMap.put("인천", 2L);
		cityToIdMap.put("인천광역시", 2L);
		cityToIdMap.put("대전", 3L);
		cityToIdMap.put("대전광역시", 3L);
		cityToIdMap.put("대구", 4L);
		cityToIdMap.put("대구광역시", 4L);
		cityToIdMap.put("광주", 5L);
		cityToIdMap.put("광주광역시", 5L);
		cityToIdMap.put("부산", 6L);
		cityToIdMap.put("부산광역시", 6L);
		cityToIdMap.put("울산", 7L);
		cityToIdMap.put("울산광역시", 7L);
		cityToIdMap.put("세종특별자치시", 8L);
		cityToIdMap.put("세종", 8L);
		cityToIdMap.put("경기", 9L);
		cityToIdMap.put("경기도", 9L);
		cityToIdMap.put("강원", 10L);
		cityToIdMap.put("강원특별자치도", 10L);
		cityToIdMap.put("충북", 11L);
		cityToIdMap.put("충청북도", 11L);
		cityToIdMap.put("충남", 12L);
		cityToIdMap.put("충청남도", 12L);
		cityToIdMap.put("경북", 13L);
		cityToIdMap.put("경상북도", 13L);
		cityToIdMap.put("경남", 14L);
		cityToIdMap.put("경상남도", 14L);
		cityToIdMap.put("전북", 15L);
		cityToIdMap.put("전북특별자치도", 15L);
		cityToIdMap.put("전남", 16L);
		cityToIdMap.put("전라남도", 16L);
		cityToIdMap.put("제주특별자치도", 17L);
		cityToIdMap.put("제주", 17L);
	}

	public static Long getIdFromCityName(String cityName) {
		return cityToIdMap.get(cityName);
	}

	public static void validateCityName(String cityName) {
		if (!cityToIdMap.containsKey(cityName)) {
			throw new CustomException(ErrorCode.INVALID_SIDO_NAME);
		}
	}
}
