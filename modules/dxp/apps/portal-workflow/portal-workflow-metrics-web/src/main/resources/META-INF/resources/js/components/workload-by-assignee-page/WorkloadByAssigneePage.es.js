/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import React from 'react';

import {
	filterKeys,
	filterTitles
} from '../../shared/components/filter/util/filterConstants.es';
import {
	getSelectedItems,
	getFilterResults
} from '../../shared/components/filter/util/filterUtil.es';
import PromisesResolver from '../../shared/components/request/PromisesResolver.es';
import {parse} from '../../shared/components/router/queryString.es';
import {useFilterItemKeys} from '../../shared/hooks/useFilterItemKeys.es';
import {useFiltersReducer} from '../../shared/hooks/useFiltersReducer.es';
import {useProcessTitle} from '../../shared/hooks/useProcessTitle.es';
import {useResource} from '../../shared/hooks/useResource.es';
import {Body} from './WorkloadByAssigneePageBody.es';
import {Header} from './WorkloadByAssigneePageHeader.es';

const WorkloadByAssigneePage = ({query, routeParams}) => {
	const {processId} = routeParams;
	useProcessTitle(processId, Liferay.Language.get('workload-by-assignee'));

	const {search = ''} = parse(query);
	const keywords = search.length ? search : null;

	const [filterValues, dispatch] = useFiltersReducer(filterKeys);
	const {roleIds, taskKeys} = useFilterItemKeys(filterKeys, filterValues);
	const filterResults = getFilterResults(
		filterKeys,
		filterTitles,
		filterValues
	);

	const selectedFilters = getSelectedItems(filterResults);
	const filtered = search.length > 0 || selectedFilters.length > 0;

	const {data, promises} = useResource(
		`/processes/${processId}/assignee-users`,
		{
			keywords,
			roleIds,
			taskKeys,
			...routeParams
		}
	);

	return (
		<PromisesResolver promises={promises}>
			<WorkloadByAssigneePage.Header
				dispatch={dispatch}
				routeParams={{...routeParams, search: keywords}}
				selectedFilters={selectedFilters}
				totalCount={data.totalCount}
			/>

			<WorkloadByAssigneePage.Body
				data={data}
				filtered={filtered}
				processId={processId}
				taskKeys={taskKeys}
			/>
		</PromisesResolver>
	);
};

WorkloadByAssigneePage.Body = Body;
WorkloadByAssigneePage.Header = Header;

export default WorkloadByAssigneePage;
