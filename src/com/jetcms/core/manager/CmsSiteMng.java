package com.jetcms.core.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;

public interface CmsSiteMng {
	public List<CmsSite> getList();

	public List<CmsSite> getListFromCache();

	public CmsSite findByDomain(String domain);
	
	public CmsSite findByAccessPath(String accessPath);
	
	public List<CmsSite> getListByMaster(Boolean master);
	
	public List<CmsSite> getListByParent(Integer parentId);
	
	public boolean hasRepeatByProperty(String property);
	
	public List<CmsSite> getTopList();

	public CmsSite findById(Integer id);
	

	public CmsSite save(CmsSite currSite, CmsUser currUser, CmsSite bean,
			Integer uploadFtpId,Integer syncPageFtpId) throws IOException;

	public CmsSite update(CmsSite bean, Integer uploadFtpId,Integer syncPageFtpId);

	public void updateTplSolution(Integer siteId, String solution,String mobileSol);
	
	public void updateRefers(Integer siteId, Integer[] referIds);
	
	public void updateAttr(Integer siteId,Map<String,String>attr);
	
	public void updateAttr(Integer siteId,Map<String,String>...attrs);

	public CmsSite deleteById(Integer id);

	public CmsSite[] deleteByIds(Integer[] ids);
	
	 
	public String getSQLInputStreamForSubsite(String domain, Integer siteId);
}