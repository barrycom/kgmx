package com.jetcms.cms.dao.main;

import com.jetcms.cms.entity.main.ContentTxt;
import com.jetcms.common.hibernate4.Updater;

public interface ContentTxtDao {
	public ContentTxt findById(Integer id);

	public ContentTxt save(ContentTxt bean);

	public ContentTxt updateByUpdater(Updater<ContentTxt> updater);
}