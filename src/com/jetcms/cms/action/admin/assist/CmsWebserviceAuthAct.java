package com.jetcms.cms.action.admin.assist;

import static com.jetcms.common.page.SimplePage.cpn;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jetcms.cms.entity.assist.CmsWebserviceAuth;
import com.jetcms.cms.manager.assist.CmsWebserviceAuthMng;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.security.encoder.Md5PwdEncoder;
import com.jetcms.common.web.CookieUtils;

@Controller
public class CmsWebserviceAuthAct {
	private static final Logger log = LoggerFactory.getLogger(CmsWebserviceAuthAct.class);

	@RequiresPermissions("webserviceAuth:v_list")
	@RequestMapping("/webserviceAuth/v_list.do")
	public String list(Integer pageNo, HttpServletRequest request, ModelMap model) {
		Pagination pagination = manager.getPage(cpn(pageNo), CookieUtils
				.getPageSize(request));
		model.addAttribute("pagination",pagination);
		return "webserviceAuth/list";
	}

	@RequiresPermissions("webserviceAuth:v_add")
	@RequestMapping("/webserviceAuth/v_add.do")
	public String add(ModelMap model) {
		return "webserviceAuth/add";
	}

	@RequiresPermissions("webserviceAuth:v_edit")
	@RequestMapping("/webserviceAuth/v_edit.do")
	public String edit(Integer id, HttpServletRequest request, ModelMap model) {
		model.addAttribute("cmsWebserviceAuth", manager.findById(id));
		return "webserviceAuth/edit";
	}

	@RequiresPermissions("webserviceAuth:o_save")
	@RequestMapping("/webserviceAuth/o_save.do")
	public String save(CmsWebserviceAuth bean, HttpServletRequest request, ModelMap model) {
		bean.setPassword(pwdEncoder.encodePassword(bean.getPassword()));
		bean = manager.save(bean);
		log.info("save CmsWebserviceAuth id={}", bean.getId());
		return "redirect:v_list.do";
	}

	@RequiresPermissions("webserviceAuth:o_update")
	@RequestMapping("/webserviceAuth/o_update.do")
	public String update(Integer id,String username,String password,String system,Boolean enable, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		CmsWebserviceAuth bean = manager.update(id, username, password, system, enable);
		log.info("update CmsWebserviceAuth id={}.", bean.getId());
		return list(pageNo, request, model);
	}

	@RequiresPermissions("webserviceAuth:o_delete")
	@RequestMapping("/webserviceAuth/o_delete.do")
	public String delete(Integer[] ids, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		manager.deleteByIds(ids);
		return list(pageNo, request, model);
	}
	
	@Autowired
	private CmsWebserviceAuthMng manager;
	@Autowired
	private Md5PwdEncoder pwdEncoder;
}