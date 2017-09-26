package com.jetcms.cms.action.front;


import static com.jetcms.cms.Constants.TPLDIR_MESSAGE;
import static com.jetcms.cms.Constants.TPLDIR_SPECIAL;
import static com.jetcms.common.page.SimplePage.cpn;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.api.response.AlipayTradeQueryResponse; 
import com.jetcms.cms.entity.assist.CmsConfigContentCharge;
import com.jetcms.cms.entity.assist.CmsMessage;
import com.jetcms.cms.entity.assist.CmsReceiverMessage;
import com.jetcms.cms.entity.main.Channel;
import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.entity.main.ContentBuy;
import com.jetcms.cms.entity.main.ContentCatalog;
import com.jetcms.cms.entity.main.ContentCharge;
import com.jetcms.cms.manager.assist.CmsConfigContentChargeMng;
import com.jetcms.cms.manager.assist.CmsMessageMng;
import com.jetcms.cms.manager.assist.CmsReceiverMessageMng;
import com.jetcms.cms.manager.main.ContentBuyMng;
import com.jetcms.cms.manager.main.ContentChargeMng;
import com.jetcms.cms.manager.main.ContentMng;
import com.jetcms.common.util.PropertyUtils;
import com.jetcms.common.util.StrUtils;
import com.jetcms.common.util.WeixinPay;
import com.jetcms.common.web.Constants;
import com.jetcms.common.web.CookieUtils;
import com.jetcms.common.web.HttpClientUtil;
import com.jetcms.common.web.ResponseUtils;
import com.jetcms.common.web.session.SessionProvider;
import com.jetcms.common.web.springmvc.RealPathResolver;
import com.jetcms.core.entity.CmsCatalog;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsUserExt;
import com.jetcms.core.entity.MemberConfig;
import com.jetcms.core.manager.CmsCatalogMng;
import com.jetcms.core.manager.CmsUserAccountMng;
import com.jetcms.core.manager.CmsUserExtMng;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;
import com.jetcms.core.web.util.FrontUtils;

import freemarker.template.utility.DateUtil;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.jetcms.common.ipseek.Util;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.util.AliPay;
import com.jetcms.common.util.CommonUtil;
import com.jetcms.common.util.DateUtils;
import com.jetcms.common.util.Num62;
import com.jetcms.common.util.PayUtil;

@Controller
public class ContentOrderAct {
	//收费
	public static final Integer CONTENT_PAY_MODEL_CHARGE=1;
	//打赏
	public static final Integer CONTENT_PAY_MODEL_REWARD=2;
	public static final String WEIXIN_PAY_URL="weixin.pay.url";
	public static final String ALI_PAY_URL="alipay.openapi.url";
	public static final String MESSAGE_MNG = "tpl.messageMng";
	public static final String CONTENT_CHOOSE_SUCCESS="tpl.content.choosesuccess";
	public static final String CONTENT_REWARD="tpl.content.reward";
	public static final String CONTENT_CHOOSE_PAGE="tpl.content.choosePage";
	public static final String CONTENT_CHOOSE_PAYMENT="tpl.content.choosePayment";
	public static final String CONTENT_ALIPAY_MOBILE="tpl.content.alipay.mobile";
	public static final String CONTENT_ORDERS="tpl.content.orders";
	public static final String WEIXIN_AUTH_CODE_URL ="weixin.auth.getCodeUrl";
	
	
	//选择支付方式（先选择支付方式，在进行支付）
	@RequestMapping(value = "/content/choosePayment.jspx")
	public String choosePayment (Integer contentId,
			HttpServletRequest request,
			HttpServletResponse response,ModelMap model) throws JSONException {
		WebErrors errors=WebErrors.create(request);
		CmsUser user=CmsUtils.getUser(request);
		CmsSite site=CmsUtils.getSite(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}else{
			if(contentId==null){
				errors.addErrorCode("error.required","contentId");
				return FrontUtils.showError(request, response, model, errors);
			}else{
				Content content=contentMng.findById(contentId); 
				 
			    if(content!=null){
			  	    if(content.getChargeAmount()<=0){
			  	    	errors.addErrorCode("error.contentChargeAmountError");
			  	    	return FrontUtils.showError(request, response, model, errors);
			  	    }else{
			  	    	if(null == user.getCreateId() || user.getCreateId()==0){
							ContentCharge charge = content.getContentCharge();
							charge.setChargeAmount(Double.valueOf(content.getAttr().get("fwjg")));
							content.setContentCharge(charge);
						}
			  	    	String ua = ((HttpServletRequest) request).getHeader("user-agent")
					  	          .toLowerCase();
				  		boolean webCatBrowser=false;
				  		String wxopenid=null;
			  	        if (ua.indexOf("micromessenger") > 0) {
			  	        	// 是微信浏览器
			  	        	webCatBrowser=true;
			  	        	wxopenid=(String) session.getAttribute(request, "wxopenid");
			  	        }
			  	    	String orderNumber=System.currentTimeMillis()+RandomStringUtils.random(5,Num62.N10_CHARS);
			  	    	FrontUtils.frontData(request, model, site);
				  		model.addAttribute("contentId", contentId);
				  		model.addAttribute("orderNumber", orderNumber);
				  		model.addAttribute("content", content); 
				  		model.addAttribute("user", user); 
				  		model.addAttribute("discount",  Double.valueOf(content.getAttr().get("fwjg"))-content.getChargeAmount()); 
				  		return FrontUtils.getTplPath(request, site.getSolutionPath(),
								TPLDIR_SPECIAL, CONTENT_CHOOSE_PAYMENT);
			  	    }
			    }else{
			    	errors.addErrorCode("error.beanNotFound","content");
			    	return FrontUtils.showError(request, response, model, errors);
			    }
			}
		}
	}
	
	
	
