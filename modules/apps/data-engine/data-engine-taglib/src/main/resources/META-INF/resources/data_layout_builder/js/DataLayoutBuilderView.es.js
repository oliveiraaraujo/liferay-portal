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

import DataLayoutBuilder from './DataLayoutBuilder.es';
import DataLayoutBuilderDragAndDrop from './DataLayoutBuilderDragAndDrop.es';
import DataLayoutBuilderSidebar from './DataLayoutBuilderSidebar.es';
import DataLayoutBuilderViewContextProvider from './DataLayoutBuilderViewContextProvider.es';

const parseProps = ({dataDefinitionId, dataLayoutId, ...props}) => ({
	...props,
	dataDefinitionId: Number(dataDefinitionId),
	dataLayoutId: Number(dataLayoutId)
});

export default ({...props}) => {
	const {
		dataDefinitionId,
		dataLayoutBuilderElementId,
		dataLayoutId
	} = parseProps(props);

	const [dataLayoutBuilder, setDataLayoutBuilder] = useState(null);
	const onLoad = instance => setDataLayoutBuilder(instance);

	return (
		<>
			<DataLayoutBuilder onLoad={onLoad} {...props}></DataLayoutBuilder>

			{dataLayoutBuilder && (
				<DataLayoutBuilderViewContextProvider
					dataDefinitionId={dataDefinitionId}
					dataLayoutBuilder={dataLayoutBuilder}
					dataLayoutBuilderElementId={dataLayoutBuilderElementId}
					dataLayoutId={dataLayoutId}
				>
					<DataLayoutBuilderSidebar />

					<DataLayoutBuilderDragAndDrop />
				</DataLayoutBuilderViewContextProvider>
			)}
		</>
	);
};
