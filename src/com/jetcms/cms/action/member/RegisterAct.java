package com.jetcms.cms.action.member;

import static com.jetcms.cms.Constants.TPLDIR_COMMON;
import static com.jetcms.cms.Constants.TPLDIR_MEMBER;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.jetcms.cms.entity.assist.CmsWebservice;
import com.jetcms.cms.manager.assist.CmsWebserviceMng;
import com.jetcms.common.util.CaptchaUtil;
import com.jetcms.common.util.CommonUtil;
import com.jetcms.common.util.JedisUtils;
import com.jetcms.common.util.SendPhoneUtils;
import com.jetcms.common.web.CookieUtils;
import com.jetcms.common.web.RequestUtils;
import com.jetcms.common.web.ResponseUtils;
import com.jetcms.common.web.session.SessionProvider;
import com.jetcms.common.web.springmvc.MessageResolver;
import com.jetcms.core.entity.CmsConfig;
import com.jetcms.core.entity.CmsConfigItem;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsUserExt;
import com.jetcms.core.entity.MemberConfig;
import com.jetcms.core.entity.UnifiedUser;
import com.jetcms.core.manager.CmsConfigItemMng;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.manager.ConfigMng;
import com.jetcms.core.manager.UnifiedUserMng;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;
import com.jetcms.core.web.util.FrontUtils; 
 

/**
 * 前台会员注册Action
 */
@Controller
public class RegisterAct {
	private static final Logger log = LoggerFactory
			.getLogger(RegisterAct.class);

	public static final String REGISTER = "tpl.register";
	public static final String REGISTER_RESULT = "tpl.registerResult";
	public static final String REGISTER_ACTIVE_SUCCESS = "tpl.registerActiveSuccess";
	public static final String LOGIN_INPUT = "tpl.loginInput";
	/**
	 * 客户端返回JSON字符串
	 * @param response
	 * @param object
	 * @return
	 */
	protected String renderString(HttpServletResponse response, Object object) {
		return renderString(response, com.jetcms.common.util.JsonMapper.toJsonString(object), "application/json");
	}
	
