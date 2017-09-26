package com.jetcms.cms.entity.main;

import com.jetcms.cms.entity.main.base.BaseContentKj;

public class ContentKj extends BaseContentKj {
	private static final long serialVersionUID = 1L;

	/* [CONSTRUCTOR MARKER BEGIN] */
	public ContentKj () {
		super();
	}

	/**
	 * Constructor for required fields
	 */
	public ContentKj ( java.lang.Integer id, 
			java.lang.String path,
			java.lang.String pathPass,
			java.lang.String name,
			java.lang.Integer priority
			 ) { 
		super (id,path,pathPass, name,priority);
	}

	/* [CONSTRUCTOR MARKER END] */

}