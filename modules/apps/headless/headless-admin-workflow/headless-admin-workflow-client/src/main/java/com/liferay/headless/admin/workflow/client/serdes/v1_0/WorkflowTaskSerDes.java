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

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.Role;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTaskSerDes {

	public static WorkflowTask toDTO(String json) {
		WorkflowTaskJSONParser workflowTaskJSONParser =
			new WorkflowTaskJSONParser();

		return workflowTaskJSONParser.parseToDTO(json);
	}

	public static WorkflowTask[] toDTOs(String json) {
		WorkflowTaskJSONParser workflowTaskJSONParser =
			new WorkflowTaskJSONParser();

		return workflowTaskJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WorkflowTask workflowTask) {
		if (workflowTask == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		if (workflowTask.getAssigneePerson() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneePerson\": ");

			sb.append(String.valueOf(workflowTask.getAssigneePerson()));
		}

		if (workflowTask.getAssigneeRoles() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneeRoles\": ");

			sb.append("[");

			for (int i = 0; i < workflowTask.getAssigneeRoles().length; i++) {
				sb.append(String.valueOf(workflowTask.getAssigneeRoles()[i]));

				if ((i + 1) < workflowTask.getAssigneeRoles().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowTask.getCompleted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(workflowTask.getCompleted());
		}

		if (workflowTask.getDateCompletion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCompletion\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					workflowTask.getDateCompletion()));

			sb.append("\"");
		}

		if (workflowTask.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(workflowTask.getDateCreated()));

			sb.append("\"");
		}

		if (workflowTask.getDateDue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateDue\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(workflowTask.getDateDue()));

			sb.append("\"");
		}

		if (workflowTask.getDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"definitionId\": ");

			sb.append(workflowTask.getDefinitionId());
		}

		if (workflowTask.getDefinitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"definitionName\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getDefinitionName()));

			sb.append("\"");
		}

		if (workflowTask.getDefinitionVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"definitionVersion\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getDefinitionVersion()));

			sb.append("\"");
		}

		if (workflowTask.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getDescription()));

			sb.append("\"");
		}

		if (workflowTask.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(workflowTask.getId());
		}

		if (workflowTask.getInstanceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instanceId\": ");

			sb.append(workflowTask.getInstanceId());
		}

		if (workflowTask.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getName()));

			sb.append("\"");
		}

		if (workflowTask.getObjectReviewed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectReviewed\": ");

			sb.append(String.valueOf(workflowTask.getObjectReviewed()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowTaskJSONParser workflowTaskJSONParser =
			new WorkflowTaskJSONParser();

		return workflowTaskJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WorkflowTask workflowTask) {
		if (workflowTask == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		if (workflowTask.getAssigneePerson() == null) {
			map.put("assigneePerson", null);
		}
		else {
			map.put(
				"assigneePerson",
				String.valueOf(workflowTask.getAssigneePerson()));
		}

		if (workflowTask.getAssigneeRoles() == null) {
			map.put("assigneeRoles", null);
		}
		else {
			map.put(
				"assigneeRoles",
				String.valueOf(workflowTask.getAssigneeRoles()));
		}

		if (workflowTask.getCompleted() == null) {
			map.put("completed", null);
		}
		else {
			map.put("completed", String.valueOf(workflowTask.getCompleted()));
		}

		map.put(
			"dateCompletion",
			liferayToJSONDateFormat.format(workflowTask.getDateCompletion()));

		map.put(
			"dateCreated",
			liferayToJSONDateFormat.format(workflowTask.getDateCreated()));

		map.put(
			"dateDue",
			liferayToJSONDateFormat.format(workflowTask.getDateDue()));

		if (workflowTask.getDefinitionId() == null) {
			map.put("definitionId", null);
		}
		else {
			map.put(
				"definitionId", String.valueOf(workflowTask.getDefinitionId()));
		}

		if (workflowTask.getDefinitionName() == null) {
			map.put("definitionName", null);
		}
		else {
			map.put(
				"definitionName",
				String.valueOf(workflowTask.getDefinitionName()));
		}

		if (workflowTask.getDefinitionVersion() == null) {
			map.put("definitionVersion", null);
		}
		else {
			map.put(
				"definitionVersion",
				String.valueOf(workflowTask.getDefinitionVersion()));
		}

		if (workflowTask.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(workflowTask.getDescription()));
		}

		if (workflowTask.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(workflowTask.getId()));
		}

		if (workflowTask.getInstanceId() == null) {
			map.put("instanceId", null);
		}
		else {
			map.put("instanceId", String.valueOf(workflowTask.getInstanceId()));
		}

		if (workflowTask.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(workflowTask.getName()));
		}

		if (workflowTask.getObjectReviewed() == null) {
			map.put("objectReviewed", null);
		}
		else {
			map.put(
				"objectReviewed",
				String.valueOf(workflowTask.getObjectReviewed()));
		}

		return map;
	}

	public static class WorkflowTaskJSONParser
		extends BaseJSONParser<WorkflowTask> {

		@Override
		protected WorkflowTask createDTO() {
			return new WorkflowTask();
		}

		@Override
		protected WorkflowTask[] createDTOArray(int size) {
			return new WorkflowTask[size];
		}

		@Override
		protected void setField(
			WorkflowTask workflowTask, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assigneePerson")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setAssigneePerson(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assigneeRoles")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setAssigneeRoles(
						Stream.of(
							toStrings((Object[])jsonParserFieldValue)
						).map(
							object -> RoleSerDes.toDTO((String)object)
						).toArray(
							size -> new Role[size]
						));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setCompleted((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCompletion")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDateCompletion(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateDue")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDateDue(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "definitionId")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "definitionName")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDefinitionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "definitionVersion")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDefinitionVersion(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "instanceId")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setInstanceId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectReviewed")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setObjectReviewed(
						ObjectReviewedSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		string = string.replace("\\", "\\\\");

		return string.replace("\"", "\\\"");
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\":");

			Object value = entry.getValue();

			Class<?> valueClass = value.getClass();

			if (value instanceof Map) {
				sb.append(_toJSON((Map)value));
			}
			else if (valueClass.isArray()) {
				Object[] values = (Object[])value;

				sb.append("[");

				for (int i = 0; i < values.length; i++) {
					sb.append("\"");
					sb.append(_escape(values[i]));
					sb.append("\"");

					if ((i + 1) < values.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else {
				sb.append("\"");
				sb.append(_escape(entry.getValue()));
				sb.append("\"");
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append("}");

		return sb.toString();
	}

}