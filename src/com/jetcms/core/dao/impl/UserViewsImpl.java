package com.jetcms.core.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.jetcms.common.hibernate4.Finder;
import com.jetcms.common.hibernate4.HibernateBaseDao;
import com.jetcms.common.page.Pagination;
import com.jetcms.core.dao.UserViewsDao;
import com.jetcms.core.entity.UserViews;

@Repository
public class UserViewsImpl extends HibernateBaseDao<UserViews, Integer>
		implements UserViewsDao {
	@SuppressWarnings("unchecked")
	public List<UserViews> getList(Integer count, boolean cacheable) {
		String hql = "from UserViews bean order by bean.count desc";
		Query query = getSession().createQuery(hql);
		if (count != null) {
			query.setMaxResults(count);
		}
		query.setCacheable(cacheable);
		return query.list();
	}

	public Pagination getPage(String name, int pageNo, int pageSize,
			boolean cacheable) {
		Finder f = Finder.create("from UserViews bean");
	 
		f.append(" order by bean.count desc");
		f.setCacheable(cacheable);
		return find(f, pageNo, pageSize); 
	}

	public UserViews findById(Integer id) {
		UserViews entity = get(id);
		return entity;
	}

	public UserViews findByName(String name, boolean cacheable) {
		String hql = "from UserViews bean where bean.name=:name";
		return (UserViews) getSession().createQuery(hql).setParameter("name",
				name).setCacheable(cacheable).uniqueResult();
	}

	public UserViews save(UserViews bean) {
		getSession().save(bean);
		return bean;
	}

	public UserViews deleteById(Integer id) {
		UserViews entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public int deleteContentRef(Integer id) {
		Query query = getSession().getNamedQuery("UserViews.deleteContentRef");
		return query.setParameter(0, id).executeUpdate();
	}

	public int countContentRef(Integer id) {
		Query query = getSession().getNamedQuery("UserViews.countContentRef");
		return ((Number) (query.setParameter(0, id).list().iterator().next()))
				.intValue();
	}

	@Override
	protected Class<UserViews> getEntityClass() {
		return UserViews.class;
	}
}