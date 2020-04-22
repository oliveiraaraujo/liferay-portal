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

import React, {useContext, useEffect, useMemo} from 'react';

import {
	LayoutDataPropTypes,
	getLayoutDataItemPropTypes,
} from '../../../prop-types/index';
import selectCanUpdateLayoutContent from '../../selectors/selectCanUpdateLayoutContent';
import {useSelector} from '../../store/index';
import {useIsActive} from '../Controls';
import TopperEmpty from '../TopperEmpty';
import Column from './Column';
import {ResizingContext} from './RowWithControls';

const ColumnWithControls = React.forwardRef(
	({children, item, layoutData}, ref) => {
		const isActive = useIsActive();
		const canUpdateLayoutContent = useSelector(
			selectCanUpdateLayoutContent
		);

		const parentItemIsActive = useMemo(
			() =>
				layoutData.items[item.parentId]
					? isActive(item.parentId)
					: false,
			[isActive, item, layoutData]
		);

		const {onResizeEnd, onResizeStart, onResizing} = useContext(
			ResizingContext
		);

		const columnInfo = useMemo(() => getColumnInfo({item, layoutData}), [
			item,
			layoutData,
		]);

		const onWindowMouseMove = event => {
			event.preventDefault();

			onResizing(event, columnInfo);
		};

		const onWindowMouseUp = event => {
			onResizeEnd(event);

			window.removeEventListener('mouseup', onWindowMouseUp);
			window.removeEventListener('mousemove', onWindowMouseMove);
		};

		const onResizeButtonMouseDown = event => {
			onResizeStart(event);

			window.addEventListener('mouseup', onWindowMouseUp);
			window.addEventListener('mousemove', onWindowMouseMove);
		};

		useEffect(
			() => () => {
				window.removeEventListener('mouseup', onWindowMouseUp);
				window.removeEventListener('mousemove', onWindowMouseMove);
			},

			// We just want to ensure that this listeners are removed if
			// the component is unmounted before resizing has ended
			// eslint-disable-next-line react-hooks/exhaustive-deps
			[]
		);

		const content = (
			<Column className="page-editor__col" item={item} ref={ref}>
				{parentItemIsActive && !columnInfo.isLastColumn ? (
					<div>
						{children}
						<button
							className="btn-primary page-editor__col__resizer"
							onMouseDown={onResizeButtonMouseDown}
						/>
					</div>
				) : (
					children
				)}
			</Column>
		);

		return canUpdateLayoutContent ? (
			<TopperEmpty item={item} layoutData={layoutData}>
				{content}
			</TopperEmpty>
		) : (
			content
		);
	}
);

ColumnWithControls.propTypes = {
	item: getLayoutDataItemPropTypes().isRequired,
	layoutData: LayoutDataPropTypes.isRequired,
};

export default ColumnWithControls;

/**
 * Retrieves necessary data from the current and next column.
 *
 * @param {!Object} options
 * @param {!Object} options.item
 * @param {!Object} options.layoutData
 *
 * @returns {!Object}
 */
function getColumnInfo({item, layoutData}) {
	const rowColumns = layoutData.items[item.parentId].children;
	const colIndex = rowColumns.indexOf(item.itemId);
	const nextColumnIndex = colIndex + 1;
	const currentColumn = item;
	const currentColumnConfig = currentColumn.config;
	const nextColumn = {...layoutData.items[rowColumns[nextColumnIndex]]};
	const nextColumnConfig =
		typeof nextColumn === 'object' && Object.keys(nextColumn).length
			? nextColumn.config
			: {};

	return {
		colIndex,
		currentColumn,
		currentColumnConfig,
		isLastColumn: rowColumns.indexOf(item.itemId) === rowColumns.length - 1,
		nextColumn: nextColumn ? nextColumn : {},
		nextColumnConfig: nextColumn ? nextColumnConfig : {},
		nextColumnIndex: colIndex + 1,
		rowColumns,
	};
}
