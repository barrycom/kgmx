package com.jetcms.cms.action.member;

import static com.jetcms.cms.Constants.TPLDIR_MEMBER;
import static com.jetcms.common.page.SimplePage.cpn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jetcms.cms.entity.main.Channel;
import com.jetcms.cms.entity.main.ChannelCatalog;
import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentBuy;
import com.jetcms.cms.entity.main.ContentCatalog;
import com.jetcms.cms.entity.main.ContentKj;
import com.jetcms.cms.manager.assist.CmsGuestbookMng;
import com.jetcms.cms.manager.main.ChannelMng;
import com.jetcms.cms.manager.main.ContentBuyMng;
import com.jetcms.cms.manager.main.ContentCatalogMng;
import com.jetcms.cms.manager.main.ContentChargeMng;
import com.jetcms.cms.manager.main.ContentMng;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.util.DateUtils;
import com.jetcms.common.web.CookieUtils;
import com.jetcms.core.entity.CmsCatalog;
import com.jetcms.core.entity.CmsDictionary;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.MemberConfig;
import com.jetcms.core.manager.CmsCatalogMng;
import com.jetcms.core.manager.CmsDictionaryMng;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;
import com.jetcms.core.web.util.FrontUtils;
 

/**
 * 用户账户相关
 * 包含笔者所写文章被用户购买记录
 * 自己的消费记录
 */
@Controller
public class UserContentBuyAct {
	
	public static final String MEMBER_BUY_LIST = "tpl.memberBuyList";
	public static final String MEMBER_MY_ORDER_LIST="tpl.memberMyOrderList";
	public static final String MEMBER_ORDER_LIST = "tpl.memberOrderList";
	public static final String MEMBER_ORDER_INFO = "tpl.memberOrderInfo";
	public static final String MEMBER_MY_ORDER_QUERY = "tpl.memberOrderQuery";
	public static final String MEMBER_MY_ORDER_KJ_LIST ="tpl.memberOrderKjQuery";
	public static final String MEMBER_MY_ORDER_KJLS_LIST ="tpl.memberOrderKjlslist";
	public static final String MEMBER_MY_ORDER_QUERY_ONLINE = "tpl.memberOrderQueryOnline";
	public static final String MEMBER_MY_ORDER_VIDEO = "tpl.memberOrdervideo";
	public static final String MEMBER_MY_KJ_ORDER_VIDEO = "tpl.memberOrderKjvideo";
	public static final String MEMBER_MY_ORDER_SY_VIDEO = "tpl.memberOrderSyvideo";
	public static final String MEMBER_MY_ORDER_SY_LIVE_VIDEO = "tpl.memberOrderSyLivevideo";
	 
	public static final String MEMBER_MY_ORDER_LIVE_VIDEO = "tpl.memberOrderLivevideo";
	public static final String MEMBER_MY_ORDER_LIVE_TWO_VIDEO = "tpl.memberOrderLiveTwovideo";
	 
	public static final String CONTENT_CHARGE_LIST = "tpl.memberContentChargeList";

