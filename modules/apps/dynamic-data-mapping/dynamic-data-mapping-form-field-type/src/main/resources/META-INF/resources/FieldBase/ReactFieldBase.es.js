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
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';
import {getRepeatedIndex} from 'dynamic-data-mapping-form-renderer';
import React, {useMemo} from 'react';

function FieldBase({
	children,
	displayErrors,
	errorMessage,
	id,
	label,
	name,
	onRemoveButton,
	onRepeatButton,
	readOnly,
	repeatable,
	required,
	showLabel = true,
	spritemap,
	tip,
	tooltip,
	valid,
	visible
}) {
	const repeatedIndex = useMemo(() => getRepeatedIndex(name), [name]);

	return (
		<ClayTooltipProvider>
			<div
				className={classNames('form-group', {
					'has-error': displayErrors && errorMessage && !valid,
					hide: !visible
				})}
				data-field-name={name}
			>
				{repeatable && (
					<div className="lfr-ddm-form-field-repeatable-toolbar">
						{repeatable && repeatedIndex > 0 && (
							<ClayButton
								className="ddm-form-field-repeatable-delete-button p-0"
								disabled={readOnly}
								onClick={onRemoveButton}
								small
								type="button"
							>
								<ClayIcon
									spritemap={spritemap}
									symbol="trash"
								/>
							</ClayButton>
						)}

						<ClayButton
							className="ddm-form-field-repeatable-add-button p-0"
							disabled={readOnly}
							onClick={onRepeatButton}
							small
							type="button"
						>
							<ClayIcon spritemap={spritemap} symbol="plus" />
						</ClayButton>
					</div>
				)}

				{((label && showLabel) ||
					required ||
					tooltip ||
					repeatable) && (
					<label
						className={classNames({
							'ddm-empty': !showLabel && !required
						})}
						htmlFor={id}
					>
						{label && showLabel && `${label} `}

						{required && spritemap && (
							<span className="reference-mark">
								<ClayIcon
									spritemap={spritemap}
									symbol="asterisk"
								/>
							</span>
						)}

						{tooltip && (
							<div className="ddm-tooltip">
								<ClayIcon
									data-tooltip-align="right"
									spritemap={spritemap}
									symbol="question-circle-full"
									title={tooltip}
								/>
							</div>
						)}
					</label>
				)}

				{children}

				{tip && <span className="form-text">{tip}</span>}

				{displayErrors && errorMessage && !valid && (
					<span className="form-feedback-group">
						<div className="form-feedback-item">{errorMessage}</div>
					</span>
				)}
			</div>
		</ClayTooltipProvider>
	);
}

/**
 * This Proxy connects to the store to send the changes directly to the store. This
 * should be replaced when we have a communication with a Store/Provider in React.
 */
const FieldBaseProxy = ({dispatch, name, ...otherProps}) => (
	<FieldBase
		{...otherProps}
		name={name}
		onRemoveButton={() => dispatch('fieldRemoved', name)}
		onRepeatButton={() => dispatch('fieldRepeated', name)}
	/>
);

export {FieldBase, FieldBaseProxy};
