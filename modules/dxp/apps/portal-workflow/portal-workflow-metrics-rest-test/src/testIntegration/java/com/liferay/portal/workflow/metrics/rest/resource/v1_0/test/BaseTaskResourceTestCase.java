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

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.rest.client.http.HttpInvoker;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.resource.v1_0.TaskResource;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.TaskSerDes;

import java.lang.reflect.InvocationTargetException;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Generated;

import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.time.DateUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public abstract class BaseTaskResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_taskResource.setContextCompany(testCompany);

		TaskResource.Builder builder = TaskResource.builder();

		taskResource = builder.locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};

		Task task1 = randomTask();

		String json = objectMapper.writeValueAsString(task1);

		Task task2 = TaskSerDes.toDTO(json);

		Assert.assertTrue(equals(task1, task2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};

		Task task = randomTask();

		String json1 = objectMapper.writeValueAsString(task);
		String json2 = TaskSerDes.toJSON(task);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		Task task = randomTask();

		task.setClassName(regex);
		task.setLabel(regex);
		task.setName(regex);
		task.setProcessVersion(regex);

		String json = TaskSerDes.toJSON(task);

		Assert.assertFalse(json.contains(regex));

		task = TaskSerDes.toDTO(json);

		Assert.assertEquals(regex, task.getClassName());
		Assert.assertEquals(regex, task.getLabel());
		Assert.assertEquals(regex, task.getName());
		Assert.assertEquals(regex, task.getProcessVersion());
	}

	@Test
	public void testGetProcessTasksPage() throws Exception {
		Page<Task> page = taskResource.getProcessTasksPage(
			testGetProcessTasksPage_getProcessId());

		Assert.assertEquals(0, page.getTotalCount());

		Long processId = testGetProcessTasksPage_getProcessId();
		Long irrelevantProcessId =
			testGetProcessTasksPage_getIrrelevantProcessId();

		if ((irrelevantProcessId != null)) {
			Task irrelevantTask = testGetProcessTasksPage_addTask(
				irrelevantProcessId, randomIrrelevantTask());

			page = taskResource.getProcessTasksPage(irrelevantProcessId);

			Assert.assertEquals(1, page.getTotalCount());

			assertEquals(
				Arrays.asList(irrelevantTask), (List<Task>)page.getItems());
			assertValid(page);
		}

		Task task1 = testGetProcessTasksPage_addTask(processId, randomTask());

		Task task2 = testGetProcessTasksPage_addTask(processId, randomTask());

		page = taskResource.getProcessTasksPage(processId);

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(task1, task2), (List<Task>)page.getItems());
		assertValid(page);
	}

	protected Task testGetProcessTasksPage_addTask(Long processId, Task task)
		throws Exception {

		return taskResource.postProcessTask(processId, task);
	}

	protected Long testGetProcessTasksPage_getProcessId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProcessTasksPage_getIrrelevantProcessId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostProcessTask() throws Exception {
		Task randomTask = randomTask();

		Task postTask = testPostProcessTask_addTask(randomTask);

		assertEquals(randomTask, postTask);
		assertValid(postTask);
	}

	protected Task testPostProcessTask_addTask(Task task) throws Exception {
		return taskResource.postProcessTask(
			testGetProcessTasksPage_getProcessId(), task);
	}

	@Test
	public void testDeleteProcessTask() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Task task = testDeleteProcessTask_addTask();

		assertHttpResponseStatusCode(
			204,
			taskResource.deleteProcessTaskHttpResponse(
				task.getProcessId(), task.getId()));

		assertHttpResponseStatusCode(
			404,
			taskResource.getProcessTaskHttpResponse(
				task.getProcessId(), task.getId()));

		assertHttpResponseStatusCode(
			404,
			taskResource.getProcessTaskHttpResponse(task.getProcessId(), 0L));
	}

	protected Task testDeleteProcessTask_addTask() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetProcessTask() throws Exception {
		Task postTask = testGetProcessTask_addTask();

		Task getTask = taskResource.getProcessTask(
			postTask.getProcessId(), postTask.getId());

		assertEquals(postTask, getTask);
		assertValid(getTask);
	}

	protected Task testGetProcessTask_addTask() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProcessTask() throws Exception {
		Task task = testGraphQLTask_addTask();

		List<GraphQLField> graphQLFields = getGraphQLFields();

		GraphQLField graphQLField = new GraphQLField(
			"query",
			new GraphQLField(
				"processTask",
				new HashMap<String, Object>() {
					{
						put("processId", task.getProcessId());
						put("taskId", task.getId());
					}
				},
				graphQLFields.toArray(new GraphQLField[0])));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			invoke(graphQLField.toString()));

		JSONObject dataJSONObject = jsonObject.getJSONObject("data");

		Assert.assertTrue(
			equalsJSONObject(
				task, dataJSONObject.getJSONObject("processTask")));
	}

	@Test
	public void testPatchProcessTask() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Task task = testPatchProcessTask_addTask();

		assertHttpResponseStatusCode(
			204,
			taskResource.patchProcessTaskHttpResponse(
				task.getProcessId(), task.getId(), task));

		assertHttpResponseStatusCode(
			404,
			taskResource.patchProcessTaskHttpResponse(
				task.getProcessId(), 0L, task));
	}

	protected Task testPatchProcessTask_addTask() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchProcessTaskComplete() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Task task = testPatchProcessTaskComplete_addTask();

		assertHttpResponseStatusCode(
			204,
			taskResource.patchProcessTaskCompleteHttpResponse(
				task.getProcessId(), task.getId(), task));

		assertHttpResponseStatusCode(
			404,
			taskResource.patchProcessTaskCompleteHttpResponse(
				task.getProcessId(), 0L, task));
	}

	protected Task testPatchProcessTaskComplete_addTask() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Task testGraphQLTask_addTask() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Task task1, Task task2) {
		Assert.assertTrue(
			task1 + " does not equal " + task2, equals(task1, task2));
	}

	protected void assertEquals(List<Task> tasks1, List<Task> tasks2) {
		Assert.assertEquals(tasks1.size(), tasks2.size());

		for (int i = 0; i < tasks1.size(); i++) {
			Task task1 = tasks1.get(i);
			Task task2 = tasks2.get(i);

			assertEquals(task1, task2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Task> tasks1, List<Task> tasks2) {

		Assert.assertEquals(tasks1.size(), tasks2.size());

		for (Task task1 : tasks1) {
			boolean contains = false;

			for (Task task2 : tasks2) {
				if (equals(task1, task2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(tasks2 + " does not contain " + task1, contains);
		}
	}

	protected void assertEqualsJSONArray(
		List<Task> tasks, JSONArray jsonArray) {

		for (Task task : tasks) {
			boolean contains = false;

			for (Object object : jsonArray) {
				if (equalsJSONObject(task, (JSONObject)object)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				jsonArray + " does not contain " + task, contains);
		}
	}

	protected void assertValid(Task task) {
		boolean valid = true;

		if (task.getDateCreated() == null) {
			valid = false;
		}

		if (task.getDateModified() == null) {
			valid = false;
		}

		if (task.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assigneeId", additionalAssertFieldName)) {
				if (task.getAssigneeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (task.getClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (task.getClassPK() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("completed", additionalAssertFieldName)) {
				if (task.getCompleted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("completionUserId", additionalAssertFieldName)) {
				if (task.getCompletionUserId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateCompletion", additionalAssertFieldName)) {
				if (task.getDateCompletion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("duration", additionalAssertFieldName)) {
				if (task.getDuration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("instanceId", additionalAssertFieldName)) {
				if (task.getInstanceId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (task.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (task.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("nodeId", additionalAssertFieldName)) {
				if (task.getNodeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("processId", additionalAssertFieldName)) {
				if (task.getProcessId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("processVersion", additionalAssertFieldName)) {
				if (task.getProcessVersion() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<Task> page) {
		boolean valid = false;

		java.util.Collection<Task> tasks = page.getItems();

		int size = tasks.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			graphQLFields.add(new GraphQLField(additionalAssertFieldName));
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(Task task1, Task task2) {
		if (task1 == task2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assigneeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getAssigneeId(), task2.getAssigneeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getClassName(), task2.getClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getClassPK(), task2.getClassPK())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getCompleted(), task2.getCompleted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completionUserId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getCompletionUserId(),
						task2.getCompletionUserId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCompletion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getDateCompletion(), task2.getDateCompletion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getDateCreated(), task2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getDateModified(), task2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("duration", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getDuration(), task2.getDuration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(task1.getId(), task2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("instanceId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getInstanceId(), task2.getInstanceId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!Objects.deepEquals(task1.getLabel(), task2.getLabel())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(task1.getName(), task2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("nodeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(task1.getNodeId(), task2.getNodeId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("processId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getProcessId(), task2.getProcessId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("processVersion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getProcessVersion(), task2.getProcessVersion())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equalsJSONObject(Task task, JSONObject jsonObject) {
		for (String fieldName : getAdditionalAssertFieldNames()) {
			if (Objects.equals("assigneeId", fieldName)) {
				if (!Objects.deepEquals(
						task.getAssigneeId(),
						jsonObject.getLong("assigneeId"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("className", fieldName)) {
				if (!Objects.deepEquals(
						task.getClassName(),
						jsonObject.getString("className"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classPK", fieldName)) {
				if (!Objects.deepEquals(
						task.getClassPK(), jsonObject.getLong("classPK"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completed", fieldName)) {
				if (!Objects.deepEquals(
						task.getCompleted(),
						jsonObject.getBoolean("completed"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completionUserId", fieldName)) {
				if (!Objects.deepEquals(
						task.getCompletionUserId(),
						jsonObject.getLong("completionUserId"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("duration", fieldName)) {
				if (!Objects.deepEquals(
						task.getDuration(), jsonObject.getLong("duration"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", fieldName)) {
				if (!Objects.deepEquals(
						task.getId(), jsonObject.getLong("id"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("instanceId", fieldName)) {
				if (!Objects.deepEquals(
						task.getInstanceId(),
						jsonObject.getLong("instanceId"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", fieldName)) {
				if (!Objects.deepEquals(
						task.getLabel(), jsonObject.getString("label"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", fieldName)) {
				if (!Objects.deepEquals(
						task.getName(), jsonObject.getString("name"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("nodeId", fieldName)) {
				if (!Objects.deepEquals(
						task.getNodeId(), jsonObject.getLong("nodeId"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("processId", fieldName)) {
				if (!Objects.deepEquals(
						task.getProcessId(), jsonObject.getLong("processId"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals("processVersion", fieldName)) {
				if (!Objects.deepEquals(
						task.getProcessVersion(),
						jsonObject.getString("processVersion"))) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid field name " + fieldName);
		}

		return true;
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_taskResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_taskResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		java.util.Collection<EntityField> entityFields = getEntityFields();

		Stream<EntityField> stream = entityFields.stream();

		return stream.filter(
			entityField ->
				Objects.equals(entityField.getType(), type) &&
				!ArrayUtil.contains(
					getIgnoredEntityFieldNames(), entityField.getName())
		).collect(
			Collectors.toList()
		);
	}

	protected String getFilterString(
		EntityField entityField, String operator, Task task) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assigneeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("className")) {
			sb.append("'");
			sb.append(String.valueOf(task.getClassName()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("classPK")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("completed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("completionUserId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCompletion")) {
			if (operator.equals("between")) {
				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(task.getDateCompletion(), -2)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(task.getDateCompletion(), 2)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_dateFormat.format(task.getDateCompletion()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(task.getDateCreated(), -2)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(task.getDateCreated(), 2)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_dateFormat.format(task.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(task.getDateModified(), -2)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(task.getDateModified(), 2)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_dateFormat.format(task.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("duration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("instanceId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("label")) {
			sb.append("'");
			sb.append(String.valueOf(task.getLabel()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			sb.append("'");
			sb.append(String.valueOf(task.getName()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("nodeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("processId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("processVersion")) {
			sb.append("'");
			sb.append(String.valueOf(task.getProcessVersion()));
			sb.append("'");

			return sb.toString();
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected Task randomTask() throws Exception {
		return new Task() {
			{
				assigneeId = RandomTestUtil.randomLong();
				className = RandomTestUtil.randomString();
				classPK = RandomTestUtil.randomLong();
				completed = RandomTestUtil.randomBoolean();
				completionUserId = RandomTestUtil.randomLong();
				dateCompletion = RandomTestUtil.nextDate();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				duration = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				instanceId = RandomTestUtil.randomLong();
				label = RandomTestUtil.randomString();
				name = RandomTestUtil.randomString();
				nodeId = RandomTestUtil.randomLong();
				processId = RandomTestUtil.randomLong();
				processVersion = RandomTestUtil.randomString();
			}
		};
	}

	protected Task randomIrrelevantTask() throws Exception {
		Task randomIrrelevantTask = randomTask();

		return randomIrrelevantTask;
	}

	protected Task randomPatchTask() throws Exception {
		return randomTask();
	}

	protected TaskResource taskResource;
	protected Group irrelevantGroup;
	protected Company testCompany;
	protected Group testGroup;

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(":");
					sb.append(entry.getValue());
					sb.append(",");
				}

				sb.setLength(sb.length() - 1);

				sb.append(")");
			}

			if (_graphQLFields.length > 0) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(",");
				}

				sb.setLength(sb.length() - 1);

				sb.append("}");
			}

			return sb.toString();
		}

		private final GraphQLField[] _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseTaskResourceTestCase.class);

	private static BeanUtilsBean _beanUtilsBean = new BeanUtilsBean() {

		@Override
		public void copyProperty(Object bean, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {

			if (value != null) {
				super.copyProperty(bean, name, value);
			}
		}

	};
	private static DateFormat _dateFormat;

	@Inject
	private com.liferay.portal.workflow.metrics.rest.resource.v1_0.TaskResource
		_taskResource;

}