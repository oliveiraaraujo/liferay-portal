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

import aQute.bnd.annotation.ProviderType;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.model.AttachedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.service.ServiceContext;

import java.io.Serializable;

import java.util.Date;

/**
 * The base model interface for the AkismetEntry service. Represents a row in the &quot;OSBCommunity_AkismetEntry&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation <code>com.liferay.akismet.model.impl.AkismetEntryModelImpl</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in <code>com.liferay.akismet.model.impl.AkismetEntryImpl</code>.
 * </p>
 *
 * @author Jamie Sammons
 * @see AkismetEntry
 * @generated
 */
@ProviderType
public interface AkismetEntryModel
	extends AttachedModel, BaseModel<AkismetEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a akismet entry model instance should use the {@link AkismetEntry} interface instead.
	 */

	/**
	 * Returns the primary key of this akismet entry.
	 *
	 * @return the primary key of this akismet entry
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this akismet entry.
	 *
	 * @param primaryKey the primary key of this akismet entry
	 */
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the akismet entry ID of this akismet entry.
	 *
	 * @return the akismet entry ID of this akismet entry
	 */
	public long getAkismetEntryId();

	/**
	 * Sets the akismet entry ID of this akismet entry.
	 *
	 * @param akismetEntryId the akismet entry ID of this akismet entry
	 */
	public void setAkismetEntryId(long akismetEntryId);

	/**
	 * Returns the modified date of this akismet entry.
	 *
	 * @return the modified date of this akismet entry
	 */
	public Date getModifiedDate();

	/**
	 * Sets the modified date of this akismet entry.
	 *
	 * @param modifiedDate the modified date of this akismet entry
	 */
	public void setModifiedDate(Date modifiedDate);

	/**
	 * Returns the fully qualified class name of this akismet entry.
	 *
	 * @return the fully qualified class name of this akismet entry
	 */
	@Override
	public String getClassName();

	public void setClassName(String className);

	/**
	 * Returns the class name ID of this akismet entry.
	 *
	 * @return the class name ID of this akismet entry
	 */
	@Override
	public long getClassNameId();

	/**
	 * Sets the class name ID of this akismet entry.
	 *
	 * @param classNameId the class name ID of this akismet entry
	 */
	@Override
	public void setClassNameId(long classNameId);

	/**
	 * Returns the class pk of this akismet entry.
	 *
	 * @return the class pk of this akismet entry
	 */
	@Override
	public long getClassPK();

	/**
	 * Sets the class pk of this akismet entry.
	 *
	 * @param classPK the class pk of this akismet entry
	 */
	@Override
	public void setClassPK(long classPK);

	/**
	 * Returns the type of this akismet entry.
	 *
	 * @return the type of this akismet entry
	 */
	@AutoEscape
	public String getType();

	/**
	 * Sets the type of this akismet entry.
	 *
	 * @param type the type of this akismet entry
	 */
	public void setType(String type);

	/**
	 * Returns the permalink of this akismet entry.
	 *
	 * @return the permalink of this akismet entry
	 */
	@AutoEscape
	public String getPermalink();

	/**
	 * Sets the permalink of this akismet entry.
	 *
	 * @param permalink the permalink of this akismet entry
	 */
	public void setPermalink(String permalink);

	/**
	 * Returns the referrer of this akismet entry.
	 *
	 * @return the referrer of this akismet entry
	 */
	@AutoEscape
	public String getReferrer();

	/**
	 * Sets the referrer of this akismet entry.
	 *
	 * @param referrer the referrer of this akismet entry
	 */
	public void setReferrer(String referrer);

	/**
	 * Returns the user agent of this akismet entry.
	 *
	 * @return the user agent of this akismet entry
	 */
	@AutoEscape
	public String getUserAgent();

	/**
	 * Sets the user agent of this akismet entry.
	 *
	 * @param userAgent the user agent of this akismet entry
	 */
	public void setUserAgent(String userAgent);

	/**
	 * Returns the user ip of this akismet entry.
	 *
	 * @return the user ip of this akismet entry
	 */
	@AutoEscape
	public String getUserIP();

	/**
	 * Sets the user ip of this akismet entry.
	 *
	 * @param userIP the user ip of this akismet entry
	 */
	public void setUserIP(String userIP);

	/**
	 * Returns the user url of this akismet entry.
	 *
	 * @return the user url of this akismet entry
	 */
	@AutoEscape
	public String getUserURL();

	/**
	 * Sets the user url of this akismet entry.
	 *
	 * @param userURL the user url of this akismet entry
	 */
	public void setUserURL(String userURL);

	@Override
	public boolean isNew();

	@Override
	public void setNew(boolean n);

	@Override
	public boolean isCachedModel();

	@Override
	public void setCachedModel(boolean cachedModel);

	@Override
	public boolean isEscapedModel();

	@Override
	public Serializable getPrimaryKeyObj();

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj);

	@Override
	public ExpandoBridge getExpandoBridge();

	@Override
	public void setExpandoBridgeAttributes(BaseModel<?> baseModel);

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge);

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext);

	@Override
	public Object clone();

	@Override
	public int compareTo(AkismetEntry akismetEntry);

	@Override
	public int hashCode();

	@Override
	public CacheModel<AkismetEntry> toCacheModel();

	@Override
	public AkismetEntry toEscapedModel();

	@Override
	public AkismetEntry toUnescapedModel();

	@Override
	public String toString();

	@Override
	public String toXmlString();

}