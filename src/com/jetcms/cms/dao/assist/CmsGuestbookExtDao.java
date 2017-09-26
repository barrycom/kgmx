package com.jetcms.cms.dao.assist;

import com.jetcms.cms.entity.assist.CmsGuestbookExt;
import com.jetcms.common.hibernate4.Updater;

public interface CmsGuestbookExtDao {
	public CmsGuestbookExt findById(Integer id);

	public CmsGuestbookExt save(CmsGuestbookExt bean);

	public CmsGuestbookExt updateByUpdater(Updater<CmsGuestbookExt> updater);

	public CmsGuestbookExt deleteById(Integer id);
}