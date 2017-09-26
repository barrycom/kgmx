package com.jetcms.cms.dao.main;

import com.jetcms.cms.entity.main.ContentDoc;
import com.jetcms.common.hibernate4.Updater;

public interface ContentDocDao {
	public ContentDoc findById(Integer id);

	public ContentDoc save(ContentDoc bean);

	public ContentDoc updateByUpdater(Updater<ContentDoc> updater);
}