package com.jetcms.cms.action.directive;

import static com.jetcms.cms.Constants.TPL_STYLE_LIST;
import static com.jetcms.cms.Constants.TPL_SUFFIX;
import static com.jetcms.common.web.Constants.UTF8;
import static com.jetcms.common.web.freemarker.DirectiveUtils.OUT_LIST;
import static com.jetcms.core.web.util.FrontUtils.PARAM_STYLE_LIST;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jetcms.cms.action.directive.abs.AbstractChannelDirective;
import com.jetcms.cms.entity.main.Channel;
import com.jetcms.cms.entity.main.ChannelCatalog;
import com.jetcms.cms.entity.main.ContentCatalog;
import com.jetcms.common.web.freemarker.DefaultObjectWrapperBuilderFactory;
import com.jetcms.common.web.freemarker.DirectiveUtils;
import com.jetcms.common.web.freemarker.ParamsRequiredException;
import com.jetcms.common.web.freemarker.DirectiveUtils.InvokeType;
import com.jetcms.core.entity.CmsDictionary;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.web.util.FrontUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 栏目列表标签
 */
public class ChannelCatalogListDirective extends AbstractChannelDirective {
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "channel_list";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		Integer parentId = DirectiveUtils.getInt(PARAM_PARENT_ID, params);
		Integer siteId = DirectiveUtils.getInt(PARAM_SITE_ID, params);
		boolean hasContentOnly = getHasContentOnly(params);

		List<Channel> list;
		if (parentId != null) {
			list = channelMng.getChildListForTag(parentId, hasContentOnly);
		} else {
			if (siteId == null) {
				siteId = site.getId();
			}
			list = channelMng.getTopListForTag(siteId, hasContentOnly);
		}
	    Integer showType = getShowType(params);
	    List<ChannelCatalog> channelCatalogs = new ArrayList<ChannelCatalog>();
	    List<ChannelCatalog> channelCatalogss = new ArrayList<ChannelCatalog>();
		if(showType!=null && showType==1){
			 for (int i = 0; i < list.size(); i++) {
				 if(list.get(i).getId().intValue()!=163){
				if(list.get(i).getChannelCatalog()!=null && list.get(i).getChannelCatalog().size()>0){
					List<ChannelCatalog> channelCatalog = list.get(i).getChannelCatalog();
					for (int j = 0; j < channelCatalog.size(); j++) {
						channelCatalog.get(j).setChannel(list.get(i));
						channelCatalogs.add(channelCatalog.get(j));
					}
				}
				 }
			 }
			
			 Collections.sort(channelCatalogs,new Comparator<ChannelCatalog>() {  
				@Override
				public int compare(ChannelCatalog o1, ChannelCatalog o2) {
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");  
					Date o1time = null;
					Date o2time = null;
					try {
						  o1time=df.parse(o1.getStartTime());
						  o2time=df.parse(o2.getStartTime()); 
					} catch (ParseException e) { 
						e.printStackTrace();
					}  
					 if(o1time.getTime()>o2time.getTime()){
						 return 1;
					 }else{
						 return 0;
					 } 
				}
	         }); 
			boolean bool = true;
			 Date now = new Date();
			 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			 String strDate=sdf.format(now); 
			 for (int j = 0; j < channelCatalogs.size(); j++) {
				 CmsDictionary dic = cmsDictionaryMng.findByValue("course_category", channelCatalogs.get(j).getCourseCategory());
				 channelCatalogs.get(j).setCourseCategory(dic==null?"":dic.getName());
		    	 if(channelCatalogs.get(j).getIsFixed()==1 && !strDate.equals(channelCatalogs.get(j).getLectureDate())) {
		    		 continue;
		    	 }
		    	 Date startTime = null;
		    	 Date endTime = null;
				 try {
					  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(strDate+" "+channelCatalogs.get(j).getStartTime(), "yyyy-MM-dd HH:mm:ss");
					  endTime = org.apache.commons.lang3.time.DateUtils.parseDate(strDate+" "+channelCatalogs.get(j).getEndTime(), "yyyy-MM-dd HH:mm:ss");
				 } catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
				 
				 if(now.getTime()<startTime.getTime()){
					 if(bool){
							channelCatalogs.get(j).setShows(1);
							bool= false;
					 }
					channelCatalogs.get(j).setShowType(3);
				 }else{
					if(now.getTime()>=startTime.getTime() && now.getTime()<endTime.getTime()){
						channelCatalogs.get(j).setShowType(2);
						if(bool){
							channelCatalogs.get(j).setShows(1);
							bool= false;
						}
					}else{
						channelCatalogs.get(j).setShowType(1);
					}
				 }  
				 channelCatalogs.get(j).setLectureDate(strDate);
				 channelCatalogss.add(channelCatalogs.get(j));  
			 }  
		}  
		
		
		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_LIST, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(channelCatalogss));
		Map<String, TemplateModel> origMap = DirectiveUtils
				.addParamsToVariable(env, paramWrap);
		InvokeType type = DirectiveUtils.getInvokeType(params);
		String listStyle = DirectiveUtils.getString(PARAM_STYLE_LIST, params);
		if (InvokeType.sysDefined == type) {
			if (StringUtils.isBlank(listStyle)) {
				throw new ParamsRequiredException(PARAM_STYLE_LIST);
			}
			env.include(TPL_STYLE_LIST + listStyle + TPL_SUFFIX, UTF8, true);
		} else if (InvokeType.userDefined == type) {
			if (StringUtils.isBlank(listStyle)) {
				throw new ParamsRequiredException(PARAM_STYLE_LIST);
			}
			FrontUtils.includeTpl(TPL_STYLE_LIST, site, env);
		} else if (InvokeType.custom == type) {
			FrontUtils.includeTpl(TPL_NAME, site, params, env);
		} else if (InvokeType.body == type) {
			body.render(env.getOut());
		} else {
			throw new RuntimeException("invoke type not handled: " + type);
		}
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
	}
	public static void main(String[] args) {
		String ss = "http://test1.jetsum.net:80/lxwm.jhtml";
		ss = ss.replaceAll(":80/", "/");
		System.out.println(ss);
	}
}
