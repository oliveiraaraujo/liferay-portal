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

package com.liferay.portal.workflow.metrics.rest.internal.dto.v1_0.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Task;

import java.text.DateFormat;

import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Rafael Praxedes
 */
public class TaskUtil {

	public static Task toTask(
		Document document, Language language, ResourceBundle resourceBundle) {

		return new Task() {
			{
				className = document.getString("className");
				classPK = document.getLong("classPK");
				completed = document.getBoolean("completed");
				completionUserId = document.getLong("completionUserId");
				dateCompletion = _parseDate(document.getDate("completionDate"));
				dateCreated = _parseDate(document.getDate("createDate"));
				dateModified = _parseDate(document.getDate("modifiedDate"));
				duration = document.getLong("duration");
				id = document.getLong("taskId");
				instanceId = document.getLong("instanceId");
				label = language.get(
					resourceBundle, document.getString("name"));
				name = document.getString("name");
				nodeId = document.getLong("nodeId");
				processId = document.getLong("processId");
				processVersion = document.getString("version");

				setAssigneeId(
					() -> {
						String assigneeType = document.getString(
							"assigneeType");

						if (Objects.deepEquals(
								assigneeType, User.class.getName())) {

							return document.getLong("assigneeIds");
						}

						return null;
					});
			}
		};
	}

	public static Task toTask(
		Language language, String taskName, ResourceBundle resourceBundle) {

		return new Task() {
			{
				label = language.get(resourceBundle, taskName);
				name = taskName;
			}
		};
	}

	private static Date _parseDate(String dateString) {
		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		try {
			return dateFormat.parse(dateString);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception, exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(TaskUtil.class);

}