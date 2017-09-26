package com.jetcms.cms.manager.main;

import com.jetcms.cms.entity.main.ContentCatalog;

public interface ContentCatalogMng {
	public ContentCatalog findById(Integer id);
	public ContentCatalog insert(ContentCatalog catalog);
 
	public ContentCatalog update(ContentCatalog catalog);
	
	public void deleteAll(ContentCatalog catalog);
}