package com.jetcms.core.dao;

import java.util.List;

import com.jetcms.common.hibernate4.Updater;
import com.jetcms.core.entity.Ftp;

public interface FtpDao {
	public List<Ftp> getList();

	public Ftp findById(Integer id);

	public Ftp save(Ftp bean);

	public Ftp updateByUpdater(Updater<Ftp> updater);

	public Ftp deleteById(Integer id);
}