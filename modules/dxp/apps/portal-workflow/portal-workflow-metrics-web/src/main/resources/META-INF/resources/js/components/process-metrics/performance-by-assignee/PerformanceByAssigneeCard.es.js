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

import React, {useContext, useMemo, useEffect} from 'react';

import Icon from '../../../shared/components/Icon.es';
import Panel from '../../../shared/components/Panel.es';
import {getFiltersParam} from '../../../shared/components/filter/util/filterUtil.es';
import PromisesResolver from '../../../shared/components/request/PromisesResolver.es';
import Request from '../../../shared/components/request/Request.es';
import {AppContext} from '../../AppContext.es';
import {ProcessStepProvider, ProcessStepContext} from '../filter/store/ProcessStepStore.es';
import {TimeRangeProvider} from '../filter/store/TimeRangeStore.es';
import Filter from './PerformanceByAssignee.Filter.es';

const Body = ({page, pageSize, processId, query, sort}) => {
	return (
		<Panel.Body>
			<PromisesResolver.Resolved>
				<PerformanceByAssigneeCard.Table
					page={page}
					pageSize={pageSize}
					processId={processId}
					query={query}
					sort={sort}
				/>
			</PromisesResolver.Resolved>
		</Panel.Body>
	);
};

const Footer = ({totalCount}) => {
	return (
		<PromisesResolver.Resolved>
			<div className="mb-1 text-right">
				<button className="border-0 btn btn-secondary btn-sm">
					<span className="mr-2" data-testid="viewAllSteps">
						{`${Liferay.Language.get(
							'view-all-assignees'
						)} (${totalCount})`}
					</span>

					<Icon iconName="caret-right-l" />
				</button>
			</div>
		</PromisesResolver.Resolved>
	);
};

const Header = ({processId, query}) => {
	return (
		<Panel.HeaderWithOptions
			description={Liferay.Language.get(
				'performance-by-step-description'
			)}
			elementClasses="dashboard-panel-header pb-0"
			title={Liferay.Language.get('performance-by-step')}
		>
			<PromisesResolver.Resolved>
				<PerformanceByAssigneeCard.Filter
					processId={processId}
               query={query}
				></PerformanceByAssigneeCard.Filter>
			</PromisesResolver.Resolved>
		</Panel.HeaderWithOptions>
	);
};

const PerformanceByAssigneeCard = ({page, pageSize, processId, query}) => {
   const data = {totalCount: 5};

   const filters = getFiltersParam(query);
   const {assigneeProcessStep = [], assigneeTimeRange = []} = filters;
   
	const functions = [Promise.resolve];
	const {totalCount} = data;

	return (
		<Panel>
         <Request>	
            <ProcessStepProvider
               processId={processId}
               processStepKeys={assigneeProcessStep}
               withAllSteps={true}
            >
               <TimeRangeProvider timeRangeKeys={assigneeTimeRange}>
                  <PromisesResolver promises={functions}>
                     <PerformanceByAssigneeCard.Header
                        processId={processId}
                        query={query}
                     />

                     <PerformanceByAssigneeCard.Body
                        page={page}
                        pageSize={pageSize}
                        processId={processId}
                        query={query}
                     />

                     <PerformanceByAssigneeCard.Footer totalCount={totalCount} />
                  </PromisesResolver>

               </TimeRangeProvider>
            </ProcessStepProvider>
         </Request>
		</Panel>
	);
};

const Table = ({page, pageSize, processId, query, sort}) => {
	const {client} = useContext(AppContext);
	const {getSelectedProcessSteps} = useContext(ProcessStepContext);
	const processSteps = useMemo(getSelectedProcessSteps, [query]);

   const fetchData = (page, pageSize, processId, sort) => {

	   const url = `/processes/:${processId}/assignee-users?`;
      const params = {
			page,
			pageSize,
			sort,
      };

      if(processSteps && processSteps.length && processSteps[0] !== 'allSteps'){
         params.taskKeys = processSteps[0];
      }

		client.get(url, {params}).then(({data}) => {
			return data;
		});
   }

	useEffect(() => {
		fetchData(page, pageSize, processId, query, sort);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [page, pageSize, processId, query, sort]);

	return (
		<div className="mb-3 table-fit-panel">
			<table className="table table-autofit table-hover">
				<tbody>
					{/* {items.map((item, index) => (
                  <WorkloadByAssigneeCard.Item
                     {...item}
                     currentTab={currentTab}
                     key={index}
                  />
               ))} */}
				</tbody>
			</table>
		</div>
	);
};

PerformanceByAssigneeCard.Body = Body;
PerformanceByAssigneeCard.Footer = Footer;
PerformanceByAssigneeCard.Filter = Filter;
PerformanceByAssigneeCard.Header = Header;
PerformanceByAssigneeCard.Table = Table;

export default PerformanceByAssigneeCard;
