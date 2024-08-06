package com.odiga.fiesta.common;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageResponse<T> {

	private final List<T> content;
	private final int offset;
	private final int pageSize;
	private final int totalElements;
	private final int totalPages;

	@Builder
	public PageResponse(List<T> content, int offset, int pageSize, int totalElements, int totalPages) {
		this.content = content;
		this.offset = offset;
		this.pageSize = pageSize;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public static <T> PageResponse<T> of(List<T> content, int offset, int pageSize, int totalElements, int totalPages) {
		return PageResponse.<T>builder()
			.content(content)
			.offset(offset)
			.pageSize(pageSize)
			.totalElements(totalElements)
			.totalPages(totalPages)
			.build();
	}

}

