package com.jetcms.core.dao;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.core.entity.DbFile;

public interface DbFileDao {
	public DbFile findById(String id);

	public DbFile save(DbFile bean);

	public DbFile updateByUpdater(Updater<DbFile> updater);

	public DbFile deleteById(String id);
}