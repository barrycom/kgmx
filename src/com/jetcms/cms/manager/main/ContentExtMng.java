package com.jetcms.cms.manager.main;

import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentExt;

public interface ContentExtMng {
	public ContentExt save(ContentExt ext, Content content);

	public ContentExt update(ContentExt ext);
}