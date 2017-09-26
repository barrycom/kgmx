package com.jetcms.core.manager;

import java.util.Set;

import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsWorkflowEvent;
import com.jetcms.core.entity.CmsWorkflowEventUser;

public interface CmsWorkflowEventUserMng {
	
	public Set<CmsWorkflowEventUser> save(CmsWorkflowEvent event,Set<CmsUser>users);

	public Set<CmsWorkflowEventUser> update(CmsWorkflowEvent event,Set<CmsUser>users);
	
	public void deleteByEvent(Integer eventId);

}