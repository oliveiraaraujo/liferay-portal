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

const CollapsablePanel = ({
	actions,
	children,
	className,
	collapsable,
	collapseClassNames,
	defaultExpanded = false,
	showCollapseIcon = true,
	title,
	type,
	...otherProps
}) => {
	const panelRef = React.useRef(null);
	const [expanded, setExpaned] = React.useState(defaultExpanded);

	const [
		transitioning,
		handleTransitionEnd,
		handleClickToggler,
	] = useTransitionHeight(expanded, setExpaned, panelRef);

	const showIconCollapsed = !(
		(!expanded && transitioning) ||
		(expanded && !transitioning)
	);

	const hasActions = actions && actions.length;

	return (
		<div
			{...otherProps}
			className={classNames('collapsable-panel', 'panel', className, {
				[`panel-${type}`]: type,
			})}
			role="tablist"
		>
			{!collapsable && (
				<>
					{title && (
						<ClayPanel.Header>
							<span className="panel-title">{title}</span>
						</ClayPanel.Header>
					)}

					{children}
				</>
			)}

			{collapsable && (
				<>
					<div className={classNames('panel-header')}>
						<span className="panel-title">{title}</span>
						<ClayButton
							aria-expanded={expanded}
							className={classNames({
								'collapse-icon': showCollapseIcon,
								'collapse-icon-middle': showCollapseIcon,
								collapsed: showIconCollapsed,
							})}
							displayType="unstyled"
							onClick={handleClickToggler}
							role="tab"
						>
							{showCollapseIcon && (
								<>
									<span
										className={classNames(
											'collapse-icon-closed',
											{
												actions: hasActions,
											}
										)}
									>
										<ClayIcon symbol="angle-down" />
									</span>
									<span
										className={classNames(
											'collapse-icon-open',
											{
												actions: hasActions,
											}
										)}
									>
										<ClayIcon symbol="angle-up" />
									</span>
								</>
							)}
						</ClayButton>

						{hasActions && (
							<span className="collapse-icon-options">
								<DropDown actions={actions} />
							</span>
						)}
					</div>

					<div
						className={classNames(
							'panel-collapse',
							collapseClassNames,
							{
								collapse: !transitioning,
								collapsing: transitioning,
								show: expanded,
							}
						)}
						onTransitionEnd={handleTransitionEnd}
						ref={panelRef}
						role="tabpanel"
					>
						{children}
					</div>
				</>
			)}
		</div>
	);
};

CollapsablePanel.Body = ClayPanel.Body;
CollapsablePanel.Footer = ClayPanel.Footer;
CollapsablePanel.Group = ClayPanel.Group;
CollapsablePanel.Header = ClayPanel.Header;

export default CollapsablePanel;
