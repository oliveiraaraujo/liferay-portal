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

package com.liferay.portal.workflow.metrics.search.index;

import com.liferay.portal.search.document.Document;

import java.util.Date;

/**
 * @author Rafael Praxedes
 */
public interface TaskWorkflowMetricsIndexer {

	public Document addTask(
		Long[] assigneeIds, String assigneeType, String className, long classPK,
		long companyId, boolean completed, Date completionDate,
		Long completionUserId, Date createDate, boolean instanceCompleted,
		long instanceId, Date modifiedDate, String name, long nodeId,
		long processId, String processVersion, long taskId, long userId);

	public Document completeTask(
		long companyId, Date completionDate, long completionUserId,
		long duration, Date modifiedDate, long taskId, long userId);

	public void deleteTask(long companyId, long taskId);

	public Document updateTask(
		Long[] assigneeIds, String assigneeType, long companyId,
		Date modifiedDate, long taskId, long userId);

}