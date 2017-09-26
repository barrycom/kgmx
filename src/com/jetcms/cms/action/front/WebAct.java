package com.jetcms.cms.action.front;

import static com.jetcms.cms.Constants.TPLDIR_MEMBER;
import static com.jetcms.common.page.SimplePage.cpn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentCatalog;
import com.jetcms.cms.manager.assist.CmsCommentMng;
import com.jetcms.cms.manager.assist.CmsGuestbookMng;
import com.jetcms.cms.manager.main.ChannelMng;
import com.jetcms.cms.manager.main.ContentDocMng;
import com.jetcms.cms.manager.main.ContentMng;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.web.session.SessionProvider;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.MemberConfig;
import com.jetcms.core.manager.CmsDictionaryMng;
import com.jetcms.core.web.util.CmsUtils;
import com.jetcms.core.web.util.FrontUtils;

@Controller
public class WebAct {
	private static final Logger log = LoggerFactory.getLogger(WebAct.class);
	public static final String MEMBER_MY_ORDER_QUERY_ONLINE = "tpl.memberOrderQueryOnline";
	public static final String  MEMBER_ZJHG_INFO ="tpl.memberZjhgInfo";
	public static final String MEMBER_HDWD ="tpl.memberHdwd";

	 

	@RequestMapping(value = "/web/live_online.jspx")
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
		 
		  
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_QUERY_ONLINE);
	}
	
	
	@RequestMapping(value = "/web/zjhg.jspx")
	public String zjhg(Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request); 
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
			 
		}  
		//buy.getContent().setCatalog(contentCatalogs);
		if(catalog!=null && catalog.size()>0){
			model.addAttribute("content",catalog.get(0).getContent());
		}
		 
	    model.addAttribute("catalog",catalog);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_MY_ORDER_QUERY_ONLINE);
	}
	
	
	@RequestMapping(value = "/web/zjhginfo.jspx")
	public String zjhginfo(Integer contentId,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request); 
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		 
		/*if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}*/
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		Content content = contentMng.findById(contentId);
		 
	    model.addAttribute("content",content);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_ZJHG_INFO);
	}
	
	
	
	@RequestMapping(value = "/web/hdwd.jspx")
	public String hdwd(Integer pageNo,HttpServletRequest request,Integer  teacher,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request); 
	 
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		if(teacher!=null){
			model.addAttribute("teacher", teacher);
		}else{
			model.addAttribute("teacher", 0);
			teacher = 0;
		}
		Pagination pagination = guestbookMng.getPage(site.getId(), teacher,null,
				null, null, null, true, false, cpn(pageNo), 10);
		model.addAttribute("pagination", pagination);
		 
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_HDWD);
	}
	
	
	
	
	@RequestMapping(value = "/web/baidu.jspx")
	public ModelAndView baidu(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request); 
 		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		String ip = request.getHeader("x-forwarded-for");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
       System.out.println(ip); 
		// 没有开启会员功能
		 
		/*if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}*/
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/ 
		  
	    String url = "redirect:http://1507k7g973.51mypc.cn/kgmx/web/baidu.jspx"; 
        return new ModelAndView(url);
	}
	
	
	
 



	@Autowired
	private CmsGuestbookMng guestbookMng;
	@Autowired
	private CmsDictionaryMng cmsDictionaryMng;
	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private CmsCommentMng cmsCommentMng;
	@Autowired
	private ContentMng contentMng;  
	@Autowired
	private SessionProvider session; 
}
