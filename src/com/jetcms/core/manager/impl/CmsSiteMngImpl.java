package com.jetcms.core.manager.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jetcms.cms.manager.assist.CmsResourceMng;
import com.jetcms.common.hibernate4.Updater;
import com.jetcms.core.dao.CmsSiteDao;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsSiteCompany;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsWorkflow;
import com.jetcms.core.manager.CmsSiteCompanyMng;
import com.jetcms.core.manager.CmsSiteMng;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.manager.CmsUserSiteMng;
import com.jetcms.core.manager.CmsWorkflowMng;
import com.jetcms.core.manager.FtpMng; 

@Service
@Transactional
public class CmsSiteMngImpl implements CmsSiteMng {
	private static final Logger log = LoggerFactory
			.getLogger(CmsSiteMngImpl.class);

	@Transactional(readOnly = true)
	public List<CmsSite> getList() {
		return dao.getList(false);
	}
	
	@Transactional(readOnly = true)
	public List<CmsSite> getListFromCache() {
		return dao.getList(true);
	}
	
	@Transactional(readOnly = true)
	public List<CmsSite> getListByMaster(Boolean master) {
		return dao.getListByMaster(master);
	}
	
	@Transactional(readOnly = true)
	public List<CmsSite> getListByParent(Integer parentId){
		return dao.getListByParent(parentId);
	}
	
	@Transactional(readOnly = true)
	public boolean hasRepeatByProperty(String property){
		return (getList().size()-dao.getCountByProperty(property))>0?true:false;
	}
	
	@Transactional(readOnly = true)
	public List<CmsSite> getTopList(){
		return dao.getTopList();
	}

	@Transactional(readOnly = true)
	public CmsSite findByDomain(String domain) {
		return dao.findByDomain(domain);
	}
	
	@Transactional(readOnly = true)
	public CmsSite findByAccessPath(String accessPath){
		return dao.findByAccessPath(accessPath);
	}

	@Transactional(readOnly = true)
	public CmsSite findById(Integer id) {
		CmsSite entity = dao.findById(id);
		return entity;
	}
	
	public CmsSite save(CmsSite currSite, CmsUser currUser, CmsSite bean,
			Integer uploadFtpId,Integer syncPageFtpId) throws IOException {
		if (uploadFtpId != null) {
			bean.setUploadFtp(ftpMng.findById(uploadFtpId));
		}
		if(syncPageFtpId!=null){
			bean.setSyncPageFtp(ftpMng.findById(syncPageFtpId));
		}
		bean.init();
		dao.save(bean);
		// 复制本站模板
		cmsResourceMng.copyTplAndRes(currSite, bean);
		// 处理管理员
		cmsUserMng.addSiteToUser(currUser, bean, bean.getFinalStep());
		//保存站点相关公司信息
		CmsSiteCompany company=new CmsSiteCompany();
		company.setName(bean.getName());
		siteCompanyMng.save(bean,company);
		return bean;
	}

	public CmsSite update(CmsSite bean, Integer uploadFtpId,Integer syncPageFtpId) {
		CmsSite entity = findById(bean.getId());
		if (uploadFtpId != null) {
			entity.setUploadFtp(ftpMng.findById(uploadFtpId));
		} else {
			entity.setUploadFtp(null);
		}
		if (syncPageFtpId != null) {
			entity.setSyncPageFtp(ftpMng.findById(syncPageFtpId));
		} else {
			entity.setSyncPageFtp(null);
		}
		Updater<CmsSite> updater = new Updater<CmsSite>(bean);
		entity = dao.updateByUpdater(updater);
		return entity;
	}

	public void updateTplSolution(Integer siteId, String solution,String mobileSol) {
		CmsSite site = findById(siteId);
		if(StringUtils.isNotBlank(mobileSol)){
			site.setTplSolution(solution);
		} 
	}
	
	public void updateRefers(Integer siteId, Integer[] referIds){
		CmsSite site = findById(siteId);
		Set<CmsSite>refers=site.getRefers();
		refers.clear();
		for(Integer referId:referIds){
			refers.add(findById(referId));
		}
		site.setRefers(refers);
	}
	
	public void updateAttr(Integer siteId,Map<String,String>attr){
		CmsSite site = findById(siteId);
		site.getAttr().putAll(attr);
	}
	
	public void updateAttr(Integer siteId,Map<String,String>...attrs){
		CmsSite site = findById(siteId);
		for(Map<String,String>m:attrs){
			site.getAttr().putAll(m);
		}
	}

