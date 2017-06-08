package com.htxx.core.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.util.StringUtils;

public abstract class SuperDAO<T, PK extends Serializable> extends
		SqlSessionDaoSupport {
	private static final Logger log = LogManager.getLogger(SuperDAO.class);
	private static final String STATMENT_COUNT_STRING = "Count";
	private static final String STATMENT_lIST_STRING = "page";
	private static final Integer DEFAULT_PAGE_ID = 1;
	private static final Integer DEFAULT_MAX_SHOW_PAGE = 8;
	private static final Integer DEFAULT_MAX_RESULT = 20;// 默认最大返回记录数

	/**
	 * 分页查询 查询名称默认用page，查询总数的sql名默认在列表查询上添加Count作为sql名称
	 * 
	 * @param search查询条件
	 * @return
	 */
	public final <P, R extends PageSearchVO> PageVO<P> queryByPage(R search) {
		return this.queryByPage(search, null);
	}

	/**
	 * 分页查询查询总数的sql名默认在列表查询上添加Count作为sql名称
	 * 
	 * @param search查询条件
	 * @param queryListString
	 *            查询明细sql 查询总数的sql名默认在列表查询上添加Count作为sql名称
	 * @return
	 */
	protected final <P, R extends PageSearchVO> PageVO<P> queryByPage(R search,
			String queryListString) {
		return this.queryByPage(search, queryListString, null);
	}

	/**
	 * 分页查询
	 * 
	 * @param search
	 *            查询条件
	 * @param queryListString
	 *            查询明细sql
	 * @param queryCountString
	 *            查询总数sql
	 * @return
	 */
	protected final <P, R extends PageSearchVO> PageVO<P> queryByPage(R search,
			String queryListString, String queryCountString) {
		if (search == null)
			throw new NullPointerException("查询对象为空");
		String listStatment = null;
		if (queryListString != null && queryListString.trim().length() > 0) {
			if (queryListString.startsWith(this.namespace()))
				listStatment = queryListString.trim();
			else
				listStatment = this.tip(queryListString.trim());
		} else {
			listStatment = this.tip(STATMENT_lIST_STRING);
		}
		if (!StringUtils.hasText(listStatment))
			throw new NullPointerException("查询语句为空");
		SqlSession template = this.getSqlSession();
		if (template == null)
			throw new NullPointerException("SqlMapClientTemplate为空");
		Integer maxResult = search.getPageMaxResult();
		if (maxResult == null || maxResult <= 0) {
			log.debug("最大记录数有错误,已经选择为默认记录数：{}", DEFAULT_MAX_RESULT);
			maxResult = DEFAULT_MAX_RESULT;
		}
		if (maxResult != null && maxResult.intValue() > 200)
			maxResult = 200;
		Integer currentPageId = 0;
		try {
			currentPageId = Integer.parseInt(search.getPageNum());
		} catch (Exception e) {
		}
		if (currentPageId == null || currentPageId <= 0) {
			log.debug("页数有错误,已经选择为默认页：{}", DEFAULT_PAGE_ID);
			currentPageId = DEFAULT_PAGE_ID;
		}
		final PageVO<P> p = new PageVO<P>();
		List<P> entityList = null;// 查询结果列表
		try {
			StringBuilder countStatment = new StringBuilder();
			if (StringUtils.hasText(queryCountString)) {
				if (queryCountString.startsWith(this.namespace())) {
					countStatment.append(queryCountString);
				} else {
					countStatment.append(this.tip(queryCountString));
				}
			} else {
				countStatment.append(listStatment);
				countStatment.append(STATMENT_COUNT_STRING);
			}
			Integer countSize = template.selectOne(countStatment.toString(),
					search);
			if (countSize == null)
				countSize = 0;
			p.setCountRecords(countSize);
			int pageSize = p.getCountRecords() % maxResult == 0 ? p
					.getCountRecords() / maxResult : (p.getCountRecords()
					/ maxResult + 1);
			if (pageSize <= 1)
				pageSize = 1;
			if (currentPageId >= pageSize) {
				currentPageId = pageSize;
			}
			p.setPageSize(pageSize);
			p.setCurrentPage(currentPageId);
			search.setPageStartIndex((currentPageId - 1) * maxResult);
			search.setPageMaxResult(maxResult);
			entityList = template.selectList(listStatment, search);
		} catch (Exception e) {
			log.error("分页查询出错", e);
		}
		p.setEntityList(entityList);
		// 设置当前页记录数
		if (entityList != null) {
			p.setCurrentPageRecords(entityList.size());
		} else {
			p.setCurrentPageRecords(0);
		}
		// 是否有下一页及最后一页
		if (p.getCurrentPage() >= p.getPageSize()) {
			p.setHasNextPage(false);
		} else {
			p.setHasNextPage(true);
		}
		// 是否有上一页第一页
		if (p.getCurrentPage() <= 1) {
			p.setHasFrontPage(false);
		} else {
			p.setHasFrontPage(true);
		}
		int nextPage = p.getCurrentPage() + 1;
		if (nextPage > p.getPageSize())
			nextPage = p.getPageSize();
		else if (nextPage <= 1)
			nextPage = 1;
		p.setNextPage(nextPage);
		int frontPage = p.getCurrentPage() - 1;
		if (frontPage <= 1)
			frontPage = 1;
		else if (frontPage >= p.getPageSize())
			frontPage = p.getPageSize() - 1;
		p.setMaxShowPage(DEFAULT_MAX_SHOW_PAGE);
		p.setFrontPage(frontPage);
		p.setLastPage(p.getPageSize());
		p.setPageList(pageList(p.getPageSize(), p.getCurrentPage()));
		long fristPage = p.getPageList().get(0);
		int pageSize = p.getPageList().size();
		if (fristPage > 1)
			p.setHasFirstPage(true);
		else
			p.setHasFirstPage(false);
		if (fristPage + pageSize < p.getPageSize())
			p.setHasLastPage(true);
		else
			p.setHasLastPage(false);
		return p;
	}

	private static List<Integer> pageList(int count, int current) {
		List<Integer> list = new ArrayList<Integer>();
		if (current > count)
			current = count;
		if (current < 1)
			current = 1;
		if (count - current < DEFAULT_MAX_SHOW_PAGE / 2) {
			int i = (count - DEFAULT_MAX_SHOW_PAGE >= 1 ? count
					- DEFAULT_MAX_SHOW_PAGE : 0) + 1;
			for (; i <= count; i++) {
				list.add(i);
			}
		}
		if (count - current >= DEFAULT_MAX_SHOW_PAGE / 2) {
			if (current - DEFAULT_MAX_SHOW_PAGE / 2 >= 1) {
				for (int i = current - DEFAULT_MAX_SHOW_PAGE / 2; i <= current
						+ DEFAULT_MAX_SHOW_PAGE / 2
						&& i <= count; i++) {
					list.add(i);
				}
			} else {
				for (int i = 1; i <= count && i <= DEFAULT_MAX_SHOW_PAGE; i++) {
					list.add(i);
				}
			}
		}
		return list;
	}

	protected String tip(String sqlName) {
		if (!StringUtils.hasText(sqlName))
			throw new NullPointerException("sql名称不能为空");
		if (!StringUtils.hasText(this.namespace()))
			throw new NullPointerException("namespace名称不能为空");
		StringBuilder sb = new StringBuilder();
		if (!sqlName.startsWith(this.namespace())) {
			sb.append(this.namespace());
			sb.append(".");
			sb.append(sqlName);
		}
		return sb.toString();
	}

	/**
	 * mybatis的namespace
	 * 
	 * @return
	 */
	protected abstract String namespace();

	/**
	 * 添加
	 * 
	 * @param t
	 */
	public int insert(T t) {
		if (t == null)
			return 0;
		return this.getSqlSession().insert(this.tip("insert"), t);
	}

	/**
	 * 批量 配置insertBatch
	 * 
	 * @param batchList
	 */
	public int insertBatch(List<T> batchList) {
		int index = 0;
		int count = 0;
		if (batchList == null || batchList.isEmpty())
			return index;
		int max = 1000;
		List<T> list = new ArrayList<T>(batchList.size() >= max ? max
				: batchList.size());
		for (T t : batchList) {
			list.add(t);
			count++;
			if (++index >= batchList.size() || list.size() >= max
					|| count >= batchList.size()) {
				this.getSqlSession().insert(this.tip("insertBatch"), list);
				list.clear();
				index = 0;
			}
		}
		return count;
	}

	/**
	 * 传入id删除数据
	 * 
	 * @param id
	 */
	public int delete(PK id) {
		if (id == null)
			return 0;
		return this.getSqlSession().delete(this.tip("deleteById"), id);
	}

	/**
	 * 查询所有
	 * 
	 * @param id
	 */
	public List<T> list() {
		return this.getSqlSession().selectList(this.tip("list"));
	}

	/**
	 * 查询单条数据，查詢selectByPrimaryKey
	 * 
	 * @param id
	 */
	public T query(PK id) {
		if (id == null)
			return null;
		return this.getSqlSession().selectOne(this.tip("selectByPrimaryKey"),
				id);
	}

	/**
	 * 删除数据
	 * 
	 * @param t
	 */
	public int delete(T t) {
		if (t == null)
			return 0;
		return this.getSqlSession().delete(this.tip("delete"), t);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	public int delete(PK[] ids) {
		if (ids == null)
			return 0;
		int count = 0;
		for (PK id : ids)
			count += this.delete(id);
		return count;
	}

	/**
	 * 更新
	 * 
	 * @param t
	 */
	public int update(T t) {
		if (t == null)
			return 0;
		return this.getSqlSession().update(this.tip("update"), t);
	}
}