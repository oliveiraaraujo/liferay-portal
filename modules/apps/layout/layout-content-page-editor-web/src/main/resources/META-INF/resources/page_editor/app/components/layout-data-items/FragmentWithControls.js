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

import React, {useMemo} from 'react';

import {
	LayoutDataPropTypes,
	getLayoutDataItemPropTypes,
} from '../../../prop-types/index';
import {LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS} from '../../config/constants/layoutDataFloatingToolbarButtons';
import selectCanUpdateLayoutContent from '../../selectors/selectCanUpdateLayoutContent';
import {useDispatch, useSelector} from '../../store/index';
import duplicateItem from '../../thunks/duplicateItem';
import {useSelectItem} from '../Controls';
import Topper from '../Topper';
import FloatingToolbar from '../floating-toolbar/FloatingToolbar';
import FragmentContent from '../fragment-content/FragmentContent';

const FragmentWithControls = React.forwardRef(({item, layoutData}, ref) => {
	const dispatch = useDispatch();
	const selectItem = useSelectItem();
	const state = useSelector(state => state);
	const canUpdateLayoutContent = useSelector(selectCanUpdateLayoutContent);

	const {fragmentEntryLinks} = state;

	const fragmentEntryLink =
		fragmentEntryLinks[item.config.fragmentEntryLinkId];

	const handleButtonClick = id => {
		if (id === LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.duplicateItem.id) {
			dispatch(
				duplicateItem({
					itemId: item.itemId,
					selectItem,
					store: state,
				})
			);
		}
	};

	const floatingToolbarButtons = useMemo(() => {
		const buttons = [];

		const portletId = fragmentEntryLink.editableValues.portletId;

		const widget = portletId && getWidget(state.widgets, portletId);

		if (!widget || widget.instanceable) {
			buttons.push(LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.duplicateItem);
		}

		const configuration = fragmentEntryLink.configuration;

		if (
			configuration &&
			Array.isArray(configuration.fieldSets) &&
			configuration.fieldSets.length
		) {
			buttons.push(
				LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.fragmentConfiguration
			);
		}

		return buttons;
	}, [
		fragmentEntryLink.configuration,
		fragmentEntryLink.editableValues.portletId,
		state.widgets,
	]);

	return (
		<Topper item={item} itemRef={ref} layoutData={layoutData}>
			<>
				{canUpdateLayoutContent && (
					<FloatingToolbar
						buttons={floatingToolbarButtons}
						item={item}
						itemRef={ref}
						onButtonClick={handleButtonClick}
					/>
				)}

				<FragmentContent
					fragmentEntryLinkId={fragmentEntryLink.fragmentEntryLinkId}
					itemId={item.itemId}
					ref={ref}
				/>
			</>
		</Topper>
	);
});

function getWidget(widgets, portletId) {
	let widget = null;

	const widgetsLength = widgets.length;

	for (let i = 0; i < widgetsLength; i++) {
		const {categories = [], portlets = []} = widgets[i];
		const categoryPortlet = portlets.find(
			_portlet => _portlet.portletId === portletId
		);
		const subCategoryPortlet = getWidget(categories, portletId);

		if (categoryPortlet) {
			widget = categoryPortlet;
		}

		if (subCategoryPortlet) {
			widget = subCategoryPortlet;
		}
	}

	return widget;
}

FragmentWithControls.displayName = 'FragmentWithControls';

FragmentWithControls.propTypes = {
	item: getLayoutDataItemPropTypes().isRequired,
	layoutData: LayoutDataPropTypes.isRequired,
};

export default FragmentWithControls;
