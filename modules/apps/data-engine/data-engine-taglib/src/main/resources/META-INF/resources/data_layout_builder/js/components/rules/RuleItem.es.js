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
import React from 'react';

import {confirmDelete} from '../../utils/client.es';
import CollapsablePanel from '../collapsable-panel/CollapsablePanel.es';

const Text = ({children = ''}) => (
	<span className="text-lowercase">{`${children} `}</span>
);

const OPERATORS = {
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

	return (
		<CollapsablePanel
			actions={[
				{
					action: () => toggleRulesEditorVisibility(rule),
					name: Liferay.Language.get('edit'),
				},
				{
					action: confirmDelete('../'),
					name: Liferay.Language.get('delete'),
				},
			]}
			title={name}
		>
			<span>
				<span className="text-capitalize">
					{`${Liferay.Language.get('if')} `}
				</span>
				{conditions.map(({operands, operator}, index) => {
					const [first, last] = operands;

					return (
						<>
							<Text>{Liferay.Language.get('field')}</Text>
							<ClayLabel displayType="success">
								{first.label}
							</ClayLabel>
							<ClayLabel displayType="secondary">
								{OPERATORS[operator] || operator}
							</ClayLabel>
							{last && last.value && (
								<ClayLabel displayType="info">
									{last.value}
								</ClayLabel>
							)}
							{conditions.length !== index + 1 && (
								<ClayLabel displayType="warning">
									{logicalOperator}
								</ClayLabel>
							)}
						</>
					);
				})}
				<ClayLabel displayType="warning">
					{Liferay.Language.get('then')}
				</ClayLabel>
				{actions.map(({action, label}, index) => (
					<>
						<Text>{action}</Text>
						<ClayLabel displayType="success">{label}</ClayLabel>
						{actions.length !== index + 1 && (
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
