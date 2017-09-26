package com.jetcms.cms.manager.main;

import java.util.List;

import com.jetcms.cms.entity.main.Channel;
import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentShareCheck;
import com.jetcms.common.page.Pagination;
import com.jetcms.core.entity.CmsUser;

/**
 * 共享内容审核Manager接口
 * 
 * '内容'数据存在，则'共享内容审核'数据必须存在。
 * 
 */
public interface ContentShareCheckMng {
	
	public ContentShareCheck findById(Integer id);
	
	public ContentShareCheck save(ContentShareCheck check);
	
	public ContentShareCheck save(ContentShareCheck check, Content content,Channel channel,CmsUser user);

	public ContentShareCheck update(ContentShareCheck bean);
	
	public ContentShareCheck deleteById(Integer id);
	
	public ContentShareCheck[] deleteByIds(Integer[] ids);
	
	public List<ContentShareCheck> getList(Integer contentId,Integer channelId);
	
	public Pagination getPageForShared(String title, Byte status, Integer siteId,Integer targetSiteId,Integer requestSiteId, int pageNo, int pageSize);
}