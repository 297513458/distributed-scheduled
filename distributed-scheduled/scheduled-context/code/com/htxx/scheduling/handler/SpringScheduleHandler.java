package com.htxx.scheduling.handler;

import java.lang.reflect.Field;
import java.util.Set;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Service;

@Service
public class SpringScheduleHandler extends ScheduledTaskRegistrar implements ScheduleHandler {

	public void reloadTask() {
		try {
			this.addCronTask(new ScheduledMethodRunnable(this, "keepalived"), "0 0 0 * * *");
		} catch (Exception e) {
		}
		super.scheduleTasks();
	}

	@Override
	public String pause(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String add(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String remove(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String resume(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String start(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	public void cancelAll() {
		try {
			super.setCronTasksList(null);
			for (Field field : this.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				if ("scheduledTasks".equals(field.getName())) {
					Object object = field.get(this);
					if (object instanceof Set) {
						@SuppressWarnings("unchecked")
						Set<ScheduledTask> set = (Set<ScheduledTask>) object;
						for (ScheduledTask task : set) {
							task.cancel();
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return;
	}

	public void handler(String data, ApplicationContext applicationContext) throws Exception {
		try {
			this.cancelAll();
			Object object = applicationContext.getBean("autoCheckTask");
			String method = "autoChecked";
			String cron = "0/3 * * * * *";
			if (object != null)
				super.addCronTask(new ScheduledMethodRunnable(object, method), cron);
		} catch (Exception e) {
		}
		this.reloadTask();
	}
}