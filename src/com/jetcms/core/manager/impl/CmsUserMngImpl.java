package com.jetcms.core.manager.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




import com.jetcms.cms.entity.main.Channel;
import com.jetcms.cms.manager.main.ChannelMng;
import com.jetcms.cms.manager.main.ContentMng;
import com.jetcms.common.email.EmailSender;
import com.jetcms.common.email.MessageTemplate;
import com.jetcms.common.hibernate4.Updater;
import com.jetcms.common.page.Pagination;
import com.jetcms.core.dao.CmsUserDao;
import com.jetcms.core.entity.CmsDepartment;
import com.jetcms.core.entity.CmsGroup;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsUserExt;
import com.jetcms.core.entity.CmsWorkflowEvent;
import com.jetcms.core.entity.UnifiedUser;
import com.jetcms.core.manager.CmsDepartmentMng;
import com.jetcms.core.manager.CmsGroupMng;
import com.jetcms.core.manager.CmsRoleMng;
import com.jetcms.core.manager.CmsSiteMng;
import com.jetcms.core.manager.CmsUserExtMng;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.manager.CmsUserSiteMng;
import com.jetcms.core.manager.CmsWorkflowEventMng;
import com.jetcms.core.manager.UnifiedUserMng;

@Service
@Transactional
public class CmsUserMngImpl implements CmsUserMng {
	@Transactional(readOnly = true)
	public Pagination getPage(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank,
			String realName,Integer departId,Integer roleId,
			Boolean allChannel,Boolean allControlChannel,
			int pageNo, int pageSize) {
		Pagination page = dao.getPage(username, email, siteId, groupId,
				disabled, admin, rank,realName,departId,roleId, 
				allChannel,allControlChannel,pageNo, pageSize);
		return page;
	}
	//查看线下用户
	@Transactional(readOnly = true)
	public Pagination getPagexxs(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank,
			String realName,Integer departId,Integer roleId,
			Boolean allChannel,Boolean allControlChannel,
			int pageNo, int pageSize,Integer userId,Boolean isPay,Boolean issubordinate) {
		Pagination page = dao.getPagexx(username, email, siteId, groupId,
				disabled, admin, rank,realName,departId,roleId, 
				allChannel,allControlChannel,pageNo, pageSize,userId,  isPay,  issubordinate);
		return page;
	}
	@Override
	public Pagination getPagexx(String username, String email, Integer siteId, 
			Integer groupId, Boolean disabled, Boolean admin, Integer rank, 
			String realName, Integer departId, Integer roleId,
			Boolean allChannel, Boolean allControlChannel, int pageNo, int pageSize, int userId) {
		Pagination page = dao.getPagexx(username, email, siteId, groupId,
				disabled, admin, rank,realName,departId,roleId, 
				allChannel,allControlChannel,pageNo, pageSize,userId);
		return page;
	}
	@Override
	public Pagination getPagexx(Boolean queryIsPay,String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank,
			String realName,Integer departId,Integer roleId,
			Boolean allChannel,Boolean allControlChannel,
			int pageNo, int pageSize,int userId){
		Pagination page = dao.getPagexx(queryIsPay,username, email, siteId, groupId,
				disabled, admin, rank,realName,departId,roleId, 
				allChannel,allControlChannel,pageNo, pageSize,userId);
		return page;
		
	}
	
	
	
	@Transactional(readOnly = true)
	public Pagination getPageHY(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank,
			String realName,Integer departId,Integer roleId,
			Boolean allChannel,Boolean allControlChannel,
			int pageNo, int pageSize,Integer userId) {
		Pagination page = dao.getPageHY(username, email, siteId, groupId,
				disabled, admin, rank,realName,departId,roleId, 
				allChannel,allControlChannel,pageNo, pageSize,userId);
		return page;
	}
	@Transactional(readOnly = true)
	public Pagination getPageSH(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank,
			String realName,Integer departId,Integer roleId,
			Boolean allChannel,Boolean allControlChannel,
			int pageNo, int pageSize) {
		Pagination page = dao.getPageSH(username, email, siteId, groupId,
				disabled, admin, rank,realName,departId,roleId, 
				allChannel,allControlChannel,pageNo, pageSize);
		return page;
	}
	
