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

import moment from 'moment';
import React from 'react';
import ReactDOM from 'react-dom';
import SearchContainer from './components/SearchContainer.es';

function AppBuilder() {
	const columns = [
		{
			name: Liferay.Language.get('name')
		},
		{
			dateCreated: Liferay.Language.get('create-date')
		},
		{
			dateModified: Liferay.Language.get('modified-date')
		}
	];

	const endpoint = `/o/data-engine/v1.0/sites/${Liferay.ThemeDisplay.getCompanyGroupId()}/data-definitions`;

	const formatter = items =>
		items.map(item => ({
			name: item.name.en_US,
			dateCreated: moment(item.dateCreated).fromNow(),
			dateModified: moment(item.dateModified).fromNow()
		}));

	return (
		<SearchContainer
			columns={columns}
			endpoint={endpoint}
			formatter={formatter}
			pageSize={10}
		/>
	);
}

export default function(id) {
	ReactDOM.render(<AppBuilder />, document.getElementById(id));
}
