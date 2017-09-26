package com.jetcms.cms.action.admin;

import static com.jetcms.cms.entity.assist.CmsSiteAccessStatistic.STATISTIC_ALL; 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jetcms.cms.entity.assist.CmsUserMenu;
import com.jetcms.cms.entity.main.Channel;
import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.Content.ContentStatus;
import com.jetcms.cms.entity.main.ContentCheck;
import com.jetcms.cms.manager.assist.CmsSiteAccessMng;
import com.jetcms.cms.manager.assist.CmsSiteAccessStatisticMng;
import com.jetcms.cms.manager.assist.CmsUserMenuMng;
import com.jetcms.cms.manager.main.ChannelMng;
import com.jetcms.cms.manager.main.ContentMng; 
import com.jetcms.cms.web.AdminContextInterceptor;
import com.jetcms.common.util.DateUtils;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.web.util.CmsUtils;

@Controller
public class WelcomeAct {
	@RequiresPermissions("index")
	@RequestMapping("/index.do")
	public String index(HttpServletRequest request) {
		return "index";
	}

	@RequiresPermissions("map")
	@RequestMapping("/map.do")
	public String map() {
		return "map";
	}

	@RequiresPermissions("top")
	@RequestMapping("/top.do")
	public String top(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		// 需要获得站点列表
		Set<CmsSite> siteList = user.getSites();
		model.addAttribute("siteList", siteList);
		model.addAttribute("site", site);
		model.addAttribute("siteParam", AdminContextInterceptor.SITE_PARAM);
		model.addAttribute("user", user);
		return "top";
	}

	@RequiresPermissions("main")
	@RequestMapping("/main.do")
	public String main() {
		return "main";

	}

	@RequiresPermissions("left")
	@RequestMapping("/left.do")
	public String left(HttpServletRequest request, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		List<CmsUserMenu>menus=userMenuMng.getList(user.getId(),10);
		model.addAttribute("menus", menus);
		return "left";
	}
	
	@RequiresPermissions("right")
	@RequestMapping("/right.do")
	public String right(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String version = site.getConfig().getVersion();
		Properties props = System.getProperties();
		Runtime runtime = Runtime.getRuntime();
		long freeMemoery = runtime.freeMemory();
		long totalMemory = runtime.totalMemory();
		long usedMemory = totalMemory - freeMemoery;
		long maxMemory = runtime.maxMemory();
		long useableMemory = maxMemory - totalMemory + freeMemoery;
		//最新10条待审内容
		@SuppressWarnings("unchecked")
		List<Content>contents=(List<Content>) contentMng.getPageByRight(null, null, user.getId(), 0, false, false, ContentStatus.prepared, user.getCheckStep(site.getId()), site.getId(), null, user.getId(), 0, 1, 10).getList();
		@SuppressWarnings("unchecked")
		List<Content>newcontents=(List<Content>)contentMng.getPageByRight(null, null,  user.getId(), 0, false, false, ContentStatus.checked,  user.getCheckStep(site.getId()), site.getId(), null,user.getId(), 0, 1, 10).getList();
		model.addAttribute("props", props);
		model.addAttribute("freeMemoery", freeMemoery);
		model.addAttribute("totalMemory", totalMemory);
		model.addAttribute("usedMemory", usedMemory);
		model.addAttribute("maxMemory", maxMemory);
		model.addAttribute("useableMemory", useableMemory);
		model.addAttribute("version", version);
		model.addAttribute("user", user);
		model.addAttribute("site", site);
		model.addAttribute("contents", contents);
		model.addAttribute("newcontents", newcontents);
		getChannelStatic(request, model);
		getContentStatic(request, model);
		getPvStatic(request, model);
		return "right";
	}
	
	private void getChannelStatic(HttpServletRequest request, ModelMap model){
		Integer siteId=CmsUtils.getSiteId(request);
		List<Channel>channelList=new ArrayList<Channel>();
		//顶层栏目
		channelList=channelMng.getTopList(siteId, false);
		model.addAttribute("channelList", channelList);
	}
	
	private void getPvStatic(HttpServletRequest request, ModelMap model){
		Integer siteId=CmsUtils.getSiteId(request);
		List<Object[]> dayPvList,weekPvList,monthPvList,yearPvList;
		//小时pv
		dayPvList=cmsAccessMng.statisticToday(siteId,null);
		Date now=Calendar.getInstance().getTime();
		Date weekBegin=DateUtils.getSpecficWeekStart(now, 0);
		Date monthBegin=DateUtils.getSpecficMonthStart(now, 0);
		//本周PV
		weekPvList=cmsAccessStatisticMng.statistic(weekBegin, now, siteId, STATISTIC_ALL,null);
		//本月pv
		monthPvList=cmsAccessStatisticMng.statistic(monthBegin, now, siteId, STATISTIC_ALL,null);
		//本年pv
		yearPvList=cmsAccessStatisticMng.statisticByYear(Calendar.getInstance().get(Calendar.YEAR), siteId,STATISTIC_ALL,null,true,null);
		model.addAttribute("dayPvList", dayPvList);
		model.addAttribute("weekPvList", weekPvList);
		model.addAttribute("monthPvList", monthPvList);
		model.addAttribute("yearPvList", yearPvList);
	}
	
	private void getContentStatic(HttpServletRequest request, ModelMap model){
		Integer siteId=CmsUtils.getSiteId(request);
		Map<String, Object> restrictions = new HashMap<String, Object>();
		 
		Date now=Calendar.getInstance().getTime();
		Date dayBegin=DateUtils.getStartDate(now);
		Date weekBegin=DateUtils.getSpecficWeekStart(now, 0);
		Date monthBegin=DateUtils.getSpecficMonthStart(now, 0);
 
		 
	}
	
	
	
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private CmsUserMenuMng userMenuMng;
	@Autowired
	private ChannelMng channelMng;
	 
	@Autowired
	private CmsSiteAccessMng cmsAccessMng;
	@Autowired
	private CmsSiteAccessStatisticMng cmsAccessStatisticMng;
}
