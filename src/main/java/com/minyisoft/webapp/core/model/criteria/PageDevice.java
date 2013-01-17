package com.minyisoft.webapp.core.model.criteria;

import java.util.ArrayList;

import com.minyisoft.webapp.core.annotation.Label;

/**
 * @author qingyong_ou
 * 分页器
 */
public class PageDevice {
	//系统默认每页显示记录数
	private static final int RECORDS_PER_PAGE=12;
	// 总记录数
	private int totalRecords;
	private int currentPage=1;
	// 当前页码
	@Label("每页显示")
	private int recordsPerPage = RECORDS_PER_PAGE;

	//oracle分页风格
	//private static final int ORACLE_PAGE_STYLE = 0;
	//mysql分页风格
	//private static final int MYSQL_PAGE_STYLE = 1;

	//当前分页风格
	//private static final int CURRENT_PAGE_STYLE = ORACLE_PAGE_STYLE;

	public PageDevice() {
		this.totalRecords = 0;
		this.currentPage = 1;
	}

	public PageDevice(int totalRecords, int currentPage) {
		this.totalRecords = totalRecords;
		this.currentPage = currentPage;
	}

	public PageDevice(int totalRecords, int currentPage, int recordsPerPage) {
		this.totalRecords = totalRecords;
		this.currentPage = currentPage;
		this.recordsPerPage = recordsPerPage;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	// 总页数=总记录数除以每页显示记录数，当不整除时加1
	public int getTotalPages() {
		return (totalRecords % recordsPerPage == 0 && totalRecords > 0) ? (totalRecords / recordsPerPage)
				: ((totalRecords / recordsPerPage) + 1);
	}

	public int getRecordsPerPage() {
		return recordsPerPage;
	}

	public void setRecordsPerPage(int recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	//返回前一页页面，若当前页为第一页，返回-1
	public int getPreviewPage(){
		if(currentPage>1){
			return currentPage-1;
		}else{
			return -1;
		}
	}
	
	//返回后一页页面，若当前页为最后一页，返回-1
	public int getNextPage(){
		if(currentPage<getTotalPages()){
			return currentPage+1;
		}else{
			return -1;
		}
	}

	// 获取当前页第一条数据对应结果集中的记录行号
	public int getStartRowNumberOfCurrentPage() {
		/*if (CURRENT_PAGE_STYLE == MYSQL_PAGE_STYLE) {
			return (currentPage - 1) * recordsPerPage;
		} else if (CURRENT_PAGE_STYLE == ORACLE_PAGE_STYLE) {
			return (currentPage - 1) * recordsPerPage + 1;
		} else {
			return (currentPage - 1) * recordsPerPage + 1;
		}*/
		return (currentPage - 1) * recordsPerPage + 1;
	}

	// 获取当前页最后一条数据对应结果集中的记录行号
	public int getEndRowNumberOfCurrentPage() {
		return Math.min(currentPage * recordsPerPage,totalRecords);
	}
	
	/**
	 * 根据当前页码和总页码，获取可供用户点击的页码列表
	 * @return
	 */
	private final int visiablePageCount=6;
	public Integer[] getVisiblePageNumbers(){
		int totalPage=getTotalPages();
		int startPageNum=Math.max(currentPage-2,1);
		ArrayList<Integer> pageList=new ArrayList<Integer>(visiablePageCount+4);
		for(int i=startPageNum;i<=startPageNum+visiablePageCount-1&&i<=totalPage;i++){
			pageList.add(i);
		}
		int currentPageListSize=pageList.size();
		if(currentPageListSize<visiablePageCount){
			for(int i=startPageNum-1;i>0&&i>=(startPageNum-(visiablePageCount-currentPageListSize));i--){
				pageList.add(0, i);
			}
		}
		if(pageList.get(0)>2){
			pageList.add(0,1);
			pageList.add(1,-1);
		}
		currentPageListSize=pageList.size();
		if(pageList.get(currentPageListSize-1)<totalPage-1){
			pageList.add(currentPageListSize,-1);
			pageList.add(currentPageListSize+1,totalPage);
		}
		return pageList.toArray(new Integer[pageList.size()]);
	}
}
