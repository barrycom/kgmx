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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jetcms.cms.entity.assist.CmsComment;
import com.jetcms.cms.entity.assist.CmsCommentExt;
import com.jetcms.cms.entity.assist.CmsGuestbook;
import com.jetcms.cms.entity.assist.CmsGuestbookCtg;
import com.jetcms.cms.entity.assist.CmsGuestbookExt;
import com.jetcms.cms.entity.main.Content;
import com.jetcms.cms.manager.assist.CmsCommentMng;
import com.jetcms.cms.manager.main.ContentMng;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.web.CookieUtils;
import com.jetcms.common.web.RequestUtils;
import com.jetcms.core.entity.CmsDepartment;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.manager.CmsLogMng;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;

@Controller
public class CmsCommentAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsCommentAct.class);

	@RequiresPermissions("comment:v_list")
	@RequestMapping("/comment/v_list.do")
	public String list(Integer queryContentId, Boolean queryChecked,
			Boolean queryRecommend, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		Pagination pagination = manager.getPage(site.getId(), queryContentId,
				null, queryChecked, queryRecommend, true, cpn(pageNo),
				CookieUtils.getPageSize(request));
		model.addAttribute("queryContentId",queryContentId);
		model.addAttribute("queryChecked", queryChecked);
		model.addAttribute("queryRecommend", queryRecommend);
		model.addAttribute("pagination", pagination);
		model.addAttribute("pageNo", pageNo);
		return "comment/list";
	}

	
	//生成评论
		@RequiresPermissions("comment:v_list")
		@RequestMapping("/comment/v_generate.do")
		public String generate(Integer queryContentId,Integer counts, Integer basecounts, Boolean queryChecked,
				Boolean queryRecommend, Integer pageNo, HttpServletRequest request,
				ModelMap model) {
			CmsSite site = CmsUtils.getSite(request);
			Pagination pagination = manager.getPage(site.getId(), queryContentId,
					null, queryChecked, queryRecommend, true, cpn(pageNo),
					CookieUtils.getPageSize(request));
			model.addAttribute("queryContentId",queryContentId);
			model.addAttribute("queryChecked", queryChecked);
			model.addAttribute("queryRecommend", queryRecommend);
			model.addAttribute("pagination", pagination);
			model.addAttribute("pageNo", pageNo);
			java.util.Random random=new java.util.Random();// 定义随机类
			String ip = RequestUtils.getIpAddr(request);
		 
			List<CmsUser> list = cmsUserMng.getList(null, null, null, null,null, false, null);
			 Properties prop = new Properties();   
		 
			    File file = new File(request.getRealPath("/")+"/u/cms/www/comment.txt");
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
				int result=random.nextInt(basecounts-1)+1; 
				String q = "comment"+result; 
				try {
					q = new String(prop.getProperty(q).trim().getBytes("ISO-8859-1"), "utf-8"); 
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				CmsComment comment = new CmsComment();
				Content content = contentMng.findById(queryContentId);
				if(content==null){
					return "comment/list";
				} 
				comment.setChecked(false);
				comment.setRecommend(false);
				comment.setScore(0);
				comment.init();
				manager.comment(0, q, ip, queryContentId, 1, list.get(result1).getId(), true,false,null); 
			} 
		 
			return "comment/list";
		}	
	
	
	
	
	
	@RequiresPermissions("comment:v_add")
	@RequestMapping("/comment/v_add.do")
	public String add(ModelMap model) {
		return "comment/add";
	}

	@RequiresPermissions("comment:v_edit")
	@RequestMapping("/comment/v_edit.do")
	public String edit(Integer id, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		model.addAttribute("cmsComment", manager.findById(id));
		return "comment/edit";
	}

	@RequiresPermissions("comment:o_update")
	@RequestMapping("/comment/o_update.do")
	public String update(Integer queryContentId, Boolean queryChecked,
			Boolean queryRecommend,String reply, CmsComment bean, CmsCommentExt ext,
			Integer pageNo, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateUpdate(bean.getId(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		//若回复内容不为空而且回复更新，则设置回复时间，已最新回复时间为准
		if(StringUtils.isNotBlank(ext.getReply())){
			bean.setReplayTime(new Date());
			bean.setReplayUser(CmsUtils.getUser(request));
		}
		bean = manager.update(bean, ext);
		log.info("update CmsComment id={}.", bean.getId());
		cmsLogMng.operating(request, "cmsComment.log.update", "id="
				+ bean.getId());
		return list(queryContentId, queryChecked, queryRecommend, pageNo,
				request, model);
	}

	@RequiresPermissions("comment:o_delete")
	@RequestMapping("/comment/o_delete.do")
	public String delete(Integer queryContentId, Boolean queryChecked,
			Boolean queryRecommend, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsComment[] beans = manager.deleteByIds(ids);
		for (CmsComment bean : beans) {
			log.info("delete CmsComment id={}", bean.getId());
			cmsLogMng.operating(request, "cmsComment.log.delete", "id="
					+ bean.getId());
		}
		return list(queryContentId, queryChecked, queryRecommend, pageNo,
				request, model);
	}
	
	@RequiresPermissions("comment:o_check")
	@RequestMapping("/comment/o_check.do")
	public String check(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsComment[] beans = manager.checkByIds(ids,CmsUtils.getUser(request),true);
		for (CmsComment bean : beans) {
			log.info("delete CmsGuestbook id={}", bean.getId());
			cmsLogMng.operating(request, "cmsComment.log.check", "id="
					+ bean.getId() + ";title=" + bean.getReplayHtml());
		}
		return list(queryCtgId, queryRecommend, queryChecked, pageNo, request,
				model);
	}
	
	@RequiresPermissions("comment:o_check_cancel")
	@RequestMapping("/comment/o_check_cancel.do")
	public String cancelCheck(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsComment[] beans = manager.checkByIds(ids,CmsUtils.getUser(request),false);
		for (CmsComment bean : beans) {
			log.info("delete CmsGuestbook id={}", bean.getId());
			cmsLogMng.operating(request, "cmsComment.log.cancelCheck", "id="
					+ bean.getId() + ";title=" + bean.getReplayHtml());
		}
		return list(queryCtgId, queryRecommend, queryChecked, pageNo, request,
				model);
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
		CmsComment entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsComment.class, id)) {
			return true;
		}
		if (!entity.getSite().getId().equals(siteId)) {
			errors.notInSite(CmsComment.class, id);
			return true;
		}
		return false;
	}
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsCommentMng manager;
	@Autowired
	private ContentMng contentMng;
}