	//选择支付方式（先选择支付方式，在进行支付）
		@RequestMapping(value = "/content/orderSuccess.jspx")
		public String orderSuccess (Integer contentId,
				HttpServletRequest request,
				HttpServletResponse response,ModelMap model) throws JSONException {
			WebErrors errors=WebErrors.create(request);
			CmsUser user=CmsUtils.getUser(request);
			CmsSite site=CmsUtils.getSite(request);
			if (user == null) {
				return FrontUtils.showLogin(request, model, site);
			}else{
				if(contentId==null){
					errors.addErrorCode("error.required","contentId");
					return FrontUtils.showError(request, response, model, errors);
				}else{
					Content content=contentMng.findById(contentId);
				    if(content!=null){
				  	    if(content.getChargeAmount()<=0){
				  	    	errors.addErrorCode("error.contentChargeAmountError");
				  	    	return FrontUtils.showError(request, response, model, errors);
				  	    }else{
				  	    	String ua = ((HttpServletRequest) request).getHeader("user-agent")
						  	          .toLowerCase();
					  		boolean webCatBrowser=false;
					  		String wxopenid=null;
				  	        if (ua.indexOf("micromessenger") > 0) {
				  	        	// 是微信浏览器
				  	        	webCatBrowser=true;
				  	        	wxopenid=(String) session.getAttribute(request, "wxopenid");
				  	        }
				  	    	String orderNumber=System.currentTimeMillis()+RandomStringUtils.random(5,Num62.N10_CHARS);
				  	    	FrontUtils.frontData(request, model, site);
				  	    	 
				  		   
				  			if( StringUtils.isNotBlank(orderNumber)){
				  			    ContentBuy b=contentBuyMng.findByOrderNumber(orderNumber);
				  			    //不能重复提交
				  			    if(b==null){
				  			    	 
				  					String[] objArray=new String[4];
				  					 
				  					Double rewardAmount=null;
				  				 
				  					Integer buyUserId=user.getId();
				  					Short chargeReward = ContentCharge.MODEL_REWARD;
				  				 
				  					 
				  					 
				  						rewardAmount=content.getChargeAmount();
				  					 
				  				 
				  						chargeReward=Short.valueOf("4");
				  					 
				  				    ContentBuy contentBuy=new ContentBuy();
				  				    if(contentId!=null){
				  				    	 
				  				   	    contentBuy.setAuthorUser(content.getUser());
				  				   	    //打赏可以匿名
				  				   	    
				  				   	    	contentBuy.setBuyUser(userMng.findById(buyUserId));
				  				   	   
				  				   	    contentBuy.setContent(content);
				  				   	    contentBuy.setHasPaidAuthor(false);
				  				   	    contentBuy.setOrderNumber(orderNumber);
				  				   	    contentBuy.setBuyTime(Calendar.getInstance().getTime());
				  				   	    Double chargeAmount=content.getChargeAmount();
				  				   	    Double platAmount=content.getChargeAmount();
				  				     	Double authorAmount=content.getChargeAmount();
				  				   	    if(rewardAmount!=null){
				  				   	    	chargeAmount=rewardAmount;
				  				   	    	platAmount=rewardAmount;
				  				   	    	authorAmount=rewardAmount;
				  				   	    }
				  				   	    if(chargeReward.equals(ContentCharge.MODEL_REWARD)){
				  				   	    	contentBuy.setChargeReward(ContentCharge.MODEL_REWARD);
				  				   	    }else{
				  				   	    	contentBuy.setChargeReward(ContentCharge.MODEL_CHARGE);
				  				   	    }
				  				   	    contentBuy.setChargeAmount(chargeAmount);
				  				   	    contentBuy.setPlatAmount(platAmount);
				  				   	    contentBuy.setAuthorAmount(authorAmount);
				  				 		// 这里是把微信商户的订单号放入了交易号中
				  			 			contentBuy.setOrderNumWeiXin("");
				  			 			contentBuy.setOrderNumAliPay(orderNumber);
				  			 			contentBuy=contentBuyMng.save(contentBuy);
				  			 			CmsUser authorUser=contentBuy.getAuthorUser();
				  			 			//笔者所得统计
				  			 			userAccountMng.userPay(contentBuy.getAuthorAmount(), authorUser);
				  			 			//平台所得统计
				  			 			configContentChargeMng.afterUserPay(contentBuy.getPlatAmount());
				  			 			//内容所得统计
				  			 			contentChargeMng.afterUserPay(contentBuy.getChargeAmount(), contentBuy.getContent());
				  				 	}
				  			    }
				  			}   
				  			model.addAttribute("orderTime",    (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) );
					  		model.addAttribute("contentId", contentId);
					  		model.addAttribute("orderNumber", orderNumber);
					  		model.addAttribute("content", content); 
					  		model.addAttribute("user", user); 
					  		model.addAttribute("discount",  Double.valueOf(content.getAttr().get("fwjg"))-content.getChargeAmount()); 
					  	 
					  		return FrontUtils.getTplPath(request, site.getSolutionPath(),
									TPLDIR_SPECIAL, CONTENT_CHOOSE_SUCCESS);
				  	    }
				    }else{
				    	errors.addErrorCode("error.beanNotFound","content");
				    	return FrontUtils.showError(request, response, model, errors);
				    }
				}
			}
		}
	
	
	
	
	//支付购买（先选择支付方式，在进行支付）
	@RequestMapping(value = "/content/chooseBuyPage.jspx")
	public String chooseBuyPage(Integer contentId,String realnames,
			HttpServletRequest request,
			HttpServletResponse response,ModelMap model) throws JSONException {
		WebErrors errors=WebErrors.create(request);
		CmsUser user=CmsUtils.getUser(request);
		CmsSite site=CmsUtils.getSite(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}else{
			if(contentId==null){
				errors.addErrorCode("error.required","contentId");
				return FrontUtils.showError(request, response, model, errors);
			}else{ 
				if(StringUtils.isNotBlank(realnames)){
		  			if(CommonUtil.isMessyCode(realnames)){
		  				try {
							realnames = new String(realnames.getBytes("ISO-8859-1"), "utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
		  			}
		  			CmsUserExt ext = user.getUserExt();
		  			ext.setRealname(realnames);  
					cmsUserExtMng.update(ext, user);
				}
				Content content=contentMng.findById(contentId);
			    if(content!=null){ 
			    	ContentCharge charge = content.getContentCharge();
			    	if(null == user.getCreateId() || user.getCreateId()==0){ 
						charge.setChargeAmount(Double.valueOf(content.getAttr().get("fwjg"))); 
					} 
			  	    if(content.getChargeAmount()<=0){
			  	    	errors.addErrorCode("error.contentChargeAmountError");
			  	    	return FrontUtils.showError(request, response, model, errors);
			  	    }else{
			  	    	String ua = ((HttpServletRequest) request).getHeader("user-agent")
					  	          .toLowerCase();
				  		boolean webCatBrowser=false;
				  		String wxopenid=null;
			  	        if (ua.indexOf("micromessenger") > 0) {
			  	        	// 是微信浏览器
			  	        	webCatBrowser=true;
			  	        	wxopenid=(String) session.getAttribute(request, "wxopenid");
			  	        }
			  	    	String orderNumber=System.currentTimeMillis()+RandomStringUtils.random(5,Num62.N10_CHARS);
			  	    	FrontUtils.frontData(request, model, site);
				  		model.addAttribute("contentId", contentId);
				  		model.addAttribute("orderNumber", orderNumber);
				  		model.addAttribute("content", content);
				  		model.addAttribute("charge", charge);
				  		model.addAttribute("type", ContentCharge.MODEL_FREE);
				  		model.addAttribute("webCatBrowser", webCatBrowser);
				  		model.addAttribute("wxopenid", wxopenid);
				  	 
				  		 
				  		return FrontUtils.getTplPath(request, site.getSolutionPath(),
								TPLDIR_SPECIAL, CONTENT_CHOOSE_PAGE);
			  	    }
			    }else{
			    	errors.addErrorCode("error.beanNotFound","content");
			    	return FrontUtils.showError(request, response, model, errors);
			    }
			}
		}
	}
	
	
	
	//支付购买（先选择支付方式，在进行支付）
	@RequestMapping(value = "/content/buy.jspx")
	public String contentBuy(Integer contentId,Integer payType,String orderNum,
			HttpServletRequest request,
			HttpServletResponse response,ModelMap model) throws JSONException {
		WebErrors errors=WebErrors.create(request);
		CmsUser user=CmsUtils.getUser(request);
		CmsSite site=CmsUtils.getSite(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}else{
			if(contentId==null){
				errors.addErrorCode("error.required","contentId");
				return FrontUtils.showError(request, response, model, errors);
			}else{
				Content content=contentMng.findById(contentId);
			    if(content!=null){
			    	 
			  	    if(content.getChargeAmount()<=0){
			  	    	errors.addErrorCode("error.contentChargeAmountError");
			  	    	return FrontUtils.showError(request, response, model, errors);
			  	    }else{
			  	     
			  	    	String ua = ((HttpServletRequest) request).getHeader("user-agent")
					  	          .toLowerCase();
				  		boolean webCatBrowser=false;
				  		String wxopenid=null;
			  	        if (ua.indexOf("micromessenger") > 0) {
			  	        	// 是微信浏览器
			  	        	webCatBrowser=true;
			  	        	wxopenid=(String) session.getAttribute(request, "wxopenid");
			  	        }
			  	    	String orderNumber=orderNum;
			  	    	
			  	    	if(payType==null ){
			  	    		 
				  	    	//生成订单
					  	  	Double totalAmount=content.getChargeAmount();
		  	    			if(null == user.getCreateId() || user.getCreateId()==0){ 
		  	    				totalAmount = Double.valueOf(content.getAttr().get("fwjg")); 
		  					} 
		  	    			ContentBuy contentBuy=new ContentBuy();
		  				   
	  				    	content=contentMng.findById(contentId);
	  				   	    contentBuy.setAuthorUser(content.getUser());
	  				   	   
	  				   	    contentBuy.setBuyUser(user); 
	  				   	    contentBuy.setContent(content);
	  				   	    contentBuy.setHasPaidAuthor(false);
	  				   	    contentBuy.setOrderNumber(orderNumber);
	  				   	    contentBuy.setBuyTime(Calendar.getInstance().getTime());
	  				   	    Double chargeAmount=totalAmount;
	  				   	     
	  				   	    contentBuy.setChargeAmount(chargeAmount);
	  				        contentBuy.setPlatAmount(Double.valueOf(0.0));
	  			   	        contentBuy.setAuthorAmount(Double.valueOf(0.0));
	  				 		// 这里是把微信商户的订单号放入了交易号中
	  			 			contentBuy.setOrderNumWeiXin("");
	  			 			contentBuy.setOrderNumAliPay("");
	  			 			contentBuy.setOrderSta((short) 0);
	  			 			contentBuy.setChargeReward((short) 1);
	  			 			if(contentBuyMng.findByOrderNumber(orderNumber)==null){
	  			 			   contentBuy=contentBuyMng.save(contentBuy);
	  			 			} 
			  	    	}else{
			  	    		orderNumber = orderNum; 
			  	    	}
			  	    	FrontUtils.frontData(request, model, site);
				  		model.addAttribute("contentId", contentId);
				  		model.addAttribute("orderNumber", orderNumber);
				  		model.addAttribute("content", content);
				  		model.addAttribute("type", ContentCharge.MODEL_REWARD);
				  		model.addAttribute("webCatBrowser", webCatBrowser);
				  		model.addAttribute("wxopenid", wxopenid);
				  		return FrontUtils.getTplPath(request, site.getSolutionPath(),
								TPLDIR_SPECIAL, CONTENT_REWARD);
			  	    }
			    }else{
			    	errors.addErrorCode("error.beanNotFound","content");
			    	return FrontUtils.showError(request, response, model, errors);
			    }
			}
		}
	}
	
	
	
	
	
	//打赏（先选择打赏金额，在选择支付方式）
	@RequestMapping(value = "/content/reward.jspx")
	public String contentReward(Integer contentId,String code,
			HttpServletRequest request,
			HttpServletResponse response,ModelMap model) throws JSONException {
		WebErrors errors=WebErrors.create(request);
		CmsSite site=CmsUtils.getSite(request);
		if(contentId==null){
			errors.addErrorCode("error.required","contentId");
			return FrontUtils.showError(request, response, model, errors);
		}else{
			Content content=contentMng.findById(contentId);
		    if(content!=null){
	  	    	String ua = ((HttpServletRequest) request).getHeader("user-agent")
			  	          .toLowerCase();
		  		boolean webCatBrowser=false;
		  		String wxopenid=null;
	  	        if (ua.indexOf("micromessenger") > 0) {
	  	        	// 是微信浏览器
	  	        	webCatBrowser=true;
	  	        	wxopenid=(String) session.getAttribute(request, "wxopenid");
	  	        }
	  	      
	  	        CmsConfigContentCharge config=configContentChargeMng.getDefault(); 
				Double max=config.getRewardMax();
				Double min=config.getRewardMin();
				List<Double>randomList=new ArrayList<Double>();
				Double s=1d;
				for(int i=0;i<6;i++){
					s=StrUtils.retainTwoDecimal(min + ((max - min) * new Random().nextDouble()));
					randomList.add(s);
				}
	  	    	String orderNumber=System.currentTimeMillis()+RandomStringUtils.random(5,Num62.N10_CHARS);
	  	    	FrontUtils.frontData(request, model, site);
	  			model.addAttribute("contentId", contentId);
		  		model.addAttribute("orderNumber", orderNumber);
		  		model.addAttribute("content", content);
		  		model.addAttribute("type", ContentCharge.MODEL_REWARD);
		  		model.addAttribute("webCatBrowser", webCatBrowser);
		  		model.addAttribute("wxopenid", wxopenid);
		  		model.addAttribute("randomList", randomList);
		  		model.addAttribute("randomOne", s);
		  		return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_SPECIAL, CONTENT_REWARD);
		    }else{
		    	errors.addErrorCode("error.beanNotFound","content");
		    	return FrontUtils.showError(request, response, model, errors);
		    }
		}
	}
	
	//内容购买或打赏记录
	@RequestMapping(value = "/content/orders.jspx")
	public String contentOrders(Integer contentId,Short type,Integer pageNo,
			HttpServletRequest request,HttpServletResponse response
			,ModelMap model) throws JSONException {
		WebErrors errors=WebErrors.create(request);
		CmsSite site=CmsUtils.getSite(request);
		if(type==null){
			type=ContentCharge.MODEL_REWARD;
		}
		if(contentId==null){
			errors.addErrorCode("error.required","contentId");
			return FrontUtils.showError(request, response, model, errors);
		}else{
			Content content=contentMng.findById(contentId);
		    if(content!=null){
	  	    	FrontUtils.frontData(request, model, site);
	  	    	Pagination pagination=contentBuyMng.getPageByContent(contentId,
	  	    			type, cpn(pageNo), CookieUtils.getPageSize(request));
		  		model.addAttribute("contentId", contentId);
		  		model.addAttribute("type", type);
		  		model.addAttribute("pagination", pagination);
		  		return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_SPECIAL, CONTENT_ORDERS);
		    }else{
		    	errors.addErrorCode("error.beanNotFound","content");
		    	return FrontUtils.showError(request, response, model, errors);
		    }
		}
	}
		
