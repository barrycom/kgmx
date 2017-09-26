package com.jetcms.cms.dao.main;

import java.util.Date;
import java.util.List;

import com.jetcms.cms.entity.main.ContentCharge;
import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;

public interface ContentChargeDao {
	
	public List<ContentCharge> getList(String contentTitle,Integer authorUserId,
			Date buyTimeBegin,Date buyTimeEnd,int orderBy,int count);
	
	public Pagination getPage(String contentTitle,Integer authorUserId,
			Date buyTimeBegin,Date buyTimeEnd,
			int orderBy,int pageNo,int pageSize);
	
	public ContentCharge findById(Integer id);

	public ContentCharge save(ContentCharge bean);

	public ContentCharge updateByUpdater(Updater<ContentCharge> updater);
}