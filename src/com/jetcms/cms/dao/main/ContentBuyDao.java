package com.jetcms.cms.dao.main;

import java.util.Date;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;
import com.jetcms.cms.entity.main.ContentBuy;

public interface ContentBuyDao {
	public Pagination getPage(String orderNum,Integer buyUserId,Integer authorUserId,
			Short payMode,int pageNo, int pageSize);
	public Pagination getPagebyPay(String orderNum,Integer buyUserId,Integer authorUserId,
			Short payMode,int pageNo, int pageSize);
	 
	
	public Pagination getPageOrder(String startTime,String endTime,int userId,int pageNo, int pageSize);
	
	public Pagination getPageByContent(Integer contentId,
			Short payMode,int pageNo, int pageSize);

	public ContentBuy findById(Long id);
	
	public ContentBuy findByOrderNumber(String orderNumber);
	public String queryCountBycontentId(Integer contentId);
	
	 
	
	public ContentBuy find(Integer buyUserId,Integer contentId);

	public ContentBuy save(ContentBuy bean);

	public ContentBuy updateByUpdater(Updater<ContentBuy> updater);

	public ContentBuy deleteById(Long id);
}