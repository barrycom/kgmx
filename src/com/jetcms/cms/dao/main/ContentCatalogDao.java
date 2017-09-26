package com.jetcms.cms.dao.main;

import com.jetcms.cms.entity.main.ContentCatalog;
import com.jetcms.common.hibernate4.Updater;

public interface ContentCatalogDao {
	public ContentCatalog findById(Integer id);
 
	public ContentCatalog insert(ContentCatalog catalog);
	public ContentCatalog updateByUpdater(Updater<ContentCatalog> updater);
	public void deleteAll(ContentCatalog catalog);
}