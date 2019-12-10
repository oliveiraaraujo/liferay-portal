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

import {ClayModalProvider} from '@clayui/modal';
import React, {useEffect, useState} from 'react';
import {DragDropContext as dragDropContext} from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';

import DataLayoutBuilderView from './DataLayoutBuilderView.es';

const DataLayoutBuilderApp = dragDropContext(HTML5Backend)(({...props}) => {
	const [loaded, setLoaded] = useState(false);

	useEffect(() => {
		Liferay.Loader.require(...props.fieldTypesModules.split(','), () =>
			setLoaded(() => true)
		);
	});

	return (
		<ClayModalProvider>
			{loaded && <DataLayoutBuilderView {...props} />}
		</ClayModalProvider>
	);
});

export default function(props) {
	return <DataLayoutBuilderApp {...props} />;
}
