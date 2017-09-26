package com.jetcms.cms.dao.assist;

import com.jetcms.cms.entity.assist.CmsVoteItem;
import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;

public interface CmsVoteItemDao {
	public Pagination getPage(int pageNo, int pageSize);

	public CmsVoteItem findById(Integer id);

	public CmsVoteItem save(CmsVoteItem bean);

	public CmsVoteItem updateByUpdater(Updater<CmsVoteItem> updater);

	public CmsVoteItem deleteById(Integer id);
}