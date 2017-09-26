package com.jetcms.cms.manager.main;

import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentTxt;

public interface ContentTxtMng {
	public ContentTxt save(ContentTxt txt, Content content);

	public ContentTxt update(ContentTxt txt, Content content);
}