package com.jetcms.cms.dao.assist;

import java.util.List;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;
import com.jetcms.cms.entity.assist.CmsDirectiveTpl;

public interface CmsDirectiveTplDao {
	public Pagination getPage(int pageNo, int pageSize);
	
	public List<CmsDirectiveTpl> getList(int count);

	public CmsDirectiveTpl findById(Integer id);

	public CmsDirectiveTpl save(CmsDirectiveTpl bean);

	public CmsDirectiveTpl updateByUpdater(Updater<CmsDirectiveTpl> updater);

	public CmsDirectiveTpl deleteById(Integer id);
}