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

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import classNames from 'classnames';
import React from 'react';

import {useTransitionHeight} from '../../hooks/useTransition.es';
import DropDown from '../drop-down/DropDown.es';

const CollapsablePanel = ({actions, children, title}) => {
	const panelRef = React.useRef(null);
	const [expanded, setExpaned] = React.useState(false);

	const [
		transitioning,
		handleTransitionEnd,
		handleClickToggler,
	] = useTransitionHeight(expanded, setExpaned, panelRef);

	const showIconCollapsed = !(
		(!expanded && transitioning) ||
		(expanded && !transitioning)
	);

	return (
		<div
			className={classNames(
				'collapsable-panel',
				'panel',
				'panel-unstyled'
			)}
			role="tablist"
		>
			<>
				<div className={classNames('panel-header')}>
					<span className="panel-title">{title}</span>
					<ClayButton
						aria-expanded={expanded}
						className={classNames(
							'collapse-icon',
							'collapse-icon-middle',
							{
								collapsed: showIconCollapsed,
							}
						)}
						displayType="unstyled"
						onClick={handleClickToggler}
						role="tab"
					>
						<>
							<span
								className={classNames(
									'collapse-icon-closed',
									'actions'
								)}
							>
								<ClayIcon symbol="angle-down" />
							</span>
							<span
								className={classNames(
									'collapse-icon-open',
									'actions'
								)}
							>
								<ClayIcon symbol="angle-up" />
							</span>
						</>
					</ClayButton>

					<span className="collapse-icon-options">
						<DropDown actions={actions} />
					</span>
				</div>

				<div
					className={classNames('panel-collapse', {
						collapse: !transitioning,
						collapsing: transitioning,
						show: expanded,
					})}
					onTransitionEnd={handleTransitionEnd}
					ref={panelRef}
					role="tabpanel"
				>
					<ClayPanel.Body>{children}</ClayPanel.Body>
				</div>
			</>
		</div>
	);
};

export default CollapsablePanel;
