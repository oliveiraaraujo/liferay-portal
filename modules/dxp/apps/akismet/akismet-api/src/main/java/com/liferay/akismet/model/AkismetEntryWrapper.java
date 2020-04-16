/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.akismet.model;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.service.ServiceContext;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This class is a wrapper for {@link AkismetEntry}.
 * </p>
 *
 * @author Jamie Sammons
 * @see AkismetEntry
 * @generated
 */
public class AkismetEntryWrapper
	implements AkismetEntry, ModelWrapper<AkismetEntry> {

	public AkismetEntryWrapper(AkismetEntry akismetEntry) {
		_akismetEntry = akismetEntry;
	}

	@Override
	public Class<?> getModelClass() {
		return AkismetEntry.class;
	}

	@Override
	public String getModelClassName() {
		return AkismetEntry.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("akismetEntryId", getAkismetEntryId());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("type", getType());
		attributes.put("permalink", getPermalink());
		attributes.put("referrer", getReferrer());
		attributes.put("userAgent", getUserAgent());
		attributes.put("userIP", getUserIP());
		attributes.put("userURL", getUserURL());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long akismetEntryId = (Long)attributes.get("akismetEntryId");

		if (akismetEntryId != null) {
			setAkismetEntryId(akismetEntryId);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		String permalink = (String)attributes.get("permalink");

		if (permalink != null) {
			setPermalink(permalink);
		}

		String referrer = (String)attributes.get("referrer");

		if (referrer != null) {
			setReferrer(referrer);
		}

		String userAgent = (String)attributes.get("userAgent");

		if (userAgent != null) {
			setUserAgent(userAgent);
		}

		String userIP = (String)attributes.get("userIP");

		if (userIP != null) {
			setUserIP(userIP);
		}

		String userURL = (String)attributes.get("userURL");

		if (userURL != null) {
			setUserURL(userURL);
		}
	}

	@Override
	public Object clone() {
		return new AkismetEntryWrapper((AkismetEntry)_akismetEntry.clone());
	}

	@Override
	public int compareTo(AkismetEntry akismetEntry) {
		return _akismetEntry.compareTo(akismetEntry);
	}

	/**
	 * Returns the akismet entry ID of this akismet entry.
	 *
	 * @return the akismet entry ID of this akismet entry
	 */
	@Override
	public long getAkismetEntryId() {
		return _akismetEntry.getAkismetEntryId();
	}

	/**
	 * Returns the fully qualified class name of this akismet entry.
	 *
	 * @return the fully qualified class name of this akismet entry
	 */
	@Override
	public String getClassName() {
		return _akismetEntry.getClassName();
	}

	/**
	 * Returns the class name ID of this akismet entry.
	 *
	 * @return the class name ID of this akismet entry
	 */
	@Override
	public long getClassNameId() {
		return _akismetEntry.getClassNameId();
	}

	/**
	 * Returns the class pk of this akismet entry.
	 *
	 * @return the class pk of this akismet entry
	 */
	@Override
	public long getClassPK() {
		return _akismetEntry.getClassPK();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _akismetEntry.getExpandoBridge();
	}

	/**
	 * Returns the modified date of this akismet entry.
	 *
	 * @return the modified date of this akismet entry
	 */
	@Override
	public Date getModifiedDate() {
		return _akismetEntry.getModifiedDate();
	}

	/**
	 * Returns the permalink of this akismet entry.
	 *
	 * @return the permalink of this akismet entry
	 */
	@Override
	public String getPermalink() {
		return _akismetEntry.getPermalink();
	}

	/**
	 * Returns the primary key of this akismet entry.
	 *
	 * @return the primary key of this akismet entry
	 */
	@Override
	public long getPrimaryKey() {
		return _akismetEntry.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _akismetEntry.getPrimaryKeyObj();
	}

	/**
	 * Returns the referrer of this akismet entry.
	 *
	 * @return the referrer of this akismet entry
	 */
	@Override
	public String getReferrer() {
		return _akismetEntry.getReferrer();
	}

	/**
	 * Returns the type of this akismet entry.
	 *
	 * @return the type of this akismet entry
	 */
	@Override
	public String getType() {
		return _akismetEntry.getType();
	}

	/**
	 * Returns the user agent of this akismet entry.
	 *
	 * @return the user agent of this akismet entry
	 */
	@Override
	public String getUserAgent() {
		return _akismetEntry.getUserAgent();
	}

	/**
	 * Returns the user ip of this akismet entry.
	 *
	 * @return the user ip of this akismet entry
	 */
	@Override
	public String getUserIP() {
		return _akismetEntry.getUserIP();
	}

	/**
	 * Returns the user url of this akismet entry.
	 *
	 * @return the user url of this akismet entry
	 */
	@Override
	public String getUserURL() {
		return _akismetEntry.getUserURL();
	}

	@Override
	public int hashCode() {
		return _akismetEntry.hashCode();
	}

	@Override
	public boolean isCachedModel() {
		return _akismetEntry.isCachedModel();
	}

	@Override
	public boolean isEscapedModel() {
		return _akismetEntry.isEscapedModel();
	}

	@Override
	public boolean isNew() {
		return _akismetEntry.isNew();
	}

	@Override
	public void persist() {
		_akismetEntry.persist();
	}

	/**
	 * Sets the akismet entry ID of this akismet entry.
	 *
	 * @param akismetEntryId the akismet entry ID of this akismet entry
	 */
	@Override
	public void setAkismetEntryId(long akismetEntryId) {
		_akismetEntry.setAkismetEntryId(akismetEntryId);
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_akismetEntry.setCachedModel(cachedModel);
	}

	@Override
	public void setClassName(String className) {
		_akismetEntry.setClassName(className);
	}

	/**
	 * Sets the class name ID of this akismet entry.
	 *
	 * @param classNameId the class name ID of this akismet entry
	 */
	@Override
	public void setClassNameId(long classNameId) {
		_akismetEntry.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this akismet entry.
	 *
	 * @param classPK the class pk of this akismet entry
	 */
	@Override
	public void setClassPK(long classPK) {
		_akismetEntry.setClassPK(classPK);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.kernel.model.BaseModel<?> baseModel) {

		_akismetEntry.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		_akismetEntry.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		_akismetEntry.setExpandoBridgeAttributes(serviceContext);
	}

	/**
	 * Sets the modified date of this akismet entry.
	 *
	 * @param modifiedDate the modified date of this akismet entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		_akismetEntry.setModifiedDate(modifiedDate);
	}

	@Override
	public void setNew(boolean n) {
		_akismetEntry.setNew(n);
	}

	/**
	 * Sets the permalink of this akismet entry.
	 *
	 * @param permalink the permalink of this akismet entry
	 */
	@Override
	public void setPermalink(String permalink) {
		_akismetEntry.setPermalink(permalink);
	}

	/**
	 * Sets the primary key of this akismet entry.
	 *
	 * @param primaryKey the primary key of this akismet entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		_akismetEntry.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_akismetEntry.setPrimaryKeyObj(primaryKeyObj);
	}

	/**
	 * Sets the referrer of this akismet entry.
	 *
	 * @param referrer the referrer of this akismet entry
	 */
	@Override
	public void setReferrer(String referrer) {
		_akismetEntry.setReferrer(referrer);
	}

	/**
	 * Sets the type of this akismet entry.
	 *
	 * @param type the type of this akismet entry
	 */
	@Override
	public void setType(String type) {
		_akismetEntry.setType(type);
	}

	/**
	 * Sets the user agent of this akismet entry.
	 *
	 * @param userAgent the user agent of this akismet entry
	 */
	@Override
	public void setUserAgent(String userAgent) {
		_akismetEntry.setUserAgent(userAgent);
	}

	/**
	 * Sets the user ip of this akismet entry.
	 *
	 * @param userIP the user ip of this akismet entry
	 */
	@Override
	public void setUserIP(String userIP) {
		_akismetEntry.setUserIP(userIP);
	}

	/**
	 * Sets the user url of this akismet entry.
	 *
	 * @param userURL the user url of this akismet entry
	 */
	@Override
	public void setUserURL(String userURL) {
		_akismetEntry.setUserURL(userURL);
	}

	@Override
	public com.liferay.portal.kernel.model.CacheModel<AkismetEntry>
		toCacheModel() {

		return _akismetEntry.toCacheModel();
	}

	@Override
	public AkismetEntry toEscapedModel() {
		return new AkismetEntryWrapper(_akismetEntry.toEscapedModel());
	}

	@Override
	public String toString() {
		return _akismetEntry.toString();
	}

	@Override
	public AkismetEntry toUnescapedModel() {
		return new AkismetEntryWrapper(_akismetEntry.toUnescapedModel());
	}

	@Override
	public String toXmlString() {
		return _akismetEntry.toXmlString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AkismetEntryWrapper)) {
			return false;
		}

		AkismetEntryWrapper akismetEntryWrapper = (AkismetEntryWrapper)obj;

		if (Objects.equals(_akismetEntry, akismetEntryWrapper._akismetEntry)) {
			return true;
		}

		return false;
	}

	@Override
	public AkismetEntry getWrappedModel() {
		return _akismetEntry;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return _akismetEntry.isEntityCacheEnabled();
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _akismetEntry.isFinderCacheEnabled();
	}

	@Override
	public void resetOriginalValues() {
		_akismetEntry.resetOriginalValues();
	}

	private final AkismetEntry _akismetEntry;

}