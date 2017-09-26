package com.jetcms.core.entity;

import com.jetcms.core.entity.base.BaseCmsWorkflowRecord;



public class CmsWorkflowRecord extends BaseCmsWorkflowRecord {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CmsWorkflowRecord () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsWorkflowRecord (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CmsWorkflowRecord (
		java.lang.Integer id,
		com.jetcms.core.entity.CmsSite site,
		com.jetcms.core.entity.CmsWorkflowEvent event,
		com.jetcms.core.entity.CmsUser user,
		java.util.Date recordTime,
		java.lang.Integer type) {

		super (
			id,
			site,
			event,
			user,
			recordTime,
			type);
	}

/*[CONSTRUCTOR MARKER END]*/


}