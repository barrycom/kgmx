package com.jetcms.cms.dao.main;

import com.jetcms.cms.entity.main.ContentExt;
import com.jetcms.common.hibernate4.Updater;

public interface ContentExtDao {
	public ContentExt findById(Integer id);

	public ContentExt save(ContentExt bean);

	public ContentExt updateByUpdater(Updater<ContentExt> updater);
}