package com.jetcms.core.manager;

import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsUserResume;

public interface CmsUserResumeMng {
	public CmsUserResume save(CmsUserResume ext, CmsUser user);

	public CmsUserResume update(CmsUserResume ext, CmsUser user);
}