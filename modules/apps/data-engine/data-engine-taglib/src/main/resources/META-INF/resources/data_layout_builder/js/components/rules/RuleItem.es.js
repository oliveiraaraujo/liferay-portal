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
import classNames from 'classnames';
import React, {useContext} from 'react';

import AppContext from '../../AppContext.es';
import {DELETE_DATA_LAYOUT_RULE} from '../../actions.es';
import CollapsablePanel from '../collapsable-panel/CollapsablePanel.es';

const Text = ({capitalize = false, children = '', lowercase = false}) => (
	<span
		className={classNames('pr-1', {
			'text-capitalize': capitalize,
			'text-lowercase': lowercase,
		})}
	>
		{children}
	</span>
);

const OPERATOR_LABELS = {
	'belongs-to': Liferay.Language.get('belongs-to'),
	contains: Liferay.Language.get('contains'),
	'equals-to': Liferay.Language.get('equals-to'),
	'is-empty': Liferay.Language.get('is-empty'),
	'not-contains': Liferay.Language.get('not-contains'),
	'not-equals-to': Liferay.Language.get('not-equals-to'),
	'not-is-empty': Liferay.Language.get('not-is-empty'),
};

export default function RuleItem({rule, toggleRulesEditorVisibility}) {
	const {actions, conditions, logicalOperator, name} = rule;
	const [, dispatch] = useContext(AppContext);

	const dropDownActions = [
		{
			action: () => toggleRulesEditorVisibility(rule),
			name: Liferay.Language.get('edit'),
		},
		{
			action: () => {
				const confirmed = confirm(
					Liferay.Language.get('are-you-sure-you-want-to-delete-this')
				);

				if (confirmed) {
					dispatch({
						payload: {
							ruleId: rule.id,
						},
						type: DELETE_DATA_LAYOUT_RULE,
					});
				}
			},
			name: Liferay.Language.get('delete'),
		},
	];

	return (
		<CollapsablePanel
			actions={dropDownActions}
			title={name}
		>
			<span>
				<Text capitalize>{Liferay.Language.get('if')}</Text>

				{conditions.map(({operands, operator}, index) => {
					const [first, last] = operands;

					return (
						<>
							<Text lowercase>
								{Liferay.Language.get('field')}
							</Text>
							<ClayLabel displayType="success">
								{first.label}
							</ClayLabel>
							<ClayLabel displayType="secondary">
								{OPERATOR_LABELS[operator] || operator}
							</ClayLabel>

							{last && last.value && (
								<ClayLabel displayType="info">
									{last.value}
								</ClayLabel>
							)}

							{index + 1 !== conditions.length && (
								<ClayLabel displayType="warning">
									{logicalOperator}
								</ClayLabel>
							)}
						</>
					);
				})}

				<Text>{Liferay.Language.get('then')}</Text>

				{actions.map(({action, label}, index) => (
					<>
						<Text lowercase>{action}</Text>
						<ClayLabel displayType="success">{label}</ClayLabel>

						{index + 1 !== actions.length && (
							<ClayLabel displayType="warning">
								{Liferay.Language.get('and')}
							</ClayLabel>
						)}
					</>
				))}
			</span>
		</CollapsablePanel>
	);
}
