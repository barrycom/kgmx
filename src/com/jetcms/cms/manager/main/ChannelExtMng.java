package com.jetcms.cms.manager.main;

import com.jetcms.cms.entity.main.Channel;
import com.jetcms.cms.entity.main.ChannelExt;

public interface ChannelExtMng {
	public ChannelExt save(ChannelExt ext, Channel channel);

	public ChannelExt update(ChannelExt ext);
}