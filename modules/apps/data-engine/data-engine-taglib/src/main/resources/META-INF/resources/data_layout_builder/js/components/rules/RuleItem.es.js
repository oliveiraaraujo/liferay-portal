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
				<Text className="text-capitalize">
					{Liferay.Language.get('if')}
				</Text>
				{conditions.map(({operands, operator}, index) => {
					const [first, last] = operands;

					return (
						<>
							<Text>{Liferay.Language.get('field')}</Text>
							<Label>{first.label}</Label>
							<Label displayType="secondary">{operator}</Label>
							<Text>{Liferay.Language.get('value')}</Text>
							<Label displayType="info">{last.label}</Label>
							{conditions.length < index && (
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
						{actions.length < index && (
							<Label displayType="warning">
								{Liferay.Language.get('and')}
							</Label>
						)}
					</>
				))}
			</span>
		</CollapsablePanel>
	);
}
