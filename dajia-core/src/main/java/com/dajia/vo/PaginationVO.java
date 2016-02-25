package com.dajia.vo;

import java.util.List;

public class PaginationVO<T> {
	public List<T> results;

	public Integer totalCount;

	public Integer totalPages;

	public Integer currentPage;
}
