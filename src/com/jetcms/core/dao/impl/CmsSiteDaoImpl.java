package com.jetcms.core.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Repository;

import com.jetcms.common.hibernate4.Finder;
import com.jetcms.common.hibernate4.HibernateBaseDao;
import com.jetcms.core.dao.CmsSiteDao;
import com.jetcms.core.entity.CmsSite;

@Repository
public class CmsSiteDaoImpl extends HibernateBaseDao<CmsSite, Integer>
		implements CmsSiteDao {

	public int siteCount(boolean cacheable) {
		String hql = "select count(*) from CmsSite bean";
		return ((Number) getSession().createQuery(hql).setCacheable(cacheable)
				.iterate().next()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<CmsSite> getListByMaster(Boolean master) {
		String hql = "from CmsSite bean  where bean.master=:master order by bean.id asc";
		return getSession().createQuery(hql).setBoolean("master", master)
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<CmsSite> getList(boolean cacheable) {
		String hql = "from CmsSite bean order by bean.id asc";
		return getSession().createQuery(hql).setCacheable(cacheable).list();
	}

	@SuppressWarnings("unchecked")
	public List<CmsSite> getListByParent(Integer parentId) {
		String hql = "from CmsSite bean ";
		Finder f = Finder.create(hql);
		if (parentId != null) {
			f.append(" where bean.parent.id=:parentId").setParam("parentId",
					parentId);
		}
		return find(f);
	}

	public int getCountByProperty(String property) {
		String hql = "select count(distinct " + property
				+ ") from CmsSite bean ";
		Query query = getSession().createQuery(hql);
		return ((Number) query.iterate().next()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<CmsSite> getTopList() {
		String hql = "from CmsSite bean where bean.parent.id is null ";
		Finder f = Finder.create(hql);
		return find(f);
	}

	public CmsSite findByDomain(String domain) {
		return findUniqueByProperty("domain", domain);
	}

	public CmsSite findByAccessPath(String accessPath) {
		return findUniqueByProperty("accessPath", accessPath);
	}

	public CmsSite findById(Integer id) {
		CmsSite entity = get(id);
		return entity;
	}

	public CmsSite save(CmsSite bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsSite deleteById(Integer id) {
		CmsSite entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public CmsSite getByDomain(String domain) {
		String hql = "from CmsSite bean where bean.domain=?";
		return findUniqueByProperty(hql, domain);
	}

	@Override
	protected Class<CmsSite> getEntityClass() {
		return CmsSite.class;
	}

	public ResultSet getResultSet(final Integer siteId, final String sql) {
	    ResultSet  resultSet = null;
	    resultSet = getSession().doReturningWork(
				new ReturningWork() {
					  @Override
					public Object execute(Connection arg0) throws SQLException {
						  try { 
								PreparedStatement ps = arg0.prepareStatement(sql);
								if (sql.contains("?"))
									ps.setInt(1, siteId.intValue());
								   return ps.executeQuery();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							return null;
					}
				} 
		
		);
			 return resultSet;
		/*new Work() { 
			@Override
			public void execute(Connection arg0) throws SQLException {
				 

			}
		});*/

	 

	/*	try {
			conn = SessionFactoryUtils.getDataSource(getSessionFactory())
					.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			if (sql.contains("?"))
				ps.setInt(1, siteId.intValue());
			return ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;*/
	}
}