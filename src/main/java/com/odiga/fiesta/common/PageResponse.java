package com.odiga.fiesta.common;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageResponse<T> {

	private final List<T> content;
	private final long offset;
	private final int pageSize;
	private final int pageNumber;
	private final long totalElements;
	private final int totalPages;

	@Builder
	public PageResponse(Page<T> page) {
		this.content = page.getContent();
		this.offset = page.getPageable().getOffset();
		this.pageSize = page.getPageable().getPageSize();
		this.pageNumber = page.getPageable().getPageNumber();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
	}

	@Builder
	public PageResponse(List<T> content, long offset, int pageSize, int pageNumber, long totalElements,
		int totalPages) {
		this.content = content;
		this.offset = offset;
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(page);
	}
}

