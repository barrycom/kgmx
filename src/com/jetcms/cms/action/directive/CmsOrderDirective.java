package com.jetcms.cms.action.directive;

import static com.jetcms.common.web.freemarker.DirectiveUtils.OUT_LIST;
import static com.jetcms.common.web.freemarker.DirectiveUtils.OUT_PAGINATION;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.manager.assist.CmsAdvertisingMng;
import com.jetcms.cms.manager.main.ContentBuyMng;
import com.jetcms.cms.manager.main.ContentMng;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.web.freemarker.DefaultObjectWrapperBuilderFactory;
import com.jetcms.common.web.freemarker.DirectiveUtils;
import com.jetcms.core.web.util.FrontUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 广告对象标签
 */
public class CmsOrderDirective implements TemplateDirectiveModel {
	 
	/**
	 * 输入参数，内容ID。
	 */
	public static final String PARAM_ID = "id";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Integer id = DirectiveUtils.getInt(PARAM_ID, params);
 
		Content content=contentMng.findById(id);
		Pagination pagination;
	    if(content!=null){
  	     
  	    	  pagination=contentBuyMng.getPageByContent(content.getId(),
  	    			null, FrontUtils.getPageNo(env), FrontUtils.getCount(params)); 
	    }else{
	    	pagination = null;
	    }
		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params); 
		paramWrap.put(OUT_PAGINATION, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(pagination));
		paramWrap.put(OUT_LIST, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(pagination.getList()));
		Map<String, TemplateModel> origMap = DirectiveUtils
				.addParamsToVariable(env, paramWrap);
		body.render(env.getOut());
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
	}

	@Autowired
	private CmsAdvertisingMng cmsAdvertisingMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private ContentBuyMng contentBuyMng;
}
