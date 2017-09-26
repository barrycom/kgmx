package com.jetcms.cms.dao.main;

import com.jetcms.cms.entity.main.ContentCheck;
import com.jetcms.common.hibernate4.Updater;

public interface ContentCheckDao {
	public ContentCheck findById(Long id);

	public ContentCheck save(ContentCheck bean);

	public ContentCheck updateByUpdater(Updater<ContentCheck> updater);
}