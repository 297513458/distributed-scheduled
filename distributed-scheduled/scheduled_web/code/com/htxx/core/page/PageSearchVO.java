package com.htxx.core.page;

/**
 * 分页信息
 * 
 * @author root
 * 
 */
public class PageSearchVO {
	/**
	 * 每页最大记录数
	 */
	private Integer pageMaxResult;
	/**
	 * 禁止手动设置该值
	 */
	private Integer pageStartIndex;
	// 当前页
	private String pageNum;

	public final Integer getPageMaxResult() {
		return pageMaxResult;
	}

	public final void setPageMaxResult(Integer pageMaxResult) {
		this.pageMaxResult = pageMaxResult;
	}

	public final Integer getPageStartIndex() {
		return pageStartIndex;
	}

	/**
	 * 禁止手动设置该值
	 */
	public final void setPageStartIndex(Integer pageStartIndex) {
		this.pageStartIndex = pageStartIndex;
	}

	public final String getPageNum() {
		return pageNum;
	}

	public final void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}
}