package com.jetcms.core.dao;

import java.util.List;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;
import com.jetcms.core.entity.UserViews;

public interface UserViewsDao {
	public List<UserViews> getList(Integer count, boolean cacheable);

	public Pagination getPage(String name, int pageNo, int pageSize,
			boolean cacheable);

	public UserViews findById(Integer id);

	public UserViews findByName(String name, boolean cacheable);

	public UserViews save(UserViews bean);

	public UserViews updateByUpdater(Updater<UserViews> updater);

	public UserViews deleteById(Integer id);

	public int deleteContentRef(Integer id);

	public int countContentRef(Integer id);
}