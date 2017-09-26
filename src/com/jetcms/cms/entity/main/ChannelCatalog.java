package com.jetcms.cms.entity.main;

import com.jetcms.cms.entity.main.base.BaseChannelCatalog;

public class ChannelCatalog extends BaseChannelCatalog {
	private static final long serialVersionUID = 1L;

	/* [CONSTRUCTOR MARKER BEGIN] */
	public ChannelCatalog () {
		super();
	}

	/**
	 * Constructor for required fields
	 */
	public ChannelCatalog (  
			java.lang.String path,
			java.lang.String name,
			java.lang.String lectureDate,
			 java.lang.Integer priority,
			 java.lang.Integer viewCount, 
			 java.lang.String courseCategory,
			 java.lang.String startTime,
			 java.lang.String endTime,Integer isFixed) { 
		super (path, name,  lectureDate, priority, viewCount, courseCategory, startTime, endTime,isFixed);
	}

	/* [CONSTRUCTOR MARKER END] */

}