	@Transactional(readOnly = true)
	public List<CmsUser> getList(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank) {
		List<CmsUser> list = dao.getList(username, email, siteId, groupId,
				disabled, admin, rank);
		return list;
	}

	@Transactional(readOnly = true)
	public List<CmsUser> getAdminList(Integer siteId, Boolean allChannel,
			Boolean disabled, Integer rank) {
		return dao.getAdminList(siteId, allChannel, disabled, rank);
	}
	
	@Transactional(readOnly = true)
	public Pagination getAdminsByDepartId(Integer id, int pageNo,int pageSize){
		return dao.getAdminsByDepartId(id, pageNo, pageSize);
	}
	
	@Transactional(readOnly = true)
	public Pagination getAdminsByRoleId(Integer roleId, int pageNo, int pageSize){
		return dao.getAdminsByRoleId(roleId, pageNo, pageSize);
	}
	
	public List<CmsUser> getAdminsListByRoleId(Integer roleId){
		return dao.getAdminsListByRoleId(roleId );
	}
	
	public List<CmsUser> getAdminsListByRoleIdAndCreateId(Integer roleId,Integer createId){
		return dao.getAdminsListByRoleIdAndCreateId(roleId,createId );
	}
	
	 
	@Transactional(readOnly = true)
	public List<CmsUser> getAdminsByDepartId(Integer roleId){
		return dao.getAdminsByDepartId(roleId);
	}
	

	@Transactional(readOnly = true)
	public CmsUser findById(Integer id) {
		CmsUser entity = dao.findById(id);
		return entity;
	}

	@Transactional(readOnly = true)
	public CmsUser findByUsername(String username) {
		CmsUser entity = dao.findByUsername(username);
		return entity;
	}
	 
			
			public CmsUser  registerMemberByCreate(Integer createId,String username, String email,
					String password, String ip, Integer groupId,Integer grain,boolean disabled,CmsUserExt userExt,Map<String,String>attr){
				UnifiedUser unifiedUser = unifiedUserMng.save(username, email,
						password, ip);
				CmsUser user = new CmsUser();
				user.setCreateId(createId);
				user.forMember(unifiedUser);
				user.setGrain(grain);
				user.setAttr(attr);
				user.setDisabled(disabled);
				user.setPay(false);
				user.setDynamicPass(password);
				user.setChargeAmount(Double.valueOf("0"));
				CmsGroup group = null;
				if (groupId != null) {
					group = cmsGroupMng.findById(groupId);
				} else {
					group = cmsGroupMng.getRegDef();
				}
				if (group == null) {
					throw new RuntimeException(
							"register default member group not found!");
				}
				user.setGroup(group);
				user.init();
				dao.save(user);
				cmsUserExtMng.save(userExt, user);
				return user;
			}
			
			
	public CmsUser registerMember(String username, String email,
			String password, String ip, Integer groupId,Integer grain,boolean disabled,CmsUserExt userExt,Map<String,String>attr){
		UnifiedUser unifiedUser = unifiedUserMng.save(username, email,
				password, ip);
		CmsUser user = new CmsUser();
		user.forMember(unifiedUser);
		user.setGrain(grain);
		user.setAttr(attr);
		user.setDisabled(disabled);
		user.setChargeAmount(Double.valueOf("0"));
		user.setPay(false);
		user.setDynamicPass(password);
		CmsGroup group = null;
		if (groupId != null) {
			group = cmsGroupMng.findById(groupId);
		} else {
			group = cmsGroupMng.getRegDef();
		}
		if (group == null) {
			throw new RuntimeException(
					"register default member group not found!");
		}
		user.setGroup(group);
		user.init();
		dao.save(user);
		cmsUserExtMng.save(userExt, user);
		return user;
	}

	
	public CmsUser registerMember(String username, String email,
			String password, String ip, Integer groupId, boolean disabled,CmsUserExt userExt,Map<String,String>attr,
			Boolean activation, EmailSender sender, MessageTemplate msgTpl)throws UnsupportedEncodingException, MessagingException{
		UnifiedUser unifiedUser = unifiedUserMng.save(username, email,
				password, ip, activation, sender, msgTpl);
		CmsUser user = new CmsUser();
		user.forMember(unifiedUser);
		user.setAttr(attr);
		user.setDisabled(disabled);
		CmsGroup group = null;
		if (groupId != null) {
			group = cmsGroupMng.findById(groupId);
		} else {
			group = cmsGroupMng.getRegDef();
		}
		if (group == null) {
			throw new RuntimeException(
					"register default member group not found!");
		}
		user.setGroup(group);
		user.setPay(false);
		user.setChargeAmount(Double.valueOf("0"));
		user.init();
		dao.save(user);
		cmsUserExtMng.save(userExt, user);
		return user;
	}

