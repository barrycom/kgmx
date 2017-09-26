package com.jetcms.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.jetcms.common.hibernate4.HibernateBaseDao;
import com.jetcms.core.dao.CmsUserExtDao;
import com.jetcms.core.entity.CmsUserExt;

@Repository
public class CmsUserExtDaoImpl extends HibernateBaseDao<CmsUserExt, Integer> implements CmsUserExtDao {
	public CmsUserExt findById(Integer id) {
		CmsUserExt entity = get(id);
		return entity;
	}

	public CmsUserExt save(CmsUserExt bean) {
		getSession().save(bean);
		return bean;
	}
	
	@Override
	protected Class<CmsUserExt> getEntityClass() {
		return CmsUserExt.class;
	}
}