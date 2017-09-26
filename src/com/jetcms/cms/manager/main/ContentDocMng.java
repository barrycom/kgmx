package com.jetcms.cms.manager.main;

import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentDoc;
import com.jetcms.core.entity.CmsUser;
public interface ContentDocMng {
	public ContentDoc save(ContentDoc doc, Content content);

	public ContentDoc update(ContentDoc doc, Content content);
	
	public ContentDoc operateDocGrain(CmsUser downUser, ContentDoc doc);
	
	public ContentDoc createSwfFile(ContentDoc doc);
	
	public ContentDoc createPdfFile(ContentDoc doc);
}