	public void updateLoginInfo(Integer userId, String ip,Date loginTime,String sessionId) {
		CmsUser user = findById(userId);
		if (user != null) {
			user.setLoginCount(user.getLoginCount() + 1);
			if(StringUtils.isNotBlank(ip)){
				user.setLastLoginIp(ip);
			}
			if(loginTime!=null){
				user.setLastLoginTime(loginTime);
			}
			user.setSessionId(sessionId);
		}
	}

	public void updateUploadSize(Integer userId, Integer size) {
		CmsUser user = findById(userId);
		user.setUploadTotal(user.getUploadTotal() + size);
		if (user.getUploadDate() != null) {
			if (CmsUser.isToday(user.getUploadDate())) {
				size += user.getUploadSize();
			}
		}
		user.setUploadDate(new java.sql.Date(System.currentTimeMillis()));
		user.setUploadSize(size);
	}
	
	public void updateUser(CmsUser user){
		Updater<CmsUser>updater=new Updater<CmsUser>(user);
		dao.updateByUpdater(updater);
	}

	public boolean isPasswordValid(Integer id, String password) {
		return unifiedUserMng.isPasswordValid(id, password);
	}

	public void updatePwdEmail(Integer id, String password, String email) {
		CmsUser user = findById(id);
		if (StringUtils.isNotBlank(email)) {
			user.setEmail(email);
		} 
		unifiedUserMng.update(id, password, email);
	}

