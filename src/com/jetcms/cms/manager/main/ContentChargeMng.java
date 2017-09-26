package com.jetcms.cms.manager.main;

import java.util.Date;
import java.util.List;

import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentCharge;
import com.jetcms.common.page.Pagination;

public interface ContentChargeMng {
	
	public List<ContentCharge> getList(String contentTitle,Integer authorUserId,
			Date buyTimeBegin,Date buyTimeEnd,int orderBy,int count);
	
	public Pagination getPage(String contentTitle,Integer authorUserId,
			Date buyTimeBegin,Date buyTimeEnd,
			int orderBy,int pageNo,int pageSize);
	
	public ContentCharge save(Double chargeAmount, Short charge,Content content);
	
	public void afterContentUpdate(Content bean,Short charge,Double chargeAmount);

	public ContentCharge update(ContentCharge charge);
	
	public ContentCharge afterUserPay(Double payAmout, Content content);
}