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

package com.liferay.portal.workflow.metrics.service.util;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManager;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.test.log.CaptureAppender;
import com.liferay.portal.test.log.Log4JLoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.kaleo.definition.Node;
import com.liferay.portal.workflow.kaleo.definition.Task;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

import org.apache.log4j.Level;

import org.junit.After;
import org.junit.Before;

/**
 * @author Rafael Praxedes
 */
public abstract class BaseWorkflowMetricsIndexerTestCase
	extends BaseWorkflowMetricsTestCase {

	@Before
	public void setUp() throws Exception {
		_deployWorkflowDefinition();
	}

	@After
	public void tearDown() throws Exception {
		undeployWorkflowDefinition();
	}

	protected BlogsEntry addBlogsEntry() throws PortalException {
		try (CaptureAppender captureAppender =
				Log4JLoggerTestUtil.configureLog4JLogger(
					"com.liferay.petra.mail.MailEngine", Level.OFF)) {

			BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), StringUtil.randomString(),
				StringUtil.randomString(), new Date(),
				ServiceContextTestUtil.getServiceContext());

			_blogsEntries.add(blogsEntry);

			return blogsEntry;
		}
	}

	protected KaleoInstance addKaleoInstance() throws Exception {
		return addKaleoInstance(getKaleoDefinition(), _createWorkflowContext());
	}

	protected KaleoInstance addKaleoInstance(
			KaleoDefinition kaleoDefinition,
			Map<String, Serializable> workflowContext)
		throws Exception {

		List<KaleoDefinitionVersion> kaleoDefinitionVersions =
			kaleoDefinition.getKaleoDefinitionVersions();

		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersions.get(0);

		KaleoInstance kaleoInstance =
			_kaleoInstanceLocalService.addKaleoInstance(
				kaleoDefinition.getKaleoDefinitionId(),
				kaleoDefinitionVersion.getKaleoDefinitionVersionId(),
				kaleoDefinitionVersion.getName(), kaleoDefinition.getVersion(),
				_createWorkflowContext(),
				ServiceContextTestUtil.getServiceContext());

		_kaleoInstances.add(kaleoInstance);

		return kaleoInstance;
	}

	protected KaleoNode addKaleoNode(KaleoDefinition kaleoDefinition, Node node)
		throws Exception {

		List<KaleoDefinitionVersion> kaleoDefinitionVersions =
			kaleoDefinition.getKaleoDefinitionVersions();

		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersions.get(0);

		KaleoNode kaleoNode = _kaleoNodeLocalService.addKaleoNode(
			kaleoDefinition.getKaleoDefinitionId(),
			kaleoDefinitionVersion.getKaleoDefinitionVersionId(), node,
			ServiceContextTestUtil.getServiceContext());

		_kaleoNodes.add(kaleoNode);

		return kaleoNode;
	}

	protected KaleoNode addKaleoNode(Node node) throws Exception {
		return addKaleoNode(getKaleoDefinition(), node);
	}

	protected KaleoTask addKaleoTask(Task task) throws Exception {
		return addKaleoTask(task, getKaleoDefinition());
	}

	protected KaleoTask addKaleoTask(Task task, KaleoDefinition kaleoDefinition)
		throws Exception {

		KaleoNode kaleoNode = addKaleoNode(kaleoDefinition, task);

		KaleoTask kaleoTask = _kaleoTaskLocalService.addKaleoTask(
			kaleoNode.getKaleoDefinitionId(),
			kaleoNode.getKaleoDefinitionVersionId(), kaleoNode.getKaleoNodeId(),
			task, ServiceContextTestUtil.getServiceContext());

		_kaleoTasks.add(kaleoTask);

		return kaleoTask;
	}

	protected KaleoTaskInstanceToken addKaleoTaskInstanceToken(String taskName)
		throws Exception {

		Map<String, Serializable> workflowContext = _createWorkflowContext();

		KaleoInstance kaleoInstance = addKaleoInstance(
			getKaleoDefinition(), workflowContext);

		KaleoInstanceToken rootKaleoInstanceToken =
			kaleoInstance.getRootKaleoInstanceToken(
				workflowContext, ServiceContextTestUtil.getServiceContext());

		Task task = new Task(taskName, StringPool.BLANK);

		task.setAssignments(Collections.emptySet());

		KaleoTask kaleoTask = addKaleoTask(task, getKaleoDefinition());

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			_kaleoTaskInstanceTokenLocalService.addKaleoTaskInstanceToken(
				rootKaleoInstanceToken.getKaleoInstanceTokenId(),
				kaleoTask.getKaleoTaskId(), kaleoTask.getName(),
				Collections.emptyList(), null, workflowContext,
				ServiceContextTestUtil.getServiceContext());

		_kaleoTaskInstanceTokens.add(kaleoTaskInstanceToken);

		return kaleoTaskInstanceToken;
	}

	protected void assertReindex(
			Map<String, Integer> indexNamesMap, String[] indexTypes,
			Object... parameters)
		throws Exception {

		_assertReindex(
			_workflowMetricsIndexer, indexNamesMap, indexTypes, parameters);
	}

	protected void assertReindex(
			String[] indexNames, String[] indexTypes, Object... parameters)
		throws Exception {

		Map<String, Integer> indexNamesMap = Stream.of(
			indexNames
		).collect(
			LinkedHashMap::new, (map, indexName) -> map.put(indexName, 1),
			Map::putAll
		);

		assertReindex(indexNamesMap, indexTypes, parameters);
	}

	protected void assertSLAReindex(
			Map<String, Integer> indexNamesMap, String[] indexTypes,
			Object... parameters)
		throws Exception {

		_assertReindex(
			_slaWorkflowMetricsIndexer, indexNamesMap, indexTypes, parameters);
	}

	protected void assertSLAReindex(
			String[] indexNames, String[] indexTypes, Object... parameters)
		throws Exception {

		Map<String, Integer> indexNamesMap = Stream.of(
			indexNames
		).collect(
			LinkedHashMap::new, (map, indexName) -> map.put(indexName, 1),
			Map::putAll
		);

		assertSLAReindex(indexNamesMap, indexTypes, parameters);
	}

	protected KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			KaleoTaskInstanceToken kaleoTaskInstanceToken)
		throws PortalException {

		KaleoInstance kaleoInstance = getKaleoInstance();

		return _kaleoTaskInstanceTokenLocalService.assignKaleoTaskInstanceToken(
			kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
			User.class.getName(), TestPropsValues.getUserId(),
			WorkflowContextUtil.convert(kaleoInstance.getWorkflowContext()),
			ServiceContextTestUtil.getServiceContext());
	}

	protected KaleoInstance completeKaleoInstance(KaleoInstance kaleoInstance)
		throws Exception {

		return _kaleoInstanceLocalService.completeKaleoInstance(
			kaleoInstance.getKaleoInstanceId());
	}

	protected KaleoInstanceToken completeKaleoInstanceToken(
			KaleoInstance kaleoInstance)
		throws Exception {

		return _kaleoInstanceTokenLocalService.completeKaleoInstanceToken(
			kaleoInstance.getRootKaleoInstanceTokenId());
	}

	protected void completeKaleoTaskInstanceToken(KaleoInstance kaleoInstance)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setUserId(0);

		List<KaleoTaskInstanceToken> kaleoTaskInstanceTokens =
			_kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
				kaleoInstance.getKaleoInstanceId(), false, -1, -1, null,
				serviceContext);

		for (KaleoTaskInstanceToken kaleoTaskInstanceToken :
				kaleoTaskInstanceTokens) {

			_kaleoTaskInstanceTokenLocalService.assignKaleoTaskInstanceToken(
				kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
				User.class.getName(), TestPropsValues.getUserId(), null,
				ServiceContextTestUtil.getServiceContext());

			_kaleoTaskInstanceTokenLocalService.completeKaleoTaskInstanceToken(
				kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
				ServiceContextTestUtil.getServiceContext());
		}
	}

	protected KaleoTaskInstanceToken completeKaleoTaskInstanceToken(
			KaleoTaskInstanceToken kaleoTaskInstanceToken)
		throws PortalException {

		kaleoTaskInstanceToken = assignKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);

		return _kaleoTaskInstanceTokenLocalService.
			completeKaleoTaskInstanceToken(
				kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
				ServiceContextTestUtil.getServiceContext());
	}

	protected void deleteKaleoInstance(KaleoInstance kaleoInstance) {
		_kaleoInstances.remove(kaleoInstance);
		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
	}

	protected void deleteKaleoNode(KaleoNode kaleoNode) {
		_kaleoNodes.remove(kaleoNode);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
	}

	protected void deleteKaleoTask(KaleoTask kaleoTask) {
		_kaleoTasks.remove(kaleoTask);
		_kaleoTaskLocalService.deleteKaleoTask(kaleoTask);
	}

	protected void deleteKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		_kaleoTaskInstanceTokens.remove(kaleoTaskInstanceToken);
		_kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	protected KaleoDefinition getKaleoDefinition() {
		return _kaleoDefinition;
	}

	protected KaleoInstance getKaleoInstance() {
		return _kaleoInstances.peek();
	}

	protected KaleoInstance getKaleoInstance(BlogsEntry blogsEntry)
		throws Exception {

		List<KaleoInstance> kaleoInstances =
			_kaleoInstanceLocalService.getKaleoInstances(
				null, BlogsEntry.class.getName(), blogsEntry.getPrimaryKey(),
				null, 0, 1, null, ServiceContextTestUtil.getServiceContext());

		return kaleoInstances.get(0);
	}

	protected void undeployWorkflowDefinition() throws Exception {
		_deleteBlogsEntries();
		_deleteKaleoInstances();
		_deleteKaleoNodes();
		_deleteKaleoTaskInstanceTokens();
		_deleteKaleoTasks();

		if (_workflowDefinition != null) {
			_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				TestPropsValues.getGroupId(), BlogsEntry.class.getName(), 0, 0,
				null);

			_workflowDefinitionManager.updateActive(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				_workflowDefinition.getName(), _workflowDefinition.getVersion(),
				false);

			_workflowDefinitionManager.undeployWorkflowDefinition(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				_workflowDefinition.getName(),
				_workflowDefinition.getVersion());

			_workflowDefinition = null;
		}
	}

	protected void updateKaleoDefinition() throws Exception {
		_workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				_workflowDefinition.getTitle(), _workflowDefinition.getName(),
				WorkflowDefinitionUtil.getBytes());
	}

	private void _assertReindex(
			Indexer<Object> indexer, Map<String, Integer> indexNamesMap,
			String[] indexTypes, Object... parameters)
		throws Exception {

		if (searchEngineAdapter == null) {
			return;
		}

		indexer.reindex(
			new String[] {String.valueOf(TestPropsValues.getCompanyId())});

		String[] indexNames = ArrayUtil.toStringArray(indexNamesMap.keySet());

		for (int i = 0; i < indexNames.length; i++) {
			retryAssertCount(
				indexNamesMap.get(indexNames[i]), indexNames[i], indexTypes[i],
				ArrayUtil.append(new Object[] {"deleted", false}, parameters));
		}
	}

	private Map<String, Serializable> _createWorkflowContext()
		throws PortalException {

		return HashMapBuilder.<String, Serializable>put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
			BlogsEntry.class.getName()
		).put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
			() -> {
				BlogsEntry blogsEntry = addBlogsEntry();

				return String.valueOf(blogsEntry.getEntryId());
			}
		).put(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT,
			(Serializable)ServiceContextTestUtil.getServiceContext()
		).build();
	}

	private void _deleteBlogsEntries() throws Exception {
		for (Iterator<BlogsEntry> blogsEntryIterator = _blogsEntries.iterator();
			 blogsEntryIterator.hasNext();) {

			BlogsEntry blogsEntry = (BlogsEntry)blogsEntryIterator.next();

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getPrimaryKey());

			blogsEntryIterator.remove();
		}
	}

	private void _deleteKaleoInstances() {
		for (Iterator<KaleoInstance> kaleoInstanceIterator =
				_kaleoInstances.iterator(); kaleoInstanceIterator.hasNext();) {

			_kaleoInstanceLocalService.deleteKaleoInstance(
				kaleoInstanceIterator.next());

			kaleoInstanceIterator.remove();
		}
	}

	private void _deleteKaleoNodes() {
		for (Iterator<KaleoNode> kaleoNodeIterator = _kaleoNodes.iterator();
			 kaleoNodeIterator.hasNext();) {

			_kaleoNodeLocalService.deleteKaleoNode(kaleoNodeIterator.next());

			kaleoNodeIterator.remove();
		}
	}

	private void _deleteKaleoTaskInstanceTokens() {
		for (Iterator<KaleoTaskInstanceToken> kaleoTaskInstanceTokenIterator =
				_kaleoTaskInstanceTokens.iterator();
			 kaleoTaskInstanceTokenIterator.hasNext();) {

			_kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
				kaleoTaskInstanceTokenIterator.next());

			kaleoTaskInstanceTokenIterator.remove();
		}
	}

	private void _deleteKaleoTasks() {
		for (Iterator<KaleoTask> kaleoTaskIterator = _kaleoTasks.iterator();
			 kaleoTaskIterator.hasNext();) {

			_kaleoTaskLocalService.deleteKaleoTask(kaleoTaskIterator.next());

			kaleoTaskIterator.remove();
		}
	}

	private void _deployWorkflowDefinition() throws Exception {
		_workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				StringUtil.randomId(), StringUtil.randomId(),
				WorkflowDefinitionUtil.getBytes());

		_kaleoDefinition = _kaleoDefinitionLocalService.getKaleoDefinition(
			_workflowDefinition.getName(),
			ServiceContextTestUtil.getServiceContext());

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			TestPropsValues.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			_workflowDefinition.getName(), _workflowDefinition.getVersion());
	}

	private final List<BlogsEntry> _blogsEntries = new ArrayList<>();

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private DocumentBuilderFactory _documentBuilderFactory;

	private KaleoDefinition _kaleoDefinition;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	private final Stack<KaleoInstance> _kaleoInstances = new Stack<>();

	@Inject
	private KaleoInstanceTokenLocalService _kaleoInstanceTokenLocalService;

	@Inject
	private KaleoNodeLocalService _kaleoNodeLocalService;

	private final List<KaleoNode> _kaleoNodes = new ArrayList<>();

	@Inject
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	private final List<KaleoTaskInstanceToken> _kaleoTaskInstanceTokens =
		new ArrayList<>();

	@Inject
	private KaleoTaskLocalService _kaleoTaskLocalService;

	private final List<KaleoTask> _kaleoTasks = new ArrayList<>();

	@Inject(
		filter = "(&(objectClass=com.liferay.portal.workflow.metrics.internal.search.SLAWorkflowMetricsIndexer))"
	)
	private Indexer<Object> _slaWorkflowMetricsIndexer;

	private WorkflowDefinition _workflowDefinition;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject(
		filter = "(&(objectClass=com.liferay.portal.workflow.metrics.internal.search.WorkflowMetricsIndexer))"
	)
	private Indexer<Object> _workflowMetricsIndexer;

}