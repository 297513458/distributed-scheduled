package com.htxx.scheduling.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.htxx.core.page.SuperDAO;
import com.htxx.scheduling.pojo.Scheduling;

@Repository("schedulingDAO")
public class SchedulingDAO extends SuperDAO<Scheduling, String> {

	@Override
	protected String namespace() {
		return "mybatis.xml.SchedulingMapper";
	}

	public List<Scheduling> queryByClass(Scheduling vo) {
		return this.getSqlSession().selectList(this.tip("queryByClazz"), vo);
	}

	public Scheduling queryByScheduling(String clazz, String method) {
		Scheduling entity = new Scheduling();
		entity.setClazz(clazz);
		entity.setMethod(method);
		return this.getSqlSession().selectOne(this.tip("queryByScheduling"), entity);
	}

	public int updateStatus(Scheduling entity) {
		if (entity != null && entity.getSyncStatus() == null)
			entity.setSyncStatus(false);
		return this.getSqlSession().update(this.tip("updateStatus"), entity);
	}

	public int updateSync(Scheduling entity) {
		if (entity != null) {
			entity.setSyncTime(new Date());
			return this.getSqlSession().update(this.tip("updateSync"), entity);
		} else
			return 0;
	}
}