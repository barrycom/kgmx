package com.jetcms.cms.manager.assist;

import java.util.Map;

import com.jetcms.cms.entity.assist.CmsConfigContentCharge;

public interface CmsConfigContentChargeMng {
	
	public CmsConfigContentCharge findById(Integer id) ;
	
	public CmsConfigContentCharge getDefault();

	public CmsConfigContentCharge update(CmsConfigContentCharge bean
			,String payTransferPassword,Map<String,String> keys);
	
	public CmsConfigContentCharge afterUserPay(Double payAmout);

}