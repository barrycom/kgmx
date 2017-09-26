package com.jetcms.cms.action.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jetcms.cms.entity.main.Channel;
import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentCatalog;
import com.jetcms.cms.entity.main.ContentCount;
import com.jetcms.cms.entity.main.ContentCount.ContentViewCount;
import com.jetcms.cms.manager.main.ContentCatalogMng;
import com.jetcms.cms.manager.main.ContentCountMng;
import com.jetcms.cms.manager.main.ContentMng;
import com.jetcms.cms.service.ChannelCountCache;
import com.jetcms.cms.service.ContentCountCache;
import com.jetcms.common.web.ResponseUtils;
import com.jetcms.core.entity.CmsCatalog;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.manager.CmsCatalogMng;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.web.util.CmsUtils;

@Controller
public class ContentCountAct {
	@RequestMapping(value = "/content_view.jspx", method = RequestMethod.GET)
	public void contentView(Integer contentId, HttpServletRequest request,
			HttpServletResponse response) throws JSONException {
		if (contentId == null) {
			ResponseUtils.renderJson(response, "[]");
			return;
		}
		int[] counts = contentCountCache.viewAndGet(contentId);
		//栏目访问量计数
		Channel channel=contentMng.findById(contentId).getChannel();
		channelCountCache.viewAndGet(channel.getId());
		String json;
		if (counts != null) {
			json = new JSONArray(counts).toString();
			ResponseUtils.renderJson(response, json);
		} else {
			ResponseUtils.renderJson(response, "[]");
		}
	}
	
	@RequestMapping(value = "/content_videoview.jspx", method = RequestMethod.GET)
	public void contentVideoView(Integer id,Integer contentId, HttpServletRequest request,
			HttpServletResponse response) throws JSONException {
		CmsUser member = CmsUtils.getUser(request);
		member = userMng.findById(member.getId());
		String result="1";
		//应查看次数
		int views = 0;
		//查看次数
		int shows = 0;
		if (id == null || contentId==null) {
			ResponseUtils.renderJson(response, "[]");
			return;
		}
		if(StringUtils.isBlank(request.getParameter("pageNo"))){
		 Content content = contentMng.findById(contentId);
		 List<ContentCatalog> catalogs = new ArrayList<ContentCatalog>(); 
		 catalogs =  content.getCatalog();
		 Map<String, String> attrOrig = member.getAttr(); 
		 //获取查看次数
		 ContentCatalog contentCatalog= contentCatalogMng.findById(id); 	 
	     views =  contentCatalog.getViewCount();
	     
	    
	      if(StringUtils.isNotBlank(attrOrig.get(id+"")) && !"null".equals(attrOrig.get(id+""))){
	    	  shows = Integer.valueOf(attrOrig.get(id+""))+1;
	    	  
	      }else{
	    	  shows=1;
	      }  
	      attrOrig.put(""+id, (shows)+""); 
	      member.setAttr(attrOrig);
	      userMng.updateUser(member);
		if(views<shows){
		   result="2";
		} 
		}else{
			 result="1";
		} 
	  ResponseUtils.renderJson(response, result);
		 
	}
	
	
	
	@RequestMapping(value = "/content_view_get.jspx")
	public void getContentView(Integer contentIds[], String view, HttpServletRequest request,
			HttpServletResponse response) throws JSONException {
		if (contentIds == null) {
			ResponseUtils.renderJson(response, "[]");
			return;
		}
		ContentViewCount viewCountType;
		JSONObject json=new JSONObject();
		Map<Integer, Integer>contentViewsMap=new HashMap<Integer, Integer>();
		if (!StringUtils.isBlank(view)) {
			viewCountType = ContentViewCount.valueOf(view);
		} else {
			viewCountType = ContentViewCount.viewTotal;
		}
		for(Integer contentId:contentIds){
			Integer counts=getViewCount(contentId, viewCountType);
			if (counts != null) {
				contentViewsMap.put(contentId, counts);
			} else{
				contentViewsMap.put(contentId, 0);
			}
			json.put("contentViewsMap", contentViewsMap);
		}
		ResponseUtils.renderJson(response, json.toString());
	}

	@RequestMapping(value = "/content_up.jspx", method = RequestMethod.GET)
	public void contentUp(Integer contentId, HttpServletRequest request,
			HttpServletResponse response) throws JSONException {
		if (contentId == null) {
			ResponseUtils.renderJson(response, "false");
		} else {
			contentCountMng.contentUp(contentId);
			ResponseUtils.renderJson(response, "true");
		}
	}

	@RequestMapping(value = "/content_down.jspx", method = RequestMethod.GET)
	public void contentDown(Integer contentId, HttpServletRequest request,
			HttpServletResponse response) throws JSONException {
		if (contentId == null) {
			ResponseUtils.renderJson(response, "false");
		} else {
			contentCountMng.contentDown(contentId);
			ResponseUtils.renderJson(response, "true");
		}
	}
	
	private Integer getViewCount(Integer contentId,ContentViewCount viewCountType){
		Integer counts=0;
		ContentCount contentCount=contentCountMng.findById(contentId);
		if(viewCountType.equals(ContentViewCount.viewTotal)){
			counts= contentCount.getViews();
		}else if(viewCountType.equals(ContentViewCount.viewMonth)){
			counts=contentCount.getViewsMonth();
		}else if(viewCountType.equals(ContentViewCount.viewWeek)){
			counts=contentCount.getViewsWeek();
		}else if(viewCountType.equals(ContentViewCount.viewDay)){
			counts=contentCount.getViewsDay();
		}else{
			
		}
		return counts;
	}

	@Autowired
	private ContentCountCache contentCountCache;
	@Autowired
	private ChannelCountCache channelCountCache;
	@Autowired
	private ContentCountMng contentCountMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private CmsCatalogMng  cmsCatalogMng;
	@Autowired
	private ContentCatalogMng contentCatalogMng;
	@Autowired
	private CmsUserMng userMng;
}
