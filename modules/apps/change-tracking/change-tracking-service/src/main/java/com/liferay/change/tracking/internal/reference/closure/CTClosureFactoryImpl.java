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

package com.liferay.change.tracking.internal.reference.closure;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.internal.reference.TableJoinHolder;
import com.liferay.change.tracking.internal.reference.TableReferenceDefinitionManager;
import com.liferay.change.tracking.internal.reference.TableReferenceInfo;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.reference.TableReferenceDefinition;
import com.liferay.change.tracking.reference.closure.CTClosure;
import com.liferay.change.tracking.reference.closure.CTClosureFactory;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(immediate = true, service = CTClosureFactory.class)
public class CTClosureFactoryImpl implements CTClosureFactory {

	@Override
	public CTClosure create(long ctCollectionId) {
		return new CTClosureImpl(
			ctCollectionId,
			_buildClosureMap(
				ctCollectionId,
				_tableReferenceDefinitionManager.
					getCombinedTableReferenceInfos()));
	}

	private Map<Node, Collection<Node>> _buildClosureMap(
		long ctCollectionId,
		Map<Long, TableReferenceInfo<?>> combinedTableReferenceInfos) {

		Map<Long, List<Long>> map = new HashMap<>();
		Set<Node> nodes = new HashSet<>();

		for (CTEntry ctEntry :
				_ctEntryLocalService.getCTCollectionCTEntries(ctCollectionId)) {

			List<Long> primaryKeys = map.computeIfAbsent(
				ctEntry.getModelClassNameId(), key -> new ArrayList<>());

			primaryKeys.add(ctEntry.getModelClassPK());

			nodes.add(
				new Node(
					ctEntry.getModelClassNameId(), ctEntry.getModelClassPK()));
		}

		Map<Node, Collection<Edge>> edgeMap = new LinkedHashMap<>();

		Queue<Map.Entry<Long, List<Long>>> queue = new LinkedList<>(
			map.entrySet());

		while (queue.size() > 0) {
			Map.Entry<Long, List<Long>> queueEntry = queue.poll();

			long childClassNameId = queueEntry.getKey();
			List<Long> childPrimaryKeys = queueEntry.getValue();

			TableReferenceInfo<?> childTableReferenceInfo =
				combinedTableReferenceInfos.get(childClassNameId);

			if (childTableReferenceInfo == null) {
				throw new IllegalArgumentException(
					"No table reference definition for " + childClassNameId);
			}

			Map<Table<?>, List<TableJoinHolder>> parentJoinHolders =
				childTableReferenceInfo.getParentJoinHoldersMap();

			for (Map.Entry<Table<?>, List<TableJoinHolder>> entry :
					parentJoinHolders.entrySet()) {

				long parentClassNameId =
					_tableReferenceDefinitionManager.getClassNameId(
						entry.getKey());

				TableReferenceInfo<?> parentTableReferenceInfo =
					combinedTableReferenceInfos.get(parentClassNameId);

				List<Long> newParents = _collectParents(
					ctCollectionId, childClassNameId,
					childPrimaryKeys.toArray(new Long[0]), parentClassNameId,
					parentTableReferenceInfo, entry.getValue(), nodes, edgeMap);

				if (newParents != null) {
					queue.add(
						new AbstractMap.SimpleImmutableEntry<>(
							parentClassNameId, newParents));
				}
			}
		}

		return GraphUtil.getNodeMap(nodes, edgeMap);
	}

	private <P extends Table<P>> List<Long> _collectParents(
		long ctCollectionId, long childClassNameId, Long[] childPrimaryKeys,
		long parentClassNameId, TableReferenceInfo<P> parentTableReferenceInfo,
		List<TableJoinHolder> parentJoinHolders, Set<Node> nodes,
		Map<Node, Collection<Edge>> edgeMap) {

		DSLQuery dslQuery = null;

		for (TableJoinHolder parentJoinHolder : parentJoinHolders) {
			Column<?, Long> parentPKColumn = parentJoinHolder.getFromPKColumn();
			Column<?, Long> childPKColumn = parentJoinHolder.getJoinPKColumn();

			FromStep fromStep = DSLQueryFactoryUtil.selectDistinct(
				parentPKColumn, childPKColumn);

			Function<FromStep, JoinStep> joinFunction =
				parentJoinHolder.getJoinFunction();

			JoinStep joinStep = joinFunction.apply(fromStep);

			GroupByStep groupByStep = joinStep.where(
				() -> {
					Predicate predicate = childPKColumn.in(childPrimaryKeys);

					Table<?> parentTable = parentPKColumn.getTable();

					Column<?, Long> ctCollectionIdColumn =
						parentTable.getColumn("ctCollectionId", Long.class);

					if ((ctCollectionIdColumn != null) &&
						ctCollectionIdColumn.isPrimaryKey()) {

						predicate = predicate.and(
							ctCollectionIdColumn.eq(
								CTConstants.CT_COLLECTION_ID_PRODUCTION
							).or(
								ctCollectionIdColumn.eq(ctCollectionId)
							).withParentheses());
					}

					return predicate;
				});

			if (dslQuery == null) {
				dslQuery = groupByStep;
			}
			else {
				dslQuery = dslQuery.union(groupByStep);
			}
		}

		TableReferenceDefinition<P> tableReferenceDefinition =
			parentTableReferenceInfo.getTableReferenceDefinition();

		PersistedModelLocalService persistedModelLocalService =
			tableReferenceDefinition.getPersistedModelLocalService();

		BasePersistence<?> basePersistence =
			persistedModelLocalService.getBasePersistence();

		DataSource dataSource = basePersistence.getDataSource();

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		try (Connection connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					dslQuery.toSQL(defaultASTNodeListener)))) {

			List<Object> scalarValues =
				defaultASTNodeListener.getScalarValues();

			for (int i = 0; i < scalarValues.size(); i++) {
				preparedStatement.setObject(i + 1, scalarValues.get(i));
			}

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<Long> newParents = null;

				while (resultSet.next()) {
					Node parentNode = new Node(
						parentClassNameId, resultSet.getLong(1));
					Node childNode = new Node(
						childClassNameId, resultSet.getLong(2));

					if (nodes.add(parentNode)) {
						if (newParents == null) {
							newParents = new ArrayList<>();
						}

						newParents.add(parentNode.getPrimaryKey());
					}

					Collection<Edge> edges = edgeMap.computeIfAbsent(
						parentNode, key -> new LinkedList<>());

					edges.add(new Edge(parentNode, childNode));
				}

				return newParents;
			}
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
	}

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private TableReferenceDefinitionManager _tableReferenceDefinitionManager;

}