	@RequestMapping(value = "/reward/random.jspx")
	public void randomReward(HttpServletRequest request,
			HttpServletResponse response) {
		 CmsConfigContentCharge config=configContentChargeMng.getDefault(); 
		 Double max=config.getRewardMax();
		 Double min=config.getRewardMin();
	     Double s =StrUtils.retainTwoDecimal(min + ((max - min) * new Random().nextDouble()));
	     ResponseUtils.renderJson(response, s.toString());
	}
	
	/**
	 * 选择支付方式
	 * @param contentId 内容ID
	 * @param orderNumber 订单号
	 * @param payMethod 支付方式 1微信扫码 2支付宝即时支付  3微信浏览器打开[微信移动端] 4支付宝扫码5支付宝手机网页
	 * @param rewardAmount 金额
	 */
	@RequestMapping(value = "/content/selectPay.jspx")
	public String selectPay(Integer contentId,String orderNumber,
			Integer payMethod,Double rewardAmount,Short chargeReward,
			HttpServletRequest request,
			HttpServletResponse response,ModelMap model) throws JSONException {
		WebErrors errors=WebErrors.create(request);
		CmsUser user=CmsUtils.getUser(request);
		CmsSite site=CmsUtils.getSite(request);
		initWeiXinPayUrl();
		initAliPayUrl();
		if(contentId==null){
			errors.addErrorCode("error.required","contentId");
			return FrontUtils.showError(request, response, model, errors);
		}else{
			Content content=contentMng.findById(contentId);
		    if(content!=null){
		    	 
		    	//收费模式金额必须大于0
		    	if(content.getChargeModel().equals(ContentCharge.MODEL_CHARGE)
		    			&&content.getChargeAmount()<=0){
		  	    	errors.addErrorCode("error.contentChargeAmountError");
		  	    	return FrontUtils.showError(request, response, model, errors);
		  	    }else{
		  	    	
		  	    	CmsConfigContentCharge config=configContentChargeMng.getDefault();
		  			//收取模式（收费 和打赏）
		  	    	if(chargeReward==null){
		  	    		chargeReward=ContentCharge.MODEL_CHARGE;
		  	    	}
		  	    	if(user!=null){
		  	    		cache.put(new Element(orderNumber,
			  	    			contentId+","+user.getId()+","+rewardAmount+","+chargeReward));
		  	    	}else{
		  	    		cache.put(new Element(orderNumber,
			  	    			contentId+",,"+rewardAmount+","+chargeReward));
		  	    	}
  	    			Double totalAmount=content.getChargeAmount();
  	    			if(null == user.getCreateId() || user.getCreateId()==0){ 
  	    				totalAmount = Double.valueOf(content.getAttr().get("fwjg")); 
  					}
  	    			if(rewardAmount!=null){
  	    				totalAmount=rewardAmount;
  	    			}
		  	    	if(payMethod!=null){
		  	    		if(payMethod==1){
		  	    			return WeixinPay.enterWeiXinPay(getWeiXinPayUrl(),config,content,
									orderNumber,rewardAmount,request, response, model);
		  	    		}else if(payMethod==3){
		  	    			String openId=(String) session.getAttribute(request, "wxopenid");
		  	    			return WeixinPay.weixinPayByMobile(getWeiXinPayUrl(),config,
		  	    					openId,content, orderNumber, rewardAmount,
		  	    					request, response, model);
		  	    		}else if(payMethod==2){
		  	    			return AliPay.enterAliPayImmediate(config,orderNumber,content, rewardAmount,
									request, response, model);
		  	    		}else if(payMethod==4){
		  	    			/*return AliPay.enterAliPayImmediate(config,orderNumber,content, rewardAmount,
									request, response, model);*/
		  	    			 return AliPay.enterAlipayScanCode(request,response, model,
		  	    					getAliPayUrl(), config, content, 
		  	    					orderNumber, totalAmount); 
		  	    		}else if(payMethod==5){
		  	    			model.addAttribute("orderNumber",orderNumber);
		  					model.addAttribute("content", content);
		  					model.addAttribute("type", chargeReward);
		  					model.addAttribute("rewardAmount", rewardAmount);
		  	    			FrontUtils.frontData(request, model, site);
		  					return FrontUtils.getTplPath(request, site.getSolutionPath(),
		  							TPLDIR_SPECIAL, CONTENT_ALIPAY_MOBILE);
		  	    		}
					}//支付宝
					return AliPay.enterAliPayImmediate(config,orderNumber,content, rewardAmount,
							request, response, model);
		  	    }
		    }else{
		    	errors.addErrorCode("error.beanNotFound","content");
		    	return FrontUtils.showError(request, response, model, errors);
		    }
		}
	}
	
