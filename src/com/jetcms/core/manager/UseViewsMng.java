package com.jetcms.core.manager;

import java.util.Collection;
import java.util.List;
 
import com.jetcms.common.page.Pagination;
import com.jetcms.core.entity.UserViews;

public interface UseViewsMng {

	public List<UserViews> getListForTag(Integer count);

	public Pagination getPageForTag(int pageNo, int pageSize);

	public Pagination getPage(String name, int pageNo, int pageSize);

	public UserViews findById(Integer id);

	public UserViews findByName(String name);

	public UserViews findByNameForTag(String name);

	/**
	 * 保存标签。不存在的保存，存在的数量加一。
	 * 
	 * @param tagArr
	 *            标签名数组
	 * @return 标签列表
	 */
	public List<UserViews> saveTags(String[] tagArr);

	public UserViews saveTag(String name);

	public List<UserViews> updateTags(List<UserViews> tags, String[] tagArr);

	public void removeTags(Collection<UserViews> tags);

	public UserViews save(UserViews bean);

	public UserViews update(UserViews bean);

	public UserViews deleteById(Integer id);

	public UserViews[] deleteByIds(Integer[] ids);
}