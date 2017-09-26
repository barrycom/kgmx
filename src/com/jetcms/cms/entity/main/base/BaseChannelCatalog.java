package com.jetcms.cms.entity.main.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the jc_content table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_content"
 */

public abstract class BaseChannelCatalog  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String REF = "ChannelCatalog";
	public static String PROP_PATH = "path";
	public static String PROP_FILENAME = "filename";
	public static String PROP_NAME = "name";
	public static String PROP_COUNT = "count";


	// constructors
	public BaseChannelCatalog () {
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseChannelCatalog ( 
		java.lang.String path,
		java.lang.String name,
		java.lang.String lectureDate,
		 java.lang.Integer priority,
		 java.lang.Integer viewCount, 
		 java.lang.String courseCategory,
		 java.lang.String startTime,
		 java.lang.String endTime,Integer isFixed) {  
		this.setPath(path);
		this.setName(name);
		this.setLectureDate(lectureDate);
		this.setPriority(priority);
		this.setViewCount(viewCount);
		this.setCourseCategory(courseCategory);
		this.setStartTime(startTime) ;
		this.setEndTime(endTime);
		this.setIsFixed(isFixed);
		initialize();
	}

	protected void initialize () {}


	private java.lang.Integer id;
	// fields
	private java.lang.String path;
	private java.lang.String name;
	private java.lang.String lectureDate;
	private java.lang.Integer priority;
	private java.lang.Integer viewCount;
	private java.lang.Integer haveCount;
	private java.lang.String courseCategory;
	private java.lang.String startTime;
	private java.lang.String endTime;
	private Integer showType;//1:录播，2：直播，3：未开始
	private Integer shownum;
	private Integer shows=0;
	
	private Integer isFixed;//是否固定
	// one to one
	private com.jetcms.cms.entity.main.Channel channel;
	
	
	
	 
	public com.jetcms.cms.entity.main.Channel getChannel() {
		return channel;
	}

	public void setChannel(com.jetcms.cms.entity.main.Channel channel) {
		this.channel = channel;
	}

	public Integer getShownum() {
		return shownum;
	}

	public void setShownum(Integer shownum) {
		this.shownum = shownum;
	}

	public Integer getShowType() {
		return showType;
	}

	public void setShowType(Integer showType) {
		this.showType = showType;
	}

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * Return the value associated with the column: attachment_path
	 */
	public java.lang.String getPath () {
		return path;
	}

	/**
	 * Set the value related to the column: attachment_path
	 * @param path the attachment_path value
	 */
	public void setPath (java.lang.String path) {
		this.path = path;
	}


	/**
	 * Return the value associated with the column: attachment_name
	 */
	public java.lang.String getName () {
		return name;
	}

	/**
	 * Set the value related to the column: attachment_name
	 * @param name the attachment_name value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}

 





	public java.lang.String getLectureDate() {
		return lectureDate;
	}

	public void setLectureDate(java.lang.String lectureDate) {
		this.lectureDate = lectureDate;
	}

	public java.lang.Integer getPriority() {
		return priority;
	}

	public void setPriority(java.lang.Integer priority) {
		this.priority = priority;
	}

	public java.lang.Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(java.lang.Integer viewCount) {
		this.viewCount = viewCount;
	}

	public java.lang.String getCourseCategory() {
		return courseCategory;
	}

	public void setCourseCategory(java.lang.String courseCategory) {
		this.courseCategory = courseCategory;
	}

	public java.lang.String getStartTime() {
		return startTime;
	}

	public void setStartTime(java.lang.String startTime) {
		this.startTime = startTime;
	}

	public java.lang.String getEndTime() {
		return endTime;
	}

	public void setEndTime(java.lang.String endTime) {
		this.endTime = endTime;
	}

	public String toString () {
		return super.toString();
	}

	public java.lang.Integer getHaveCount() {
		return haveCount;
	}

	public void setHaveCount(java.lang.Integer haveCount) {
		this.haveCount = haveCount;
	}

	public Integer getIsFixed() {
		return isFixed;
	}

	public void setIsFixed(Integer isFixed) {
		this.isFixed = isFixed;
	}

	public Integer getShows() {
		return shows;
	}

	public void setShows(Integer shows) {
		this.shows = shows;
	}


}