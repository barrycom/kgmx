package com.jetcms.core.dao;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;
import com.jetcms.core.entity.CmsWorkflowEventUser;

public interface CmsWorkflowEventUserDao {
	public Pagination getPage(int pageNo, int pageSize);

	public CmsWorkflowEventUser findById(Integer id);

	public CmsWorkflowEventUser save(CmsWorkflowEventUser bean);

	public CmsWorkflowEventUser updateByUpdater(Updater<CmsWorkflowEventUser> updater);

	public void deleteByEvent(Integer eventId);
	
	public CmsWorkflowEventUser deleteById(Integer id);
	
}