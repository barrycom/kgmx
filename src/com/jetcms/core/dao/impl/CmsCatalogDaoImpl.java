package com.jetcms.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jetcms.common.hibernate4.HibernateBaseDao;
import com.jetcms.core.dao.CmsCatalogDao;
import com.jetcms.core.dao.CmsRoleDao;
import com.jetcms.core.entity.CmsCatalog;
import com.jetcms.core.entity.CmsRole;

@Repository
public class CmsCatalogDaoImpl extends HibernateBaseDao<CmsCatalog, Integer>
		implements CmsCatalogDao {
	@SuppressWarnings("unchecked")
	public List<CmsCatalog> getList() {
		String hql = "from CmsCatalog bean ";
		hql+=" order by bean.priority asc";
		return find(hql);
	}

	public CmsCatalog findById(Integer id) {
		CmsCatalog entity = get(id);
		return entity;
	}

	public CmsCatalog save(CmsCatalog bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsCatalog deleteById(Integer id) {
		CmsCatalog entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<CmsCatalog> getEntityClass() {
		return CmsCatalog.class;
	}
}