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

const Label = ({children, displayType = 'success'}) => (
	<ClayLabel displayType={displayType}>{children}</ClayLabel>
);

const Text = ({children = '', className = ''}) => (
	<span className={className}>{`${children.toLowerCase()} `}</span>
);

export default function RuleItem({rule, toggleRulesEditorVisibility}) {
	const {actions, conditions, logicalOperator, name} = rule;

	const dropDownActions = [
		{
			action: () => toggleRulesEditorVisibility(rule),
			name: Liferay.Language.get('edit'),
		},
		{
			action: confirmDelete('../'),
			name: Liferay.Language.get('delete'),
		},
	];

	const RULE_SENTENCES = {
		and: Liferay.Language.get('and'),
		field: Liferay.Language.get('field'),
		if: Liferay.Language.get('if'),
		value: Liferay.Language.get('value'),
	};

	return (
		<CollapsablePanel
			actions={dropDownActions}
			collapsable
			title={name}
			type="unstyled"
		>
			<CollapsablePanel.Body>
				<span>
					<Text className="text-capitalize">{RULE_SENTENCES.if}</Text>
					{conditions.map(({operands, operator}, index) => {
						const [first, last] = operands;

						return (
							<>
								<Text>{RULE_SENTENCES.field}</Text>
								<Label>{first.label}</Label>
								<Label displayType="secondary">
									{operator}
								</Label>
								<Text>{RULE_SENTENCES.value}</Text>
								<Label displayType="info">{last.label}</Label>
								{conditions.length !== index + 1 && (
									<Label displayType="warning">
										{logicalOperator}
									</Label>
								)}
							</>
						);
					})}
					{actions.map(({action, label}, index) => (
						<>
							<Text>{action}</Text>
							<Label>{label}</Label>
							{actions.length !== index + 1 && (
								<Label displayType="warning">
									{RULE_SENTENCES.and}
								</Label>
							)}
						</>
					))}
				</span>
			</CollapsablePanel.Body>
		</CollapsablePanel>
	);
}
