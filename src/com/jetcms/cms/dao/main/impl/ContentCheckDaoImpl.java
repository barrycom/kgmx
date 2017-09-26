package com.jetcms.cms.dao.main.impl;

import org.springframework.stereotype.Repository;

import com.jetcms.cms.dao.main.ContentCheckDao;
import com.jetcms.cms.entity.main.ContentCheck;
import com.jetcms.common.hibernate4.HibernateBaseDao;

@Repository
public class ContentCheckDaoImpl extends HibernateBaseDao<ContentCheck, Long>
		implements ContentCheckDao {
	public ContentCheck findById(Long id) {
		ContentCheck entity = get(id);
		return entity;
	}

	public ContentCheck save(ContentCheck bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<ContentCheck> getEntityClass() {
		return ContentCheck.class;
	}
}