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
import React, {useLayoutEffect, useRef, useState} from 'react';

import ConfigurationPanel from './ConfigurationPanel';
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

	const [activeConfigurationPanel, setActiveConfigurationPanel] = useState(
		null
	);

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
					ref={popoverRef}
					show
				>
					{buttons.map(button => (
						<FloatingToolbarButton
							active={button.panelId === activeConfigurationPanel}
							key={button.panelId}
							onClick={panelId => {
								if (activeConfigurationPanel === panelId) {
									setActiveConfigurationPanel(null);
								} else {
									setActiveConfigurationPanel(panelId);
								}
							}}
							{...button}
						/>
					))}
				</ClayPopover>

				{activeConfigurationPanel && (
					<ConfigurationPanel
						configurationPanel={activeConfigurationPanel}
						popoverRef={popoverRef}
					/>
				)}
			</div>
		)
	);
}

function FloatingToolbarButton({active, icon, onClick, panelId}) {
	return (
		<ClayButtonWithIcon
			borderless
			className={active && 'active'}
			displayType="secondary"
			onClick={() => {
				if (onClick) {
					onClick(panelId);
				}
			}}
			small
			symbol={icon}
		/>
	);
}
