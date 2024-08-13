package com.odiga.fiesta.common;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PageResponse<T> {

	private final List<T> content;
	private final long offset;
	private final int pageSize;
	private final long totalElements;
	private final int totalPages;

	public PageResponse(Page<T> page) {
		this.content = page.getContent();
		this.offset = page.getPageable().getOffset();
		this.pageSize = page.getPageable().getPageSize();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
	}
}