	/**
	 * 客户端返回字符串
	 * @param response
	 * @param string
	 * @return
	 */
	protected String renderString(HttpServletResponse response, String string, String type) {
		try {
			response.reset();
	        response.setContentType(type);
	        response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	@RequestMapping(value = "/register.jspx", method = RequestMethod.GET)
	public String input(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		// 没有开启会员注册
		if (!mcfg.isRegisterOn()) {
			return FrontUtils.showMessage(request, model,
					"member.registerClose");
		}
		List<CmsConfigItem>items=cmsConfigItemMng.getList(site.getConfig().getId(), CmsConfigItem.CATEGORY_REGISTER);
		FrontUtils.frontData(request, model, site);
		String createUser = request.getParameter("createUser"); 
		if(StringUtils.isNotBlank(createUser)){
		    try {
				createUser = new String(createUser.getBytes("ISO-8859-1"),"utf-8");
				 
				CookieUtils.addCookie(request, response, "registers",
						  URLEncoder.encode(createUser, "utf-8"), Integer.MAX_VALUE, null);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
			 if(CookieUtils.getCookie(request, "registers")!=null){
				 createUser = URLDecoder.decode(CookieUtils.getCookie(request, "registers").getValue(),"utf-8");
			 }
				 
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		model.addAttribute("createUser", createUser);
		model.addAttribute("mcfg", mcfg);
		model.addAttribute("items", items);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, REGISTER);
	}
	 
	
	
 
	@RequestMapping(value = "/sendSms.jspx", method = RequestMethod.POST)
	public String sendSms(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username) throws IOException {
		Map< String, Object> map = new HashMap<String, Object>();
		try { 
			System.out.println( JedisUtils.get("register"+"-"+username));
			if(StringUtils.isBlank(username)){
				map.put("success", false);
				map.put("msg", "手机号不能为空!");
				return renderString(response, map);
			}
			if(!CommonUtil.isMobile(username)){
				map.put("success", false);
				map.put("msg", "手机号格式不正确!");
				return renderString(response, map);
			}
			String randomCode =  CaptchaUtil.generateVerityCode();
			StringBuffer buf=new StringBuffer("");
			 buf.append("register");	//注册
			 
			//生产六位随机数验证码
			SendPhoneUtils. sendPlatformMessage(username, "{'code':'"+randomCode+"','product':'嗑股明星'}","SMS_71810278");
			
			buf.append("-" + username);
			JedisUtils.set(buf.toString(), randomCode, 300);//失效时间单位：5分钟  
			System.out.println(buf.toString()+" : "+randomCode);
			map.put("success", true);
			return renderString(response, map);
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", "系统错误");
			return renderString(response, map);
		}
	}
	
	

	@RequestMapping(value = "/register.jspx", method = RequestMethod.POST)
	public String submit(String username, String email, String loginPassword,
			CmsUserExt userExt, String captcha, String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws IOException {
		CmsSite site = CmsUtils.getSite(request);
	 
		 
		CmsConfig config = site.getConfig();
		WebErrors errors = validateSubmit(username, email, loginPassword, captcha,
				site, request, response);
		boolean disabled=false;
		if(config.getMemberConfig().isCheckOn()){
			disabled=true;
		}
		if (errors.hasErrors()) { 
			FrontUtils.frontData(request, model, site);
			errors.toModel(model);
			List<String> lists = errors.getErrors();
			  String errorInfo ="";
			for (int i = 0; i < lists.size(); i++) {
				errorInfo+=lists.get(i).trim()+";";
			}
		  
			model.addAttribute("errorInfo", errorInfo);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_MEMBER, REGISTER);
		}
		String ip = RequestUtils.getIpAddr(request);
		Map<String,String>attrs=RequestUtils.getRequestMap(request, "attr_");
		CmsUser  createUser = null;
		String createUserName = request.getParameter("createUser");
		if(StringUtils.isNotBlank(createUserName)){
			   createUser =  cmsUserMng.findByUsername(createUserName);
		}
		if(createUser==null){
		    cmsUserMng.registerMember(username, email, loginPassword, ip, null,null,disabled,userExt,attrs);
		}else{
			cmsUserMng.registerMemberByCreate(createUser.getId(),username, email, loginPassword, ip, null,null,disabled,userExt,attrs);
		}
		cmsWebserviceMng.callWebService("false",username, loginPassword, email, userExt,CmsWebservice.SERVICE_TYPE_ADD_USER);
		log.info("member register success. username={}", username);
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		model.addAttribute("success", true);
		//保存密码
		//JedisUtils.set("password-"+username, loginPassword, 0);//失效时间单位：5分钟  
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, LOGIN_INPUT);
		 
	}

	@RequestMapping(value = "/active.jspx", method = RequestMethod.GET)
	public String active(String username, String key,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws IOException {
		CmsSite site = CmsUtils.getSite(request);
	    username = new String(request.getParameter("username").getBytes("iso8859-1"),"GBK");
		WebErrors errors = validateActive(username, key, request, response);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		unifiedUserMng.active(username, key);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, REGISTER_ACTIVE_SUCCESS);
	}

	@RequestMapping(value = "/username_unique.jspx")
	public void usernameUnique(HttpServletRequest request,
			HttpServletResponse response) {
		String username = RequestUtils.getQueryParam(request, "username");
		// 用户名为空，返回false。
		if (StringUtils.isBlank(username)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		// 保留字检查不通过，返回false。
		if (!config.getMemberConfig().checkUsernameReserved(username)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		// 用户名存在，返回false。
		if (unifiedUserMng.usernameExist(username)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		ResponseUtils.renderJson(response, "true");
	}

	@RequestMapping(value = "/username_enunique.jspx")
	public void usernameenUnique(HttpServletRequest request,
			HttpServletResponse response) {
		String username = RequestUtils.getQueryParam(request, "username");
		// 用户名为空，返回false。
		if (StringUtils.isBlank(username)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		// 保留字检查不通过，返回false。
		if (!config.getMemberConfig().checkUsernameReserved(username)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		// 用户名存在，返回false。
		if (unifiedUserMng.usernameExist(username)) {
			ResponseUtils.renderJson(response, "true");
			return;
		}
		ResponseUtils.renderJson(response, "false");
	}

	
	
	
	@RequestMapping(value = "/email_unique.jspx")
	public void emailUnique(HttpServletRequest request,
			HttpServletResponse response) {
		String email = RequestUtils.getQueryParam(request, "email");
		// email为空，返回false。
		if (StringUtils.isBlank(email)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		// email存在，返回false。
		if (unifiedUserMng.emailExist(email)) {
			ResponseUtils.renderJson(response, "false");
			return;
		}
		ResponseUtils.renderJson(response, "true");
	}

	private WebErrors validateSubmit(String username, String email,
			String loginPassword, String captcha, CmsSite site,
			HttpServletRequest request, HttpServletResponse response) {
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		WebErrors errors = WebErrors.create(request);
		//验证短信验证码
		try { 
			if(org.apache.commons.lang3.StringUtils.isBlank(captcha)){
				errors.addErrorCode("error.invalidCaptcha");
				return errors;
			}
			if(!JedisUtils.get("register"+"-"+username).equals(captcha)){
				errors.addErrorCode("error.invalidCaptcha");
				return errors;
			} 
		} catch (Exception e) {
			errors.addErrorCode("error.exceptionCaptcha");
			log.warn("", e);
			return errors;
		}
		if (errors.ifOutOfLength(username,MessageResolver.getMessage(request, "field.username"),
				mcfg.getUsernameMinLen(), 100)) {
			return errors;
		}
		if (errors.ifNotUsername(username,MessageResolver.getMessage(request, "field.username"),
				mcfg.getUsernameMinLen(), 100)) {
			return errors;
		}
		if (errors.ifOutOfLength(loginPassword, MessageResolver.getMessage(request, "field.password"),
				mcfg.getPasswordMinLen(), 100)) {
			return errors;
		}
	/*	if (errors.ifNotEmail(email, MessageResolver.getMessage(request, "field.email"), 100)) {
			return errors;
		}*/
		// 保留字检查不通过，返回false。
		if (!mcfg.checkUsernameReserved(username)) {
			errors.addErrorCode("error.usernameReserved");
			return errors;
		}
		// 用户名存在，返回false。
		if (unifiedUserMng.usernameExist(username)) {
			errors.addErrorCode("error.usernameExist");
			return errors;
		}
		return errors;
	}

	private WebErrors validateActive(String username, String activationCode,
			HttpServletRequest request, HttpServletResponse response) {
		WebErrors errors = WebErrors.create(request);
		if (StringUtils.isBlank(username)
				|| StringUtils.isBlank(activationCode)) {
			errors.addErrorCode("error.exceptionParams");
			return errors;
		}
		UnifiedUser user = unifiedUserMng.getByUsername(username);
		if (user == null) {
			errors.addErrorCode("error.usernameNotExist");
			return errors;
		}
		/*
		 * firefox访问链接二次访问，现简单不验证
		if (user.getActivation()
				|| StringUtils.isBlank(user.getActivationCode())) {
			errors.addErrorCode("error.usernameActivated");
			return errors;
		}
		if (!user.getActivationCode().equals(activationCode)) {
			errors.addErrorCode("error.exceptionActivationCode");
			return errors;
		}
		*/
		if (StringUtils.isNotBlank(user.getActivationCode())&&!user.getActivationCode().equals(activationCode)) {
			errors.addErrorCode("error.exceptionActivationCode");
			return errors;
		}
		return errors;
	}
	
	@Autowired
	private CmsWebserviceMng cmsWebserviceMng;
	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private SessionProvider session;
	/*@Autowired
	private ImageCaptchaService imageCaptchaService;*/
	@Autowired
	private ConfigMng configMng;
	@Autowired
	private CmsConfigItemMng cmsConfigItemMng;
}
