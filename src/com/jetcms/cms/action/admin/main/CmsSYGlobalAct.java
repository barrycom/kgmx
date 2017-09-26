package com.jetcms.cms.action.admin.main;

import static com.jetcms.common.page.SimplePage.cpn;

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
 * 省运ACTION
 * 
 */
@Controller
public class CmsSYGlobalAct extends CmsAdminAbstract {
	private static final Logger log = LoggerFactory
			.getLogger(CmsSYGlobalAct.class);

	@RequiresPermissions("global_sy:v_list")
	@RequestMapping("/global_sy/v_list.do")
	public String list(String queryUsername, String queryEmail,
			Integer queryGroupId, Boolean queryDisabled, 
			String queryRealName,Integer queryDepartId,Integer queryRoleId,
			Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsUser currUser = CmsUtils.getUser(request);
		//获取省运的角色信息
		queryRoleId = 3;
		Pagination pagination = manager.getPage(queryUsername, queryEmail,
				null, queryGroupId, queryDisabled, true, currUser.getRank(),
				queryRealName,queryDepartId,queryRoleId,
				null,null,
				cpn(pageNo), CookieUtils.getPageSize(request));
		List<CmsRole> roleList = cmsRoleMng.getList();
		List<CmsDepartment> departList=cmsDepartmentMng.getList(null,true);
		model.addAttribute("pagination", pagination);
		model.addAttribute("roleList", roleList);
		model.addAttribute("departList", departList);
		appendQueryParam(model, queryUsername, queryEmail, queryGroupId, 
				queryDisabled, queryRealName, queryDepartId, queryRoleId);
		model.addAttribute("groupList", cmsGroupMng.getList());
		return "admin/sy/list";
	} 
	@RequiresPermissions("global_sy:v_add")
	@RequestMapping("/global_sy/v_add.do")
	public String add(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser currUser = CmsUtils.getUser(request);
		List<CmsGroup> groupList = cmsGroupMng.getList();
		List<CmsSite> siteList = cmsSiteMng.getList();
		List<CmsRole> roleList = cmsRoleMng.getList();
		model.addAttribute("site", site);
		model.addAttribute("groupList", groupList);
		model.addAttribute("siteList", siteList);
		model.addAttribute("roleList", roleList);
		model.addAttribute("currRank", currUser.getRank());
		return "admin/sy/add";
	}

