package com.jetcms.cms.dao.assist;

import java.util.Date;
import com.jetcms.cms.entity.assist.CmsMessage;
import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;

/**
 *江西金磊科技发展有限公司jetcms研发
 */
public interface CmsMessageDao {

	public Pagination getPage(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable,
			int pageNo, int pageSize);

	public CmsMessage findById(Integer id);

	public CmsMessage save(CmsMessage bean);

	public CmsMessage updateByUpdater(Updater<CmsMessage> updater);
	
	public CmsMessage update(CmsMessage bean);

	public CmsMessage deleteById(Integer id);

	public CmsMessage[] deleteByIds(Integer[] ids);
}