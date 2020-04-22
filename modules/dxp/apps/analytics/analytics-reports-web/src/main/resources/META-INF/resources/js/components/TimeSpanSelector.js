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

import {ClayButtonWithIcon} from '@clayui/button';
import {ClaySelect} from '@clayui/form';
import {ClayTooltipProvider} from '@clayui/tooltip';
import PropTypes from 'prop-types';
import React from 'react';

export default function TimeSpanSelector({
	disabledNextTimeSpan,
	disabledPreviousPeriodButton,
	onNextTimeSpanClick,
	onPreviousTimeSpanClick,
	onTimeSpanChange,
	timeSpanOption,
	timeSpanOptions,
}) {
	return (
		<div className="d-flex mb-3 mt-3">
			<ClaySelect
				aria-label={Liferay.Language.get('select-date-range')}
				onChange={onTimeSpanChange}
				value={timeSpanOption}
			>
				{timeSpanOptions.map(option => {
					return (
						<ClaySelect.Option
							key={option.key}
							label={option.label}
							value={option.key}
						/>
					);
				})}
			</ClaySelect>

			<div className="d-flex ml-2">
				<ClayTooltipProvider>
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('previous-period')}
						className="mr-1"
						data-tooltip-align="top-right"
						disabled={disabledPreviousPeriodButton}
						displayType="secondary"
						onClick={onPreviousTimeSpanClick}
						small
						symbol="angle-left"
						title={
							disabledPreviousPeriodButton
								? Liferay.Language.get(
										'you-cannot-choose-a-date-prior-to-the-publication-date'
								  )
								: undefined
						}
					/>
				</ClayTooltipProvider>
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('next-period')}
					disabled={disabledNextTimeSpan}
					displayType="secondary"
					onClick={onNextTimeSpanClick}
					small
					symbol="angle-right"
				/>
			</div>
		</div>
	);
}

TimeSpanSelector.proptypes = {
	disabledNextTimeSpan: PropTypes.bool.isRequired,
	disabledPreviousPeriodButton: PropTypes.bool.isRequired,
	onNextTimeSpanClick: PropTypes.func.isRequired,
	onPreviousTimeSpanClick: PropTypes.func.isRequired,
	onTimeSpanChange: PropTypes.func.isRequired,
	timeSpanOption: PropTypes.string.isRequired,
	timeSpanOptions: PropTypes.arrayOf(
		PropTypes.shape({
			key: PropTypes.string.isRequired,
			label: PropTypes.string.isRequired,
		})
	).isRequired,
};
