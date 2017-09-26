package com.jetcms.cms.manager.assist;

import com.jetcms.cms.entity.assist.CmsGuestbook;
import com.jetcms.cms.entity.assist.CmsGuestbookExt;

public interface CmsGuestbookExtMng {
	public CmsGuestbookExt save(CmsGuestbookExt ext, CmsGuestbook guestbook);

	public CmsGuestbookExt update(CmsGuestbookExt ext);
}