package com.jetcms.cms.dao.main.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.jetcms.common.hibernate4.Finder;
import com.jetcms.common.hibernate4.HibernateBaseDao;
import com.jetcms.common.page.Pagination;
import com.jetcms.cms.dao.main.ContentBuyDao;
import com.jetcms.cms.entity.main.ContentBuy;

@Repository
public class ContentBuyDaoImpl extends HibernateBaseDao<ContentBuy, Long> implements ContentBuyDao {
	public Pagination getPage(String orderNum,Integer buyUserId,Integer authorUserId,
			Short payMode,int pageNo, int pageSize) {
		String hql="from ContentBuy bean where 1=1 ";
		Finder f=Finder.create(hql);
		if(StringUtils.isNotBlank(orderNum)){
			f.append(" and bean.orderNumber like:num").setParam("num", "%"+orderNum+"%");
		}
		if(buyUserId!=null){
			f.append(" and bean.buyUser.id=:buyUserId").setParam("buyUserId", buyUserId);
		}
		if(authorUserId!=null){
			f.append(" and bean.authorUser.id=:authorUserId").setParam("authorUserId", authorUserId);
		}
		if(payMode!=null&&payMode!=0){
			f.append(" and bean.chargeReward=:payMode")
			.setParam("payMode", payMode);
		}
		f.append(" order by bean.buyTime desc");
		f.setCacheable(false);
		Pagination page = find(f, pageNo, pageSize);
		return page;
	}
	
	public Pagination getPagebyPay(String orderNum,Integer buyUserId,Integer authorUserId,
			Short payMode,int pageNo, int pageSize) {
		String hql="from ContentBuy bean where 1=1 ";
		Finder f=Finder.create(hql);
		if(StringUtils.isNotBlank(orderNum)){
			f.append(" and bean.orderNumber like:num").setParam("num", "%"+orderNum+"%");
		}
		if(buyUserId!=null){
			f.append(" and bean.buyUser.id=:buyUserId").setParam("buyUserId", buyUserId);
		}
		if(authorUserId!=null){
			f.append(" and bean.authorUser.id=:authorUserId").setParam("authorUserId", authorUserId);
		}
		if(payMode!=null&&payMode!=0){
			f.append(" and bean.chargeReward=:payMode")
			.setParam("payMode", payMode);
		}
		f.append(" and bean.orderSta <>0");
		f.append(" order by bean.buyTime desc");
		f.setCacheable(false);
		Pagination page = find(f, pageNo, pageSize);
		return page;
	}
	
	
	
	
	public Pagination getPageOrder(String startTime,String endTime,int userId,int pageNo, int pageSize) {
		/*String sql  ="SELECT b.content_buy_id,b.buy_user_id,b.order_number,b.buy_time,u.username,ue.realname,c.title FROM	ContentBuy b "
				+ "LEFT JOIN jc_user u ON u.user_id = b.buy_user_id "
				+ "LEFT JOIN jc_user_ext ue ON b.buy_user_id = ue.user_id "
				+ " LEFT JOIN jc_content_ext c ON c.content_id = b.content_id where 1 = 1";*/
		String hql="from ContentBuy bean where 1=1 ";
		Finder f= Finder.create(hql); 
		f.append(" and bean.buyUser.createId = "+userId);
		if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
			f.append(" and bean.buyTime between '"+startTime+"' and '"+endTime+"'");
		}
		f.append(" and bean.orderSta <> 0 order by bean.buyTime desc");
		f.setCacheable(true);
		Pagination page = find(f, pageNo, pageSize);
		return page;
	}
	
	public Pagination getPageByContent(Integer contentId,
			Short payMode,int pageNo, int pageSize){
		String hql="from ContentBuy bean where 1=1 ";
		Finder f=Finder.create(hql);
		if(contentId!=null){
			f.append(" and bean.content.id=:contentId").setParam("contentId", contentId);
		}
		if(payMode!=null&&payMode!=0){
			f.append(" and bean.chargeReward=:payMode")
			.setParam("payMode", payMode);
		}
		
		f.append(" and bean.orderSta=1");
		f.append(" order by bean.buyTime desc");
		f.setCacheable(true);
		Pagination page = find(f, pageNo, pageSize);
		return page;
	}

	public ContentBuy findById(Long id) {
		ContentBuy entity = get(id);
		return entity;
	}
	
	public ContentBuy findByOrderNumber(String orderNumber){
		String hql="from ContentBuy bean where bean.orderNumber=:orderNumber";
		Finder finder=Finder.create(hql).setParam("orderNumber", orderNumber);
		List<ContentBuy>list=find(finder);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	public String queryCountBycontentId(Integer contentId){
		String hql="from ContentBuy bean where bean.content.id=:contentId";
		Finder finder=Finder.create(hql).setParam("contentId", contentId);
		List<ContentBuy>list=find(finder);
		if(list!=null&&list.size()>0){
			return list.size()+"";
		}else{
			return "0";
		}
	}
	
	public ContentBuy find(Integer buyUserId,Integer contentId){
		String hql="from ContentBuy bean where bean.content.id=:contentId "
				+ "and bean.buyUser.id=:buyUserId";
		Finder finder=Finder.create(hql).setParam("contentId", contentId)
				.setParam("buyUserId", buyUserId);
		finder.setCacheable(true);
		List<ContentBuy>list=find(finder);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	public ContentBuy save(ContentBuy bean) {
		getSession().save(bean);
		return bean;
	}

	public ContentBuy deleteById(Long id) {
		ContentBuy entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<ContentBuy> getEntityClass() {
		return ContentBuy.class;
	}

	 

	 
}