package com.jetcms.cms.dao.assist;

import java.util.List;

import com.jetcms.cms.entity.assist.CmsSensitivity;
import com.jetcms.common.hibernate4.Updater;

public interface CmsSensitivityDao {
	public List<CmsSensitivity> getList(boolean cacheable);

	public CmsSensitivity findById(Integer id);

	public CmsSensitivity save(CmsSensitivity bean);

	public CmsSensitivity updateByUpdater(Updater<CmsSensitivity> updater);

	public CmsSensitivity deleteById(Integer id);
}