	/**
	 * 自己消费记录
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/buy_list.jspx")
	public String buyList(String orderNum,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		Pagination pagination=contentBuyMng.getPage(orderNum,user.getId(),
				null,null,cpn(pageNo), CookieUtils.getPageSize(request));
		model.addAttribute("pagination",pagination);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_BUY_LIST);
	}
	
	/**
	 * 订单列表(被购买记录)
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list.jspx")
	public String orderList(String orderNum,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		Pagination pagination=contentBuyMng.getPage(orderNum, user.getId(), null,
				null,cpn(pageNo), 5);
		model.addAttribute("pagination",pagination);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_ORDER_LIST);
	}
	
	 
	
	@RequestMapping(value = "/member/my_order_kj_list.jspx")
	public String myOrderKjList(String orderNum,  Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String schannelId =request.getParameter("schannelId"); 
		 
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		if(StringUtils.isBlank(schannelId)){
			schannelId = "136";
		}
		Integer channelId = Integer.valueOf(schannelId);
		Channel channel = channelMng.findById(channelId);
		if(channel==null){
			return myOrderKjlsList(null, pageNo, request, response, model);
		}
		 
		Pagination  pagination=contentBuyMng.getPagebyPay(null, user.getId(), null, null,cpn(pageNo),  500 );
		
		List<ContentBuy> buys = (List<ContentBuy>) pagination.getList();
	 
	    List<Content> contents = new ArrayList<Content>();
	 
		for (int i = 0; i < buys.size(); i++) {  
			if(channelId.intValue()==buys.get(i).getContent().getChannel().getId().intValue()){
			  
				
				contents.add(buys.get(i).getContent()); 
			  
			} 
			//如果是全网通
			if(buys.get(i).getContent().getChannel().getId().intValue()==163){
				String classType =buys.get(i).getContent().getAttr().get("classType");
				Integer[] ii = {channelId};
				List<Content> list = contentMng.getListByChannelIdsForTag(ii, null, null, null, null, 2, 0, null, 4, 1, null, null);
	            for (int j = 0; j <list.size(); j++) {
	            	if(StringUtils.isNotBlank(list.get(j).getAttr().get("classType"))){ 
		            	if(list.get(j).getAttr().get("classType").equals(classType)){ 
							if(list.get(j).getAttr().get("upDown").equals("是")){
								contents.add(list.get(j)); 
							}
		            	}
	            	}
				}
			}
		}  
		List<ContentKj> contentKjs = new ArrayList<ContentKj>();
		for (int i = 0; i < contents.size(); i++) { 
			for (int j = 0; j < contents.get(i).getKj().size(); j++) {
				ContentKj contentKj = new ContentKj();
				contentKj.setId(contents.get(i).getKj().get(j).getId());
				contentKj.setName(contents.get(i).getKj().get(j).getName());
				contentKj.setPath(contents.get(i).getKj().get(j).getPath()); 
				contentKj.setPathPass(contents.get(i).getKj().get(j).getPathPass()); 
				contentKj.setContent(contents.get(i));
				contentKjs.add(contentKj);
			} 
		} 
	 
		 Set<ContentKj> ts = new HashSet<ContentKj>();  
	        ts.addAll(contentKjs);  
	        List<ContentKj> contentKjList = new ArrayList<ContentKj>(ts);
	         
	        Collections.sort(contentKjList, new Comparator<ContentKj>() {  
	            public int compare(ContentKj arg0, ContentKj arg1) {  
	                return arg0.getId().compareTo(arg1.getId()); // 按照id排列  
	            }  
	        });
	    List<Channel> channelList = new ArrayList<Channel>();
	    channelList = channelMng.getChildList(75, true);
	    for (int i = 0; i < channelList.size(); i++) {
	    	if(channelList.get(i).getId().intValue()==channelId){
	    	   channelList.get(i).setShow(1);
	    	}
		}
	    
	    model.addAttribute("channelList",channelList);
		model.addAttribute("contentKjs",contentKjList);
		model.addAttribute("channel",channel);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_KJ_LIST);
	}
	@RequestMapping(value = "/member/my_order_kjls_list.jspx")
	public String myOrderKjlsList(String orderNum,  Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		 
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		 List<Channel> channels = channelMng.getChildList(75,true); 
		 
		model.addAttribute("channels",channels);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_KJLS_LIST);
	}
	
	
	
	@RequestMapping(value = "/member/my_order_list.jspx")
	public String myOrderList(String orderNum,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		Pagination  pagination=contentBuyMng.getPagebyPay(orderNum, user.getId(), null,
				null,cpn(pageNo), CookieUtils.getPageSize(request));
		
		List<ContentBuy> buys = (List<ContentBuy>) pagination.getList();
		
		for (int i = 0; i < buys.size(); i++) { 
			buys.get(i).getContent().setPurchaseCount(contentBuyMng.queryCountBycontentId(buys.get(i).getContent().getId()));
		}
		
		pagination.setList(buys);
		
		model.addAttribute("pagination",pagination);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_LIST);
	}
	
	
	@RequestMapping(value = "/member/my_order_query.jspx")
	public String myOrderquery(String orderNum,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String schannelId =request.getParameter("schannelId"); 
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
	String orderTime = request.getParameter("orderTime");
	
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		user =cmsUserMng.findById(user.getId());
		 
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
	 
		ContentBuy buy = contentBuyMng.findByOrderNumber(orderNum); 
		if(StringUtils.isBlank(orderTime)){
			orderTime =     DateFormatUtils.format(new Date(), "yyyy-MM");
		}
		if(buy==null){
			return myOrderList(null, pageNo, request, response, model);
		}
		//没有购买
		if(user.getId().intValue()!=buy.getBuyUser().getId().intValue()){
			return  myOrderList(null, pageNo, request, response, model);
		}
		 
		List<ContentCatalog> contentCatalogss =  new ArrayList<ContentCatalog>();  
		List<ContentCatalog> contentCatalogs =  new ArrayList<ContentCatalog>();  
		Set<CmsCatalog> cmsCatalogs = new HashSet<CmsCatalog>();
		List<Channel> channelList = new ArrayList<Channel>();
		//如果是全网通的订单
		if(buy.getContent().getChannel().getId().intValue() == 163){model.addAttribute("teacherType",1);
					Integer channelId  =null;
					String[] channelIds = buy.getContent().getAttr().get("channelIds").split(",");  
					//如果没传值，默认本课程第一个导师查询
					if(StringUtils.isBlank(schannelId)){ 
						channelId = Integer.valueOf(channelIds[0]); 
					}else{
						//如果有导师值，验证是否存在，不存在取奔课程第一位老师   
						List<String> list = Arrays.asList(channelIds);
						 
						if(list.contains(schannelId)){
							channelId = Integer.valueOf(schannelId);
							
						}else{
							channelId = Integer.valueOf(channelIds[0]);
						}  
					}
					for (int i = 0; i < channelIds.length; i++) {
						Channel channel = channelMng.findById(Integer.valueOf(channelIds[i]));
						if(channel.getId().intValue() == channelId.intValue()){
							channel.setShow(1);
						}
						channelList.add(channel);
					}
					Channel channel =channelMng.findById(channelId);
					Integer mainContentId =  StringUtils.isBlank(channel.getAttr().get("yearClassId"))?0:Integer.valueOf(channel.getAttr().get("yearClassId"));
					Content mainContent = contentMng.findById(mainContentId);
					if(mainContentId==0){
						return  myOrderList(null, pageNo, request, response, model);
					}
					//获取主课程
				 
					List<ContentCatalog> catalogs =  mainContent.getCatalog();
					Collections.sort(catalogs,new Comparator<ContentCatalog>() {  
						@Override
						public int compare(ContentCatalog o1, ContentCatalog o2) {
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
							Date o1time = null;
							Date o2time = null;
							try {
								if(StringUtils.isBlank(o1.getStartTime())){
									o1.setStartTime("00:00:00");
								}
								if(StringUtils.isBlank(o2.getStartTime())){
									o2.setStartTime("00:00:00");
								}
								o1time=df.parse(o1.getLectureDate() +" "+o1.getStartTime());
								o2time=df.parse(o2.getLectureDate() +" "+o2.getStartTime()); 
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  
							if(o1time.getTime()>o2time.getTime()){
							   return 1;
							}else{
							   return 0;
							} 
						}
			        }); 
					
					
					List<ContentCatalog> contentCatalog = new ArrayList<ContentCatalog>(); 
					List<ContentCatalog> giveCatalog = new ArrayList<ContentCatalog>(); 
					//如果本内容是主课内容
					 
					 
					    //非主课，进行判断
						//课程类型（半年，季，月课）
						String classType = buy.getContent().getAttr().get("classType");
						//课节数（按月来计算）
						String classCount = buy.getContent().getAttr().get("classCount");
						int counts = 0;
						if(StringUtils.isNotBlank(classCount)){
							counts = Integer.valueOf(classCount);
						}
						 
						setLastClass(catalogs, contentCatalog,buy.getBuyTime());
						if(contentCatalog!=null){
							if(counts>=contentCatalog.size()){
								giveCatalog = contentCatalog; 
								buy.setIsGave((short) 0);
							}else{
								for (int i = 0; i < counts; i++) {
									giveCatalog.add(contentCatalog.get(i));
								}
								buy.setIsGave((short) 1);
							} 
						} else{
							buy.setIsGave((short) 0);
						}
						contentCatalog = giveCatalog;
						 
				  		Map<String, String> attrOrig = buy.getBuyUser().getAttr();
						 
						 
				  		for (int i = 0; i < giveCatalog.size(); i++) {  
							CmsCatalog catalog = new CmsCatalog();
							 
							attrOrig.put(giveCatalog.get(i).getId()+"", "0");
							catalog.setCatalogId(giveCatalog.get(i).getId());
							catalog.setUser(buy.getBuyUser()); 
							catalog.setViewnum(0);
							catalog.setPriority(i+1);
							catalogMng.save(catalog);
						}
				  		buy.getBuyUser().setAttr(attrOrig); 
			  		    userMng.updateUser(buy.getBuyUser());
					
					
					
					
					contentCatalogss =  contentCatalog;   
					for (int i = 0; i < contentCatalogss.size(); i++) {
						if(StringUtils.isNotBlank(orderTime)){ 
							String catalogDates = contentCatalogss.get(i).getLectureDate().substring(0, 7) ;
							if(!orderTime.equals(catalogDates)){
								continue;
							}else{
								contentCatalogs.add(contentCatalogss.get(i));
							}
							
						} 
					}
					
					
					
					for (int i = 0; i < contentCatalogs.size(); i++) {
						 
						CmsCatalog catalog = new CmsCatalog();
						catalog.setCatalogId(contentCatalogs.get(i).getId());
						String viewnum ="0";
						if(StringUtils.isNoneBlank(user.getAttr().get(contentCatalogs.get(i).getId()+""))){
							viewnum = user.getAttr().get(contentCatalogs.get(i).getId()+"");
						}
						catalog.setViewnum(Integer.valueOf(viewnum));
						
						 
						cmsCatalogs.add(catalog);
					    CmsDictionary dic = cmsDictionaryMng.findByValue("course_category", contentCatalogs.get(i).getCourseCategory());
					    contentCatalogs.get(i).setCourseCategory(dic.getName());
						String dates = contentCatalogs.get(i).getLectureDate();
						String startTimes = contentCatalogs.get(i).getStartTime();
						String endTimes =contentCatalogs.get(i).getEndTime();
						Date startTime=null;
						Date endTime=null;
						Date now = new Date();
						
						if(StringUtils.isBlank(startTimes)){
							startTimes ="00:00:00";
						}
						if(StringUtils.isBlank(endTimes)){
							endTimes ="23:59:59";
						}
						if(StringUtils.isBlank(dates) || StringUtils.isBlank(startTimes)|| StringUtils.isBlank(endTimes)){
							contentCatalogs.get(i).setShowType(1);
						}else{
							try {
								  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(dates+" "+startTimes, "yyyy-MM-dd HH:mm:ss");
								  endTime =  org.apache.commons.lang3.time.DateUtils.parseDate(dates+" "+endTimes, "yyyy-MM-dd HH:mm:ss");
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							 
							if(now.getTime()<startTime.getTime()){
								contentCatalogs.get(i).setShowType(3);
							}else{
								if(now.getTime()>=startTime.getTime() && now.getTime()<endTime.getTime()){
									contentCatalogs.get(i).setShowType(2);
								}else{
									contentCatalogs.get(i).setShowType(1);
								}
							} 
						}
					}
			 
		}else{ 
		 
			Channel channel = buy.getContent().getChannel();
			Integer mainContentId =  StringUtils.isBlank(channel.getAttr().get("yearClassId"))?0:Integer.valueOf(channel.getAttr().get("yearClassId"));
			//获取主课程
			Content mainContent = contentMng.findById(mainContentId);
			List<ContentCatalog> catalogs =  mainContent.getCatalog();
			Collections.sort(catalogs,new Comparator<ContentCatalog>() {  
				@Override
				public int compare(ContentCatalog o1, ContentCatalog o2) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
					Date o1time = null;
					Date o2time = null;
					try {
						if(StringUtils.isBlank(o1.getStartTime())){
							o1.setStartTime("00:00:00");
						}
						if(StringUtils.isBlank(o2.getStartTime())){
							o2.setStartTime("00:00:00");
						}
						o1time=df.parse(o1.getLectureDate() +" "+o1.getStartTime());
						o2time=df.parse(o2.getLectureDate() +" "+o2.getStartTime()); 
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
					if(o1time.getTime()>o2time.getTime()){
					   return 1;
					}else{
					   return 0;
					} 
				}
	        }); 
			
			
			List<ContentCatalog> contentCatalog = new ArrayList<ContentCatalog>(); 
			List<ContentCatalog> giveCatalog = new ArrayList<ContentCatalog>(); 
			//如果本内容是主课内容
			 
			if(buy.getContent().getId().intValue() == mainContentId.intValue()){
				setLastClass(catalogs, contentCatalog,buy.getBuyTime()); 
			}else{
			    //非主课，进行判断
				//课程类型（半年，季，月课）
				String classType = buy.getContent().getAttr().get("classType");
				//课节数（按月来计算）
				String classCount = buy.getContent().getAttr().get("classCount");
				int counts = 0;
				if(StringUtils.isNotBlank(classCount)){
					counts = Integer.valueOf(classCount);
				}
				 
				setLastClass(catalogs, contentCatalog,buy.getBuyTime());
				if(contentCatalog!=null){
					if(counts>=contentCatalog.size()){
						giveCatalog = contentCatalog; 
						buy.setIsGave((short) 0);
					}else{
						for (int i = 0; i < counts; i++) {
							giveCatalog.add(contentCatalog.get(i));
						}
						buy.setIsGave((short) 1);
					} 
				} else{
					buy.setIsGave((short) 0);
				}
				contentCatalog = giveCatalog; 
				
			} 
	  		userMng.updateUser(buy.getBuyUser());
			
			
			
			
			 contentCatalogss =  contentCatalog;  
			 
			for (int i = 0; i < contentCatalogss.size(); i++) {
				if(StringUtils.isNotBlank(orderTime)){ 
					String catalogDates = contentCatalogss.get(i).getLectureDate().substring(0, 7) ;
					if(!orderTime.equals(catalogDates)){
						continue;
					}else{
						contentCatalogs.add(contentCatalogss.get(i));
					}
					
				} 
			}
			
			
			
			for (int i = 0; i < contentCatalogs.size(); i++) {
				 
				CmsCatalog catalog = new CmsCatalog();
				catalog.setCatalogId(contentCatalogs.get(i).getId());
				String viewnum ="0";
				if(StringUtils.isNoneBlank(user.getAttr().get(contentCatalogs.get(i).getId()+""))){
					viewnum = user.getAttr().get(contentCatalogs.get(i).getId()+"");
				}
				catalog.setViewnum(Integer.valueOf(viewnum));
				
				 
				cmsCatalogs.add(catalog);
			    CmsDictionary dic = cmsDictionaryMng.findByValue("course_category", contentCatalogs.get(i).getCourseCategory());
			    contentCatalogs.get(i).setCourseCategory(dic.getName());
				String dates = contentCatalogs.get(i).getLectureDate();
				String startTimes = contentCatalogs.get(i).getStartTime();
				String endTimes =contentCatalogs.get(i).getEndTime();
				Date startTime=null;
				Date endTime=null;
				Date now = new Date();
				
				if(StringUtils.isBlank(startTimes)){
					startTimes ="00:00:00";
				}
				if(StringUtils.isBlank(endTimes)){
					endTimes ="23:59:59";
				}
				if(StringUtils.isBlank(dates) || StringUtils.isBlank(startTimes)|| StringUtils.isBlank(endTimes)){
					contentCatalogs.get(i).setShowType(1);
				}else{
					try {
						  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(dates+" "+startTimes, "yyyy-MM-dd HH:mm:ss");
						  endTime =  org.apache.commons.lang3.time.DateUtils.parseDate(dates+" "+endTimes, "yyyy-MM-dd HH:mm:ss");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
					if(now.getTime()<startTime.getTime()){
						contentCatalogs.get(i).setShowType(3);
					}else{
						if(now.getTime()>=startTime.getTime() && now.getTime()<endTime.getTime()){
							contentCatalogs.get(i).setShowType(2);
						}else{
							contentCatalogs.get(i).setShowType(1);
						}
					} 
				}
			}
			model.addAttribute("teacherType",0);
		}
		
		 
		model.addAttribute("channelList",channelList); 
		user.setCatalog(cmsCatalogs);
		buy.getContent().setCatalog(contentCatalogs);
		model.addAttribute("buy",buy);
		model.addAttribute("user",user);
		model.addAttribute("orderTime",orderTime);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_QUERY);
	}
	public void setLastClass(List<ContentCatalog> catalogs,List<ContentCatalog> contentCatalog,Date now){ 
		Long newData = now.getTime()+90000;
		for (int i = 0; i < catalogs.size(); i++) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date o1time = null;
			if(StringUtils.isBlank(catalogs.get(i).getStartTime())){
				catalogs.get(i).setStartTime("00:00:00");
			}
			try {
				o1time=df.parse(catalogs.get(i).getLectureDate() +" "+catalogs.get(i).getStartTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(o1time.getTime()<newData){
				continue;
			}else{
				contentCatalog.add(catalogs.get(i));
			}
		}
	}
	
	@RequestMapping(value = "/member/my_vip_query.jspx")
	public String myVipquery(String orderNum,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
	
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		user =cmsUserMng.findById(user.getId());
		 if(user.getGroup()==null || user.getGroup().getId()!=3){
			 return FrontUtils.showMessage(request, model, "member.memberClose");
		 }
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
	 
		ContentBuy buy = contentBuyMng.findByOrderNumber(orderNum); 
		 
		
		Set<CmsCatalog> cmsCatalogs = new HashSet<CmsCatalog>();
		List<ContentCatalog> contentCatalogs =  buy.getContent().getCatalog();  
		for (int i = 0; i < contentCatalogs.size(); i++) {
			CmsCatalog catalog = new CmsCatalog();
			catalog.setCatalogId(contentCatalogs.get(i).getId());
			String viewnum ="0";
			if(StringUtils.isNoneBlank(user.getAttr().get(contentCatalogs.get(i).getId()+""))){
				viewnum = user.getAttr().get(contentCatalogs.get(i).getId()+"");
			}
			catalog.setViewnum(Integer.valueOf(viewnum));
			cmsCatalogs.add(catalog);
		  CmsDictionary dic = cmsDictionaryMng.findByValue("course_category", contentCatalogs.get(i).getCourseCategory());
		  contentCatalogs.get(i).setCourseCategory(dic.getName());
			String dates = contentCatalogs.get(i).getLectureDate();
			String startTimes = contentCatalogs.get(i).getStartTime();
			String endTimes =contentCatalogs.get(i).getEndTime();
			Date startTime=null;
			Date endTime=null;
			Date now = new Date();
			if(StringUtils.isBlank(dates) || StringUtils.isBlank(startTimes)|| StringUtils.isBlank(endTimes)){
				contentCatalogs.get(i).setShowType(1);
			}else{
				try {
					  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(dates+" "+startTimes, "yyyy-MM-dd HH:mm:ss");
					  endTime =  org.apache.commons.lang3.time.DateUtils.parseDate(dates+" "+endTimes, "yyyy-MM-dd HH:mm:ss");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				if(now.getTime()<startTime.getTime()){
					contentCatalogs.get(i).setShowType(3);
				}else{
					if(now.getTime()>=startTime.getTime() && now.getTime()<endTime.getTime()){
						contentCatalogs.get(i).setShowType(2);
					}else{
						contentCatalogs.get(i).setShowType(1);
					}
				} 
			}
		}
		user.setCatalog(cmsCatalogs);
		buy.getContent().setCatalog(contentCatalogs);
		model.addAttribute("buy",buy);
		model.addAttribute("user",user);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_QUERY);
	}
	
	
	@RequestMapping(value = "/member/my_order_video.jspx")
	public String myOrderquery(String orderNum,String id, Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		String showUrl = ""; 
		String viewNum = ""; 
		 Long nows = new Date().getTime();
		 ContentCatalog  catalog = new ContentCatalog();
		ContentBuy buy = contentBuyMng.findByOrderNumber(orderNum);
	 
		if(buy==null || buy.getBuyUser().getId().intValue()!=user.getId().intValue() || buy.getOrderSta()==0){
		 
			return myOrderList(null, pageNo, request, response, model);
		}else{
			 ContentCatalog contentCatalog= contentCatalogMng.findById(Integer.valueOf(id));
			Channel channel = buy.getContent().getChannel();
			
			if(channel.getId().intValue()==163){
				 
				 if(contentCatalog!=null && contentCatalog.getContentId()!=null){
					Content content = contentMng.findById(contentCatalog.getContentId());
					 channel = content.getChannel();
				 }else{
						return myOrderList(null, pageNo, request, response, model);
				 } 
			}
			
			
			Integer mainContentId =  StringUtils.isBlank(channel.getAttr().get("yearClassId"))?0:Integer.valueOf(channel.getAttr().get("yearClassId"));
			//获取主课程
			Content mainContent = contentMng.findById(mainContentId);
			List<ContentCatalog> catalogs =  mainContent.getCatalog();
			Collections.sort(catalogs,new Comparator<ContentCatalog>() {  
				@Override
				public int compare(ContentCatalog o1, ContentCatalog o2) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
					Date o1time = null;
					Date o2time = null;
					try {
						if(StringUtils.isBlank(o1.getStartTime())){
							o1.setStartTime("00:00:00");
						}
						if(StringUtils.isBlank(o2.getStartTime())){
							o2.setStartTime("00:00:00");
						}
						o1time=df.parse(o1.getLectureDate() +" "+o1.getStartTime());
						o2time=df.parse(o2.getLectureDate() +" "+o2.getStartTime()); 
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
					if(o1time.getTime()>o2time.getTime()){
					   return 1;
					}else{
					   return 0;
					} 
				}
	        }); 
			List<ContentCatalog> contentCatalogs = new ArrayList<ContentCatalog>(); 
			setLastClass(catalogs, contentCatalogs, buy.getBuyTime());
			String classCount = buy.getContent().getAttr().get("classCount");
			int counts = 0;
			if(StringUtils.isNotBlank(classCount)){
				counts = Integer.valueOf(classCount);
			} 
			List<ContentCatalog> contentCatalogTemp = new ArrayList<ContentCatalog>(); 
			String ids = "";
			for (int i = 0; i < contentCatalogs.size(); i++) {
				 if(counts>i){
					 contentCatalogTemp.add(contentCatalogs.get(i));
					 ids = ids+contentCatalogs.get(i).getId()+",";
				 }
			}
			if(StringUtils.isNotBlank(ids)){
			   ids = ids.substring(0,ids.length()-1); 
			   contentCatalogs = contentCatalogTemp;
			   List<String> list = Arrays.asList(ids.split(","));
	           if(list.contains(contentCatalog.getId()+"")){ 
				   showUrl = contentCatalog.getPath();
				   viewNum = contentCatalog.getShownum()+"";  
				   String startdates = contentCatalog.getLectureDate()+" "+contentCatalog.getStartTime(); 
				   if(StringUtils.isBlank(contentCatalog.getStartTime())){
					   startdates =  contentCatalog.getLectureDate()+" 00:00:00";
				   }
				   Date startTime=null;
				   try {
						  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(startdates, "yyyy-MM-dd HH:mm:ss");
						  
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   if(nows>startTime.getTime()){
					   catalog = contentCatalog; 
				   } 
	           }
			}
		}
		 if(catalog.getId()==null){
			 return myOrderquery(orderNum, pageNo, request, response, model);
		 }
		model.addAttribute("pageNo",pageNo); 
		model.addAttribute("orderNum",orderNum); 
		model.addAttribute("user",user); 
		model.addAttribute("catalog",catalog); 
		model.addAttribute("buy",buy);
		model.addAttribute("showUrl",showUrl);
		model.addAttribute("viewNum",viewNum);
		model.addAttribute("id",Integer.valueOf(id)); 
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_VIDEO);
	}
	
	
	@RequestMapping(value = "/member/my_order_kj_video.jspx")
	public String myOrderKjquery(String orderNum,String id,String kjid, Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		String showUrl = ""; 
		 
		 if(StringUtils.isBlank(id) || StringUtils.isBlank(kjid)){
			 return myOrderList(null, pageNo, request, response, model);
		 }
		 if(!StringUtils.isNumeric(id) || !StringUtils.isNumeric(kjid)){
			 return myOrderList(null, pageNo, request, response, model);
		 }
		 Integer contentId = Integer.valueOf(id);
		 Integer kid = Integer.valueOf(kjid); 
			 Content  content = contentMng.findById(contentId);
			 if(content==null){
				 return myOrderList(null, pageNo, request, response, model);
			 }
			 
			 ContentKj contentKj = new ContentKj();
			 List<ContentKj> kjlist = content.getKj();
			 if(kjlist==null && kjlist.size()<1){
				 return myOrderList(null, pageNo, request, response, model);
			 }else{
				 boolean bool = true;
				 for (int i = 0; i < kjlist.size(); i++) {
					if(kjlist.get(i).getId().intValue()==kid.intValue()){
						contentKj = kjlist.get(i);
						contentKj.setContent(content);
						bool = false;
					}
				 }
				 if(bool){
					 return myOrderList(null, pageNo, request, response, model);
				 }
			 }
			 model.addAttribute("contentKj",contentKj); 
		 
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_KJ_ORDER_VIDEO);
	}
	
	
	
	
	@RequestMapping(value = "/member/my_order_sy_video.jspx")
	public String myOrdersyquery(String id,String catalogId, Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		/*if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}*/
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		String showUrl = ""; 
		String viewNum = ""; 
		long liveTimes =9999999;
		//如果当前用户没买该课程
		if(!contentBuyMng.hasBuyContent(user.getId(), Integer.valueOf(id))){
			liveTimes = 900000;
		}  
		ContentCatalog  catalog = new ContentCatalog();
		 Content content = contentMng.findById(Integer.valueOf(id));
		 List<ContentCatalog> contentCatalogs =  content.getCatalog();
		 Long nows = new Date().getTime();
		for (int i = 0; i < contentCatalogs.size(); i++) {
		  ContentCatalog contentCatalog= contentCatalogMng.findById(contentCatalogs.get(i).getId());
		  if(StringUtils.isBlank(catalogId)){
			  if(i==0){ 
				   showUrl = contentCatalog.getPath();
				   viewNum = contentCatalog.getShownum()+""; 
			   }
		  }else{
			  if( Integer.valueOf(catalogId) == contentCatalogs.get(i).getId()){ 
				   showUrl = contentCatalog.getPath();
				   viewNum = contentCatalog.getShownum()+""; 
			   }
		  }
		   
		   
		   String startdates = contentCatalogs.get(i).getLectureDate()+" "+contentCatalogs.get(i).getStartTime(); 
		   Date startTime=null;
		   try {
				  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(startdates, "yyyy-MM-dd HH:mm:ss");
				  
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   if(nows<startTime.getTime()){
			   catalog = contentCatalog;
			 continue;  
		   }
		} 
		model.addAttribute("catalog",catalog); 
		model.addAttribute("content",content); 
		model.addAttribute("liveTimes",liveTimes); 
		model.addAttribute("showUrl",showUrl);
		model.addAttribute("viewNum",viewNum);
		model.addAttribute("id",Integer.valueOf(id));
		Pagination pagination = guestbookMng.getPage(site.getId(), null,null,
				user.getId(), null, null, true, false, cpn(pageNo), 10);
		model.addAttribute("pagination", pagination);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_SY_VIDEO);
	}
	
	@RequestMapping(value = "/member/my_order_sy_live_video.jspx")
	public String myOrdersyLivequery(String id, Integer priority,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		String showUrl = ""; 
		String viewNum = ""; 
		long liveTimes =9999999;
		//如果当前用户没买该课程
		if(!contentBuyMng.hasBuyContent(user.getId(), Integer.valueOf(id))){
			liveTimes = 900000;
		}  
		Channel  channel = channelMng.findById(Integer.valueOf(id));
		ChannelCatalog channelCatalog = new ChannelCatalog();
	    for (int i = 0; i < channel.getChannelCatalog().size(); i++) {
			if( channel.getChannelCatalog().get(i).getPriority()==priority){
				channelCatalog = channel.getChannelCatalog().get(i);
				break;
			}
		}
		model.addAttribute("channel",channel); 
		model.addAttribute("channelCatalog",channelCatalog);  
	 
		Pagination pagination = guestbookMng.getPage(site.getId(), null,null,
				user.getId(), null, null, true, false, cpn(pageNo), 10);
		model.addAttribute("pagination", pagination);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_SY_LIVE_VIDEO);
	}
	
	
	
	
	@RequestMapping(value = "/member/my_order_livevideo.jspx")
	public String myOrderLiveVideo(String orderNum,String id, Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		String showUrl = ""; 
		String viewNum = ""; 
		Date date = new Date();
		Long nows = date.getTime();
		Long liveTimes =0L;
		ContentBuy buy = contentBuyMng.findByOrderNumber(orderNum);

		if(buy==null || buy.getBuyUser().getId().intValue()!=user.getId().intValue() || buy.getOrderSta()==0){
		 
			return myOrderList(null, pageNo, request, response, model);
		}
		
		
		Channel channel = buy.getContent().getChannel();
		
		if(channel.getId().intValue()==163){
			 ContentCatalog catalog = contentCatalogMng.findById(Integer.valueOf(id));
			 if(catalog!=null && catalog.getContentId()!=null){
				Content content = contentMng.findById(catalog.getContentId());
				 channel = content.getChannel();
			 }else{
					return myOrderList(null, pageNo, request, response, model);
			 } 
		}
		
		
		Integer mainContentId =  StringUtils.isBlank(channel.getAttr().get("yearClassId"))?0:Integer.valueOf(channel.getAttr().get("yearClassId"));
		//获取主课程 
		Content mainContent = contentMng.findById(mainContentId);
		List<ContentCatalog> catalogs =  mainContent.getCatalog();
		Collections.sort(catalogs,new Comparator<ContentCatalog>() {  
			@Override
			public int compare(ContentCatalog o1, ContentCatalog o2) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				Date o1time = null;
				Date o2time = null;
				try {
					if(StringUtils.isBlank(o1.getStartTime())){
						o1.setStartTime("00:00:00");
					}
					if(StringUtils.isBlank(o2.getStartTime())){
						o2.setStartTime("00:00:00");
					}
					o1time=df.parse(o1.getLectureDate() +" "+o1.getStartTime());
					o2time=df.parse(o2.getLectureDate() +" "+o2.getStartTime()); 
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				if(o1time.getTime()>o2time.getTime()){
				   return 1;
				}else{
				   return 0;
				} 
			}
        }); 
		List<ContentCatalog> contentCatalogs = new ArrayList<ContentCatalog>(); 
		setLastClass(catalogs, contentCatalogs, buy.getBuyTime());
		String classCount = buy.getContent().getAttr().get("classCount");
		int counts = 0;
		if(StringUtils.isNotBlank(classCount)){
			counts = Integer.valueOf(classCount);
		}
		
		 
		 ContentCatalog contentCatalog = new  ContentCatalog();
		for (int i = 0; i < counts; i++) {
		   if(contentCatalogs.get(i).getId().intValue()==Integer.valueOf(id).intValue()){
			     contentCatalog= contentCatalogMng.findById(Integer.valueOf(id));
			       String starttime ="00:00:00";
			       String endtime ="23:59:59";
			      
			     String startdates = contentCatalog.getLectureDate()+" "+(StringUtils.isBlank(contentCatalog.getStartTime())?starttime:contentCatalog.getStartTime());
			     String enddates = contentCatalog.getLectureDate()+" "+(StringUtils.isBlank(contentCatalog.getEndTime())?endtime:contentCatalog.getEndTime());
			     Date startTime=null;
				 Date endTime=null;
					 
					try {
						  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(startdates, "yyyy-MM-dd HH:mm:ss");
						  endTime =  org.apache.commons.lang3.time.DateUtils.parseDate(enddates, "yyyy-MM-dd HH:mm:ss");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(startTime.getTime()< nows && nows<endTime.getTime()){
						liveTimes = endTime.getTime()-nows;
					}else{
						return myOrderquery(orderNum, pageNo, request, response, model);
					}
					
			     
		   }
		}
		if(contentCatalog.getId()==null){
			return myOrderquery(orderNum, pageNo, request, response, model);
		}
		model.addAttribute("contentCatalog",contentCatalog);
		model.addAttribute("liveTimes",liveTimes);
		model.addAttribute("buy",buy); 
		model.addAttribute("id",Integer.valueOf(id)); 
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_LIVE_VIDEO);
	}
	
	
	@RequestMapping(value = "/member/my_order_livetwovideo.jspx")
	public String myOrderLivetwoVideo(String id,Integer contentId,Integer channelId,String url,Integer types, Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		 
	 	Date date = new Date();
			Long nows = date.getTime();
			long liveTimes =0;
			Channel  channel = channelMng.findById(channelId);
			List<ChannelCatalog> channelCatalogs = channel.getChannelCatalog();
			if(channelCatalogs!=null && channelCatalogs.size()>0){
				if(channelCatalogs.get(0).getIsFixed()==0){
					channelCatalogs.get(0).setLectureDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
				}
				String startdates = channelCatalogs.get(0).getLectureDate()+" "+channelCatalogs.get(0).getStartTime();
			     String enddates = channelCatalogs.get(0).getLectureDate()+" "+channelCatalogs.get(0).getEndTime();
			     Date startTime=null;
					Date endTime=null;
					 
					try {
						  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(startdates, "yyyy-MM-dd HH:mm:ss");
						  endTime =  org.apache.commons.lang3.time.DateUtils.parseDate(enddates, "yyyy-MM-dd HH:mm:ss");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					if(startTime.getTime()< nows && nows<endTime.getTime()){
						liveTimes = endTime.getTime()-nows;
						 url = channelCatalogs.get(0).getPath();
					}else{
						  return FrontUtils.showLogin(request, model, site);
					}
			}else{
				 return FrontUtils.showLogin(request, model, site);
			}
				       
			model.addAttribute("url",url); 
			model.addAttribute("liveTimes",liveTimes); 
			  
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_LIVE_TWO_VIDEO);
	}
	
	/**
	 * 订单列表(被购买记录)
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_info.jspx")
	public String orderInfo(String orderNum, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		ContentBuy buy = contentBuyMng.findByOrderNumber(orderNum);
		 
		model.addAttribute("buy",buy);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_ORDER_INFO);
	}
	
	/**
	 * 我的内容收益列表
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/charge_list.jspx")
	public String contentChargeList(Integer orderBy,
			Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}
		if(orderBy==null){
			orderBy=1;
		}
		Pagination pagination=contentChargeMng.getPage(null,user.getId(),
				null,null,orderBy,cpn(pageNo), CookieUtils.getPageSize(request));
		model.addAttribute("pagination",pagination);
		model.addAttribute("orderBy", orderBy);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, CONTENT_CHARGE_LIST);
	}
	
	
	@RequestMapping(value = "/member/live_online1.jspx")
	public String liveOnline(Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		/*if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}*/
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		 List<com.jetcms.cms.entity.main.Channel> channels = channelMng.getChildList(75, true);
		 List<Integer> list =new ArrayList<Integer>();
		 Integer[] channelIds = new Integer[channels.size()];
		 for (int i = 0; i < channels.size(); i++) { 
			channelIds[i] = channels.get(i).getId();
		 }  
	  
         List<Content>  contentList=contentMng.getListByChannelIdsForTag( channelIds, null, null, null, null,2,null, 1, 0, null, null);
		 List<ContentCatalog> catalog = new ArrayList<ContentCatalog>();
		 Date now = new Date();
		 String nowsDate =  (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    	 Date startTime = null;
    	 Date endTime = null;
		 try {
			  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(nowsDate+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
			  endTime = org.apache.commons.lang3.time.DateUtils.parseDate(nowsDate+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		for (int i = 0; i < contentList.size(); i++) {
			Content content = contentList.get(i);
			for (int j = 0; j < content.getCatalog().size(); j++) {
				 ContentCatalog contentCatalog = content.getCatalog().get(j); 
				 Date startTimes = null;
				 Date endTimes = null;
				 try {
					 startTimes =  org.apache.commons.lang3.time.DateUtils.parseDate(contentCatalog.getLectureDate()+" "+contentCatalog.getStartTime(), "yyyy-MM-dd HH:mm:ss");
					 endTimes =  org.apache.commons.lang3.time.DateUtils.parseDate(contentCatalog.getLectureDate()+" "+contentCatalog.getEndTime(), "yyyy-MM-dd HH:mm:ss");
				 } catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 } 
				 if(startTime.getTime()<startTimes.getTime() && startTimes.getTime()<endTime.getTime()){
					    if(now.getTime()<startTimes.getTime()){
						        contentCatalog.setShowType(3);
						}else{
							if(now.getTime()>=startTimes.getTime() && now.getTime()<endTimes.getTime()){
								contentCatalog.setShowType(2);
							}else{
								contentCatalog.setShowType(1);
							}
						}    
						contentCatalog.setContent(content);
						contentCatalog.setCourseCategory(cmsDictionaryMng.findByValue("course_category", contentCatalog.getCourseCategory()).getName());
					    catalog.add(contentCatalog); 
				 } 
			}
		} 
		Collections.sort(catalog,new Comparator<ContentCatalog>() {  
			@Override
			public int compare(ContentCatalog o1, ContentCatalog o2) {
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");  
				Date o1time = null;
				Date o2time = null;
				try {
					  o1time=df.parse(o1.getStartTime());
					  o2time=df.parse(o2.getStartTime()); 
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				 if(o1time.getTime()>o2time.getTime()){
					 return 1;
				 }else{
					 return 0;
				 } 
			}
        }); 
		//buy.getContent().setCatalog(contentCatalogs);
		if(catalog!=null && catalog.size()>0){
			model.addAttribute("content",catalog.get(0).getContent());
		}
		 
	    model.addAttribute("catalog",catalog);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_QUERY_ONLINE);
	}
	
	 
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat   df   =   new   SimpleDateFormat("HH:mm:ss");  
		Date begin=df.parse("11:30:24");  
		Date sss=df.parse("10:00:01");  
		System.out.println(begin.getTime()+"+"+sss.getTime());
	}
	
	@Autowired
	private CmsGuestbookMng guestbookMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private ContentBuyMng contentBuyMng;
	@Autowired
	private ContentChargeMng contentChargeMng;
	@Autowired
	private ContentCatalogMng contentCatalogMng;
	
	@Autowired
	private CmsDictionaryMng cmsDictionaryMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private CmsUserMng userMng;
	@Autowired
	private CmsCatalogMng catalogMng;
}
