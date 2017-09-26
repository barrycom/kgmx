package com.jetcms.core.dao;

import java.util.List;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;
import com.jetcms.core.entity.CmsWorkflow;

public interface CmsWorkflowDao {
	public Pagination getPage(Integer siteId,int pageNo, int pageSize);
	
	public List<CmsWorkflow> getList(Integer siteId,Boolean disabled);

	public CmsWorkflow findById(Integer id);

	public CmsWorkflow save(CmsWorkflow bean);

	public CmsWorkflow updateByUpdater(Updater<CmsWorkflow> updater);

	public CmsWorkflow deleteById(Integer id);
}