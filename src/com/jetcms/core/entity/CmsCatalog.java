package com.jetcms.core.entity;

import java.util.HashSet;
import java.util.Set;

import com.jetcms.core.entity.base.BaseCmsCatalog;

public class CmsCatalog extends BaseCmsCatalog {
	private static final long serialVersionUID = 1L;

 

	/* [CONSTRUCTOR MARKER BEGIN] */
	public CmsCatalog () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsCatalog (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsCatalog (
		java.lang.Integer id,
		java.lang.Integer catalogId,
		 
		java.lang.Integer viewnum,
		java.lang.Integer priority ) {

		super (
			id,catalogId,
			viewnum,
			priority );
	}
 
 
	/* [CONSTRUCTOR MARKER END] */

}