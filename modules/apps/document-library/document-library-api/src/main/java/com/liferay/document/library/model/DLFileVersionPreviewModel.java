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

package com.liferay.document.library.model;

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ShardedModel;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The base model interface for the DLFileVersionPreview service. Represents a row in the &quot;DLFileVersionPreview&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation <code>com.liferay.document.library.model.impl.DLFileVersionPreviewModelImpl</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in <code>com.liferay.document.library.model.impl.DLFileVersionPreviewImpl</code>.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DLFileVersionPreview
 * @generated
 */
@ProviderType
public interface DLFileVersionPreviewModel
	extends BaseModel<DLFileVersionPreview>, ShardedModel {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a dl file version preview model instance should use the {@link DLFileVersionPreview} interface instead.
	 */

	/**
	 * Returns the primary key of this dl file version preview.
	 *
	 * @return the primary key of this dl file version preview
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this dl file version preview.
	 *
	 * @param primaryKey the primary key of this dl file version preview
	 */
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the dl file version preview ID of this dl file version preview.
	 *
	 * @return the dl file version preview ID of this dl file version preview
	 */
	public long getDlFileVersionPreviewId();

	/**
	 * Sets the dl file version preview ID of this dl file version preview.
	 *
	 * @param dlFileVersionPreviewId the dl file version preview ID of this dl file version preview
	 */
	public void setDlFileVersionPreviewId(long dlFileVersionPreviewId);

	/**
	 * Returns the group ID of this dl file version preview.
	 *
	 * @return the group ID of this dl file version preview
	 */
	public long getGroupId();

	/**
	 * Sets the group ID of this dl file version preview.
	 *
	 * @param groupId the group ID of this dl file version preview
	 */
	public void setGroupId(long groupId);

	/**
	 * Returns the company ID of this dl file version preview.
	 *
	 * @return the company ID of this dl file version preview
	 */
	@Override
	public long getCompanyId();

	/**
	 * Sets the company ID of this dl file version preview.
	 *
	 * @param companyId the company ID of this dl file version preview
	 */
	@Override
	public void setCompanyId(long companyId);

	/**
	 * Returns the file entry ID of this dl file version preview.
	 *
	 * @return the file entry ID of this dl file version preview
	 */
	public long getFileEntryId();

	/**
	 * Sets the file entry ID of this dl file version preview.
	 *
	 * @param fileEntryId the file entry ID of this dl file version preview
	 */
	public void setFileEntryId(long fileEntryId);

	/**
	 * Returns the file version ID of this dl file version preview.
	 *
	 * @return the file version ID of this dl file version preview
	 */
	public long getFileVersionId();

	/**
	 * Sets the file version ID of this dl file version preview.
	 *
	 * @param fileVersionId the file version ID of this dl file version preview
	 */
	public void setFileVersionId(long fileVersionId);

	/**
	 * Returns the preview status of this dl file version preview.
	 *
	 * @return the preview status of this dl file version preview
	 */
	public int getPreviewStatus();

	/**
	 * Sets the preview status of this dl file version preview.
	 *
	 * @param previewStatus the preview status of this dl file version preview
	 */
	public void setPreviewStatus(int previewStatus);

}