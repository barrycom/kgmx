package com.jetcms.cms.action.admin.main;

import static com.jetcms.cms.Constants.TPLDIR_MEMBER;
import static com.jetcms.common.page.SimplePage.cpn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jetcms.cms.entity.assist.CmsAcquisition;
import com.jetcms.cms.entity.assist.CmsWebservice;
import com.jetcms.cms.manager.main.ContentBuyMng;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.util.CaptchaUtil;
import com.jetcms.common.util.CommonUtil;
import com.jetcms.common.util.JedisUtils;
import com.jetcms.common.util.SendPhoneUtils;
import com.jetcms.common.web.CookieUtils;
import com.jetcms.common.web.RequestUtils;
import com.jetcms.common.web.ResponseUtils;
import com.jetcms.core.entity.CmsDepartment;
import com.jetcms.core.entity.CmsGroup;
import com.jetcms.core.entity.CmsRole;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsUserExt;
import com.jetcms.core.entity.MemberConfig;
import com.jetcms.core.entity.UnifiedUser;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;
import com.jetcms.core.web.util.FrontUtils;

/**
 * 公用用户统计ACTION
 * 
 */
@Controller
public class CmsGYYHTJGlobalAct extends CmsAdminAbstract {
	private static final Logger log = LoggerFactory
			.getLogger(CmsGYYHTJGlobalAct.class);
    //消费明细

	@RequiresPermissions("global_gyyhtj:v_list")
	@RequestMapping("/global_gyyhtj/order_list.do") 
	public String orderList(String orderNum,Integer userId, Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		CmsUser queryuser = manager.findById(userId);
		
		
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		/*if(user.getUserAccount()==null){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.userAccount.notfound");
			return FrontUtils.showError(request, response, model, errors);
		}*/
		Pagination pagination=contentBuyMng.getPage(orderNum, queryuser.getId(), null,
				null,cpn(pageNo), CookieUtils.getPageSize(request));
		model.addAttribute("pagination",pagination);
		model.addAttribute("queryuser",queryuser);
		return "admin/gyyhtj/order_list";
	}
	
	
	
	@RequiresPermissions("global_gyyhtj:v_list")
	@RequestMapping("/global_gyyhtj/v_list.do")
	public String list(String queryUsername, String querysyId,
			String queryhyId,String querydlId, Boolean queryDisabled, 
			String queryRealName,Integer queryDepartId,Integer queryRoleId,
			Integer pageNo,Boolean queryIsPay,Boolean issubordinate,
			HttpServletRequest request, ModelMap model) {
		
		
	 
		CmsUser currUser = CmsUtils.getUser(request);
		//获取省运的角色信息
		queryRoleId = 3;
		//省运人员
		List<CmsUser> syry = manager.getAdminsListByRoleId(queryRoleId);
		 
		List<CmsUser> hyry = new ArrayList<CmsUser>(); 
		
		List<CmsUser> dlry =new ArrayList<CmsUser>(); 
		
		
		Integer createId =null;
		if(StringUtils.isNotBlank(querydlId)){
			createId = Integer.valueOf(querydlId);
		}else{
			if(StringUtils.isNotBlank(queryhyId)){
				createId = Integer.valueOf(queryhyId);
				dlry = manager.getAdminsListByRoleIdAndCreateId(5, Integer.valueOf(querysyId));
			}else{
				if(StringUtils.isNotBlank(querysyId)){
					createId = Integer.valueOf(querysyId);
					hyry = manager.getAdminsListByRoleIdAndCreateId(4, Integer.valueOf(querysyId));
				} 
			}
		}
		if(StringUtils.isNotBlank(querysyId)){ 
			hyry = manager.getAdminsListByRoleIdAndCreateId(4, Integer.valueOf(querysyId));
		} 
		if(StringUtils.isNotBlank(queryhyId)){
			 
			dlry = manager.getAdminsListByRoleIdAndCreateId(5, Integer.valueOf(queryhyId));
		}
		if(createId!=null){
			CmsUser createUser = manager.findById(createId);
			if(createUser==null){
				createId = null;
			} 
		}
	 
		Pagination pagination = manager.getPagexxs(queryUsername, null,
				null, null, queryDisabled, false, null, null,
				null,null,null,null,cpn(pageNo),
				CookieUtils.getPageSize(request),createId,queryIsPay,false); 
		 
		List<CmsUser> cmsUsers = (List<CmsUser>) pagination.getList();
		for (int i = 0; i < cmsUsers.size(); i++) {
			if(cmsUsers.get(i).getCreateId()!=null){
				CmsUser createUser = manager.findById(cmsUsers.get(i).getCreateId());
				cmsUsers.get(i).setCreateby(createUser );
			}
		}
		pagination.setList(cmsUsers);
		List<CmsRole> roleList = cmsRoleMng.getList();
		List<CmsDepartment> departList=cmsDepartmentMng.getList(null,true);
		model.addAttribute("syry", syry);
		model.addAttribute("hyry", hyry);
		model.addAttribute("dlry", dlry);
		model.addAttribute("pagination", pagination);
		model.addAttribute("roleList", roleList);
		model.addAttribute("departList", departList);
		appendQueryParam(model, queryUsername, null, null, 
				queryDisabled, queryRealName, queryDepartId, queryRoleId,  querysyId,
				  queryhyId,  querydlId, queryIsPay,  issubordinate);
		model.addAttribute("groupList", cmsGroupMng.getList());
		return "admin/gyyhtj/list";
	} 
	@RequiresPermissions("global_gyyhtj:v_add")
	@RequestMapping("/global_gyyhtj/v_add.do")
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