	@RequestMapping(value = "/content/alipayInMobile.jspx")
	public String enterAlipayInMobile(Integer contentId,String orderNumber,
			Double rewardAmount,HttpServletRequest request,
			HttpServletResponse response,ModelMap model) throws JSONException {
		WebErrors errors=WebErrors.create(request);
		initAliPayUrl();
		if(contentId==null){
			errors.addErrorCode("error.required","contentId");
			return FrontUtils.showError(request, response, model, errors);
		}else{
			Content content=contentMng.findById(contentId);
			CmsConfigContentCharge config=configContentChargeMng.getDefault();
			if(content!=null){
				Double totalAmount=content.getChargeAmount();
    			if(rewardAmount!=null){
    				totalAmount=rewardAmount;
    			}
				AliPay.enterAlipayInMobile(request, response,
						getAliPayUrl(), config, content, orderNumber, totalAmount);
				return "";
			}else{
		    	errors.addErrorCode("error.beanNotFound","content");
		    	return FrontUtils.showError(request, response, model, errors);
		    }
		}
	}
	
	
	/**
	 * 微信回调
	 * @param code
	 */
	@RequestMapping(value = "/order/payCallByWeiXin.jspx")
	public void orderPayCallByWeiXin(String orderNumber,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JDOMException, IOException, JSONException {
		JSONObject json = new JSONObject();
		CmsConfigContentCharge config=configContentChargeMng.getDefault();
		if (StringUtils.isNotBlank(orderNumber)) {
			ContentBuy order=contentBuyMng.findByOrderNumber(orderNumber);
			if (order!=null&&StringUtils.isNotBlank(order.getOrderNumWeiXin())) {
				//已成功支付过
				WeixinPay.noticeWeChatSuccess(getWeiXinPayUrl());
				json.put("status", 4);
			} else {
				//订单未成功支付
				json.put("status", 6);
			}
		}else{
			// 回调结果
			String xml_receive_result = PayUtil.getWeiXinResponse(request);
			if (StringUtils.isBlank(xml_receive_result)) {
				//检测到您可能没有进行扫码支付，请支付
				json.put("status", 5);
			} else {
				Map<String, String> result_map = PayUtil.parseXMLToMap(xml_receive_result);
				String sign_receive = result_map.get("sign");
				result_map.remove("sign");
				String key = config.getWeixinPassword();
				if (key == null) {
					//微信扫码支付密钥错误，请通知商户
					json.put("status", 1);
				}
				String checkSign = PayUtil.createSign(result_map, key);
				if (checkSign != null && checkSign.equals(sign_receive)) {
					try {
						if (result_map != null) {
							String return_code = result_map.get("return_code");
							if ("SUCCESS".equals(return_code)
									&& "SUCCESS".equals(result_map
											.get("result_code"))) {
								// 微信返回的微信订单号（属于微信商户管理平台的订单号，跟自己的系统订单号不一样）
								String transaction_id = result_map
										.get("transaction_id");
								// 商户系统的订单号，与请求一致。
								String out_trade_no = result_map.get("out_trade_no");
								// 通知微信该订单已处理
								WeixinPay.noticeWeChatSuccess(getWeiXinPayUrl());
								payAfter(out_trade_no,config.getChargeRatio(),transaction_id, null);
								//支付成功
								json.put("status", 0);
							} else if ("SUCCESS".equals(return_code)
									&& result_map.get("err_code") != null) {
								String message = result_map.get("err_code_des");
								json.put("status", 2);
								json.put("error", message);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Map<String, String> parames = new HashMap<String, String>();
					parames.put("return_code", "FAIL");
					parames.put("return_msg", "校验错误");
					// 将参数转成xml格式
					String xmlWeChat = PayUtil.assembParamToXml(parames);
					try {
						HttpClientUtil.post(getWeiXinPayUrl(), xmlWeChat, Constants.UTF8);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//支付参数错误，请重新支付!
					json.put("status", 3);
				}
			}
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	//支付宝即时支付回调地址
	@RequestMapping(value = "/order/payCallByAliPay.jspx")
	public String payCallByAliPay(HttpServletRequest request,
			HttpServletResponse response, ModelMap model)
					throws UnsupportedEncodingException {
		CmsConfigContentCharge config=configContentChargeMng.getDefault();
		CmsSite site=CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		//商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//支付宝交易号
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
		FrontUtils.frontData(request, model, site);
		if(PayUtil.verifyAliPay(params,config.getAlipayPartnerId(),config.getAlipayKey())){//验证成功
			if(trade_status.equals("TRADE_FINISHED")||trade_status.equals("TRADE_SUCCESS")){
				//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//如果有做过处理，不执行商户的业务程序
				Content content=payAfter(out_trade_no,config.getChargeRatio(), null, trade_no);
				
				CmsMessage message = new CmsMessage();
				// 发送端
				 ContentBuy b=contentBuyMng.findByOrderNumber(out_trade_no);
				 if(b!=null && b.getBuyUser()!=null){
						message.setMsgBox(1);
						  user = userMng.findById(2);
						message.setMsgSendUser(user);
						message.setMsgTitle("订单购买成功（"+b.getContent().getTitle()+"）");
						message.setMsgContent("您购买的课程《"+b.getContent().getTitle()+"》，支付费用："+b.getChargeAmount()+",订单编号："+out_trade_no);
						message.setMsgReceiverUser( b.getBuyUser());
						message.setMsgStatus(false);
						message.setSendTime(new Date());
						message.setSite(site);
						messageMng.save(message);
						CmsReceiverMessage receiverMessage = new CmsReceiverMessage();
						receiverMessage.setMsgBox(0);
						receiverMessage.setMsgContent(message.getMsgContent());
						receiverMessage.setMsgSendUser(user);
						receiverMessage.setMsgReceiverUser(b.getBuyUser());
						receiverMessage.setMsgStatus(false);
						receiverMessage.setMsgTitle(message.getMsgTitle());
						receiverMessage.setSendTime(new Date());
						receiverMessage.setSite(site);
						receiverMessage.setMessage(message);
						// 接收端（有一定冗余）
						receiverMessageMng.save(receiverMessage);
						
						Double total = b.getBuyUser().getChargeAmount();
						total +=b.getChargeAmount();
						b.getBuyUser().setChargeAmount(total);
						b.getBuyUser().setPay(true);
						
						
						Channel channel = content.getChannel();
						if(channel.getId().intValue()!=163){
									Integer mainContentId =  StringUtils.isBlank(channel.getAttr().get("yearClassId"))?0:Integer.valueOf(channel.getAttr().get("yearClassId"));
									//获取主课程
									Content mainContent = contentMng.findById(mainContentId);
									List<ContentCatalog> catalogs =  mainContent.getCatalog();
									
									Collections.sort(catalogs,new Comparator<ContentCatalog>() {  
										@Override
										public int compare(ContentCatalog o1, ContentCatalog o2) {
											SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
											Date o1time = null;
											Date o2time = null;
											try {
												if(StringUtils.isBlank(o1.getStartTime())){
													o1.setStartTime("00:00:00");
												}
												if(StringUtils.isBlank(o2.getStartTime())){
													o2.setStartTime("00:00:00");
												}
												o1time=df.parse(o1.getLectureDate() +" "+o1.getStartTime());
												o2time=df.parse(o2.getLectureDate() +" "+o2.getStartTime()); 
											} catch (ParseException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}  
											if(o1time.getTime()>o2time.getTime()){
											   return 1;
											}else{
											   return 0;
											} 
										}
							        }); 
									
									
								 
									List<ContentCatalog> contentCatalog = new ArrayList<ContentCatalog>(); 
									List<ContentCatalog> giveCatalog = new ArrayList<ContentCatalog>(); 
									//如果本内容是主课内容 
									if(content.getId().intValue() == mainContentId.intValue()){
										setLastClass(catalogs, contentCatalog);
										 
								  		Map<String, String> attrOrig = b.getBuyUser().getAttr();
										 
										 
								  		for (int i = 0; i < contentCatalog.size(); i++) {  
											CmsCatalog catalog = new CmsCatalog(); 
											attrOrig.put(contentCatalog.get(i).getId()+"", "0");
											catalog.setCatalogId(contentCatalog.get(i).getId());
											catalog.setUser(b.getBuyUser()); 
											catalog.setViewnum(0);
											catalog.setPriority(i+1);
											catalogMng.save(catalog);
										}
								  		b.getBuyUser().setAttr(attrOrig);
									}else{
									    //非主课，进行判断
										//课程类型（半年，季，月课）
										String classType = content.getAttr().get("classType");
										//课节数（按月来计算）
										String classCount = content.getAttr().get("classCount");
										int counts = 0;
										if(StringUtils.isNotBlank(classCount)){
											counts = Integer.valueOf(classCount);
										}
										
										setLastClass(catalogs, contentCatalog);
										if(contentCatalog!=null){
											if(counts>=contentCatalog.size()){
												giveCatalog = contentCatalog; 
												b.setIsGave((short) 0);
											}else{
												for (int i = 0; i < counts; i++) {
													giveCatalog.add(contentCatalog.get(i));
												}
												b.setIsGave((short) 1);
											} 
										} else{
											b.setIsGave((short) 0);
										}
										
									 
								  		Map<String, String> attrOrig = b.getBuyUser().getAttr();
										 
										 
								  		for (int i = 0; i < giveCatalog.size(); i++) {  
											CmsCatalog catalog = new CmsCatalog();
											 
											attrOrig.put(giveCatalog.get(i).getId()+"", "0");
											catalog.setCatalogId(giveCatalog.get(i).getId());
											catalog.setUser(b.getBuyUser()); 
											catalog.setViewnum(0);
											catalog.setPriority(i+1);
											catalogMng.save(catalog);
										}
								  		b.getBuyUser().setAttr(attrOrig);
										
										
									} 
						}
				  		userMng.updateUser(b.getBuyUser());
				 } 
				 
				model.addAttribute("box", 0);
				model.addAttribute("msg", "");
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_MESSAGE, MESSAGE_MNG);
					 
				    
				 
				//注意：TRADE_FINISHED
				//该种交易状态只在两种情况下出现
				//1、开通了普通即时到账，买家付款成功后。
				//2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
				//TRADE_SUCCESS
				//该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
			}
		}else{//验证失败
			return  FrontUtils.showMessage(request, model,"error.alipay.status.valifail");
		}
		return  FrontUtils.showMessage(request, model,"error.alipay.status.payfail");
	}
	
	
	public void setLastClass(List<ContentCatalog> catalogs,List<ContentCatalog> contentCatalog){
		Date now = new Date();
		Long newData = now.getTime()+900000;
		for (int i = 0; i < catalogs.size(); i++) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date o1time = null;
			if(StringUtils.isBlank(catalogs.get(i).getStartTime())){
				catalogs.get(i).setStartTime("00:00:00");
			}
			try {
				o1time=df.parse(catalogs.get(i).getLectureDate() +" "+catalogs.get(i).getStartTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(o1time.getTime()<newData){
				continue;
			}else{
				contentCatalog.add(catalogs.get(i));
			}
		}
	}
	
	//支付宝查询订单状态（扫码支付和手机网页支付均由此处理订单）
	@RequestMapping(value = "/content/orderQuery.jspx")
	public void aliPayOrderQuery(String orderNumber,HttpServletRequest request,
			HttpServletResponse response, ModelMap model)
					throws UnsupportedEncodingException {
		CmsConfigContentCharge config=configContentChargeMng.getDefault();
		JSONObject json = new JSONObject();
		CmsSite site=CmsUtils.getSite(request);
		initAliPayUrl();
		FrontUtils.frontData(request, model, site);
		AlipayTradeQueryResponse res=AliPay.query(getAliPayUrl(), config, orderNumber);
		try {
			if (null != res && res.isSuccess()) {
				if (res.getCode().equals("10000")) { 
					if ("TRADE_SUCCESS".equalsIgnoreCase(res
							.getTradeStatus())) {
							json.put("status", 0);
							payAfter(orderNumber, config.getChargeRatio(), null, res.getTradeNo()); 
					} else if ("WAIT_BUYER_PAY".equalsIgnoreCase(res
							.getTradeStatus())) {
						// 等待用户付款状态，需要轮询查询用户的付款结果
						json.put("status", 1);
					} else if ("TRADE_CLOSED".equalsIgnoreCase(res.getTradeStatus())) {
						// 表示未付款关闭，或已付款的订单全额退款后关闭
						json.put("status", 2);
					} else if ("TRADE_FINISHED".equalsIgnoreCase(res.getTradeStatus())) {
						// 此状态，订单不可退款或撤销
						json.put("status", 0);
					}
				} else {
					// 如果请求未成功，请重试
					json.put("status", 3);
				}
			}else{
				json.put("status", 4);
			}
		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	
	//支付宝查询订单状态 
	@RequestMapping(value = "/content/orderQuerys.jspx")
	public void aliPayOrderQuerys(String orderNumber,HttpServletRequest request,
			HttpServletResponse response, ModelMap model)
					throws UnsupportedEncodingException {
		CmsConfigContentCharge config=configContentChargeMng.getDefault();
		JSONObject json = new JSONObject();
		CmsSite site=CmsUtils.getSite(request);
		initAliPayUrl();
		FrontUtils.frontData(request, model, site);
		AlipayTradeQueryResponse res=AliPay.query(getAliPayUrl(), config, orderNumber);
		try {
			if (null != res && res.isSuccess()) {
				if (res.getCode().equals("10000")) {
					if ("TRADE_SUCCESS".equalsIgnoreCase(res
							.getTradeStatus())) {
							json.put("status", 0);
							payAfter(orderNumber, config.getChargeRatio(), null, res.getTradeNo());
							CmsMessage message = new CmsMessage();
							// 发送端
							 ContentBuy b=contentBuyMng.findByOrderNumber(orderNumber);
							 if(b.getOrderSta()==0){
								 if(b!=null && b.getBuyUser()!=null){
										message.setMsgBox(1);
										CmsUser user = userMng.findById(2);
										message.setMsgSendUser(user);
										message.setMsgTitle("订单购买成功（"+b.getContent().getTitle()+"）");
										message.setMsgContent("您购买的课程《"+b.getContent().getTitle()+"》，支付费用："+b.getChargeAmount()+",订单编号："+orderNumber);
										message.setMsgReceiverUser( b.getBuyUser());
										message.setMsgStatus(false);
										message.setSendTime(new Date());
										message.setSite(site);
										messageMng.save(message);
										CmsReceiverMessage receiverMessage = new CmsReceiverMessage();
										receiverMessage.setMsgBox(0);
										receiverMessage.setMsgContent(message.getMsgContent());
										receiverMessage.setMsgSendUser(user);
										receiverMessage.setMsgReceiverUser(b.getBuyUser());
										receiverMessage.setMsgStatus(false);
										receiverMessage.setMsgTitle(message.getMsgTitle());
										receiverMessage.setSendTime(new Date());
										receiverMessage.setSite(site);
										receiverMessage.setMessage(message);
										// 接收端（有一定冗余）
										receiverMessageMng.save(receiverMessage);
										
										Double total = b.getBuyUser().getChargeAmount();
										total +=b.getChargeAmount();
										b.getBuyUser().setChargeAmount(total);
										b.getBuyUser().setPay(true);
										   
										List<ContentCatalog> catalogs = b.getContent().getCatalog();
								  		Set<CmsUser> users = new HashSet<CmsUser>();
								  		users.add(b.getBuyUser());
								  		Map<String, String> attrOrig = b.getBuyUser().getAttr();
										 
										 
								  		for (int i = 0; i < catalogs.size(); i++) {  
											CmsCatalog catalog = new CmsCatalog();
											 
											attrOrig.put(catalogs.get(i).getId()+"", "0");
											catalog.setCatalogId(catalogs.get(i).getId());
											catalog.setUser(b.getBuyUser()); 
											catalog.setViewnum(0);
											catalog.setPriority(i+1);
											catalogMng.save(catalog);
										}
								  		b.getBuyUser().setAttr(attrOrig);
								  		userMng.updateUser(b.getBuyUser());
								 } 
							
							 }
					} else if ("WAIT_BUYER_PAY".equalsIgnoreCase(res
							.getTradeStatus())) {
						 
						json.put("status", 1);
					} else if ("TRADE_CLOSED".equalsIgnoreCase(res.getTradeStatus())) {
						// 表示未付款关闭，或已付款的订单全额退款后关闭
						json.put("status", 2);
					} else if ("TRADE_FINISHED".equalsIgnoreCase(res.getTradeStatus())) {
						// 此状态，订单不可退款或撤销
						json.put("status", 0);
					}
				} else {
					// 如果请求未成功，请重试
					json.put("status", 3);
				}
			}else{
				json.put("status", 4);
			}
		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	
	
	private Content payAfter(String orderNumber,Double ratio,
			String weixinOrderNum,
			String alipyOrderNum){
		Element e = cache.get(orderNumber);
	    Content content = new Content();
		if(e!=null&&StringUtils.isNotBlank(orderNumber)){
		    ContentBuy b=contentBuyMng.findByOrderNumber(orderNumber);
		    //不能重复提交
		    if(b==null){
		    	Object obj= e.getObjectValue();
				String[] objArray=new String[4];
				if(obj!=null){
					objArray=obj.toString().split(",");
				}
				Double rewardAmount=null;
				Integer contentId=null;
				Integer buyUserId=null;
				Short chargeReward = ContentCharge.MODEL_REWARD;
				if(objArray!=null&&objArray[0]!=null){
					contentId=Integer.parseInt(objArray[0]) ;
				}
				if(objArray!=null&&objArray[1]!=null&&StringUtils.isNotBlank(objArray[1])){
					buyUserId=Integer.parseInt(objArray[1]);
				}
				if(objArray!=null&&objArray[2]!=null&&StringUtils.isNotBlank(objArray[2])
						&&!objArray[2].toLowerCase().equals("null")){
					rewardAmount=Double.parseDouble(objArray[2]);
				}
				if(objArray!=null&&objArray[3]!=null){
					chargeReward=Short.valueOf(objArray[3]);
				}
			    ContentBuy contentBuy=new ContentBuy();
			    if(contentId!=null){
			    	content=contentMng.findById(contentId);
			   	    contentBuy.setAuthorUser(content.getUser());
			   	    //打赏可以匿名
			   	    if(buyUserId!=null){
			   	    	contentBuy.setBuyUser(userMng.findById(buyUserId));
			   	    }
			   	 CmsUser user= userMng.findById(buyUserId);
			   	    
			   	    contentBuy.setContent(content);
			   	    contentBuy.setHasPaidAuthor(false);
			   	    contentBuy.setOrderNumber(orderNumber);
			   	    contentBuy.setBuyTime(Calendar.getInstance().getTime());
			   	    Double chargeAmount=content.getChargeAmount();
			   	    Double platAmount=content.getChargeAmount()*ratio;
			     	Double authorAmount=content.getChargeAmount()*(1-ratio);
			   	    if(rewardAmount!=null){
			   	    	chargeAmount=rewardAmount;
			   	    	platAmount=rewardAmount*ratio;
			   	    	authorAmount=rewardAmount*(1-ratio);
			   	    }
			   	    if(chargeReward.equals(ContentCharge.MODEL_REWARD)){
			   	    	contentBuy.setChargeReward(ContentCharge.MODEL_REWARD);
			   	    }else{
			   	    	contentBuy.setChargeReward(ContentCharge.MODEL_CHARGE);
			   	    }
			    
			   	    if(user!=null){
				   	    if(null == user.getCreateId() || user.getCreateId()==0){ 
				   	    	chargeAmount = Double.valueOf(content.getAttr().get("fwjg"));  
				   	     
						}
			   	    }
			   	    
			   	    contentBuy.setChargeAmount(chargeAmount);
			   	    contentBuy.setPlatAmount(platAmount);
			   	    contentBuy.setAuthorAmount(authorAmount);
			 		// 这里是把微信商户的订单号放入了交易号中
		 			contentBuy.setOrderNumWeiXin(weixinOrderNum);
		 			contentBuy.setOrderNumAliPay(alipyOrderNum);
		 			contentBuy.setOrderSta((short) 1);
		 			contentBuy=contentBuyMng.save(contentBuy);
		 			contentBuy.setChargeAmount(content.getChargeAmount());
		 			CmsUser authorUser=contentBuy.getAuthorUser();
		 			//笔者所得统计
		 			userAccountMng.userPay(contentBuy.getAuthorAmount(), authorUser);
		 			//平台所得统计
		 			configContentChargeMng.afterUserPay(contentBuy.getPlatAmount());
		 		 
		 			//内容所得统计
		 			contentChargeMng.afterUserPay(contentBuy.getChargeAmount(), contentBuy.getContent());
			 	}
		    }else{//订单列表提交订单，支付完成
		    	Object obj= e.getObjectValue();
				String[] objArray=new String[4];
				if(obj!=null){
					objArray=obj.toString().split(",");
				}
				Double rewardAmount=null;
				Integer contentId=null;
				Integer buyUserId=null;
				Short chargeReward = ContentCharge.MODEL_REWARD;
				if(objArray!=null&&objArray[0]!=null){
					contentId=Integer.parseInt(objArray[0]) ;
				}
				if(objArray!=null&&objArray[1]!=null&&StringUtils.isNotBlank(objArray[1])){
					buyUserId=Integer.parseInt(objArray[1]);
				}
				if(objArray!=null&&objArray[2]!=null&&StringUtils.isNotBlank(objArray[2])
						&&!objArray[2].toLowerCase().equals("null")){
					rewardAmount=Double.parseDouble(objArray[2]);
				}
				if(objArray!=null&&objArray[3]!=null){
					chargeReward=Short.valueOf(objArray[3]);
				}
			    ContentBuy contentBuy=b;
			    if(contentId!=null){
			    	content=contentMng.findById(contentId);
			   	    contentBuy.setAuthorUser(content.getUser());
			   	    //打赏可以匿名
			   	    if(buyUserId!=null){
			   	    	contentBuy.setBuyUser(userMng.findById(buyUserId));
			   	    }
			   	    CmsUser user= userMng.findById(buyUserId);
			   	    
			   	    contentBuy.setContent(content);
			   	    contentBuy.setHasPaidAuthor(false);
			   	    contentBuy.setOrderNumber(orderNumber);
			   	    contentBuy.setBuyTime(Calendar.getInstance().getTime());
			   	    Double chargeAmount=content.getChargeAmount();
			   	    Double platAmount=content.getChargeAmount()*ratio;
			     	Double authorAmount=content.getChargeAmount()*(1-ratio);
			   	    if(rewardAmount!=null){
			   	    	chargeAmount=rewardAmount;
			   	    	platAmount=rewardAmount*ratio;
			   	    	authorAmount=rewardAmount*(1-ratio);
			   	    }
			   	    if(chargeReward.equals(ContentCharge.MODEL_REWARD)){
			   	    	contentBuy.setChargeReward(ContentCharge.MODEL_REWARD);
			   	    }else{
			   	    	contentBuy.setChargeReward(ContentCharge.MODEL_CHARGE);
			   	    }
			    
			 
			   	    contentBuy.setPlatAmount(platAmount);
			   	    contentBuy.setAuthorAmount(authorAmount);
			 		// 这里是把微信商户的订单号放入了交易号中
		 			contentBuy.setOrderNumWeiXin(weixinOrderNum);
		 			contentBuy.setOrderNumAliPay(alipyOrderNum);
		 			contentBuy.setOrderSta((short) 1);
		 			contentBuy=contentBuyMng.update(contentBuy);
		 		/*	contentBuy.setChargeAmount(content.getChargeAmount());*/
		 			CmsUser authorUser=contentBuy.getAuthorUser();
		 			//笔者所得统计
		 		 	userAccountMng.userPay(contentBuy.getAuthorAmount(), authorUser);
		 			//平台所得统计
		 			configContentChargeMng.afterUserPay(contentBuy.getPlatAmount());
		 			//内容所得统计
		 			contentChargeMng.afterUserPay(contentBuy.getChargeAmount(), contentBuy.getContent());
			 	} 
		    }
		}
	    return content;
	}
	
	private void initAliPayUrl(){
		if(getAliPayUrl()==null){
			setAliPayUrl(PropertyUtils.getPropertyValue(
					new File(realPathResolver.get(com.jetcms.cms.Constants.jetcms_CONFIG)),ALI_PAY_URL));
		}
	}
	
	private void initWeiXinPayUrl(){
		if(getWeiXinPayUrl()==null){
			setWeiXinPayUrl(PropertyUtils.getPropertyValue(
					new File(realPathResolver.get(com.jetcms.cms.Constants.jetcms_CONFIG)),WEIXIN_PAY_URL));
		}
	}
	
	private String weiXinPayUrl;
	
	private String aliPayUrl;
	private String weixinAuthCodeUrl;
	
	public String getWeiXinPayUrl() {
		return weiXinPayUrl;
	}

	public void setWeiXinPayUrl(String weiXinPayUrl) {
		this.weiXinPayUrl = weiXinPayUrl;
	}

	public String getAliPayUrl() {
		return aliPayUrl;
	}

	public void setAliPayUrl(String aliPayUrl) {
		this.aliPayUrl = aliPayUrl;
	}
	
	public String getWeixinAuthCodeUrl() {
		return weixinAuthCodeUrl;
	}

	public void setWeixinAuthCodeUrl(String weixinAuthCodeUrl) {
		this.weixinAuthCodeUrl = weixinAuthCodeUrl;
	}

	@Autowired
	private ContentMng contentMng;
	@Autowired
	private ContentChargeMng contentChargeMng;
	@Autowired
	private ContentBuyMng contentBuyMng;
	@Autowired
	private CmsConfigContentChargeMng configContentChargeMng;
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private CmsUserAccountMng userAccountMng;
	@Autowired
	private CmsUserMng userMng;
	@Autowired
	private SessionProvider session;
	@Autowired
	@Qualifier("contentOrderTemp")
	private Ehcache cache;
	@Autowired
	private CmsUserExtMng cmsUserExtMng;
	@Autowired
	private CmsMessageMng messageMng;
	@Autowired
	private CmsCatalogMng catalogMng;
	@Autowired
	private CmsReceiverMessageMng receiverMessageMng; 
}

