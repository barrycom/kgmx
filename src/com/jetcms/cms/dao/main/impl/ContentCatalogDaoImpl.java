package com.jetcms.cms.dao.main.impl;

import static com.jetcms.cms.entity.main.Content.ContentStatus.passed;
import static com.jetcms.cms.entity.main.Content.ContentStatus.prepared;
import static com.jetcms.cms.entity.main.Content.ContentStatus.rejected;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.jetcms.cms.dao.main.ContentCatalogDao;
import com.jetcms.cms.entity.main.ContentCatalog;
import com.jetcms.common.hibernate4.Finder;
import com.jetcms.common.hibernate4.HibernateBaseDao;

@Repository
public class ContentCatalogDaoImpl extends HibernateBaseDao<ContentCatalog, Integer>
		implements ContentCatalogDao {
	public ContentCatalog findById(Integer id) {
		ContentCatalog entity = get(id);
		return entity;
	}
	public ContentCatalog insert(ContentCatalog catalog) {
		getSession().save(catalog);
		return catalog;
	}
	public void deleteAll(ContentCatalog catalog){ 
			String hql = "delete from ContentCatalog bean where bean.contentId=:contentId";
			Query query = getSession().createQuery(hql).setParameter("contentId", catalog.getContentId());
			query.executeUpdate();
		  
		  
	}
 

	@Override
	protected Class<ContentCatalog> getEntityClass() {
		return ContentCatalog.class;
	}
}