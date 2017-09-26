package com.jetcms.cms.action.member;

import static com.jetcms.cms.Constants.TPLDIR_MEMBER;
import static org.apache.shiro.web.filter.authc.FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME;

import java.io.IOException;
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

import com.jetcms.common.util.CaptchaUtil;
import com.jetcms.common.util.CommonUtil;
import com.jetcms.common.util.JedisUtils;
import com.jetcms.common.util.SendPhoneUtils;
import com.jetcms.common.web.RequestUtils;
import com.jetcms.common.web.session.SessionProvider;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.UnifiedUser;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.manager.ConfigMng;
import com.jetcms.core.manager.UnifiedUserMng;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;
import com.jetcms.core.web.util.FrontUtils;
import com.octo.captcha.service.CaptchaServiceException;

/**
 * 找回密码Action
 * 
 * 用户忘记密码后点击找回密码链接，输入用户名、邮箱和验证码<li>
 * 如果信息正确，返回一个提示页面，并发送一封找回密码的邮件，邮件包含一个链接及新密码，点击链接新密码即生效<li>
 * 如果输入错误或服务器邮箱等信息设置不完整，则给出提示信息<li>
 */
@Controller
public class ForgotPasswordAct {
	private static Logger log = LoggerFactory
			.getLogger(ForgotPasswordAct.class);

	public static final String FORGOT_PASSWORD_INPUT = "tpl.forgotPasswordInput";
	public static final String FORGOT_LOGIN_INPUT = "tpl.forgotLoginInput";
	public static final String FORGOT_PASSWORD_RESULT = "tpl.forgotPasswordResult";
	public static final String PASSWORD_RESET = "tpl.passwordReset";
	public static final String LOGIN_INPUT = "tpl.loginInput";
	/**
	 * 找回密码输入页
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/forgot_password.jspx", method = RequestMethod.GET)
	public String forgotPasswordInput(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, FORGOT_PASSWORD_INPUT);
	}

	  
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
	@RequestMapping(value = "/sendSmsBack.jspx", method = RequestMethod.POST)
	public String sendSmsBack(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username) throws IOException {
		Map< String, Object> map = new HashMap<String, Object>();
		try { 
		 
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
			
			UnifiedUser user = unifiedUserMng.getByUsername(username);
			if (user == null) {
				map.put("success", false);
				map.put("msg", "用户名不存在!");
				return renderString(response, map);
			}
			//生产六位随机数验证码
			String randomCode =  CaptchaUtil.generateVerityCode();
			StringBuffer buf=new StringBuffer("");
			 buf.append("back");	//密码找回
			 buf.append("-" + username);
			 JedisUtils.set(buf.toString(), randomCode, 300);//失效时间单位：5分钟  
			//发送验证码
			SendPhoneUtils. sendPlatformMessage(username, "{'code':'"+randomCode+"'}","SMS_71820382");
			
			 
			System.out.println(buf.toString()+" : "+randomCode);
			map.put("success", true);
			return renderString(response, map);
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", "系统错误");
			return renderString(response, map);
		}
	}
	
	@RequestMapping(value = "/sendSmsBacks.jspx", method = RequestMethod.POST)
	public String sendSmsBacks(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username) throws IOException {
		Map< String, Object> map = new HashMap<String, Object>();
		try { 
		 
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
			
			UnifiedUser user = unifiedUserMng.getByUsername(username);
			if (user == null) {
				map.put("success", false);
				map.put("msg", "用户名不存在!");
				return renderString(response, map);
			}
			//生产六位随机数验证码
			String randomCode =  CaptchaUtil.generateVerityCode();
			StringBuffer buf=new StringBuffer("");
			 buf.append("pass");	//密码找回
			 buf.append("-" + username);
			 JedisUtils.set(buf.toString(), randomCode, 300);//失效时间单位：5分钟  
			 
			//发送验证码
			SendPhoneUtils. sendPlatformMessage(username, "{'code':'"+randomCode+"'}","SMS_71820382"); 
		 
			 
			map.put("success", true);
			return renderString(response, map);
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", "系统错误");
			return renderString(response, map);
		}
	}
	
	
	/**
	 * 找回密码提交页
	 * 
	 * @param username
	 * @param email
	 * @param captcha
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/member/forgot_password.jspx", method = RequestMethod.POST)
	public String forgotPasswordSubmit(String email,
			String captcha, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		String username=RequestUtils.getQueryParam(request,"username");
		String password=RequestUtils.getQueryParam(request,"loginPassword");
		WebErrors errors = validateForgotPasswordSubmit(username, email,
				captcha, request, response);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		} 
		UnifiedUser user = unifiedUserMng.getByUsername(username);
	 
		model.addAttribute("user", user);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			// 用户名不存在
			model.addAttribute("status", 1);
		}    else {
			
			
			unifiedUserMng.update(user.getId(), password, null); 
				model.addAttribute("status", 0);
				CmsUser cmsUser = userMng.findByUsername(username);
				cmsUser.setDynamicPass(password);
				userMng.updateUser(cmsUser);
				//JedisUtils.set("password-"+username, password, 0);//失效时间单位：5分钟  
		 
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, LOGIN_INPUT);
	}

	@RequestMapping(value = "/member/password_reset.jspx", method = RequestMethod.GET)
	public String passwordReset(Integer uid, String key,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validatePasswordReset(uid, key, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		UnifiedUser user = unifiedUserMng.findById(uid);
		if (user == null) {
			// 用户不存在
			model.addAttribute("status", 1);
		} else if (StringUtils.isBlank(user.getResetKey())) {
			// resetKey不存在
			model.addAttribute("status", 2);
		} else if (!user.getResetKey().equals(key)) {
			// 重置key错误
			model.addAttribute("status", 3);
		} else {
			unifiedUserMng.resetPassword(uid);
			model.addAttribute("status", 0);
		}
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, PASSWORD_RESET);
	}

	private WebErrors validateForgotPasswordSubmit(String username,
			String email, String captcha, HttpServletRequest request,
			HttpServletResponse response) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifBlank(username, "username", 100)) {
			return errors;
		} 
		try { 
				if(org.apache.commons.lang3.StringUtils.isBlank(captcha)){
					errors.addErrorCode("error.invalidCaptcha");
					return errors;
				}
				if(org.apache.commons.lang3.StringUtils.isBlank(JedisUtils.get("back"+"-"+username))){
					errors.addErrorCode("error.invalidCaptcha");
					return errors;
				}
				if(!JedisUtils.get("back"+"-"+username).equals(captcha)){
					errors.addErrorCode("error.invalidCaptcha");
					return errors;
				}  
		} catch (CaptchaServiceException e) {
			errors.addErrorCode("error.exceptionCaptcha");
			log.warn("", e);
			return errors;
		}
		return errors;
	}

	private WebErrors validatePasswordReset(Integer uid, String key,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifNull(uid, "uid")) {
			return errors;
		}
		if (errors.ifBlank(key, "key", 50)) {
			return errors;
		}
		return errors;
	}

	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private ConfigMng configMng;
	@Autowired
	private SessionProvider session;
	
	@Autowired
	private CmsUserMng userMng;
/*	@Autowired
	private ImageCaptchaService imageCaptchaService;*/
}
