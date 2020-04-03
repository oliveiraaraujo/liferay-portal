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

import ClayLabel from '@clayui/label';
import ClayPanel from '@clayui/panel';
import React from 'react';

import {confirmDelete} from '../../utils/client.es';
import CustomPanel from '../custom-panel/CustomPanel.es';

const ShowLabel = (text, displayType) => (
	<ClayLabel displayType={displayType}>{text}</ClayLabel>
);

const ShowText = (text, className = 'ruleitem-lowercase') => (
	<span className={className}>{`${Liferay.Language.get(text)} `}</span>
);

export default function RuleItem({rule, toggleRulesEditor}) {
	const {actions, conditions, logicalOperator, name} = rule;

	const dropDownActions = [
		{
			action: () => toggleRulesEditor(rule),
			name: Liferay.Language.get('edit'),
		},
		{
			action: confirmDelete('../'),
			name: Liferay.Language.get('delete'),
		},
	];

	return (
		<CustomPanel
			actions={dropDownActions}
			collapsable
			displayTitle={name}
			displayType="unstyled"
		>
			<ClayPanel.Body>
				<span>
					{ShowText('if', 'ruleitem-capitalize')}
					{conditions.map(({operands, operator}, index) => {
						const [first, last] = operands;

						return (
							<>
								{ShowText('field')}
								{ShowLabel(first.label, 'success')}
								{ShowLabel(operator, 'secondary')}
								{ShowText('value')}
								{ShowLabel(last.label, 'info')}
								{conditions.length !== index + 1 &&
									ShowLabel(logicalOperator, 'warning')}
							</>
						);
					})}
					{actions.map(({action, label}, index) => (
						<>
							{ShowText(action)}
							{ShowLabel(label, 'success')}
							{actions.length !== index + 1 &&
								ShowLabel(
									Liferay.Language.get('and'),
									'warning'
								)}
						</>
					))}
				</span>
			</ClayPanel.Body>
		</CustomPanel>
	);
}
