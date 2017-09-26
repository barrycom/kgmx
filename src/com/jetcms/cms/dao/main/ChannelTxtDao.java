package com.jetcms.cms.dao.main;

import com.jetcms.cms.entity.main.ChannelTxt;
import com.jetcms.common.hibernate4.Updater;

public interface ChannelTxtDao {
	public ChannelTxt findById(Integer id);

	public ChannelTxt save(ChannelTxt bean);

	public ChannelTxt updateByUpdater(Updater<ChannelTxt> updater);
}