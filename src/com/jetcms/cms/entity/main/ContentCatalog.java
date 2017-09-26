package com.jetcms.cms.entity.main;

import com.jetcms.cms.entity.main.base.BaseContentCatalog;

public class ContentCatalog extends BaseContentCatalog {
	private static final long serialVersionUID = 1L;

	/* [CONSTRUCTOR MARKER BEGIN] */
	public ContentCatalog () {
		super();
	}

	/**
	 * Constructor for required fields
	 */
	public ContentCatalog ( java.lang.Integer id,String pathpass,String livepass,String livepath,
			java.lang.String path,
			java.lang.String name,
			java.lang.String lectureDate,
			 java.lang.Integer priority,
			 java.lang.Integer viewCount, 
			 java.lang.String courseCategory,
			 java.lang.String startTime,
			 java.lang.String endTime ) { 
		super (id,path, name,  lectureDate, priority, viewCount, courseCategory, startTime, endTime,livepath,livepass,pathpass);
	}

	public ContentCatalog ( java.lang.Integer id,java.lang.Integer contentId,String pathpass,String livepass,String livepath,
			java.lang.String path,
			java.lang.String name,
			java.lang.String lectureDate,
			 java.lang.Integer priority,
			 java.lang.Integer viewCount, 
			 java.lang.String courseCategory,
			 java.lang.String startTime,
			 java.lang.String endTime ) { 
		super (id,contentId,path, name,  lectureDate, priority, viewCount, courseCategory, startTime, endTime,livepath,livepass,pathpass);
	}
	/* [CONSTRUCTOR MARKER END] */

}