package com.jetcms.core.manager;

import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsSiteCompany;

public interface CmsSiteCompanyMng {
	public CmsSiteCompany save(CmsSite site,CmsSiteCompany bean);

	public CmsSiteCompany update(CmsSiteCompany bean);
}