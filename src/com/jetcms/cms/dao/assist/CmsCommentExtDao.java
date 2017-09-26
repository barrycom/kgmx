package com.jetcms.cms.dao.assist;

import com.jetcms.cms.entity.assist.CmsCommentExt;
import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;

public interface CmsCommentExtDao {
	public Pagination getPage(int pageNo, int pageSize);

	public CmsCommentExt findById(Integer id);

	public CmsCommentExt save(CmsCommentExt bean);

	public CmsCommentExt updateByUpdater(Updater<CmsCommentExt> updater);

	public int deleteByContentId(Integer contentId);

	public CmsCommentExt deleteById(Integer id);
}