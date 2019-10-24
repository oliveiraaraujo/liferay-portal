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
import PromisesResolver from '../../../shared/components/request/PromisesResolver.es';
import { AppContext } from '../../AppContext.es';
import { ProcessStepContext } from '../filter/store/ProcessStepStore.es';
import Filter from './PerformanceByAssignee.Filter.es';

const Body = ({data}) => {

   return (
      <Panel.Body>
         <PromisesResolver.Resolved>
            <PerformanceByAssigneeCard.Bottom totalCount={data.totalCount} />
         </PromisesResolver.Resolved>   
      </Panel.Body>
   );
};

const Bottom = ({totalCount}) => {

   return (
      <div className="mb-1 text-right">
         <button className="border-0 btn btn-secondary btn-sm">
            <span className="mr-2" data-testid="viewAllSteps">
               {`${Liferay.Language.get('view-all-assignees')} (${totalCount})`}
            </span>

            <Icon iconName="caret-right-l" />
         </button>
      </div>
   );
};

const Header = (props) => {

	return (
		<Panel.HeaderWithOptions
			description={Liferay.Language.get(
				'performance-by-step-description'
			)}
			elementClasses="dashboard-panel-header pb-0"
			title={Liferay.Language.get('performance-by-step')}
		>
			<PromisesResolver.Resolved>
				<PerformanceByAssigneeCard.Filter {...props}></PerformanceByAssigneeCard.Filter>
			</PromisesResolver.Resolved>
		</Panel.HeaderWithOptions>
	);
};

const PerformanceByAssigneeCard = ({page, pageSize, processId, query}) => {
   const data = {totalCount: 5};
   const functions = [Promise.resolve];

	return (
		<Panel>
			<PromisesResolver promises={functions}>
				<PerformanceByAssigneeCard.Header processId={processId} query={query} />
            <PerformanceByAssigneeCard.Body data={data} page={page} pageSize={pageSize} />
			</PromisesResolver>
		</Panel>
	);
};

const Table = ({page, pageSize, processId, query, sort}) => {
   const {client} = useContext(AppContext);
   const {getSelectedProcessSteps} = useContext(ProcessStepContext);
   const processSteps = useMemo(getSelectedProcessSteps, [query]);
   const url = `/processes/:${processId}/assignee-users?`;

   useEffect(()=>{
      const params = {
         page,
			pageSize,
			sort,
			taskKeys: processSteps,
      };

      client.get(url, {params})
      .then(({data}) => {
			return data;
      });

      // eslint-disable-next-line react-hooks/exhaustive-deps
   },[page, pageSize, processId, query, sort]);

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
PerformanceByAssigneeCard.Bottom = Bottom;
PerformanceByAssigneeCard.Filter = Filter;
PerformanceByAssigneeCard.Header = Header;
PerformanceByAssigneeCard.Table = Table;

export default PerformanceByAssigneeCard;
