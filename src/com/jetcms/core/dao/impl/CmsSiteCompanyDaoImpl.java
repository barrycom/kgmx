package com.jetcms.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.jetcms.common.hibernate4.HibernateBaseDao;
import com.jetcms.core.dao.CmsSiteCompanyDao;
import com.jetcms.core.entity.CmsSiteCompany;

@Repository
public class CmsSiteCompanyDaoImpl extends
		HibernateBaseDao<CmsSiteCompany, Integer> implements CmsSiteCompanyDao {

	public CmsSiteCompany save(CmsSiteCompany bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<CmsSiteCompany> getEntityClass() {
		return CmsSiteCompany.class;
	}
}