	public CmsUser saveAdmin(String username, String email, String password,
			String ip, boolean viewOnly, boolean selfAdmin, int rank,
			Integer groupId,Integer departmentId, Integer[] roleIds,Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels, Boolean[] allControlChannels,
			CmsUserExt userExt) {
		UnifiedUser unifiedUser = unifiedUserMng.save(username, email,
				password, ip);
		CmsUser user = new CmsUser();
		user.forAdmin(unifiedUser, viewOnly, selfAdmin, rank);
		CmsGroup group = null;
		CmsDepartment department=null;
		if (groupId != null) {
			group = cmsGroupMng.findById(groupId);
		} else {
			group = cmsGroupMng.getRegDef();
		}
		if(departmentId!=null){
			department=cmsDepartmentMng.findById(departmentId);
		}
		if (group == null) {
			throw new RuntimeException(
					"register default member group not setted!");
		}
		/*
		if (department == null) {
			throw new RuntimeException(
					"register default admin department not setted!");
		}
		*/
		user.setPay(false);
		user.setChargeAmount(Double.valueOf("0"));
		user.setGroup(group);
		user.setDepartment(department);
		user.init();
		dao.save(user);
		cmsUserExtMng.save(userExt, user);
		if (roleIds != null) {
			for (Integer rid : roleIds) {
				user.addToRoles(cmsRoleMng.findById(rid));
			}
		}
		if (channelIds != null) {
			Channel channel;
			for (Integer cid : channelIds) {
				channel = channelMng.findById(cid);
				channel.addToUsers(user);
			}
		}
		if (siteIds != null) {
			CmsSite site;
			for (int i = 0, len = siteIds.length; i < len; i++) {
				site = cmsSiteMng.findById(siteIds[i]);
				cmsUserSiteMng.save(site, user, steps[i], allChannels[i],allControlChannels[i]);
			}
		}
		return user;
	} 
	
	
	public CmsUser saveAdmin(String username, String email, String password,
			String ip, boolean viewOnly, boolean selfAdmin, int rank,
			Integer groupId,Integer departmentId, Integer[] roleIds,Integer[] channelIds,Integer[] controlChannelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels, Boolean[] allControlChannels,
			CmsUserExt userExt) {
		UnifiedUser unifiedUser = unifiedUserMng.save(username, email,
				password, ip);
		CmsUser user = new CmsUser();
		user.forAdmin(unifiedUser, viewOnly, selfAdmin, rank);
		CmsGroup group = null;
		CmsDepartment department=null;
		if (groupId != null) {
			group = cmsGroupMng.findById(groupId);
		} else {
			group = cmsGroupMng.getRegDef();
		}
		if(departmentId!=null){
			department=cmsDepartmentMng.findById(departmentId);
		}
		if (group == null) {
			throw new RuntimeException(
					"register default member group not setted!");
		}
		/*
		if (department == null) {
			throw new RuntimeException(
					"register default admin department not setted!");
		}
		*/
		user.setGroup(group);
		user.setDepartment(department);
		user.setPay(false);
		user.setChargeAmount(Double.valueOf("0"));
		user.init();
		 
		dao.save(user);
		cmsUserExtMng.save(userExt, user);
		if (roleIds != null) {
			for (Integer rid : roleIds) {
				user.addToRoles(cmsRoleMng.findById(rid));
			}
		} 
		if (channelIds != null) {
			Channel channel;
			for (Integer cid : channelIds) {
				channel = channelMng.findById(cid);
				user.addToChannels(channel);
			}
		} 
		if (controlChannelIds != null) {
			Channel channel;
			for (Integer cid : controlChannelIds) {
				channel = channelMng.findById(cid);
				user.addToControlChannels(channel);
			}
		} 
		if (siteIds != null) {
			CmsSite site;
			for (int i = 0, len = siteIds.length; i < len; i++) {
				site = cmsSiteMng.findById(siteIds[i]);
				cmsUserSiteMng.save(site, user, steps[i],true,true);
			}
		}
		return user;
	}

	public CmsUser saveAdminHY(String username, String email, String password,
			String ip, boolean viewOnly, boolean selfAdmin, int rank,
			Integer groupId,Integer departmentId, Integer[] roleIds,Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels, Boolean[] allControlChannels,
			CmsUserExt userExt,Integer id) {
		UnifiedUser unifiedUser = unifiedUserMng.save(username, email,
				password, ip);
		CmsUser user = new CmsUser();
		user.forAdmin(unifiedUser, viewOnly, selfAdmin, rank);
		CmsGroup group = null;
		CmsDepartment department=null;
		if (groupId != null) {
			group = cmsGroupMng.findById(groupId);
		} else {
			group = cmsGroupMng.getRegDef();
		}
		if(departmentId!=null){
			department=cmsDepartmentMng.findById(departmentId);
		}
		if (group == null) {
			throw new RuntimeException(
					"register default member group not setted!");
		}
		/*
		if (department == null) {
			throw new RuntimeException(
					"register default admin department not setted!");
		}
		*/
		user.setGroup(group);
		user.setDepartment(department);
		user.setCreateId(id);
		user.init();
		if(roleIds[0]==6){
			user.setDisabled(false);
		}else{
			user.setDisabled(true);
		}
		user.setPay(false);
		user.setChargeAmount(Double.valueOf("0"));
		dao.save(user);
		cmsUserExtMng.save(userExt, user);
		if (roleIds != null) {
			for (Integer rid : roleIds) {
				user.addToRoles(cmsRoleMng.findById(rid));
			}
		}
		if (channelIds != null) {
			Channel channel;
			for (Integer cid : channelIds) {
				channel = channelMng.findById(cid);
				channel.addToUsers(user);
			}
		}
		if (siteIds != null) {
			CmsSite site;
			for (int i = 0, len = siteIds.length; i < len; i++) {
				site = cmsSiteMng.findById(siteIds[i]);
				cmsUserSiteMng.save(site, user, steps[i], allChannels[i],allControlChannels[i]);
			}
		}
		return user;
	}
	
