package com.jetcms.core.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;
import com.jetcms.core.dao.UserViewsDao;
import com.jetcms.core.entity.UserViews;
import com.jetcms.core.manager.UseViewsMng;

@Service
@Transactional
public class UseViewsMngImpl implements UseViewsMng {
	private static final Logger log = LoggerFactory
			.getLogger(UseViewsMngImpl.class);

	@Transactional(readOnly = true)
	public List<UserViews> getListForTag(Integer count) {
		return dao.getList(count, true);
	}

	@Transactional(readOnly = true)
	public Pagination getPageForTag(int pageNo, int pageSize) {
		Pagination page = dao.getPage(null, pageNo, pageSize, true);
		return page;
	}

	@Transactional(readOnly = true)
	public Pagination getPage(String name, int pageNo, int pageSize) {
		Pagination page = dao.getPage(name, pageNo, pageSize, false);
		return page;
	}

	@Transactional(readOnly = true)
	public UserViews findById(Integer id) {
		UserViews entity = dao.findById(id);
		return entity;
	}

	@Transactional(readOnly = true)
	public UserViews findByName(String name) {
		return dao.findByName(name, false);
	}

	@Transactional(readOnly = true)
	public UserViews findByNameForTag(String name) {
		return dao.findByName(name, true);
	}

 
	public List<UserViews> saveTags(String[] tagArr) {
		if (tagArr == null || tagArr.length <= 0) {
			return null;
		}
		List<UserViews> list = new ArrayList<UserViews>();
		// 用于检查重复
		Set<String> tagSet = new HashSet<String>();
		UserViews tag;
		for (String name : tagArr) {
			// 检测重复
			for (String t : tagSet) {
				if (t.equalsIgnoreCase(name)) {
					continue;
				}
			}
			tagSet.add(name);
			tag = saveTag(name);
			list.add(tag);
		}
		return list;
	}

 
	public UserViews saveTag(String name) {
		UserViews tag = findByName(name);
		if (tag != null) {
			tag.setCount(tag.getCount() + 1);
		} else {
			tag = new UserViews();
		 
			tag = save(tag);
		}
		return tag;
	}

	public List<UserViews> updateTags(List<UserViews> tags, String[] tagArr) {
		if (tagArr == null) {
			tagArr = new String[0];
		}
		List<UserViews> list = new ArrayList<UserViews>();
		UserViews bean;
		for (String t : tagArr) {
			bean = null;
			 
			 
			if (bean == null) {
				bean = saveTag(t);
			}
			list.add(bean);
		}
		Set<UserViews> toBeRemove = new HashSet<UserViews>();
		boolean contains;
		for (UserViews tag : tags) {
			contains = false;
		 
			if (!contains) {
				toBeRemove.add(tag);
			}
		}
		tags.clear();
		tags.addAll(list);
		removeTags(toBeRemove);
		return tags;
 
	}

	public void removeTags(Collection<UserViews> tags) {
		Set<UserViews> toRemove = new HashSet<UserViews>();
		for (UserViews tag : tags) {
			if(tag!=null){
				tag.setCount(tag.getCount() - 1);
				if (tag.getCount() <= 0) {
					toRemove.add(tag);
				}
			}
		}
		for (UserViews tag : toRemove) {
			//由于事务真正删除关联的sql语句还没有执行，这个时候 里至少还有一条数据。
			if (dao.countContentRef(tag.getId()) <= 1) {
				dao.deleteById(tag.getId());
			} else {
				// 还有引用，不应该出现的情况，此时无法删除。
				log.warn("UserViews ref to Content > 1,"
						+ " while UserViews.ref_counter <= 0");
			}
		}
	}

	public UserViews save(UserViews bean) {
		bean.init();
		dao.save(bean);
		return bean;
	}

	public UserViews update(UserViews bean) {
		Updater<UserViews> updater = new Updater<UserViews>(bean);
		UserViews entity = dao.updateByUpdater(updater);
		return entity;
	}

	public UserViews deleteById(Integer id) {
		dao.deleteContentRef(id);
		UserViews bean = dao.deleteById(id);
		return bean;
	}

	public UserViews[] deleteByIds(Integer[] ids) {
		UserViews[] beans = new UserViews[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	private UserViewsDao dao;

	@Autowired
	public void setDao(UserViewsDao dao) {
		this.dao = dao;
	}
}