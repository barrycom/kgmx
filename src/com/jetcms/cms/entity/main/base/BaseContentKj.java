package com.jetcms.cms.entity.main.base;

import java.io.Serializable;

import com.jetcms.cms.entity.main.ContentKj;


/**
 * This is an object that contains data related to the jc_content table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_content"
 */

public abstract class BaseContentKj  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String REF = "ContentCatalog";
	public static String PROP_PATH = "path";
	public static String PROP_FILENAME = "filename";
	public static String PROP_NAME = "name";
	public static String PROP_COUNT = "count";


	// constructors
	public BaseContentKj () {
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseContentKj (
			java.lang.Integer id,
			java.lang.String path,
			java.lang.String pathPass,
			java.lang.String name, 
		    java.lang.Integer priority
		 ) { 
		this.setId(id);
		this.setPath(path);
		this.setPathPass(pathPass);
		this.setName(name); 
		this.setPriority(priority); 
		initialize();
	}

	protected void initialize () {}


	private java.lang.Integer id;
	// fields
	private java.lang.String path; 
	private java.lang.String pathPass; 
	private java.lang.String name; 
	private java.lang.Integer priority; 
	private Integer contentId;
	// one to one
	private com.jetcms.cms.entity.main.Content content;


	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getPath() {
		return path;
	}

	public void setPath(java.lang.String path) {
		this.path = path;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.Integer getPriority() {
		return priority;
	}

	public void setPriority(java.lang.Integer priority) {
		this.priority = priority;
	}

	public Integer getContentId() {
		return contentId;
	}

	public void setContentId(Integer contentId) {
		this.contentId = contentId;
	}

	public com.jetcms.cms.entity.main.Content getContent() {
		return content;
	}

	public void setContent(com.jetcms.cms.entity.main.Content content) {
		this.content = content;
	} 
	
	@Override  
	public boolean equals(Object obj) {  
		ContentKj s=(ContentKj)obj;   
	return name.equals(s.getName()) && path.equals(s.getPath());   
	}  
	@Override  
	public int hashCode() {  
	String in = path + name;  
	return in.hashCode();  
	}

	public java.lang.String getPathPass() {
		return pathPass;
	}

	public void setPathPass(java.lang.String pathPass) {
		this.pathPass = pathPass;
	}  
	
}