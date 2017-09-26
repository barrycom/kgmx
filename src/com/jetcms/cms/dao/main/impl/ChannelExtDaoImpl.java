package com.jetcms.cms.dao.main.impl;

import org.springframework.stereotype.Repository;

import com.jetcms.cms.dao.main.ChannelExtDao;
import com.jetcms.cms.entity.main.ChannelExt;
import com.jetcms.common.hibernate4.HibernateBaseDao;

@Repository
public class ChannelExtDaoImpl extends HibernateBaseDao<ChannelExt, Integer>
		implements ChannelExtDao {
	public ChannelExt save(ChannelExt bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<ChannelExt> getEntityClass() {
		return ChannelExt.class;
	}
}