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

import {getIndexes} from 'dynamic-data-mapping-form-renderer/js/components/FormRenderer/FormSupport.es';
import dom from 'metal-dom';
import {useEffect, useCallback, useContext} from 'react';
import {useDrop} from 'react-dnd';

import DataLayoutBuilderContext from './DataLayoutBuilderContext.es';
import {dropLayoutBuilderField} from './actions.es';
import {DRAG_FIELD_TYPE} from './utils/dragTypes.es';

const replaceColumn = node => {
	if (node.parentNode) {
		node.parentNode.replaceChild(node.cloneNode(true), node);
	}
};

export default ({node}) => {
	const [dataLayoutBuilder] = useContext(DataLayoutBuilderContext);
	const onDrop = useCallback(
		({data, type}) => {
			const addedToPlaceholder = !!dom.closest(node, '.placeholder');
			const indexes = getIndexes(node.parentElement);

			if (type === DRAG_FIELD_TYPE) {
				dataLayoutBuilder.dispatch(
					'fieldAdded',
					dropLayoutBuilderField({
						addedToPlaceholder,
						dataLayoutBuilder,
						fieldTypeName: data.name,
						indexes
					})
				);
			}
		},
		[dataLayoutBuilder, node]
	);
	const [{canDrop, overTarget}, dropColumn] = useDrop({
		accept: [DRAG_FIELD_TYPE],
		collect: monitor => ({
			canDrop: monitor.canDrop(),
			overTarget: monitor.isOver()
		}),
		drop: onDrop
	});

	useEffect(() => {
		dropColumn(node);

		return () => replaceColumn(node);
	}, [dropColumn, node]);

	useEffect(() => {
		const {classList} = node;

		if (canDrop && classList.contains('ddm-empty-page')) {
			classList.add('target-droppable');
		} else {
			classList.remove('target-droppable');
		}

		if (overTarget) {
			classList.add('target-over');
		} else {
			classList.remove('target-over');
		}
	}, [canDrop, node, overTarget]);

	return null;
};
