package com.htxx.core.page;

import java.util.List;

/**
 * 
 * 描述:封装分页信息<br />
 */
public class PageVO<E> {
	/**
	 * 当前页编号
	 */
	private int currentPage = 1;// 当前页编号
	/**
	 * 总页数
	 */
	private int pageSize = 1;// 总页数
	/**
	 * 每页最大记录数
	 */
	private int prePageRecords = 20;// 每页最大记录数
	/**
	 * 当前页记录数
	 */
	private int currentPageRecords = 0;// 当前页记录数
	/**
	 * 总记录数
	 */
	private int countRecords = 0;// 总记录数
	/**
	 * 返回结果
	 */
	private List<E> entityList = null;// 返回结果
	/**
	 * 第一页
	 */
	private int firstPage = 1;// 第一页
	/**
	 * 是否有第一页链接
	 */
	private boolean hasFirstPage = false;// 是否有第一页链接
	/**
	 * 上一页
	 */
	private int frontPage = 1;// 上一页
	/**
	 * 是否有第上一页链接
	 */
	private boolean hasFrontPage = false;// 是否有第上一页链接
	/**
	 * 下一页
	 */
	private int nextPage = 1;// 下一页
	/**
	 * 是否有下一页链接
	 */
	private boolean hasNextPage = false;// 是否有下一页链接
	/**
	 * 最后一页
	 */
	private int lastPage = 1;// 最后一页
	/**
	 * 最大页数
	 */
	private int maxShowPage = 8;
	/**
	 * 是否有最后一页链接
	 */
	private boolean hasLastPage = false;
	/**
	 * 分页信息
	 */
	private List<Integer> pageList;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPrePageRecords() {
		return prePageRecords;
	}

	public void setPrePageRecords(int prePageRecords) {
		this.prePageRecords = prePageRecords;
	}

	public int getCurrentPageRecords() {
		return currentPageRecords;
	}

	public void setCurrentPageRecords(int currentPageRecords) {
		this.currentPageRecords = currentPageRecords;
	}

	public int getCountRecords() {
		return countRecords;
	}

	public void setCountRecords(int countRecords) {
		this.countRecords = countRecords;
	}

	public List<E> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<E> entityList) {
		this.entityList = entityList;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public boolean isHasFirstPage() {
		return hasFirstPage;
	}

	public void setHasFirstPage(boolean hasFirstPage) {
		this.hasFirstPage = hasFirstPage;
	}

	public int getFrontPage() {
		return frontPage;
	}

	public void setFrontPage(int frontPage) {
		this.frontPage = frontPage;
	}

	public boolean isHasFrontPage() {
		return hasFrontPage;
	}

	public void setHasFrontPage(boolean hasFrontPage) {
		this.hasFrontPage = hasFrontPage;
	}

	public long getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public boolean isHasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public long getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public long getMaxShowPage() {
		return maxShowPage;
	}

	public void setMaxShowPage(int maxShowPage) {
		this.maxShowPage = maxShowPage;
	}

	public boolean isHasLastPage() {
		return hasLastPage;
	}

	public void setHasLastPage(boolean hasLastPage) {
		this.hasLastPage = hasLastPage;
	}

	public List<Integer> getPageList() {
		return pageList;
	}

	public void setPageList(List<Integer> pageList) {
		this.pageList = pageList;
	}
}
