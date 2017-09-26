package com.jetcms.core.entity.base;

import java.io.Serializable;

import com.jetcms.core.entity.UserViews;


/**
 * This is an object that contains data related to the jc_content_tag table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_content_tag"
 */

public abstract class BaseUserViews  implements Serializable {
 
	public static String PROP_ID = "id";
	public static String PROP_COUNT = "count";


	// constructors
	public BaseUserViews () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseUserViews (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseUserViews (
		java.lang.Integer id, 
		java.lang.Integer count) {

		this.setId(id); 
		this.setCount(count);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;
 
	private java.lang.Integer count;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="tag_id"
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
	/**
	 * Return the value associated with the column: ref_counter
	 */
	public java.lang.Integer getCount () {
		return count;
	}

	/**
	 * Set the value related to the column: ref_counter
	 * @param count the ref_counter value
	 */
	public void setCount (java.lang.Integer count) {
		this.count = count;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof UserViews)) return false;
		else {
			 UserViews  userViews = (UserViews) obj;
			if (null == this.getId() || null == userViews.getId()) return false;
			else return (this.getId().equals(userViews.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}