package com.odiga.fiesta.festival.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DailyFestivalContents {

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
	private LocalDate date;
	private List<FestivalBasic> festivals;
	private Integer totalElements;
}