	public CmsSite deleteById(Integer id) {
		// 删除用户、站点关联
		cmsUserSiteMng.deleteBySiteId(id);
		// 删除工作流相关
		List<CmsWorkflow>workflows=workflowMng.getList(id, null);
		for(CmsWorkflow workflow:workflows){
			workflowMng.deleteById(workflow.getId());
		}
		CmsSite bean = dao.findById(id);
		bean.getRefers().clear();
		bean.getToRefers().clear();
		dao.deleteById(id);
		log.info("delete site "+id);
		// 删除模板
		/*
		try {
			cmsResourceMng.delTplAndRes(bean);
		} catch (IOException e) {
			log.error("delete Template and Resource fail!", e);
		}
		*/
		return bean;
	}

	public CmsSite[] deleteByIds(Integer[] ids) {
		CmsSite[] beans = new CmsSite[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	private CmsUserMng cmsUserMng;
	private CmsUserSiteMng cmsUserSiteMng;
	private CmsResourceMng cmsResourceMng;
	private FtpMng ftpMng;
	private CmsSiteDao dao;
	@Autowired
	private CmsSiteCompanyMng siteCompanyMng;
	@Autowired
	private CmsWorkflowMng workflowMng;

	@Autowired
	public void setCmsUserMng(CmsUserMng cmsUserMng) {
		this.cmsUserMng = cmsUserMng;
	}

	@Autowired
	public void setCmsUserSiteMng(CmsUserSiteMng cmsUserSiteMng) {
		this.cmsUserSiteMng = cmsUserSiteMng;
	}

	@Autowired
	public void setCmsResourceMng(CmsResourceMng cmsResourceMng) {
		this.cmsResourceMng = cmsResourceMng;
	}

	@Autowired
	public void setFtpMng(FtpMng ftpMng) {
		this.ftpMng = ftpMng;
	}

	@Autowired
	public void setDao(CmsSiteDao dao) {
		this.dao = dao;
	}
	
	@Autowired
	private com.jetcms.core.manager.CmsConfigMng configMng;
	@Override
	public String getSQLInputStreamForSubsite(String domain, Integer siteId)
	  {
	    String[] tables = { 
	      "jc_site", 
	      "jc_model", 
	      "jc_model_item", 
	     /* "jc_topic",*/ 
	      "jc_channel", 
	      "jc_channel_attr", 
	      "jc_channel_ext", 
	      "jc_channel_count",
	      "jc_channel_txt", 
	      "jc_channel_user", 
	      "jc_channel_model",
	      "jc_chnl_group_contri", 
	      "jc_chnl_group_view", 
	      "jc_content", 
	      "jc_content_attachment", 
	      "jc_content_attr", 
	      "jc_content_channel", 
	      "jc_content_check", 
	      "jc_content_count", 
	      "jc_content_ext", 
	      "jc_content_group_view", 
	      "jc_content_picture", 
	      "jc_content_topic", 
	      "jc_content_txt", 
	       "jc_content_tag", 
	      "jc_contenttag", 
	      
	      "jc_file",
	      
	      "jc_comment", 
	      "jc_comment_ext", 
	      "jc_guestbook_ctg", 
	      "jc_guestbook", 
	      "jc_guestbook_ext", 
	      "jc_keyword", 
	      "jc_role", 
	      "jc_role_permission", 
	      "jc_vote_topic", 
	      "jc_vote_item", 
	      "jc_vote_record",
	      "jc_friendlink_ctg",
	      "jc_friendlink",
	      "jc_message",
	      "jc_advertising_space",
	      "jc_advertising",  
	      "jc_advertising_attr",
	      "jc_score_group",
	      "jc_score_item",
	      "jc_score_record",
	      "jc_task",
	      "jc_task_attr",
        "jc_third_account",
        "jc_acquisition",
        "jc_acquisition_history",
        "jc_acquisition_temp"  
          
        
        /*"jc_directive_tpl"*/
	       };
	    String[] sqls = { 
	      "SELECT jc_site.* FROM jc_site where jc_site.`site_id` = ?;",
	      "SELECT jc_model.* FROM jc_model;", 
	      "SELECT jc_model_item.* FROM jc_model_item, jc_model WHERE jc_model.`model_id` = jc_model_item.`model_id`;", 
	       
	      "SELECT jc_channel.* FROM jc_channel WHERE jc_channel.`site_id` = ?;", 
	      "SELECT jc_channel_attr.* FROM jc_channel_attr,jc_channel WHERE jc_channel.`channel_id` = jc_channel_attr.`channel_id` AND jc_channel.`site_id` = ?;", 
	      "SELECT jc_channel_ext.* FROM jc_channel_ext,jc_channel WHERE jc_channel.`channel_id` = jc_channel_ext.`channel_id` AND jc_channel.`site_id` = ?;", 
	      "SELECT jc_channel_count.* FROM jc_channel_count ", 
	      
	      "SELECT jc_channel_txt.* FROM jc_channel_txt,jc_channel WHERE jc_channel.`channel_id` = jc_channel_txt.`channel_id` AND jc_channel.`site_id` = ?;", 
	      "SELECT jc_channel_user.* FROM jc_channel_user,jc_channel WHERE jc_channel.`channel_id` = jc_channel_user.`channel_id` AND jc_channel.`site_id` = ?;", 
	      "SELECT jc_channel_model.* FROM jc_channel_model,jc_channel WHERE jc_channel.`channel_id` = jc_channel_model.`channel_id` AND jc_channel.`site_id` = ?;",
	      "SELECT jc_chnl_group_contri.* FROM jc_chnl_group_contri,jc_channel WHERE jc_channel.`channel_id` = jc_chnl_group_contri.`channel_id` AND jc_channel.`site_id` = ?;", 
	      "SELECT jc_chnl_group_view.* FROM jc_chnl_group_view,jc_channel WHERE jc_channel.`channel_id` = jc_chnl_group_view.`channel_id` AND jc_channel.`site_id` = ?;", 
	      "SELECT jc_content.* FROM jc_content WHERE jc_content.`site_id` = ?;", 
	      "SELECT jc_content_attachment.* FROM jc_content_attachment,jc_content WHERE jc_content_attachment.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_attr.* FROM jc_content_attr,jc_content WHERE jc_content_attr.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_channel.* FROM jc_content_channel,jc_content WHERE jc_content_channel.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_check.* FROM jc_content_check,jc_content WHERE jc_content_check.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_count.* FROM jc_content_count,jc_content WHERE jc_content_count.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_ext.* FROM jc_content_ext,jc_content WHERE jc_content_ext.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_group_view.* FROM jc_content_group_view,jc_content WHERE jc_content_group_view.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_picture.* FROM jc_content_picture,jc_content WHERE jc_content_picture.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_topic.* FROM jc_content_topic,jc_content WHERE jc_content_topic.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_content_txt.* FROM jc_content_txt,jc_content WHERE jc_content_txt.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	       "SELECT jc_content_tag.* FROM jc_content_tag ;",  
	      "SELECT jc_contenttag.* FROM jc_contenttag ", 
	      
	      "SELECT jc_file.* FROM jc_file,jc_content WHERE jc_file.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      
	      "SELECT jc_comment.* FROM jc_comment WHERE jc_comment.`site_id` = ?;", 
	      "SELECT jc_comment_ext.* FROM jc_comment_ext,jc_comment WHERE jc_comment.`comment_id` = jc_comment_ext.`comment_id` AND jc_comment.`site_id` = ?;", 
	      "SELECT jc_guestbook_ctg.* FROM jc_guestbook_ctg WHERE jc_guestbook_ctg.`site_id` = ?;", 
	      "SELECT jc_guestbook.* FROM jc_guestbook WHERE jc_guestbook.`site_id` = ?;", 
	      "SELECT jc_guestbook_ext.* FROM jc_guestbook_ext,jc_guestbook WHERE jc_guestbook_ext.`guestbook_id` = jc_guestbook.`guestbook_id` AND jc_guestbook.`site_id` = ?;", 
	      "SELECT jc_keyword.* FROM jc_keyword WHERE jc_keyword.`site_id` = ?;", 
	      "SELECT jc_role.* FROM jc_role WHERE jc_role.`site_id` = ?;", 
	      "SELECT jc_role_permission.* FROM jc_role_permission,jc_role WHERE jc_role_permission.`role_id` = jc_role.`role_id` AND jc_role.`site_id` = ?;", 
	      "select jc_vote_topic.* from jc_vote_topic where jc_vote_topic.`site_id` = ?;", 
	      "select jc_vote_item.* from jc_vote_item,jc_vote_topic where jc_vote_item.`votetopic_id` = jc_vote_topic.`votetopic_id` and jc_vote_topic.`site_id` = ?;", 
	      "select jc_vote_record.* from jc_vote_record,jc_vote_topic where jc_vote_record.`votetopic_id` = jc_vote_topic.`votetopic_id` and jc_vote_topic.`site_id` = ?;",
	      "SELECT jc_friendlink_ctg.* FROM jc_friendlink_ctg WHERE jc_friendlink_ctg.`site_id` = ?;",
	      "SELECT jc_friendlink.* FROM jc_friendlink WHERE jc_friendlink.`site_id` = ?;", 
	      "SELECT jc_message.* FROM jc_message WHERE jc_message.`site_id` = ?;",
	      "SELECT jc_advertising_space.* FROM jc_advertising_space WHERE site_id = ?;",
	      "SELECT jc_advertising.* FROM jc_advertising WHERE site_id = ?;", 
	      "SELECT jc_advertising_attr.* FROM jc_advertising_attr,jc_advertising WHERE jc_advertising.advertising_id = jc_advertising_attr.advertising_id AND jc_advertising.site_id = ?;", 
	      "SELECT jc_score_group.* FROM jc_score_group WHERE jc_score_group.`site_id` = ?;",
	      "SELECT jc_score_item.* FROM jc_score_item,jc_score_group WHERE jc_score_item.`score_group_id` = jc_score_group.`score_group_id` AND jc_score_group.`site_id` = ?;", 
	      "SELECT jc_score_record.* FROM jc_score_record,jc_content WHERE jc_score_record.`content_id` = jc_content.`content_id` AND jc_content.`site_id` = ?;", 
	      "SELECT jc_task.* FROM jc_task WHERE jc_task.`site_id` = ?;",
	      "SELECT jc_task_attr.* FROM jc_task_attr,jc_task WHERE jc_task_attr.task_id = jc_task.task_id AND jc_task.site_id = ?;",
	      "SELECT jc_third_account.* FROM jc_third_account;", 
	      "SELECT jc_acquisition.* FROM jc_acquisition WHERE site_id = ?;",
	      "SELECT jc_acquisition_history.* FROM jc_acquisition_history,jc_acquisition WHERE jc_acquisition_history.acquisition_id = jc_acquisition.acquisition_id AND jc_acquisition.site_id = ?;",
	      "SELECT jc_acquisition_temp.* FROM jc_acquisition_temp WHERE site_id = ?;",
	      /*"SELECT jc_directive_tpl.* FROM jc_directive_tpl WHERE site_id = ?;",*/
	    	};
	    StringBuffer dbsql = new StringBuffer();
	    String contextPath = configMng.get().getContextPath();
         String  channelId ="";
         String  contentId ="";
	    for (int i = 0; i < tables.length; i++) {
	      try {
	    	  System.out.println(sqls[i]);
	    	  if(tables[i].equals("jc_channel_count")){
	    		  String str = "";
	    		  channelId = channelId.substring(0,channelId.length()-2); 
	    		  sqls[i] = sqls[i]+"where channel_id in ("+channelId+");";
	    	  }
	    	  if(tables[i].equals("jc_contenttag")){
	    		  String str = "";
	    		  contentId = contentId.substring(0,contentId.length()-2); 
	    		  sqls[i] = sqls[i]+"where content_id in ("+contentId+");";
	    	  }
	        ResultSet rs = this.dao.getResultSet(siteId, sqls[i]);
	        ResultSetMetaData metaData = rs.getMetaData();
	        StringBuffer sql = null;
	        int columnCount = metaData.getColumnCount();
	        StringBuffer columns = new StringBuffer();
	        StringBuffer values = null;
	        String columnName = null;
	        Object value = null;
	        Integer siteIdPosition = null;
	        for (int j = 1; j <= columnCount; j++) {
	          columnName = metaData.getColumnName(j);
	          columns.append(columnName);
	          if (columnName.equals("site_id"))
	            siteIdPosition = Integer.valueOf(j);
	          columns.append(",");
	        }
	        columns.deleteCharAt(columns.length() - 1);
	        while (rs.next()) {
	        	 
	          sql = new StringBuffer();
	          values = new StringBuffer("");
	          sql.append("insert into " + tables[i] + "(");
	          sql.append(columns);
	          if(tables[i].equals("jc_channel")){ 
	        		channelId+=rs.getObject(1)+" ,";
	        	}
	          if(tables[i].equals("jc_content")){ 
	        	  contentId+=rs.getObject(1)+" ,";
	        	}
	          for (int j = 1; j <= columnCount; j++) {
	            if ((siteIdPosition != null) && (siteIdPosition.intValue() == j)){
	              value = Integer.valueOf(1);
	               
	            } else{
	              value = rs.getObject(j);
	            }
	             
	             
	            if (value == null) {
	              values.append(value);
	            } else {
	              String str_value = value.toString();
	              str_value = str_value.replaceAll("\\\n", "");
	              str_value = str_value.replaceAll("\\\r", "");
	              str_value = str_value.replaceAll("'", "\\\"");
	              if ((str_value.equals("true") | str_value.equals("false")))
	                values.append(str_value);
	              else
	                values.append("'" + str_value + "'");
	              if(values.indexOf(contextPath+"/u")!=-1){
		            	values.replace(values.indexOf(contextPath+"/u"), values.indexOf(contextPath+"/u")+contextPath.length()+2,  "/u");  
		              }
	              if (values.indexOf("cms/u") != -1)
	                values.replace(values.indexOf("cms/u"), values.indexOf("cms/u") + 5,  "/u");
	            }
	            values.append(",");
	          }
	          values.deleteCharAt(values.length() - 1);
	          sql.append(") values(").append(values).append(");\n");
	          dbsql.append(sql);
	        }
	        rs.close();
	        rs = null;
	      }
	      catch (SQLException e) {
	        e.printStackTrace();
	      }
	    } 
	    return dbsql.toString();
	  }

	 
}