package com.jetcms.core.manager;

import java.util.List;
import java.util.Set;

import com.jetcms.core.entity.CmsCatalog;
import com.jetcms.core.entity.CmsRole;

public interface CmsCatalogMng {
	
	public List<CmsCatalog> getList();

	public CmsCatalog findById(Integer id);

	public CmsCatalog save(CmsCatalog bean);

	public CmsCatalog update(CmsCatalog bean);

	public CmsCatalog deleteById(Integer id);

	public CmsCatalog[] deleteByIds(Integer[] ids);

	public void deleteMembers(CmsCatalog role, Integer[] userIds);
}