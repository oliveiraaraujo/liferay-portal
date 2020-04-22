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

import PropTypes from 'prop-types';
import React from 'react';

import BasicInformation from './BasicInformation';
import Chart from './Chart';
import Hint from './Hint';
import TotalCount from './TotalCount';
import TrafficSources from './TrafficSources';

export default function Main({
	authorName,
	chartDataProviders,
	defaultTimeSpanOption,
	languageTag,
	onTrafficSourceClick,
	pagePublishDate,
	pageTitle,
	timeSpanOptions,
	totalReadsDataProvider,
	totalViewsDataProvider,
	trafficSources,
}) {
	return (
		<>
			<BasicInformation
				authorName={authorName}
				languageTag={languageTag}
				publishDate={pagePublishDate}
				title={pageTitle}
			/>

			<h5 className="mt-4 sheet-subtitle text-secondary">
				{Liferay.Language.get('views-and-reads')}
			</h5>

			<TotalCount
				className="mb-2"
				dataProvider={totalViewsDataProvider}
				label={Liferay.Util.sub(Liferay.Language.get('total-views'))}
				popoverHeader={Liferay.Language.get('total-views')}
				popoverMessage={Liferay.Language.get(
					'this-number-refers-to-the-total-number-of-views-since-the-content-was-published'
				)}
			/>
			<TotalCount
				dataProvider={totalReadsDataProvider}
				label={Liferay.Util.sub(Liferay.Language.get('total-reads'))}
				popoverHeader={Liferay.Language.get('total-reads')}
				popoverMessage={Liferay.Language.get(
					'this-number-refers-to-the-total-number-of-reads-since-the-content-was-published'
				)}
			/>

			<Chart
				dataProviders={chartDataProviders}
				defaultTimeSpanOption={defaultTimeSpanOption}
				languageTag={languageTag}
				publishDate={pagePublishDate}
				timeSpanOptions={timeSpanOptions}
			/>

			<h5 className="mt-2 sheet-subtitle text-secondary">
				{Liferay.Language.get('search-engines-traffic')}
				<Hint
					message={Liferay.Language.get(
						'search-engines-traffic-help'
					)}
					title={Liferay.Language.get('search-engines-traffic')}
				/>
			</h5>

			<TrafficSources
				languageTag={languageTag}
				onTrafficSourceClick={onTrafficSourceClick}
				trafficSources={trafficSources}
			/>
		</>
	);
}

Main.proptypes = {
	authorName: PropTypes.string.isRequired,
	chartDataProviders: PropTypes.arrayOf(PropTypes.func.isRequired).isRequired,
	defaultTimeSpanOption: PropTypes.string.isRequired,
	languageTag: PropTypes.string.isRequired,
	onTrafficSourceClick: PropTypes.func.isRequired,
	pagePublishDate: PropTypes.number.isRequired,
	pageTitle: PropTypes.string.isRequired,
	timeSpanOptions: PropTypes.arrayOf(
		PropTypes.shape({
			key: PropTypes.string,
			label: PropTypes.string,
		})
	).isRequired,
	totalReadsDataProvider: PropTypes.func.isRequired,
	totalViewsDataProvider: PropTypes.func.isRequired,
	trafficSources: PropTypes.array.isRequired,
};
