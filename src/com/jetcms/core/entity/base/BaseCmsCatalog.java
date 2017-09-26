package com.jetcms.core.entity.base;

import java.io.Serializable;

import com.jetcms.core.entity.CmsUser;


/**
 * This is an object that contains data related to the jc_role table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class 
 */

public abstract class BaseCmsCatalog  implements Serializable {

	public static String REF = "CmsCatalog";  
	public static String PROP_PRIORITY = "priority"; 
	public static String PROP_ID = "id";


	// constructors
	public BaseCmsCatalog () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCmsCatalog (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCmsCatalog (
		java.lang.Integer id,
		java.lang.Integer catalogId,
		java.lang.Integer viewnum,
		java.lang.Integer priority ) { 
		this.setId(id);
		this.setViewnum(viewnum);
		this.setCatalogId(catalogId); 
		this.setPriority(priority); 
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields 
	private java.lang.Integer catalogId; 
	private java.lang.Integer viewnum;
	private java.lang.Integer priority;
	
	 
	private CmsUser user ;


	public java.lang.Integer getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(java.lang.Integer catalogId) {
		this.catalogId = catalogId;
	}

	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="role_id"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	} 
 
 

	public java.lang.Integer getViewnum() {
		return viewnum;
	}

	public void setViewnum(java.lang.Integer viewnum) {
		this.viewnum = viewnum;
	}

	public java.lang.Integer getPriority() {
		return priority;
	}

	public void setPriority(java.lang.Integer priority) {
		this.priority = priority;
	}

 

	public CmsUser getUser() {
		return user;
	}

	public void setUser(CmsUser user) {
		this.user = user;
	}

	public String toString () {
		return super.toString();
	}


}