package com.jetcms.cms.action.directive;

import static com.jetcms.cms.Constants.TPL_STYLE_LIST;
import static com.jetcms.cms.Constants.TPL_SUFFIX;
import static com.jetcms.common.web.Constants.UTF8;
import static com.jetcms.common.web.freemarker.DirectiveUtils.OUT_PAGINATION;
import static com.jetcms.core.web.util.FrontUtils.PARAM_STYLE_LIST;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jetcms.cms.action.directive.abs.AbstractChannelDirective;
import com.jetcms.common.page.Pagination;
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
 * 栏目分页标签
 */
public class ChannelPageDirective extends AbstractChannelDirective {
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "channel_page";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		Integer parentId = DirectiveUtils.getInt(PARAM_PARENT_ID, params);
		Integer siteId = DirectiveUtils.getInt(PARAM_SITE_ID, params);
		boolean hasContentOnly = getHasContentOnly(params);

		Pagination page;
		if (parentId != null) {
			page = channelMng.getChildPageForTag(parentId, hasContentOnly,
					FrontUtils.getPageNo(env), FrontUtils.getCount(params));
		} else {
			if (siteId == null) {
				siteId = site.getId();
			}
			page = channelMng.getTopPageForTag(siteId, hasContentOnly,
					FrontUtils.getPageNo(env), FrontUtils.getCount(params));
		}

		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_PAGINATION, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(page));
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
}
