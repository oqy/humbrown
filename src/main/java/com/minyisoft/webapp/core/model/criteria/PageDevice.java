package com.minyisoft.webapp.core.model.criteria;

import java.util.List;

import lombok.Getter;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.annotation.Label;

/**
 * @author qingyong_ou 分页器
 */
@Getter
public class PageDevice {
	// 系统默认每页显示记录数
	private static final int RECORDS_PER_PAGE = 12;
	// 总记录数
	private int totalRecords;
	private int currentPage = 1;
	// 当前页码
	@Label("每页显示")
	private int recordsPerPage = RECORDS_PER_PAGE;

	// oracle分页风格
	// private static final int ORACLE_PAGE_STYLE = 0;
	// mysql分页风格
	// private static final int MYSQL_PAGE_STYLE = 1;

	// 当前分页风格
	// private static final int CURRENT_PAGE_STYLE = ORACLE_PAGE_STYLE;

	public PageDevice() {
	}

	public PageDevice(int totalRecords) {
		setTotalRecords(totalRecords);
	}

	/**
	 * 设置总记录数，并根据总页数按需要调整当前页码
	 * 
	 * @param totalRecords
	 */
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords >= 0 ? totalRecords : 0;
		if (currentPage > getTotalPages()) {
			currentPage = getTotalPages();
		}
	}

	/**
	 * 设置当前页数
	 * 
	 * @param currentPage
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage > 0 ? currentPage : 1;
	}

	/**
	 * 设置每页显示记录数
	 * 
	 * @param recordsPerPage
	 */
	public void setRecordsPerPage(int recordsPerPage) {
		this.recordsPerPage = recordsPerPage > 0 ? recordsPerPage
				: RECORDS_PER_PAGE;
	}

	/**
	 * 总页数=总记录数除以每页显示记录数，当不整除时加1
	 * 
	 * @return
	 */
	public int getTotalPages() {
		return (totalRecords % recordsPerPage == 0 && totalRecords > 0) ? (totalRecords / recordsPerPage)
				: ((totalRecords / recordsPerPage) + 1);
	}

	/**
	 * 返回前一页页面，若当前页为第一页，返回-1
	 * 
	 * @return
	 */
	public int getPreviewPage() {
		return currentPage > 1 ? currentPage - 1 : -1;
	}

	/**
	 * 返回后一页页面，若当前页为最后一页，返回-1
	 * 
	 * @return
	 */
	public int getNextPage() {
		return currentPage < getTotalPages() ? currentPage + 1 : -1;
	}

	/**
	 * 获取当前页第一条数据对应结果集中的记录行号
	 * 
	 * @return
	 */
	public int getStartRowNumberOfCurrentPage() {
		/*
		 * if (CURRENT_PAGE_STYLE == MYSQL_PAGE_STYLE) { return (currentPage -
		 * 1) * recordsPerPage; } else if (CURRENT_PAGE_STYLE ==
		 * ORACLE_PAGE_STYLE) { return (currentPage - 1) * recordsPerPage + 1; }
		 * else { return (currentPage - 1) * recordsPerPage + 1; }
		 */
		return (currentPage - 1) * recordsPerPage + 1;
	}

	/**
	 * 获取当前页最后一条数据对应结果集中的记录行号
	 * 
	 * @return
	 */
	public int getEndRowNumberOfCurrentPage() {
		return Math.min(currentPage * recordsPerPage, totalRecords);
	}

	/**
	 * 根据当前页码和总页码，获取可供用户点击的页码列表
	 * 
	 * @return
	 */
	public Integer[] getVisiblePageNumbers(int visiblePageCount) {
		List<Integer> numbers = Lists.newLinkedList();
		int startNum = Math.max(currentPage - visiblePageCount / 2 + 1, 1);
		for (int i = startNum; i < startNum + visiblePageCount
				&& i <= getTotalPages(); i++) {
			numbers.add(i);
		}
		if (numbers.size() < visiblePageCount && numbers.get(0) > 1) {
			for (int i = numbers.get(0) - 1; i >= 1
					&& numbers.size() < visiblePageCount; i--) {
				numbers.add(0, i);
			}
		}
		return numbers.toArray(new Integer[numbers.size()]);
	}

	private static final int DEFAULT_VISIBLE_PAGE_COUNT = 6;

	public Integer[] getVisiblePageNumbers() {
		return getVisiblePageNumbers(DEFAULT_VISIBLE_PAGE_COUNT);
	}

	/**
	 * 按反序获取当前页码和总页码
	 * 
	 * @return
	 */
	public Integer[] getReverseVisiblePageNumbers() {
		Integer[] numbers = getVisiblePageNumbers();
		ArrayUtils.reverse(numbers);
		return numbers;
	}
}
