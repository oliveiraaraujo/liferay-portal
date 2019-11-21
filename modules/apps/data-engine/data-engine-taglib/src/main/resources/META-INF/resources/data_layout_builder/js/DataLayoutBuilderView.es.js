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

import React, {useState} from 'react';

import DataLayoutBuilderDragAndDrop from './DataLayoutBuilderDragAndDrop.es';
import DataLayoutBuilderSidebar from './DataLayoutBuilderSidebar.es';
import DataLayoutBuilderViewContextProvider from './DataLayoutBuilderViewContextProvider.es';

const parseProps = ({dataDefinitionId, dataLayoutId, ...props}) => ({
	...props,
	dataDefinitionId: Number(dataDefinitionId),
	dataLayoutId: Number(dataLayoutId)
});

const DataLayoutBuilderView = props => {
	const {
		dataDefinitionId,
		dataLayoutBuilder,
		dataLayoutBuilderElementId,
		dataLayoutId
	} = parseProps(props);

	return (
		<DataLayoutBuilderViewContextProvider
			dataDefinitionId={dataDefinitionId}
			dataLayoutBuilder={dataLayoutBuilder}
			dataLayoutId={dataLayoutId}
		>
			<DataLayoutBuilderSidebar
				dataLayoutBuilder={dataLayoutBuilder}
				dataLayoutBuilderElementId={dataLayoutBuilderElementId}
			/>

			<DataLayoutBuilderDragAndDrop
				dataLayoutBuilder={dataLayoutBuilder}
			/>
		</DataLayoutBuilderViewContextProvider>
	);
};

export default ({dataLayoutBuilderId, ...props}) => {
	const [dataLayoutBuilder, setDataLayoutBuilder] = useState();

	if (!dataLayoutBuilder) {
		Liferay.componentReady(dataLayoutBuilderId).then(setDataLayoutBuilder);
	}

	return dataLayoutBuilder ? (
		<DataLayoutBuilderView
			dataLayoutBuilder={dataLayoutBuilder}
			{...props}
		/>
	) : null;
};
