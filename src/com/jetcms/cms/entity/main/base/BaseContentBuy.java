package com.jetcms.cms.entity.main.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the jc_content_buy table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_content_buy"
 */

public abstract class BaseContentBuy  implements Serializable {

	public static String REF = "CmsContentBuy";
	public static String PROP_BUY_TIME = "buyTime";
	public static String PROP_PLAT_AMOUNT = "platAmount";
	public static String PROP_HAS_PAID_AUTHOR = "hasPaidAuthor";
	public static String PROP_BUY_USER = "buyUser";
	public static String PROP_AUTHOR_USER = "authorUser";
	public static String PROP_CONTENT = "content";
	public static String PROP_ID = "id";
	public static String PROP_CHARGE_AMOUNT = "chargeAmount";
	public static String PROP_AUTHOR_AMOUNT = "authorAmount";

	// constructors
	public BaseContentBuy () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseContentBuy (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseContentBuy (
		java.lang.Long id,
		com.jetcms.cms.entity.main.Content content,
		com.jetcms.core.entity.CmsUser buyUser,
		com.jetcms.core.entity.CmsUser authorUser,
		java.lang.Double chargeAmount,
		java.lang.Double authorAmount,
		java.lang.Double platAmount,
		java.util.Date buyTime,
		boolean hasPaidAuthor) {

		this.setId(id);
		this.setContent(content);
		this.setBuyUser(buyUser);
		this.setAuthorUser(authorUser);
		this.setChargeAmount(chargeAmount);
		this.setAuthorAmount(authorAmount);
		this.setPlatAmount(platAmount);
		this.setBuyTime(buyTime);
		this.setHasPaidAuthor(hasPaidAuthor);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.Double chargeAmount;
	private java.lang.Double authorAmount;
	private java.lang.Double platAmount;
	private java.util.Date buyTime;
	private boolean hasPaidAuthor;
	private String orderNumber;
	private String orderNumWeiXin;
	private String orderNumAliPay;
	private java.lang.Short chargeReward;
	private java.lang.Short orderSta;
	private java.lang.Short isGave;
	private String buyer;//购买人
	private String tel;//联系方式
	// many to one
	private com.jetcms.cms.entity.main.Content content;
	private com.jetcms.core.entity.CmsUser buyUser;
	private com.jetcms.core.entity.CmsUser authorUser;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="content_buy_id"
     */
	public java.lang.Long getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: charge_amount
	 */
	public java.lang.Double getChargeAmount () {
		return chargeAmount;
	}

	/**
	 * Set the value related to the column: charge_amount
	 * @param chargeAmount the charge_amount value
	 */
	public void setChargeAmount (java.lang.Double chargeAmount) {
		this.chargeAmount = chargeAmount;
	}


	/**
	 * Return the value associated with the column: author_amount
	 */
	public java.lang.Double getAuthorAmount () {
		return authorAmount;
	}

	/**
	 * Set the value related to the column: author_amount
	 * @param authorAmount the author_amount value
	 */
	public void setAuthorAmount (java.lang.Double authorAmount) {
		this.authorAmount = authorAmount;
	}


	/**
	 * Return the value associated with the column: plat_amount
	 */
	public java.lang.Double getPlatAmount () {
		return platAmount;
	}

	/**
	 * Set the value related to the column: plat_amount
	 * @param platAmount the plat_amount value
	 */
	public void setPlatAmount (java.lang.Double platAmount) {
		this.platAmount = platAmount;
	}


	/**
	 * Return the value associated with the column: buy_time
	 */
	public java.util.Date getBuyTime () {
		return buyTime;
	}

	/**
	 * Set the value related to the column: buy_time
	 * @param buyTime the buy_time value
	 */
	public void setBuyTime (java.util.Date buyTime) {
		this.buyTime = buyTime;
	}


	/**
	 * Return the value associated with the column: has_paid_author
	 */
	public boolean getHasPaidAuthor () {
		return hasPaidAuthor;
	}

	/**
	 * Set the value related to the column: has_paid_author
	 * @param hasPaidAuthor the has_paid_author value
	 */
	public void setHasPaidAuthor (boolean hasPaidAuthor) {
		this.hasPaidAuthor = hasPaidAuthor;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderNumWeiXin() {
		return orderNumWeiXin;
	}

	public void setOrderNumWeiXin(String orderNumWeiXin) {
		this.orderNumWeiXin = orderNumWeiXin;
	}

	public String getOrderNumAliPay() {
		return orderNumAliPay;
	}

	public void setOrderNumAliPay(String orderNumAliPay) {
		this.orderNumAliPay = orderNumAliPay;
	}

	public java.lang.Short getChargeReward() {
		return chargeReward;
	}

	public void setChargeReward(java.lang.Short chargeReward) {
		this.chargeReward = chargeReward;
	}

	/**
	 * Return the value associated with the column: contentId
	 */
	public com.jetcms.cms.entity.main.Content getContent () {
		return content;
	}

	/**
	 * Set the value related to the column: contentId
	 * @param content the contentId value
	 */
	public void setContent (com.jetcms.cms.entity.main.Content content) {
		this.content = content;
	}


	/**
	 * Return the value associated with the column: buy_user_id
	 */
	public com.jetcms.core.entity.CmsUser getBuyUser () {
		return buyUser;
	}

	/**
	 * Set the value related to the column: buy_user_id
	 * @param buyUser the buy_user_id value
	 */
	public void setBuyUser (com.jetcms.core.entity.CmsUser buyUser) {
		this.buyUser = buyUser;
	}


	/**
	 * Return the value associated with the column: author_user_id
	 */
	public com.jetcms.core.entity.CmsUser getAuthorUser () {
		return authorUser;
	}

	/**
	 * Set the value related to the column: author_user_id
	 * @param authorUser the author_user_id value
	 */ 
	public void setAuthorUser (com.jetcms.core.entity.CmsUser authorUser) {
		this.authorUser = authorUser;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jetcms.cms.entity.main.ContentBuy)) return false;
		else {
			com.jetcms.cms.entity.main.ContentBuy cmsContentBuy = (com.jetcms.cms.entity.main.ContentBuy) obj;
			if (null == this.getId() || null == cmsContentBuy.getId()) return false;
			else return (this.getId().equals(cmsContentBuy.getId()));
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

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public java.lang.Short getOrderSta() {
		return orderSta;
	}

	public void setOrderSta(java.lang.Short orderSta) {
		this.orderSta = orderSta;
	}

	public java.lang.Short getIsGave() {
		return isGave;
	}

	public void setIsGave(java.lang.Short isGave) {
		this.isGave = isGave;
	}

	
}