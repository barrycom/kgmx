package com.jetcms.cms.manager.main.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jetcms.cms.dao.main.ContentCatalogDao;
import com.jetcms.cms.entity.main.ContentBuy;
import com.jetcms.cms.entity.main.ContentCatalog;
import com.jetcms.cms.entity.main.ContentCharge;
import com.jetcms.cms.manager.main.ContentCatalogMng;
import com.jetcms.common.hibernate4.Updater;

@Service
@Transactional
public class ContentCatalogMngImpl implements ContentCatalogMng {
	@Transactional(readOnly = true)
	public ContentCatalog findById(Integer id) {
		ContentCatalog entity = dao.findById(id);
		return entity;
	} 
	public ContentCatalog insert(ContentCatalog catalog) {
		  dao.insert(catalog);
		return catalog;
	} 
	public ContentCatalog update(ContentCatalog bean) {
		Updater<ContentCatalog> updater = new Updater<ContentCatalog>(bean);
		bean = dao.updateByUpdater(updater);
		 
		return bean;
	}

	private ContentCatalogDao dao;

	@Autowired
	public void setDao(ContentCatalogDao dao) {
		this.dao = dao;
	}
	@Override
	public void deleteAll(ContentCatalog catalog) {
		// TODO Auto-generated method stub
		dao.deleteAll(catalog);
	}
}