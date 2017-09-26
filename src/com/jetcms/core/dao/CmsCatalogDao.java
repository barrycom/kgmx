package com.jetcms.core.dao;

import java.util.List;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.core.entity.CmsCatalog;

public interface CmsCatalogDao {
	public List<CmsCatalog> getList();

	public CmsCatalog findById(Integer id);

	public CmsCatalog save(CmsCatalog bean);

	public CmsCatalog updateByUpdater(Updater<CmsCatalog> updater);

	public CmsCatalog deleteById(Integer id);
}