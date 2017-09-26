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
import java.util.Arrays;
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
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.web.util.FrontUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 栏目列表标签
 */
public class ChannelListDirective extends AbstractChannelDirective {
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "channel_list";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		Integer parentId = DirectiveUtils.getInt(PARAM_PARENT_ID, params);
		String showIds = DirectiveUtils.getString("showIds", params);
		Integer siteId = DirectiveUtils.getInt(PARAM_SITE_ID, params);
		boolean hasContentOnly = getHasContentOnly(params);

		List<Channel> list;
		if (parentId != null) {
			list = channelMng.getChildListForTag(parentId, hasContentOnly);
			
			if(StringUtils.isNotBlank(showIds)){ 
				   String[] channelIdarray = showIds.split(",");
				   List<String> lists = Arrays.asList(channelIdarray);
				   for (int i = 0; i < list.size(); i++) {
					   if(lists.contains(list.get(i).getId()+"")){
						   list.get(i).setShow(1);
					   }
				   }
				} 
		} else {
			if (siteId == null) {
				siteId = site.getId();
			}
			list = channelMng.getTopListForTag(siteId, hasContentOnly);
		}
		/*Integer showType = getShowType(params);
		if(showType!=null && showType==1){
			Collections.sort(list,new Comparator<Channel>() {  
				@Override
				public int compare(ChannelCatalog o1, ContentCatalog o2) {
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
		}*/
		

		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_LIST, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(list));
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
