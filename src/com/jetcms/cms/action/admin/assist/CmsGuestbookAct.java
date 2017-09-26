package com.jetcms.cms.action.admin.assist;

import static com.jetcms.common.page.SimplePage.cpn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jetcms.cms.entity.assist.CmsGuestbook;
import com.jetcms.cms.entity.assist.CmsGuestbookCtg;
import com.jetcms.cms.entity.assist.CmsGuestbookExt;
import com.jetcms.cms.manager.assist.CmsGuestbookCtgMng;
import com.jetcms.cms.manager.assist.CmsGuestbookMng;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.util.PropertiesLoader;
import com.jetcms.common.util.QuestionConfig;
import com.jetcms.common.web.CookieUtils;
import com.jetcms.common.web.RequestUtils;
import com.jetcms.core.entity.CmsDepartment;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.manager.CmsDepartmentMng;
import com.jetcms.core.manager.CmsLogMng;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;

@Controller
public class CmsGuestbookAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsGuestbookAct.class);

	@RequiresPermissions("guestbook:v_list")
	@RequestMapping("/guestbook/v_list.do")
	public String list(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user=CmsUtils.getUser(request);
		CmsDepartment userDepart=user.getDepartment();
		Integer[] ctgIds;
 
			List<CmsGuestbookCtg> list=cmsGuestbookCtgMng.getList(site.getId());
			model.addAttribute("ctgList",list);
			ctgIds=CmsGuestbookCtg.fetchIds(list);
	 
		Pagination pagination = manager.getPage(site.getId(),queryCtgId,ctgIds,null,
				queryRecommend, queryChecked, true, false, cpn(pageNo),
				CookieUtils.getPageSize(request));
		model.addAttribute("pagination", pagination);
		model.addAttribute("pageNo", pagination.getPageNo());
		model.addAttribute("queryCtgId", queryCtgId);
		model.addAttribute("queryRecommend", queryRecommend);
		model.addAttribute("queryChecked", queryChecked);
	    model.addAttribute("ctgList",cmsGuestbookCtgMng.getList(site.getId()));
		 
		return "guestbook/list";
	} 
		
	//生成评论
	@RequiresPermissions("guestbook:v_list")
	@RequestMapping("/guestbook/v_generate.do")
	public String generate(Integer queryCtgId, Integer counts, Boolean queryRecommend,
			Boolean queryChecked, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user=CmsUtils.getUser(request);
		CmsDepartment userDepart=user.getDepartment();
		Integer[] ctgIds;
		if(userDepart!=null){
			CmsDepartment depart= departmentMng.findById(userDepart.getId());
			model.addAttribute("ctgList",depart.getGuestBookCtgs());
			ctgIds=CmsGuestbookCtg.fetchIds(depart.getGuestBookCtgs());
		}else{
			List<CmsGuestbookCtg> list=cmsGuestbookCtgMng.getList(site.getId());
			model.addAttribute("ctgList",list);
			ctgIds=CmsGuestbookCtg.fetchIds(list);
		}
		Pagination pagination = manager.getPage(site.getId(),queryCtgId,ctgIds,null,
				queryRecommend, queryChecked, true, false, cpn(pageNo),
				CookieUtils.getPageSize(request));
		model.addAttribute("pagination", pagination);
		model.addAttribute("pageNo", pagination.getPageNo());
		model.addAttribute("queryCtgId", queryCtgId);
		model.addAttribute("queryRecommend", queryRecommend);
		model.addAttribute("queryChecked", queryChecked);
		java.util.Random random=new java.util.Random();// 定义随机类
		String ip = RequestUtils.getIpAddr(request);
		CmsGuestbookCtg ctg = cmsGuestbookCtgMng.findById(queryCtgId);
		List<CmsUser> list = cmsUserMng.getList(null, null, null, null,null, false, null);
		 Properties prop = new Properties();   
		    //InputStream in = Object.class.getResourceAsStream("/test.properties");
		    // 文件在src下 
		    //InputStream in = TestProperties.class.getClassLoader().getResourceAsStream("jdbc.properties");
		    // 文件在src下 
		  //  InputStream in = CmsGuestbookAct.class.getResourceAsStream("/u/cms/www/question.properties");
		 
		    File file = new File(request.getRealPath("/")+"/u/cms/www/question.txt");
		    InputStream in =null;
			try {
				in = new FileInputStream(file);
				prop.load(in); 
				in.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (IOException e) {   
			      e.printStackTrace();   
			  }  
		   

		for (int i = 0; i < counts; i++) {
			int result1=random.nextInt(list.size());
			int result=random.nextInt(9)+1;
			 
			String q = "question"+result;
			String a = "answer"+result;
			try {
				q = new String(prop.getProperty(q).trim().getBytes("ISO-8859-1"), "utf-8");
				a = new String(prop.getProperty(a).trim().getBytes("ISO-8859-1"), "utf-8"); 
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			 
			 CmsGuestbook bean = new CmsGuestbook();
			 CmsGuestbookExt ext = new CmsGuestbookExt();
	 
			 bean.setMember(list.get(result1));
			 bean.setCreateTime(new Date());
			 Calendar   calendar   =   new   GregorianCalendar(); 
		     calendar.setTime(new Date()); 
		     calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动   
		     bean.setAdmin(user);
			 bean.setReplayTime(calendar.getTime());
			 bean.setChecked(true);
			 bean.setRecommend(true);
			 bean.setSite(site);
			 ext.setGuestbook(bean);
			 ext.setContent(q);
			 ext.setReply(a);
			 bean = manager.save(bean, ext, queryCtgId, ip); 
		}
		 
		 
		
		
		if(userDepart!=null){
			model.addAttribute("ctgList",departmentMng.findById(userDepart.getId()).getGuestBookCtgs());
		}else{
			model.addAttribute("ctgList",cmsGuestbookCtgMng.getList(site.getId()));
		}
		return "guestbook/list";
	}	

	@RequiresPermissions("guestbook:v_add")
	@RequestMapping("/guestbook/v_add.do")
	public String add(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		List<CmsGuestbookCtg> ctgList = cmsGuestbookCtgMng
				.getList(site.getId());
		model.addAttribute("ctgList", ctgList);
		return "guestbook/add";
	}

	@RequiresPermissions("guestbook:v_edit")
	@RequestMapping("/guestbook/v_edit.do")
	public String edit(Integer id, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsGuestbook cmsGuestbook = manager.findById(id);
		List<CmsGuestbookCtg> ctgList = cmsGuestbookCtgMng
				.getList(site.getId());

		model.addAttribute("cmsGuestbook", cmsGuestbook);
		model.addAttribute("ctgList", ctgList);
		model.addAttribute("pageNo", pageNo);
		return "guestbook/edit";
	}

	@RequiresPermissions("guestbook:o_save")
	@RequestMapping("/guestbook/o_save.do")
	public String save(CmsGuestbook bean, CmsGuestbookExt ext, Integer ctgId,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateSave(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		String ip = RequestUtils.getIpAddr(request);
		bean = manager.save(bean, ext, ctgId, ip);
		log.info("save CmsGuestbook id={}", bean.getId());
		cmsLogMng.operating(request, "cmsGuestbook.log.save", "id="
				+ bean.getId() + ";title=" + bean.getTitle());
		return "redirect:v_list.do";
	}

	@RequiresPermissions("guestbook:o_update")
	@RequestMapping("/guestbook/o_update.do")
	public String update(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, String oldreply,CmsGuestbook bean, CmsGuestbookExt ext,
			Integer ctgId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateUpdate(bean.getId(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Date now=new Date();
		if(StringUtils.isNotBlank(ext.getReply())&&!oldreply.equals(ext.getReply())){
			bean.setReplayTime(now);
			if(bean.getAdmin()!=null){
				if(!bean.getAdmin().equals(CmsUtils.getUser(request))){
					bean.setAdmin(CmsUtils.getUser(request));
				}
			}else{
				bean.setAdmin(CmsUtils.getUser(request));
			}
		}
		bean = manager.update(bean, ext, ctgId);
		log.info("update CmsGuestbook id={}.", bean.getId());
		cmsLogMng.operating(request, "cmsGuestbook.log.update", "id="
				+ bean.getId() + ";title=" + bean.getTitle());
		return list(queryCtgId, queryRecommend, queryChecked, pageNo, request,
				model);
	}

	@RequiresPermissions("guestbook:o_delete")
	@RequestMapping("/guestbook/o_delete.do")
	public String delete(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsGuestbook[] beans = manager.deleteByIds(ids);
		for (CmsGuestbook bean : beans) {
			log.info("delete CmsGuestbook id={}", bean.getId());
			cmsLogMng.operating(request, "cmsGuestbook.log.delete", "id="
					+ bean.getId() + ";title=" + bean.getTitle());
		}
		return list(queryCtgId, queryRecommend, queryChecked, pageNo, request,
				model);
	}
	
	@RequiresPermissions("guestbook:o_check")
	@RequestMapping("/guestbook/o_check.do")
	public String check(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsGuestbook[] beans = manager.checkByIds(ids,CmsUtils.getUser(request),true);
		for (CmsGuestbook bean : beans) {
			log.info("delete CmsGuestbook id={}", bean.getId());
			cmsLogMng.operating(request, "cmsGuestbook.log.check", "id="
					+ bean.getId() + ";title=" + bean.getTitle());
		}
		return list(queryCtgId, queryRecommend, queryChecked, pageNo, request,
				model);
	}
	
	@RequiresPermissions("guestbook:o_check_cancel")
	@RequestMapping("/guestbook/o_check_cancel.do")
	public String cancel_check(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsGuestbook[] beans = manager.checkByIds(ids,CmsUtils.getUser(request),false);
		for (CmsGuestbook bean : beans) {
			log.info("delete CmsGuestbook id={}", bean.getId());
			cmsLogMng.operating(request, "cmsGuestbook.log.check", "id="
					+ bean.getId() + ";title=" + bean.getTitle());
		}
		return list(queryCtgId, queryRecommend, queryChecked, pageNo, request,
				model);
	}
	
	private WebErrors validateSave(CmsGuestbook bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		bean.setSite(site);
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (errors.ifEmpty(ids, "ids")) {
			return errors;
		}
		for (Integer id : ids) {
			vldExist(id, site.getId(), errors);
		}
		return errors;
	}

	private boolean vldExist(Integer id, Integer siteId, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsGuestbook entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsGuestbook.class, id)) {
			return true;
		}
		if (!entity.getSite().getId().equals(siteId)) {
			errors.notInSite(CmsGuestbook.class, id);
			return true;
		}
		return false;
	}

	@Autowired
	private CmsGuestbookCtgMng cmsGuestbookCtgMng;
	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsGuestbookMng manager;
	@Autowired
	private CmsDepartmentMng departmentMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	 
}