	@RequiresPermissions("global_gyyhtj:v_edit")
	@RequestMapping("/global_gyyhtj/v_edit.do")
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
		model.addAttribute("cmsAdmin", admin); 
		/*appendQueryParam(model, queryUsername, queryEmail, queryGroupId, 
				queryDisabled, queryRealName, queryDepartId, queryRoleId, queryRealName, queryRealName, queryRealName);*/
		return "admin/gyyhtj/edit";
	}

	@RequiresPermissions("global_gyyhtj:o_save")
	@RequestMapping("/global_gyyhtj/o_save.do")
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


	@RequiresPermissions("global_gyyhtj:v_list")
	@RequestMapping("/global_gyyhtj/queryhy.do")
	public void queryhy(HttpServletRequest request, HttpServletResponse response, @RequestParam("querysyId") String querysyId) throws IOException {
		 
		JSONObject json = new JSONObject(); 
		 try{
		List<CmsUser> hyry = manager.getAdminsListByRoleIdAndCreateId(4, Integer.valueOf(querysyId));
		 
	    Map<String,String> map = new HashMap<String, String>();
	    for (int i = 0; i < hyry.size(); i++) {
	    	String realname = "";
	    	if(StringUtils.isNotBlank(hyry.get(i).getRealname())){
	    		realname="("+hyry.get(i).getRealname()+")";
	    	}
	    	map.put(""+hyry.get(i).getId(), hyry.get(i).getUsername()+realname);
		}
	    json.put("map", map);
		json.put("success", true);
		ResponseUtils.renderJson(response, json.toString());
		} catch (Exception e) {
			try {
				json.put("success", false);
				json.put("msg", "系统错误");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	@RequiresPermissions("global_gyyhtj:v_list")
	@RequestMapping("/global_gyyhtj/querydl.do")
	public void querydl(HttpServletRequest request, HttpServletResponse response, @RequestParam("querysyId") String querysyId) throws IOException {
	 
		JSONObject json = new JSONObject(); 
		 try{
		List<CmsUser> dlry = manager.getAdminsListByRoleIdAndCreateId(5, Integer.valueOf(querysyId));
		 
	    Map<String,String> map = new HashMap<String, String>();
	    for (int i = 0; i < dlry.size(); i++) {
	    	String realname = "";
	    	if(StringUtils.isNotBlank(dlry.get(i).getRealname())){
	    		realname="("+dlry.get(i).getRealname()+")";
	    	}
	    	map.put(""+dlry.get(i).getId(), dlry.get(i).getUsername()+realname);
		}
	    json.put("map", map);
		json.put("success", true);
		ResponseUtils.renderJson(response, json.toString());
		} catch (Exception e) {
			try {
				json.put("success", false);
				json.put("msg", "系统错误");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	@RequiresPermissions("global_gyyhtj:o_update")
	@RequestMapping("/global_gyyhtj/o_update.do")
	public String update(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer departmentId, Integer[] roleIds, Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels,Boolean[] allControlChannels,
			String queryUsername, String queryEmail, Integer queryGroupId,
			Boolean queryDisabled, String queryRealName,Integer queryDepartId
			,Integer queryRoleId,
			Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		CmsUser beans = manager.findById(bean.getId());
		beans.setCreateId(bean.getCreateId());
		try {
			manager.updateUser(beans);
		 
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		cmsWebserviceMng.callWebService("true",bean.getUsername(), password, null, ext,CmsWebservice.SERVICE_TYPE_UPDATE_USER);
		log.info("update CmsAdmin id={}.", bean.getId());
		cmsLogMng.operating(request, "cmsUser.log.update", "id=" + bean.getId()
				+ ";username=" + bean.getUsername());
		return    list(  queryUsername, null,
		    		  null,null, null, 
				  queryRealName,null,null,
				  pageNo,null,null,  request,  model); 
	}

	@RequiresPermissions("global_gyyhtj:o_delete")
	@RequestMapping("/global_gyyhtj/o_delete.do")
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
		return null;/*list(queryUsername, queryEmail, queryGroupId, queryDisabled,
				queryRealName,queryDepartId,queryRoleId,
				pageNo, request, model);*/
	}

	@RequiresPermissions("global_gyyhtj:v_channels_add")
	@RequestMapping(value = "/global_gyyhtj/v_channels_add.do")
	public String channelsAdd(Integer siteId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return channelsAddJson(siteId, request, response, model);
	}

	@RequiresPermissions("global_gyyhtj:v_channels_edit")
	@RequestMapping(value = "/global_gyyhtj/v_channels_edit.do")
	public String channelsEdit(Integer userId, Integer siteId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return channelsEditJson(userId, siteId, request, response, model);
	}

	@RequiresPermissions("global_gyyhtj:v_check_username")
	@RequestMapping(value = "/global_gyyhtj/v_check_username.do")
	public void checkUsername(HttpServletRequest request, HttpServletResponse response) {
		checkUserJson(request, response);
	}

	@RequiresPermissions("global_gyyhtj:v_check_email")
	@RequestMapping(value = "/global_gyyhtj/v_check_email.do")
	public void checkEmail(String email, HttpServletResponse response) {
		checkEmailJson(email, response);
	}
	
	@RequiresPermissions("global_gyyhtj:v_depart_add")
	@RequestMapping(value = "/global_gyyhtj/v_depart_add.do")
	public String departTree(String root, HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		return departAdd(root, request, response, model);
	}
	
	@RequiresPermissions("global_gyyhtj:v_site_edit")
	@RequestMapping(value = "/global_gyyhtj/v_site_edit.do")
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
	
	@RequiresPermissions("global_gyyhtj:v_site_add")
	@RequestMapping(value = "/global_gyyhtj/v_site_add.do")
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
			,String querysyId,
			String queryhyId,String querydlId,Boolean queryIsPay,Boolean issubordinate){
		model.addAttribute("queryUsername", queryUsername);
		model.addAttribute("queryEmail", queryEmail);
		model.addAttribute("queryGroupId", queryGroupId);
		model.addAttribute("queryDisabled", queryDisabled);
		model.addAttribute("queryRealName", queryRealName);
		model.addAttribute("queryDepartId", queryDepartId);
		model.addAttribute("queryRoleId", queryRoleId);
		model.addAttribute("querysyId", querysyId);
		model.addAttribute("queryhyId", queryhyId);
		model.addAttribute("querydlId", querydlId);
		model.addAttribute("queryIsPay", queryIsPay);
		model.addAttribute("issubordinate", issubordinate);
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
	@Autowired
	private ContentBuyMng contentBuyMng;
}