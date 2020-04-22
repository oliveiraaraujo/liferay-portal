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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class TaskResourceTest extends BaseTaskResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_process = _workflowMetricsRESTTestHelper.addProcess(
			testGroup.getCompanyId());

		_instance = _workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), false, _process.getId());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_instance != null) {
			_workflowMetricsRESTTestHelper.deleteInstance(
				testGroup.getCompanyId(), _instance);
		}

		if (_process != null) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				testGroup.getCompanyId(), _process);
		}
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProcessTask() throws Exception {
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	@Override
	protected Task randomTask() throws Exception {
		Task task = super.randomTask();

		task.setInstanceId(_instance.getId());
		task.setProcessId(_process.getId());
		task.setProcessVersion(_process.getVersion());

		return task;
	}

	@Override
	protected Task testDeleteProcessTask_addTask() throws Exception {
		return testGetProcessTask_addTask();
	}

	@Override
	protected Task testGetProcessTask_addTask() throws Exception {
		return _workflowMetricsRESTTestHelper.addTask(
			0, testGroup.getCompanyId(), _instance);
	}

	@Override
	protected Long testGetProcessTasksPage_getProcessId() throws Exception {
		return _process.getId();
	}

	@Override
	protected Task testPatchProcessTask_addTask() throws Exception {
		return testGetProcessTask_addTask();
	}

	@Override
	protected Task testPatchProcessTaskComplete_addTask() throws Exception {
		return _workflowMetricsRESTTestHelper.addTask(
			testGroup.getCompanyId(), _instance, randomPatchTask());
	}

	private Instance _instance;
	private Process _process;

	@Inject
	private WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

}