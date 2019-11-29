/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.fragment.model.impl;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryModel;
import com.liferay.fragment.model.FragmentEntrySoap;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The base model implementation for the FragmentEntry service. Represents a row in the &quot;FragmentEntry&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface </code>FragmentEntryModel</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link FragmentEntryImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryImpl
 * @generated
 */
@JSON(strict = true)
public class FragmentEntryModelImpl
	extends BaseModelImpl<FragmentEntry> implements FragmentEntryModel {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a fragment entry model instance should use the <code>FragmentEntry</code> interface instead.
	 */
	public static final String TABLE_NAME = "FragmentEntry";

	public static final Object[][] TABLE_COLUMNS = {
		{"mvccVersion", Types.BIGINT}, {"uuid_", Types.VARCHAR},
		{"fragmentEntryId", Types.BIGINT}, {"groupId", Types.BIGINT},
		{"companyId", Types.BIGINT}, {"userId", Types.BIGINT},
		{"userName", Types.VARCHAR}, {"createDate", Types.TIMESTAMP},
		{"modifiedDate", Types.TIMESTAMP},
		{"fragmentCollectionId", Types.BIGINT},
		{"fragmentEntryKey", Types.VARCHAR}, {"name", Types.VARCHAR},
		{"css", Types.CLOB}, {"html", Types.CLOB}, {"js", Types.CLOB},
		{"configuration", Types.CLOB}, {"previewFileEntryId", Types.BIGINT},
		{"type_", Types.INTEGER}, {"readOnly", Types.BOOLEAN},
		{"lastPublishDate", Types.TIMESTAMP}, {"status", Types.INTEGER},
		{"statusByUserId", Types.BIGINT}, {"statusByUserName", Types.VARCHAR},
		{"statusDate", Types.TIMESTAMP}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
		new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("mvccVersion", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("uuid_", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("fragmentEntryId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("groupId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("modifiedDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("fragmentCollectionId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("fragmentEntryKey", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("name", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("css", Types.CLOB);
		TABLE_COLUMNS_MAP.put("html", Types.CLOB);
		TABLE_COLUMNS_MAP.put("js", Types.CLOB);
		TABLE_COLUMNS_MAP.put("configuration", Types.CLOB);
		TABLE_COLUMNS_MAP.put("previewFileEntryId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("type_", Types.INTEGER);
		TABLE_COLUMNS_MAP.put("readOnly", Types.BOOLEAN);
		TABLE_COLUMNS_MAP.put("lastPublishDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("status", Types.INTEGER);
		TABLE_COLUMNS_MAP.put("statusByUserId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("statusByUserName", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("statusDate", Types.TIMESTAMP);
	}

	public static final String TABLE_SQL_CREATE =
		"create table FragmentEntry (mvccVersion LONG default 0 not null,uuid_ VARCHAR(75) null,fragmentEntryId LONG not null primary key,groupId LONG,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,fragmentCollectionId LONG,fragmentEntryKey VARCHAR(75) null,name VARCHAR(75) null,css TEXT null,html TEXT null,js TEXT null,configuration TEXT null,previewFileEntryId LONG,type_ INTEGER,readOnly BOOLEAN,lastPublishDate DATE null,status INTEGER,statusByUserId LONG,statusByUserName VARCHAR(75) null,statusDate DATE null)";

	public static final String TABLE_SQL_DROP = "drop table FragmentEntry";

	public static final String ORDER_BY_JPQL =
		" ORDER BY fragmentEntry.name ASC";

	public static final String ORDER_BY_SQL =
		" ORDER BY FragmentEntry.name ASC";

	public static final String DATA_SOURCE = "liferayDataSource";

	public static final String SESSION_FACTORY = "liferaySessionFactory";

	public static final String TX_MANAGER = "liferayTransactionManager";

	public static final long COMPANYID_COLUMN_BITMASK = 1L;

	public static final long FRAGMENTCOLLECTIONID_COLUMN_BITMASK = 2L;

	public static final long FRAGMENTENTRYKEY_COLUMN_BITMASK = 4L;

	public static final long GROUPID_COLUMN_BITMASK = 8L;

	public static final long NAME_COLUMN_BITMASK = 16L;

	public static final long STATUS_COLUMN_BITMASK = 32L;

	public static final long TYPE_COLUMN_BITMASK = 64L;

	public static final long UUID_COLUMN_BITMASK = 128L;

	public static void setEntityCacheEnabled(boolean entityCacheEnabled) {
		_entityCacheEnabled = entityCacheEnabled;
	}

	public static void setFinderCacheEnabled(boolean finderCacheEnabled) {
		_finderCacheEnabled = finderCacheEnabled;
	}

	/**
	 * Converts the soap model instance into a normal model instance.
	 *
	 * @param soapModel the soap model instance to convert
	 * @return the normal model instance
	 */
	public static FragmentEntry toModel(FragmentEntrySoap soapModel) {
		if (soapModel == null) {
			return null;
		}

		FragmentEntry model = new FragmentEntryImpl();

		model.setMvccVersion(soapModel.getMvccVersion());
		model.setUuid(soapModel.getUuid());
		model.setFragmentEntryId(soapModel.getFragmentEntryId());
		model.setGroupId(soapModel.getGroupId());
		model.setCompanyId(soapModel.getCompanyId());
		model.setUserId(soapModel.getUserId());
		model.setUserName(soapModel.getUserName());
		model.setCreateDate(soapModel.getCreateDate());
		model.setModifiedDate(soapModel.getModifiedDate());
		model.setFragmentCollectionId(soapModel.getFragmentCollectionId());
		model.setFragmentEntryKey(soapModel.getFragmentEntryKey());
		model.setName(soapModel.getName());
		model.setCss(soapModel.getCss());
		model.setHtml(soapModel.getHtml());
		model.setJs(soapModel.getJs());
		model.setConfiguration(soapModel.getConfiguration());
		model.setPreviewFileEntryId(soapModel.getPreviewFileEntryId());
		model.setType(soapModel.getType());
		model.setReadOnly(soapModel.isReadOnly());
		model.setLastPublishDate(soapModel.getLastPublishDate());
		model.setStatus(soapModel.getStatus());
		model.setStatusByUserId(soapModel.getStatusByUserId());
		model.setStatusByUserName(soapModel.getStatusByUserName());
		model.setStatusDate(soapModel.getStatusDate());

		return model;
	}

	/**
	 * Converts the soap model instances into normal model instances.
	 *
	 * @param soapModels the soap model instances to convert
	 * @return the normal model instances
	 */
	public static List<FragmentEntry> toModels(FragmentEntrySoap[] soapModels) {
		if (soapModels == null) {
			return null;
		}

		List<FragmentEntry> models = new ArrayList<FragmentEntry>(
			soapModels.length);

		for (FragmentEntrySoap soapModel : soapModels) {
			models.add(toModel(soapModel));
		}

		return models;
	}

	public FragmentEntryModelImpl() {
	}

	@Override
	public long getPrimaryKey() {
		return _fragmentEntryId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setFragmentEntryId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _fragmentEntryId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Class<?> getModelClass() {
		return FragmentEntry.class;
	}

	@Override
	public String getModelClassName() {
		return FragmentEntry.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		Map<String, Function<FragmentEntry, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		for (Map.Entry<String, Function<FragmentEntry, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<FragmentEntry, Object> attributeGetterFunction =
				entry.getValue();

			attributes.put(
				attributeName,
				attributeGetterFunction.apply((FragmentEntry)this));
		}

		attributes.put("entityCacheEnabled", isEntityCacheEnabled());
		attributes.put("finderCacheEnabled", isFinderCacheEnabled());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Map<String, BiConsumer<FragmentEntry, Object>>
			attributeSetterBiConsumers = getAttributeSetterBiConsumers();

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attributeName = entry.getKey();

			BiConsumer<FragmentEntry, Object> attributeSetterBiConsumer =
				attributeSetterBiConsumers.get(attributeName);

			if (attributeSetterBiConsumer != null) {
				attributeSetterBiConsumer.accept(
					(FragmentEntry)this, entry.getValue());
			}
		}
	}

	public Map<String, Function<FragmentEntry, Object>>
		getAttributeGetterFunctions() {

		return _attributeGetterFunctions;
	}

	public Map<String, BiConsumer<FragmentEntry, Object>>
		getAttributeSetterBiConsumers() {

		return _attributeSetterBiConsumers;
	}

	private static Function<InvocationHandler, FragmentEntry>
		_getProxyProviderFunction() {

		Class<?> proxyClass = ProxyUtil.getProxyClass(
			FragmentEntry.class.getClassLoader(), FragmentEntry.class,
			ModelWrapper.class);

		try {
			Constructor<FragmentEntry> constructor =
				(Constructor<FragmentEntry>)proxyClass.getConstructor(
					InvocationHandler.class);

			return invocationHandler -> {
				try {
					return constructor.newInstance(invocationHandler);
				}
				catch (ReflectiveOperationException roe) {
					throw new InternalError(roe);
				}
			};
		}
		catch (NoSuchMethodException nsme) {
			throw new InternalError(nsme);
		}
	}

	private static final Map<String, Function<FragmentEntry, Object>>
		_attributeGetterFunctions;
	private static final Map<String, BiConsumer<FragmentEntry, Object>>
		_attributeSetterBiConsumers;

	static {
		Map<String, Function<FragmentEntry, Object>> attributeGetterFunctions =
			new LinkedHashMap<String, Function<FragmentEntry, Object>>();
		Map<String, BiConsumer<FragmentEntry, ?>> attributeSetterBiConsumers =
			new LinkedHashMap<String, BiConsumer<FragmentEntry, ?>>();

		attributeGetterFunctions.put(
			"mvccVersion", FragmentEntry::getMvccVersion);
		attributeSetterBiConsumers.put(
			"mvccVersion",
			(BiConsumer<FragmentEntry, Long>)FragmentEntry::setMvccVersion);
		attributeGetterFunctions.put("uuid", FragmentEntry::getUuid);
		attributeSetterBiConsumers.put(
			"uuid", (BiConsumer<FragmentEntry, String>)FragmentEntry::setUuid);
		attributeGetterFunctions.put(
			"fragmentEntryId", FragmentEntry::getFragmentEntryId);
		attributeSetterBiConsumers.put(
			"fragmentEntryId",
			(BiConsumer<FragmentEntry, Long>)FragmentEntry::setFragmentEntryId);
		attributeGetterFunctions.put("groupId", FragmentEntry::getGroupId);
		attributeSetterBiConsumers.put(
			"groupId",
			(BiConsumer<FragmentEntry, Long>)FragmentEntry::setGroupId);
		attributeGetterFunctions.put("companyId", FragmentEntry::getCompanyId);
		attributeSetterBiConsumers.put(
			"companyId",
			(BiConsumer<FragmentEntry, Long>)FragmentEntry::setCompanyId);
		attributeGetterFunctions.put("userId", FragmentEntry::getUserId);
		attributeSetterBiConsumers.put(
			"userId",
			(BiConsumer<FragmentEntry, Long>)FragmentEntry::setUserId);
		attributeGetterFunctions.put("userName", FragmentEntry::getUserName);
		attributeSetterBiConsumers.put(
			"userName",
			(BiConsumer<FragmentEntry, String>)FragmentEntry::setUserName);
		attributeGetterFunctions.put(
			"createDate", FragmentEntry::getCreateDate);
		attributeSetterBiConsumers.put(
			"createDate",
			(BiConsumer<FragmentEntry, Date>)FragmentEntry::setCreateDate);
		attributeGetterFunctions.put(
			"modifiedDate", FragmentEntry::getModifiedDate);
		attributeSetterBiConsumers.put(
			"modifiedDate",
			(BiConsumer<FragmentEntry, Date>)FragmentEntry::setModifiedDate);
		attributeGetterFunctions.put(
			"fragmentCollectionId", FragmentEntry::getFragmentCollectionId);
		attributeSetterBiConsumers.put(
			"fragmentCollectionId",
			(BiConsumer<FragmentEntry, Long>)
				FragmentEntry::setFragmentCollectionId);
		attributeGetterFunctions.put(
			"fragmentEntryKey", FragmentEntry::getFragmentEntryKey);
		attributeSetterBiConsumers.put(
			"fragmentEntryKey",
			(BiConsumer<FragmentEntry, String>)
				FragmentEntry::setFragmentEntryKey);
		attributeGetterFunctions.put("name", FragmentEntry::getName);
		attributeSetterBiConsumers.put(
			"name", (BiConsumer<FragmentEntry, String>)FragmentEntry::setName);
		attributeGetterFunctions.put("css", FragmentEntry::getCss);
		attributeSetterBiConsumers.put(
			"css", (BiConsumer<FragmentEntry, String>)FragmentEntry::setCss);
		attributeGetterFunctions.put("html", FragmentEntry::getHtml);
		attributeSetterBiConsumers.put(
			"html", (BiConsumer<FragmentEntry, String>)FragmentEntry::setHtml);
		attributeGetterFunctions.put("js", FragmentEntry::getJs);
		attributeSetterBiConsumers.put(
			"js", (BiConsumer<FragmentEntry, String>)FragmentEntry::setJs);
		attributeGetterFunctions.put(
			"configuration", FragmentEntry::getConfiguration);
		attributeSetterBiConsumers.put(
			"configuration",
			(BiConsumer<FragmentEntry, String>)FragmentEntry::setConfiguration);
		attributeGetterFunctions.put(
			"previewFileEntryId", FragmentEntry::getPreviewFileEntryId);
		attributeSetterBiConsumers.put(
			"previewFileEntryId",
			(BiConsumer<FragmentEntry, Long>)
				FragmentEntry::setPreviewFileEntryId);
		attributeGetterFunctions.put("type", FragmentEntry::getType);
		attributeSetterBiConsumers.put(
			"type", (BiConsumer<FragmentEntry, Integer>)FragmentEntry::setType);
		attributeGetterFunctions.put("readOnly", FragmentEntry::getReadOnly);
		attributeSetterBiConsumers.put(
			"readOnly",
			(BiConsumer<FragmentEntry, Boolean>)FragmentEntry::setReadOnly);
		attributeGetterFunctions.put(
			"lastPublishDate", FragmentEntry::getLastPublishDate);
		attributeSetterBiConsumers.put(
			"lastPublishDate",
			(BiConsumer<FragmentEntry, Date>)FragmentEntry::setLastPublishDate);
		attributeGetterFunctions.put("status", FragmentEntry::getStatus);
		attributeSetterBiConsumers.put(
			"status",
			(BiConsumer<FragmentEntry, Integer>)FragmentEntry::setStatus);
		attributeGetterFunctions.put(
			"statusByUserId", FragmentEntry::getStatusByUserId);
		attributeSetterBiConsumers.put(
			"statusByUserId",
			(BiConsumer<FragmentEntry, Long>)FragmentEntry::setStatusByUserId);
		attributeGetterFunctions.put(
			"statusByUserName", FragmentEntry::getStatusByUserName);
		attributeSetterBiConsumers.put(
			"statusByUserName",
			(BiConsumer<FragmentEntry, String>)
				FragmentEntry::setStatusByUserName);
		attributeGetterFunctions.put(
			"statusDate", FragmentEntry::getStatusDate);
		attributeSetterBiConsumers.put(
			"statusDate",
			(BiConsumer<FragmentEntry, Date>)FragmentEntry::setStatusDate);

		_attributeGetterFunctions = Collections.unmodifiableMap(
			attributeGetterFunctions);
		_attributeSetterBiConsumers = Collections.unmodifiableMap(
			(Map)attributeSetterBiConsumers);
	}

	@JSON
	@Override
	public long getMvccVersion() {
		return _mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		_mvccVersion = mvccVersion;
	}

	@JSON
	@Override
	public String getUuid() {
		if (_uuid == null) {
			return "";
		}
		else {
			return _uuid;
		}
	}

	@Override
	public void setUuid(String uuid) {
		_columnBitmask |= UUID_COLUMN_BITMASK;

		if (_originalUuid == null) {
			_originalUuid = _uuid;
		}

		_uuid = uuid;
	}

	public String getOriginalUuid() {
		return GetterUtil.getString(_originalUuid);
	}

	@JSON
	@Override
	public long getFragmentEntryId() {
		return _fragmentEntryId;
	}

	@Override
	public void setFragmentEntryId(long fragmentEntryId) {
		_fragmentEntryId = fragmentEntryId;
	}

	@JSON
	@Override
	public long getGroupId() {
		return _groupId;
	}

	@Override
	public void setGroupId(long groupId) {
		_columnBitmask |= GROUPID_COLUMN_BITMASK;

		if (!_setOriginalGroupId) {
			_setOriginalGroupId = true;

			_originalGroupId = _groupId;
		}

		_groupId = groupId;
	}

	public long getOriginalGroupId() {
		return _originalGroupId;
	}

	@JSON
	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public void setCompanyId(long companyId) {
		_columnBitmask |= COMPANYID_COLUMN_BITMASK;

		if (!_setOriginalCompanyId) {
			_setOriginalCompanyId = true;

			_originalCompanyId = _companyId;
		}

		_companyId = companyId;
	}

	public long getOriginalCompanyId() {
		return _originalCompanyId;
	}

	@JSON
	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setUserId(long userId) {
		_userId = userId;
	}

	@Override
	public String getUserUuid() {
		try {
			User user = UserLocalServiceUtil.getUserById(getUserId());

			return user.getUuid();
		}
		catch (PortalException pe) {
			return "";
		}
	}

	@Override
	public void setUserUuid(String userUuid) {
	}

	@JSON
	@Override
	public String getUserName() {
		if (_userName == null) {
			return "";
		}
		else {
			return _userName;
		}
	}

	@Override
	public void setUserName(String userName) {
		_userName = userName;
	}

	@JSON
	@Override
	public Date getCreateDate() {
		return _createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	@JSON
	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public boolean hasSetModifiedDate() {
		return _setModifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_setModifiedDate = true;

		_modifiedDate = modifiedDate;
	}

	@JSON
	@Override
	public long getFragmentCollectionId() {
		return _fragmentCollectionId;
	}

	@Override
	public void setFragmentCollectionId(long fragmentCollectionId) {
		_columnBitmask |= FRAGMENTCOLLECTIONID_COLUMN_BITMASK;

		if (!_setOriginalFragmentCollectionId) {
			_setOriginalFragmentCollectionId = true;

			_originalFragmentCollectionId = _fragmentCollectionId;
		}

		_fragmentCollectionId = fragmentCollectionId;
	}

	public long getOriginalFragmentCollectionId() {
		return _originalFragmentCollectionId;
	}

	@JSON
	@Override
	public String getFragmentEntryKey() {
		if (_fragmentEntryKey == null) {
			return "";
		}
		else {
			return _fragmentEntryKey;
		}
	}

	@Override
	public void setFragmentEntryKey(String fragmentEntryKey) {
		_columnBitmask |= FRAGMENTENTRYKEY_COLUMN_BITMASK;

		if (_originalFragmentEntryKey == null) {
			_originalFragmentEntryKey = _fragmentEntryKey;
		}

		_fragmentEntryKey = fragmentEntryKey;
	}

	public String getOriginalFragmentEntryKey() {
		return GetterUtil.getString(_originalFragmentEntryKey);
	}

	@JSON
	@Override
	public String getName() {
		if (_name == null) {
			return "";
		}
		else {
			return _name;
		}
	}

	@Override
	public void setName(String name) {
		_columnBitmask = -1L;

		if (_originalName == null) {
			_originalName = _name;
		}

		_name = name;
	}

	public String getOriginalName() {
		return GetterUtil.getString(_originalName);
	}

	@JSON
	@Override
	public String getCss() {
		if (_css == null) {
			return "";
		}
		else {
			return _css;
		}
	}

	@Override
	public void setCss(String css) {
		_css = css;
	}

	@JSON
	@Override
	public String getHtml() {
		if (_html == null) {
			return "";
		}
		else {
			return _html;
		}
	}

	@Override
	public void setHtml(String html) {
		_html = html;
	}

	@JSON
	@Override
	public String getJs() {
		if (_js == null) {
			return "";
		}
		else {
			return _js;
		}
	}

	@Override
	public void setJs(String js) {
		_js = js;
	}

	@JSON
	@Override
	public String getConfiguration() {
		if (_configuration == null) {
			return "";
		}
		else {
			return _configuration;
		}
	}

	@Override
	public void setConfiguration(String configuration) {
		_configuration = configuration;
	}

	@JSON
	@Override
	public long getPreviewFileEntryId() {
		return _previewFileEntryId;
	}

	@Override
	public void setPreviewFileEntryId(long previewFileEntryId) {
		_previewFileEntryId = previewFileEntryId;
	}

	@JSON
	@Override
	public int getType() {
		return _type;
	}

	@Override
	public void setType(int type) {
		_columnBitmask |= TYPE_COLUMN_BITMASK;

		if (!_setOriginalType) {
			_setOriginalType = true;

			_originalType = _type;
		}

		_type = type;
	}

	public int getOriginalType() {
		return _originalType;
	}

	@JSON
	@Override
	public boolean getReadOnly() {
		return _readOnly;
	}

	@JSON
	@Override
	public boolean isReadOnly() {
		return _readOnly;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	@JSON
	@Override
	public Date getLastPublishDate() {
		return _lastPublishDate;
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_lastPublishDate = lastPublishDate;
	}

	@JSON
	@Override
	public int getStatus() {
		return _status;
	}

	@Override
	public void setStatus(int status) {
		_columnBitmask |= STATUS_COLUMN_BITMASK;

		if (!_setOriginalStatus) {
			_setOriginalStatus = true;

			_originalStatus = _status;
		}

		_status = status;
	}

	public int getOriginalStatus() {
		return _originalStatus;
	}

	@JSON
	@Override
	public long getStatusByUserId() {
		return _statusByUserId;
	}

	@Override
	public void setStatusByUserId(long statusByUserId) {
		_statusByUserId = statusByUserId;
	}

	@Override
	public String getStatusByUserUuid() {
		try {
			User user = UserLocalServiceUtil.getUserById(getStatusByUserId());

			return user.getUuid();
		}
		catch (PortalException pe) {
			return "";
		}
	}

	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
	}

	@JSON
	@Override
	public String getStatusByUserName() {
		if (_statusByUserName == null) {
			return "";
		}
		else {
			return _statusByUserName;
		}
	}

	@Override
	public void setStatusByUserName(String statusByUserName) {
		_statusByUserName = statusByUserName;
	}

	@JSON
	@Override
	public Date getStatusDate() {
		return _statusDate;
	}

	@Override
	public void setStatusDate(Date statusDate) {
		_statusDate = statusDate;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(
			PortalUtil.getClassNameId(FragmentEntry.class.getName()));
	}

	@Override
	public boolean isApproved() {
		if (getStatus() == WorkflowConstants.STATUS_APPROVED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isDenied() {
		if (getStatus() == WorkflowConstants.STATUS_DENIED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isDraft() {
		if (getStatus() == WorkflowConstants.STATUS_DRAFT) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isExpired() {
		if (getStatus() == WorkflowConstants.STATUS_EXPIRED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isInactive() {
		if (getStatus() == WorkflowConstants.STATUS_INACTIVE) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isIncomplete() {
		if (getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isPending() {
		if (getStatus() == WorkflowConstants.STATUS_PENDING) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isScheduled() {
		if (getStatus() == WorkflowConstants.STATUS_SCHEDULED) {
			return true;
		}
		else {
			return false;
		}
	}

	public long getColumnBitmask() {
		return _columnBitmask;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(
			getCompanyId(), FragmentEntry.class.getName(), getPrimaryKey());
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		ExpandoBridge expandoBridge = getExpandoBridge();

		expandoBridge.setAttributes(serviceContext);
	}

	@Override
	public FragmentEntry toEscapedModel() {
		if (_escapedModel == null) {
			Function<InvocationHandler, FragmentEntry>
				escapedModelProxyProviderFunction =
					EscapedModelProxyProviderFunctionHolder.
						_escapedModelProxyProviderFunction;

			_escapedModel = escapedModelProxyProviderFunction.apply(
				new AutoEscapeBeanHandler(this));
		}

		return _escapedModel;
	}

	@Override
	public Object clone() {
		FragmentEntryImpl fragmentEntryImpl = new FragmentEntryImpl();

		fragmentEntryImpl.setMvccVersion(getMvccVersion());
		fragmentEntryImpl.setUuid(getUuid());
		fragmentEntryImpl.setFragmentEntryId(getFragmentEntryId());
		fragmentEntryImpl.setGroupId(getGroupId());
		fragmentEntryImpl.setCompanyId(getCompanyId());
		fragmentEntryImpl.setUserId(getUserId());
		fragmentEntryImpl.setUserName(getUserName());
		fragmentEntryImpl.setCreateDate(getCreateDate());
		fragmentEntryImpl.setModifiedDate(getModifiedDate());
		fragmentEntryImpl.setFragmentCollectionId(getFragmentCollectionId());
		fragmentEntryImpl.setFragmentEntryKey(getFragmentEntryKey());
		fragmentEntryImpl.setName(getName());
		fragmentEntryImpl.setCss(getCss());
		fragmentEntryImpl.setHtml(getHtml());
		fragmentEntryImpl.setJs(getJs());
		fragmentEntryImpl.setConfiguration(getConfiguration());
		fragmentEntryImpl.setPreviewFileEntryId(getPreviewFileEntryId());
		fragmentEntryImpl.setType(getType());
		fragmentEntryImpl.setReadOnly(isReadOnly());
		fragmentEntryImpl.setLastPublishDate(getLastPublishDate());
		fragmentEntryImpl.setStatus(getStatus());
		fragmentEntryImpl.setStatusByUserId(getStatusByUserId());
		fragmentEntryImpl.setStatusByUserName(getStatusByUserName());
		fragmentEntryImpl.setStatusDate(getStatusDate());

		fragmentEntryImpl.resetOriginalValues();

		return fragmentEntryImpl;
	}

	@Override
	public int compareTo(FragmentEntry fragmentEntry) {
		int value = 0;

		value = getName().compareTo(fragmentEntry.getName());

		if (value != 0) {
			return value;
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof FragmentEntry)) {
			return false;
		}

		FragmentEntry fragmentEntry = (FragmentEntry)obj;

		long primaryKey = fragmentEntry.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return _entityCacheEnabled;
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _finderCacheEnabled;
	}

	@Override
	public void resetOriginalValues() {
		FragmentEntryModelImpl fragmentEntryModelImpl = this;

		fragmentEntryModelImpl._originalUuid = fragmentEntryModelImpl._uuid;

		fragmentEntryModelImpl._originalGroupId =
			fragmentEntryModelImpl._groupId;

		fragmentEntryModelImpl._setOriginalGroupId = false;

		fragmentEntryModelImpl._originalCompanyId =
			fragmentEntryModelImpl._companyId;

		fragmentEntryModelImpl._setOriginalCompanyId = false;

		fragmentEntryModelImpl._setModifiedDate = false;

		fragmentEntryModelImpl._originalFragmentCollectionId =
			fragmentEntryModelImpl._fragmentCollectionId;

		fragmentEntryModelImpl._setOriginalFragmentCollectionId = false;

		fragmentEntryModelImpl._originalFragmentEntryKey =
			fragmentEntryModelImpl._fragmentEntryKey;

		fragmentEntryModelImpl._originalName = fragmentEntryModelImpl._name;

		fragmentEntryModelImpl._originalType = fragmentEntryModelImpl._type;

		fragmentEntryModelImpl._setOriginalType = false;

		fragmentEntryModelImpl._originalStatus = fragmentEntryModelImpl._status;

		fragmentEntryModelImpl._setOriginalStatus = false;

		fragmentEntryModelImpl._columnBitmask = 0;
	}

	@Override
	public CacheModel<FragmentEntry> toCacheModel() {
		FragmentEntryCacheModel fragmentEntryCacheModel =
			new FragmentEntryCacheModel();

		fragmentEntryCacheModel.mvccVersion = getMvccVersion();

		fragmentEntryCacheModel.uuid = getUuid();

		String uuid = fragmentEntryCacheModel.uuid;

		if ((uuid != null) && (uuid.length() == 0)) {
			fragmentEntryCacheModel.uuid = null;
		}

		fragmentEntryCacheModel.fragmentEntryId = getFragmentEntryId();

		fragmentEntryCacheModel.groupId = getGroupId();

		fragmentEntryCacheModel.companyId = getCompanyId();

		fragmentEntryCacheModel.userId = getUserId();

		fragmentEntryCacheModel.userName = getUserName();

		String userName = fragmentEntryCacheModel.userName;

		if ((userName != null) && (userName.length() == 0)) {
			fragmentEntryCacheModel.userName = null;
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			fragmentEntryCacheModel.createDate = createDate.getTime();
		}
		else {
			fragmentEntryCacheModel.createDate = Long.MIN_VALUE;
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			fragmentEntryCacheModel.modifiedDate = modifiedDate.getTime();
		}
		else {
			fragmentEntryCacheModel.modifiedDate = Long.MIN_VALUE;
		}

		fragmentEntryCacheModel.fragmentCollectionId =
			getFragmentCollectionId();

		fragmentEntryCacheModel.fragmentEntryKey = getFragmentEntryKey();

		String fragmentEntryKey = fragmentEntryCacheModel.fragmentEntryKey;

		if ((fragmentEntryKey != null) && (fragmentEntryKey.length() == 0)) {
			fragmentEntryCacheModel.fragmentEntryKey = null;
		}

		fragmentEntryCacheModel.name = getName();

		String name = fragmentEntryCacheModel.name;

		if ((name != null) && (name.length() == 0)) {
			fragmentEntryCacheModel.name = null;
		}

		fragmentEntryCacheModel.css = getCss();

		String css = fragmentEntryCacheModel.css;

		if ((css != null) && (css.length() == 0)) {
			fragmentEntryCacheModel.css = null;
		}

		fragmentEntryCacheModel.html = getHtml();

		String html = fragmentEntryCacheModel.html;

		if ((html != null) && (html.length() == 0)) {
			fragmentEntryCacheModel.html = null;
		}

		fragmentEntryCacheModel.js = getJs();

		String js = fragmentEntryCacheModel.js;

		if ((js != null) && (js.length() == 0)) {
			fragmentEntryCacheModel.js = null;
		}

		fragmentEntryCacheModel.configuration = getConfiguration();

		String configuration = fragmentEntryCacheModel.configuration;

		if ((configuration != null) && (configuration.length() == 0)) {
			fragmentEntryCacheModel.configuration = null;
		}

		fragmentEntryCacheModel.previewFileEntryId = getPreviewFileEntryId();

		fragmentEntryCacheModel.type = getType();

		fragmentEntryCacheModel.readOnly = isReadOnly();

		Date lastPublishDate = getLastPublishDate();

		if (lastPublishDate != null) {
			fragmentEntryCacheModel.lastPublishDate = lastPublishDate.getTime();
		}
		else {
			fragmentEntryCacheModel.lastPublishDate = Long.MIN_VALUE;
		}

		fragmentEntryCacheModel.status = getStatus();

		fragmentEntryCacheModel.statusByUserId = getStatusByUserId();

		fragmentEntryCacheModel.statusByUserName = getStatusByUserName();

		String statusByUserName = fragmentEntryCacheModel.statusByUserName;

		if ((statusByUserName != null) && (statusByUserName.length() == 0)) {
			fragmentEntryCacheModel.statusByUserName = null;
		}

		Date statusDate = getStatusDate();

		if (statusDate != null) {
			fragmentEntryCacheModel.statusDate = statusDate.getTime();
		}
		else {
			fragmentEntryCacheModel.statusDate = Long.MIN_VALUE;
		}

		return fragmentEntryCacheModel;
	}

	@Override
	public String toString() {
		Map<String, Function<FragmentEntry, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			4 * attributeGetterFunctions.size() + 2);

		sb.append("{");

		for (Map.Entry<String, Function<FragmentEntry, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<FragmentEntry, Object> attributeGetterFunction =
				entry.getValue();

			sb.append(attributeName);
			sb.append("=");
			sb.append(attributeGetterFunction.apply((FragmentEntry)this));
			sb.append(", ");
		}

		if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		Map<String, Function<FragmentEntry, Object>> attributeGetterFunctions =
			getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			5 * attributeGetterFunctions.size() + 4);

		sb.append("<model><model-name>");
		sb.append(getModelClassName());
		sb.append("</model-name>");

		for (Map.Entry<String, Function<FragmentEntry, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<FragmentEntry, Object> attributeGetterFunction =
				entry.getValue();

			sb.append("<column><column-name>");
			sb.append(attributeName);
			sb.append("</column-name><column-value><![CDATA[");
			sb.append(attributeGetterFunction.apply((FragmentEntry)this));
			sb.append("]]></column-value></column>");
		}

		sb.append("</model>");

		return sb.toString();
	}

	private static class EscapedModelProxyProviderFunctionHolder {

		private static final Function<InvocationHandler, FragmentEntry>
			_escapedModelProxyProviderFunction = _getProxyProviderFunction();

	}

	private static boolean _entityCacheEnabled;
	private static boolean _finderCacheEnabled;

	private long _mvccVersion;
	private String _uuid;
	private String _originalUuid;
	private long _fragmentEntryId;
	private long _groupId;
	private long _originalGroupId;
	private boolean _setOriginalGroupId;
	private long _companyId;
	private long _originalCompanyId;
	private boolean _setOriginalCompanyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private boolean _setModifiedDate;
	private long _fragmentCollectionId;
	private long _originalFragmentCollectionId;
	private boolean _setOriginalFragmentCollectionId;
	private String _fragmentEntryKey;
	private String _originalFragmentEntryKey;
	private String _name;
	private String _originalName;
	private String _css;
	private String _html;
	private String _js;
	private String _configuration;
	private long _previewFileEntryId;
	private int _type;
	private int _originalType;
	private boolean _setOriginalType;
	private boolean _readOnly;
	private Date _lastPublishDate;
	private int _status;
	private int _originalStatus;
	private boolean _setOriginalStatus;
	private long _statusByUserId;
	private String _statusByUserName;
	private Date _statusDate;
	private long _columnBitmask;
	private FragmentEntry _escapedModel;

}