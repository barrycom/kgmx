package com.jetcms.cms.dao.main;

import com.jetcms.cms.entity.main.ChannelExt;
import com.jetcms.common.hibernate4.Updater;

public interface ChannelExtDao {
	public ChannelExt save(ChannelExt bean);

	public ChannelExt updateByUpdater(Updater<ChannelExt> updater);
}