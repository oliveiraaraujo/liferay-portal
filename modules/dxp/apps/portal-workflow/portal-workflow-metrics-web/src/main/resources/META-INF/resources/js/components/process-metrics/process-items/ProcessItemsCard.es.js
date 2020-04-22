/* eslint-disable react-hooks/exhaustive-deps */
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

import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useMemo} from 'react';

import Panel from '../../../shared/components/Panel.es';
import ContentView from '../../../shared/components/content-view/ContentView.es';
import ReloadButton from '../../../shared/components/list/ReloadButton.es';
import PromisesResolver from '../../../shared/components/promises-resolver/PromisesResolver.es';
import {useFetch} from '../../../shared/hooks/useFetch.es';
import PANELS from './Panels.es';
import SummaryCard from './SummaryCard.es';

const ProcessItemsCard = ({
	children,
	completed,
	description,
	processId,
	timeRange,
	title,
}) => {
	const timeRangeParams = timeRange || {};

	const {data, fetchData} = useFetch({
		params: {
			completed,
			...timeRangeParams,
		},
		url: `/processes/${processId}/metrics`,
	});

	const promises = useMemo(() => [fetchData()], [fetchData]);

	return (
		<PromisesResolver promises={promises}>
			<Panel>
				<ProcessItemsCard.Header
					data={data}
					description={description}
					title={title}
				>
					{children}
				</ProcessItemsCard.Header>

				<ProcessItemsCard.Body
					completed={completed}
					data={data}
					processId={processId}
					timeRange={timeRange}
				/>
			</Panel>
		</PromisesResolver>
	);
};

const Body = ({completed = false, data, processId, timeRange}) => {
	const statesProps = {
		errorProps: {
			actionButton: <ReloadButton />,
			className: 'mt-2 pb-5 pt-4',
			hideAnimation: true,
			message: Liferay.Language.get(
				'there-was-a-problem-retrieving-data-please-try-reloading-the-page'
			),
			messageClassName: 'small',
		},
		loadingProps: {className: 'mt-2 pb-5 pt-4'},
	};

	return (
		<Panel.Body>
			<ContentView {...statesProps}>
				{data ? (
					<div className="d-flex pb-3">
						{PANELS.map((panel, index) => (
							<SummaryCard
								{...panel}
								completed={completed}
								key={index}
								processId={processId}
								timeRange={timeRange}
								total={
									panel.addressedToField === panel.totalField
								}
								totalValue={data[panel.totalField]}
								value={data[panel.addressedToField]}
							/>
						))}
					</div>
				) : (
					<></>
				)}
			</ContentView>
		</Panel.Body>
	);
};

const Header = ({children, data, description, title}) => (
	<Panel.Header
		elementClasses={['dashboard-panel-header', children && 'pb-0']}
	>
		<div className="autofit-row">
			<div className="autofit-col autofit-col-expand flex-row">
				<span className="mr-2">{title}</span>

				<ClayTooltipProvider>
					<span>
						<span
							className="workflow-tooltip"
							data-tooltip-align={'right'}
							title={description}
						>
							<ClayIcon symbol="question-circle-full" />
						</span>
					</span>
				</ClayTooltipProvider>
			</div>

			{children && data && (
				<div className="autofit-col m-0 management-bar management-bar-light navbar">
					<ul className="navbar-nav">{children}</ul>
				</div>
			)}
		</div>
	</Panel.Header>
);

ProcessItemsCard.Body = Body;
ProcessItemsCard.Header = Header;

export default ProcessItemsCard;
