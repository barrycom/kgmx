package com.jetcms.cms.action.directive;

import static com.jetcms.cms.Constants.TPL_STYLE_LIST;
import static com.jetcms.cms.Constants.TPL_SUFFIX;
import static com.jetcms.common.web.Constants.UTF8;
import static com.jetcms.common.web.freemarker.DirectiveUtils.OUT_LIST;
import static com.jetcms.core.web.util.FrontUtils.PARAM_STYLE_LIST;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jetcms.cms.action.directive.abs.AbstractContentDirective;
import com.jetcms.cms.entity.main.Content;
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
 * 内容列表标签
 */
public class CatalogListDirective extends AbstractContentDirective {
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "cms_catalog_live_list";

	/**
	 * 输入参数，文章ID。允许多个文章ID，用","分开。排斥其他所有筛选参数。
	 */
	public static final String PARAM_IDS = "ids";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		List<Content> list = getList(params, env);
		List<ContentCatalog> contentCatalogs = new ArrayList<ContentCatalog>();
		for (int i = 0; i < list.size(); i++) {
			List<ContentCatalog> catalogs = list.get(i).getCatalog();
		     for (int j = 0; j < catalogs.size(); j++) {
		    	 Date now = new Date();
		    	 Date startTime = null;
		    	 Date endTime = null;
				 try {
					  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(catalogs.get(j).getLectureDate()+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
					  endTime = org.apache.commons.lang3.time.DateUtils.parseDate(catalogs.get(j).getLectureDate()+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
				 } catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
				 if(startTime.getTime()<now.getTime() && now.getTime()<endTime.getTime()){
					 catalogs.get(j).setContent(list.get(i));
					 catalogs.get(j).setCourseCategory(cmsDictionaryMng.findByValue("course_category", catalogs.get(j).getCourseCategory()).getName());
					   
						try {
							  startTime =  org.apache.commons.lang3.time.DateUtils.parseDate(catalogs.get(j).getLectureDate()+" "+catalogs.get(j).getStartTime(), "yyyy-MM-dd HH:mm:ss");
							  endTime =  org.apache.commons.lang3.time.DateUtils.parseDate(catalogs.get(j).getLectureDate()+" "+catalogs.get(j).getEndTime(), "yyyy-MM-dd HH:mm:ss");
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(now.getTime()<startTime.getTime()){
							catalogs.get(j).setShowType(3);
						}else{
							if(now.getTime()>=startTime.getTime() && now.getTime()<endTime.getTime()){
								catalogs.get(j).setShowType(2);
							}else{
								catalogs.get(j).setShowType(1);
							}
						}  
					 contentCatalogs.add(catalogs.get(j));  
				 } 
			 } 
		}

		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_LIST, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(contentCatalogs));
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

	@SuppressWarnings("unchecked")
	protected List<Content> getList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		Integer[] ids = DirectiveUtils.getIntArray(PARAM_IDS, params);
		if (ids != null) {
			return contentMng.getListByIdsForTag(ids, getOrderBy(params));
		} else {
			return (List<Content>) super.getData(params, env);
		}
	}

	@Override
	protected boolean isPage() {
		return false;
	} 
}
