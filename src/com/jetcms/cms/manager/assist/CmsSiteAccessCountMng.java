package com.jetcms.cms.manager.assist;

import java.util.Date;
import java.util.List;

import com.jetcms.cms.entity.assist.CmsSiteAccessCount;

/**
 * @author Tom
 */
public interface CmsSiteAccessCountMng {
	public List<Object[]> statisticVisitorCountByDate(Integer siteId,Date begin, Date end);

	public List<Object[]> statisticVisitorCountByYear(Integer siteId,Integer year);

	public CmsSiteAccessCount save(CmsSiteAccessCount count);

	public void statisticCount(Date date, Integer siteId);
}
