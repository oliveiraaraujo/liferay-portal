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
import React, {useReducer, useRef, createContext, useLayoutEffect} from 'react';

import Portal from '../../common/components/Portal';
import alignFloatingToolbar from '../actions/alignFloatingToolbar';
import floatingToolbarReducer from '../reducers/floatingToolbarReducer';

const ALIGNMENTS = [
	'top',
	'top-right',
	'right',
	'bottom-right',
	'bottom',
	'bottom-left',
	'left',
	'top-left'
];

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

const initialState = {
	align: 'bottom-right',
	buttons: [],
	show: false,
	targetContainerRef: {
		current: document.body
	}
};

const FloatingToolbarContext = createContext([initialState, () => {}]);

FloatingToolbarContext.displayName = 'FloatingToolbarContext';

const FloatingToolbarProvider = ({autoAlign, children, popoverProps = {}}) => {
	const [{align, buttons, show, targetContainerRef}, dispatch] = useReducer(
		floatingToolbarReducer,
		initialState
	);

	const popoverRef = useRef(null);

	useLayoutEffect(() => {
		if (show && popoverRef.current) {
			const newAlignment =
				ALIGNMENTS[
					Align.align(
						popoverRef.current,
						targetContainerRef.current,
						ALIGNMENTS_MAP[align || 'bottom-right'],
						autoAlign
					)
				];

			if (newAlignment !== align) {
				dispatch(alignFloatingToolbar({align: newAlignment}));
			}
		}
	}, [align, autoAlign, show, targetContainerRef]);

	return (
		<>
			{show && buttons.length > 0 && (
				<Portal>
					<div className="fragments-editor__floating-toolbar">
						<div className="fragments-editor__floating-toolbar-buttons">
							<ClayPopover
								alignPosition={align}
								ref={popoverRef}
								show={show}
								{...popoverProps}
							>
								{buttons.map(
									({icon, id, panelId, title, type}) => (
										<ClayButtonWithIcon
											borderless
											data-panelid={panelId}
											data-title={title}
											data-type={type}
											displayType="secondary"
											id={id}
											key={icon}
											small
											symbol={icon}
										/>
									)
								)}
							</ClayPopover>
						</div>
					</div>
				</Portal>
			)}

			<FloatingToolbarContext.Provider value={{dispatch}}>
				{children}
			</FloatingToolbarContext.Provider>
		</>
	);
};

export {FloatingToolbarContext};
export default FloatingToolbarProvider;
