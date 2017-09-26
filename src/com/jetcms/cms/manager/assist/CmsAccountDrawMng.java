package com.jetcms.cms.manager.assist;

import com.jetcms.common.page.Pagination;
import com.jetcms.core.entity.CmsUser;

import java.util.Date;

import com.jetcms.cms.entity.assist.CmsAccountDraw;

public interface CmsAccountDrawMng {
	
	public CmsAccountDraw draw(CmsUser user,Double amount,String applyAccount);
	
	public Double getAppliedSum(Integer userId);
	
	public Pagination getPage(Integer userId,Short applyStatus,
			Date applyTimeBegin,Date applyTimeEnd,int pageNo, int pageSize);

	public CmsAccountDraw findById(Integer id);

	public CmsAccountDraw save(CmsAccountDraw bean);

	public CmsAccountDraw update(CmsAccountDraw bean);

	public CmsAccountDraw deleteById(Integer id);
	
	public CmsAccountDraw[] deleteByIds(Integer[] ids);
}