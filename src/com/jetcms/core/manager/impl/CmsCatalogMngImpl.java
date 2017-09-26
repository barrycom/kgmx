package com.jetcms.core.manager.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.core.dao.CmsCatalogDao;
import com.jetcms.core.dao.CmsRoleDao;
import com.jetcms.core.entity.CmsCatalog;
import com.jetcms.core.entity.CmsRole;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.manager.CmsCatalogMng;
import com.jetcms.core.manager.CmsRoleMng;
import com.jetcms.core.manager.CmsUserMng;

@Service
@Transactional
public class CmsCatalogMngImpl implements CmsCatalogMng {
	@Transactional(readOnly = true)
	public List<CmsCatalog> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public CmsCatalog findById(Integer id) {
		CmsCatalog entity = dao.findById(id);
		return entity;
	}

	public CmsCatalog save(CmsCatalog bean) { 
		dao.save(bean);
		return bean;
	}

	public CmsCatalog update(CmsCatalog bean ) {
		Updater<CmsCatalog> updater = new Updater<CmsCatalog>(bean);
		bean = dao.updateByUpdater(updater); 
		return bean;
	}

	public CmsCatalog deleteById(Integer id) {
		CmsCatalog bean = dao.deleteById(id);
		return bean;
	}

	 
	public void deleteMembers(CmsCatalog role, Integer[] userIds) {
		Updater<CmsCatalog> updater = new Updater<CmsCatalog>(role);
		role = dao.updateByUpdater(updater);
		if (userIds != null) {
			CmsUser user;
			for (Integer uid : userIds) {
				user = userMng.findById(uid);
				 
			}
		}
	}

	private CmsCatalogDao dao;
	@Autowired
	private CmsUserMng userMng;

	@Autowired
	public void setDao(CmsCatalogDao dao) {
		this.dao = dao;
	}

	@Override
	public CmsCatalog[] deleteByIds(Integer[] ids) {
		// TODO Auto-generated method stub
		return null;
	}
}