	public void addSiteToUser(CmsUser user, CmsSite site, Byte checkStep) {
		cmsUserSiteMng.save(site, user, checkStep, true,true);
	}

	public CmsUser updateAdmin(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer departmentId, Integer[] roleIds, Integer[] channelIds,
			Integer siteId, Byte step, Boolean allChannel,Boolean allControlChannel) {
		CmsUser user = updateAdmin(bean, ext, password, groupId,departmentId, roleIds,
				channelIds);
		// 更新所属站点
		cmsUserSiteMng.updateByUser(user, siteId, step, allChannel,allControlChannel);
		return user;
	}

	public CmsUser updateAdmin(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer departmentId, Integer[] roleIds, Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels,Boolean[] allControlChannels) {
		CmsUser user = updateAdmin(bean, ext, password, groupId,departmentId,roleIds,channelIds);
		// 更新所属站点
		cmsUserSiteMng.updateByUser(user, siteIds, steps, allChannels,allControlChannels);
		return user;
	}

	private CmsUser updateAdmin(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer departmentId,Integer[] roleIds, Integer[] channelIds) {
		Updater<CmsUser> updater = new Updater<CmsUser>(bean);
		updater.include("email");
		CmsUser user = dao.updateByUpdater(updater);
		user.setGroup(cmsGroupMng.findById(groupId));
		if(departmentId!=null){
			user.setDepartment(cmsDepartmentMng.findById(departmentId));
		}
		cmsUserExtMng.update(ext, user);
		// 更新角色
		user.getRoles().clear();
		if (roleIds != null) {
			for (Integer rid : roleIds) {
				user.addToRoles(cmsRoleMng.findById(rid));
			}
		}
		/*
		// 更新栏目权限
		Set<Channel> channels = user.getChannels();
		// 清除
		for (Channel channel : channels) {
			channel.getUsers().remove(user);
		}
		user.getChannels().clear();
		// 添加
		if (channelIds != null) {
			Channel channel;
			for (Integer cid : channelIds) {
				channel = channelMng.findById(cid);
				channel.addToUsers(user);
			}
		}
		*/
		unifiedUserMng.update(bean.getId(), password, bean.getEmail());
		return user;
	}

	public CmsUser updateMember(Integer id, String email, String password,
			Boolean isDisabled, CmsUserExt ext, Integer groupId,Integer grain,Map<String,String>attr) {
		CmsUser entity = findById(id);
		entity.setEmail(email);
		/*
		if (!StringUtils.isBlank(email)) {
			entity.setEmail(email);
		}
		*/
		if (isDisabled != null) {
			entity.setDisabled(isDisabled);
		}
		if (groupId != null) {
			entity.setGroup(cmsGroupMng.findById(groupId));
		}
		if(grain!=null){
			entity.setGrain(grain);
		}
		// 更新属性表
		if (attr != null) {
			Map<String, String> attrOrig = entity.getAttr();
			attrOrig.clear();
			attrOrig.putAll(attr);
		}
		cmsUserExtMng.update(ext, entity);
		unifiedUserMng.update(id, password, email);
		return entity;
	}
	