	@RequiresPermissions("global_sy:v_edit")
	@RequestMapping("/global_sy/v_edit.do")
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
		return "admin/sy/edit";
	}

	@RequiresPermissions("global_sy:o_save")
	@RequestMapping("/global_sy/o_save.do")
	public String save(CmsUser bean, CmsUserExt ext, String username,
			String email, String password, Boolean selfAdmin, Integer rank, Integer groupId,Integer departmentId,
			Integer[] roleIds,Integer[] channelIds, Integer[] siteIds,
			Byte[] steps, Boolean[] allChannels,Boolean[] allControlChannels,
			HttpServletRequest request,ModelMap model) {
		groupId = 1;
		roleIds = new Integer[]{3};
		WebErrors errors = validateSave(bean, siteIds, steps, allChannels,
				request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		String ip = RequestUtils.getIpAddr(request);
		bean = manager.saveAdmin(username, email, password, ip, false,
				selfAdmin, rank, groupId,departmentId, roleIds, channelIds, siteIds, steps,
				allChannels, allControlChannels,ext);
		cmsWebserviceMng.callWebService("true",username, password, email, ext,CmsWebservice.SERVICE_TYPE_ADD_USER);
		log.info("save CmsAdmin id={}", bean.getId());
		cmsLogMng.operating(request, "cmsUser.log.save", "id=" + bean.getId()
				+ ";username=" + bean.getUsername());
		return "redirect:v_list.do";
	}

	@RequiresPermissions("global_sy:o_update")
	@RequestMapping("/global_sy/o_update.do")
	public String update(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer departmentId, Integer[] roleIds, Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels,Boolean[] allControlChannels,
			String queryUsername, String queryEmail, Integer queryGroupId,
			Boolean queryDisabled, String queryRealName,Integer queryDepartId
			,Integer queryRoleId,
			Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		groupId = 1;
		roleIds = new Integer[]{3};
		WebErrors errors = validateUpdate(bean.getId(),bean.getRank(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		try {
			bean = manager.updateAdmin(bean, ext, password, groupId,
					departmentId, roleIds, channelIds, siteIds, steps,
					allChannels, allControlChannels);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		cmsWebserviceMng.callWebService("true",bean.getUsername(), password, null, ext,CmsWebservice.SERVICE_TYPE_UPDATE_USER);
		log.info("update CmsAdmin id={}.", bean.getId());
		cmsLogMng.operating(request, "cmsUser.log.update", "id=" + bean.getId()
				+ ";username=" + bean.getUsername());
		return list(queryUsername, queryEmail, queryGroupId, queryDisabled,queryRealName,
				queryDepartId,queryRoleId,
				pageNo, request, model);
	}

	@RequiresPermissions("global_sy:o_delete")
	@RequestMapping("/global_sy/o_delete.do")
	public String delete(Integer[] ids, Integer queryGroupId,
			Boolean queryDisabled,String queryRealName,Integer queryDepartId,
			Integer queryRoleId,Boolean queryAllChannel,Boolean queryAllControlChannel,
			Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		String queryUsername = RequestUtils.getQueryParam(request,
				"queryUsername");
		String queryEmail = RequestUtils.getQueryParam(request, "queryEmail");
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser[] beans = manager.deleteByIds(ids);
		CmsUser user =CmsUtils.getUser(request);
		boolean deleteCurrentUser=false;
		for (CmsUser bean : beans) {
			Map<String,String>paramsValues=new HashMap<String, String>();
			paramsValues.put("username", bean.getUsername());
			paramsValues.put("admin", "true");
			cmsWebserviceMng.callWebService(CmsWebservice.SERVICE_TYPE_DELETE_USER, paramsValues);
			log.info("delete CmsAdmin id={}", bean.getId());
			if(user.getUsername().equals(bean.getUsername())){
				deleteCurrentUser=true;
			}else{
				cmsLogMng.operating(request, "cmsUser.log.delete", "id="
						+ bean.getId() + ";username=" + bean.getUsername());
			}
		}
		if(deleteCurrentUser){
			 Subject subject = SecurityUtils.getSubject();
			 subject.logout();
			 return "login";
		}
		return list(queryUsername, queryEmail, queryGroupId, queryDisabled,
				queryRealName,queryDepartId,queryRoleId,
				pageNo, request, model);
	}

	@RequiresPermissions("global_sy:v_channels_add")
	@RequestMapping(value = "/global_sy/v_channels_add.do")
	public String channelsAdd(Integer siteId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return channelsAddJson(siteId, request, response, model);
	}

	@RequiresPermissions("global_sy:v_channels_edit")
	@RequestMapping(value = "/global_sy/v_channels_edit.do")
	public String channelsEdit(Integer userId, Integer siteId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return channelsEditJson(userId, siteId, request, response, model);
	}

	@RequiresPermissions("global_sy:v_check_username")
	@RequestMapping(value = "/global_sy/v_check_username.do")
	public void checkUsername(HttpServletRequest request, HttpServletResponse response) {
		checkUserJson(request, response);
	}

	@RequiresPermissions("global_sy:v_check_email")
	@RequestMapping(value = "/global_sy/v_check_email.do")
	public void checkEmail(String email, HttpServletResponse response) {
		checkEmailJson(email, response);
	}
	
	@RequiresPermissions("global_sy:v_depart_add")
	@RequestMapping(value = "/global_sy/v_depart_add.do")
	public String departTree(String root, HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		return departAdd(root, request, response, model);
	}
	
	@RequiresPermissions("global_sy:v_site_edit")
	@RequestMapping(value = "/global_sy/v_site_edit.do")
	public String siteEditTree(Integer id,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		List<CmsSite> siteList= cmsSiteMng.getTopList();
		CmsUser admin = manager.findById(id);
		model.addAttribute("cmsAdmin", admin);
		model.addAttribute("siteIds", admin.getSiteIds());
		model.addAttribute("siteList", siteList);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		return "admin/sites_edit";
	}
	
	@RequiresPermissions("global_sy:v_site_add")
	@RequestMapping(value = "/global_sy/v_site_add.do")
	public String siteAddTree(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		List<CmsSite> siteList= cmsSiteMng.getTopList();
		model.addAttribute("siteList", siteList);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		return "admin/sites_add";
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

	private WebErrors validateSave(CmsUser bean, Integer[] siteIds,
			Byte[] steps, Boolean[] allChannels, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (siteIds != null) {
			if (steps == null) {
				errors.addError("steps cannot be null");
				return errors;
			}
			if (allChannels == null) {
				errors.addError("allChannels cannot be null");
				return errors;
			}
			if (siteIds.length != steps.length
					|| siteIds.length != allChannels.length) {
				errors.addError("siteIds length, steps length,"
						+ " allChannels length not equals");
				return errors;
			}
		}
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		// TODO 检查管理员rank
		return errors;
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

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, errors);
		}
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