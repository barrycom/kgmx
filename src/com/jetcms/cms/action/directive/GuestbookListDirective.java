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
import com.jetcms.cms.entity.assist.CmsGuestbook;
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
public class GuestbookListDirective extends AbstractChannelDirective {
 
	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
	  
		List<CmsGuestbook> list = cmsGuestbookMng.getList(site.getId(),
				null, null, null, true, true, 1,10);

		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put("tag_list", DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(list));
		Map<String, TemplateModel> origMap = DirectiveUtils
				.addParamsToVariable(env, paramWrap);
			body.render(env.getOut());
		 
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap); 
	}
	 
}
