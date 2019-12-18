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

import {ClayButtonWithIcon} from '@clayui/button';
import ClayPopover from '@clayui/popover';
import {Align} from 'metal-position';
import React, {useRef, useLayoutEffect} from 'react';
import ReactDOM from 'react-dom';

import {useIsActive} from './Controls';

const ALIGNMENTS_MAP = {
	bottom: Align.Bottom,
	'bottom-left': Align.BottomLeft,
	'bottom-right': Align.BottomRight,
	left: Align.Left,
	right: Align.Right,
	top: Align.Top,
	'top-left': Align.TopLeft,
	'top-right': Align.TopRight
};

export default function FloatingToolbar({buttons, item, itemRef}) {
	const isActive = useIsActive();
	const popoverRef = useRef(null);
	const show = isActive(item.itemId);

	useLayoutEffect(() => {
		if (show && itemRef.current && popoverRef.current) {
			Align.align(
				popoverRef.current,
				itemRef.current,
				ALIGNMENTS_MAP['bottom-right'],
				false
			);
		}
	}, [show, itemRef, popoverRef]);

	return (
		show &&
		buttons.length && (
			<div className="position-absolute pr-2 pt-2" ref={popoverRef}>
				<ClayPopover
					alignPosition={false}
					className="position-static"
					show
				>
					{buttons.map(button => (
						<FloatingToolbarButton key={button.panelId} {...button}/>
					))}
				</ClayPopover>
			document.body
		)
	);
}

function FloatingToolbarButton({icon}) {
	return (
		<ClayButtonWithIcon
			borderless
			displayType="secondary"
			small
			symbol={icon}
		/>
	);
}