	public CmsUser updateMember(Integer id, String email, String password,Integer groupId,String realname,String mobile,Boolean sex) {
		CmsUser entity = findById(id);
		CmsUserExt ext =entity.getUserExt();
		if (!StringUtils.isBlank(email)) {
			entity.setEmail(email);
		}
		if (groupId != null) {
			entity.setGroup(cmsGroupMng.findById(groupId));
		}
		if (!StringUtils.isBlank(realname)) {
			ext.setRealname(realname);
		}
		if (!StringUtils.isBlank(mobile)) {
			ext.setMobile(mobile);
		}
		if (sex!=null) {
			ext.setGender(sex);
		}
		cmsUserExtMng.update(ext, entity);
		unifiedUserMng.update(id, password, email);
		return entity;
	}
	
	public CmsUser updateUserConllection(CmsUser user,Integer cid,Integer operate){
		Updater<CmsUser> updater = new Updater<CmsUser>(user);
		user = dao.updateByUpdater(updater);
		if (operate.equals(1)) {
			user.addToCollection(contentMng.findById(cid));
		}// 取消收藏
		else if (operate.equals(0)) {
			user.delFromCollection(contentMng.findById(cid));
		}
		return user;
	}

	public CmsUser deleteById(Integer id) {
		//清除流程轨迹
		List<CmsWorkflowEvent>events=workflowEventMng.getListByUserId(id);
		for(CmsWorkflowEvent event:events){
			workflowEventMng.deleteById(event.getId());
		}
		unifiedUserMng.deleteById(id);
		CmsUser bean = dao.deleteById(id);
		//删除收藏信息
		bean.clearCollection();
		return bean;
	}

	public CmsUser[] deleteByIds(Integer[] ids) {
		CmsUser[] beans = new CmsUser[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public boolean usernameNotExist(String username) {
		return dao.countByUsername(username) <= 0;
	}
	
	public boolean usernameNotExistInMember(String username){
		return dao.countMemberByUsername(username)<= 0;
	}

	public boolean emailNotExist(String email) {
		return dao.countByEmail(email) <= 0;
	}

	private CmsUserSiteMng cmsUserSiteMng;
	private CmsSiteMng cmsSiteMng;
	private ChannelMng channelMng;
	private CmsRoleMng cmsRoleMng;
	private CmsDepartmentMng cmsDepartmentMng;
	private CmsGroupMng cmsGroupMng;
	private UnifiedUserMng unifiedUserMng;
	private CmsUserExtMng cmsUserExtMng;
	private CmsUserDao dao;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private CmsWorkflowEventMng workflowEventMng;

	@Autowired
	public void setCmsUserSiteMng(CmsUserSiteMng cmsUserSiteMng) {
		this.cmsUserSiteMng = cmsUserSiteMng;
	}

	@Autowired
	public void setCmsSiteMng(CmsSiteMng cmsSiteMng) {
		this.cmsSiteMng = cmsSiteMng;
	}

	@Autowired
	public void setChannelMng(ChannelMng channelMng) {
		this.channelMng = channelMng;
	}

	@Autowired
	public void setCmsRoleMng(CmsRoleMng cmsRoleMng) {
		this.cmsRoleMng = cmsRoleMng;
	}
	
	@Autowired
	public void setCmsDepartmentMng(CmsDepartmentMng cmsDepartmentMng) {
		this.cmsDepartmentMng = cmsDepartmentMng;
	}

	@Autowired
	public void setUnifiedUserMng(UnifiedUserMng unifiedUserMng) {
		this.unifiedUserMng = unifiedUserMng;
	}

	@Autowired
	public void setCmsUserExtMng(CmsUserExtMng cmsUserExtMng) {
		this.cmsUserExtMng = cmsUserExtMng;
	}

	@Autowired
	public void setCmsGroupMng(CmsGroupMng cmsGroupMng) {
		this.cmsGroupMng = cmsGroupMng;
	}

	@Autowired
	public void setDao(CmsUserDao dao) {
		this.dao = dao;
	}
/*	 public static void main(String[] args) {
		Integer i1 =null;
		Integer i2 =1000;
		System.out.println(i1.intValue()==i2.intValue());
		  i1 =1000;
		  i2 = 1000;
		System.out.println(i1==i2);
	}*/
}