package com.jetcms.cms.action.admin.main;

import static com.jetcms.common.page.SimplePage.cpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jetcms.cms.entity.assist.CmsWebservice;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.web.CookieUtils;
import com.jetcms.common.web.RequestUtils;
import com.jetcms.core.entity.CmsDepartment;
import com.jetcms.core.entity.CmsGroup;
import com.jetcms.core.entity.CmsRole;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsUserExt;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;

/**
 * 帐号审核ACTION
 * 
 */
@Controller
public class CmsSHGlobalAct extends CmsAdminAbstract {
	private static final Logger log = LoggerFactory
			.getLogger(CmsSHGlobalAct.class);

	@RequiresPermissions("global_sh:v_list")
	@RequestMapping("/global_sh/v_list.do")
	public String list(String queryUsername, String queryEmail,
			Integer queryGroupId, Boolean queryDisabled, 
			String queryRealName,Integer queryDepartId,Integer queryRoleId,
			Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsUser currUser = CmsUtils.getUser(request);
		//获取所有审核待帐号
		queryDisabled = true;
		Pagination pagination = manager.getPageSH(queryUsername, queryEmail,
				null, queryGroupId, queryDisabled, true, currUser.getRank(),
				queryRealName,queryDepartId,queryRoleId,
				null,null,
				cpn(pageNo), CookieUtils.getPageSize(request));
		List<CmsRole> roleListAll = cmsRoleMng.getList();
		List<CmsRole> roleList = new ArrayList<CmsRole>();
		for (CmsRole cmsRole : roleListAll) {
			if("会员".equals(cmsRole.getName())||"代理".equals(cmsRole.getName())){
				roleList.add(cmsRole);
			}
		}
		List<CmsDepartment> departList=cmsDepartmentMng.getList(null,true);
		model.addAttribute("pagination", pagination);
		model.addAttribute("roleList", roleList);
		model.addAttribute("departList", departList);
		appendQueryParam(model, queryUsername, queryEmail, queryGroupId, 
				queryDisabled, queryRealName, queryDepartId, queryRoleId);
		model.addAttribute("groupList", cmsGroupMng.getList());
		return "admin/sh/list";
	} 

	@RequiresPermissions("global_sh:v_edit")
	@RequestMapping("/global_sh/v_edit.do")
	public String edit(Integer id, String queryUsername, String queryEmail,
			Integer queryGroupId, Boolean queryDisabled, 
			String queryRealName,Integer queryDepartId,Integer queryRoleId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser currUser = CmsUtils.getUser(request);
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser admin = manager.findById(id);

		List<CmsGroup> groupList = cmsGroupMng.getList();
		List<CmsSite> siteList = cmsSiteMng.getList();
		List<CmsRole> roleList = cmsRoleMng.getList();

		model.addAttribute("cmsAdmin", admin);
		model.addAttribute("site", site);
		model.addAttribute("sites", admin.getSites());
		model.addAttribute("roleIds", admin.getRoleIds());
		model.addAttribute("groupList", groupList);
		model.addAttribute("siteList", siteList);
		model.addAttribute("roleList", roleList);
		model.addAttribute("currRank", currUser.getRank());

		appendQueryParam(model, queryUsername, queryEmail, queryGroupId, 
				queryDisabled, queryRealName, queryDepartId, queryRoleId);
		return "admin/sh/edit";
	}
	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		// TODO 检查管理员rank
		return errors;
	}
	private boolean vldExist(Integer id, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsUser entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsUser.class, id)) {
			return true;
		}
		return false;
	}
	private void appendQueryParam(ModelMap model,String queryUsername, String queryEmail,
			Integer queryGroupId, Boolean queryDisabled, 
			String queryRealName,Integer queryDepartId,Integer queryRoleId
			){
		model.addAttribute("queryUsername", queryUsername);
		model.addAttribute("queryEmail", queryEmail);
		model.addAttribute("queryGroupId", queryGroupId);
		model.addAttribute("queryDisabled", queryDisabled);
		model.addAttribute("queryRealName", queryRealName);
		model.addAttribute("queryDepartId", queryDepartId);
		model.addAttribute("queryRoleId", queryRoleId);
	}
	@RequiresPermissions("global_sh:o_check")
	@RequestMapping("/global_sh/o_check.do")
	public String check(Integer[] ids,String queryRealName,Integer queryDepartId,Integer queryRoleId, Integer queryGroupId,
			Boolean queryDisabled, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		String queryUsername = RequestUtils.getQueryParam(request,
				"queryUsername");
		String queryEmail = RequestUtils.getQueryParam(request, "queryEmail");
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		for(Integer id:ids){
			CmsUser user=manager.findById(id);
			user.setDisabled(false);
			manager.updateUser(user);
			log.info("check CmsMember id={}", user.getId());
			cmsLogMng.operating(request, "cmsMember.log.delete", "id="
					+ user.getId() + ";username=" + user.getUsername());
		}
		return list(queryUsername, queryEmail, queryGroupId, queryDisabled,
				queryRealName,queryDepartId,queryRoleId,
				pageNo, request, model);
	}
	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, errors);
		}
		return errors;
	}
	@RequiresPermissions("global_sh:o_check")
	@RequestMapping("/global_sh/o_checkOne.do")
	public String update(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer departmentId, Integer[] roleIds, Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels,Boolean[] allControlChannels,
			String queryUsername, String queryEmail, Integer queryGroupId,
			Boolean queryDisabled, String queryRealName,Integer queryDepartId
			,Integer queryRoleId,
			Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		groupId = 1;
		roleIds = new Integer[]{4};
		WebErrors errors = validateUpdate(bean.getId(),bean.getRank(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser user=manager.findById(bean.getId());
		user.setDisabled(false);
		manager.updateUser(user);
		log.info("check CmsMember id={}", user.getId());
		cmsLogMng.operating(request, "cmsMember.log.delete", "id="
				+ user.getId() + ";username=" + user.getUsername());
		return list(queryUsername, queryEmail, queryGroupId, queryDisabled,queryRealName,
				queryDepartId,queryRoleId,
				pageNo, request, model);
	}
	private WebErrors validateUpdate(Integer id, Integer rank,HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		if (vldParams(id,rank, request, errors)) {
			return errors;
		}
		// TODO 检查管理员rank
		return errors;
	}
	private boolean vldParams(Integer id,Integer rank, HttpServletRequest request,
			WebErrors errors) {
		CmsUser user = CmsUtils.getUser(request);
		CmsUser entity = manager.findById(id);
		//提升等级大于当前登录用户
		if (rank > user.getRank()) {
			errors.addErrorCode("error.noPermissionToRaiseRank", id);
			return true;
		}
		//修改的用户等级大于当前登录用户 无权限
		if (entity.getRank() > user.getRank()) {
			errors.addErrorCode("error.noPermission", CmsUser.class, id);
			return true;
		}
		return false;
	}
}