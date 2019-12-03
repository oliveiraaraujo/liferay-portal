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

import ClayColorPicker from '@clayui/color-picker';
import React, {useState} from 'react';

/**
 * Renders BackgroundColor Configuration Panel Configuration Panel.
 * *WARNING:* This component is unfinished since this is just used
 * to test FloatingToolbar ConfigurationPanel mechanism.
 */
export const BackgroundColorConfigurationPanel = () => {
	const [customColors, setCustoms] = useState([
		'0b5fff',
		'287d3d',
		'da1414',
		'b95000',
		'2e5aac',
		'272833',
		'393a4a',
		'6b6c7e',
		'f1f2f5',
		'f7f8f9',
		'ffffff'
	]);

	const [color, setColor] = useState(customColors[0]);

	return (
		<div className="form-group">
			<label htmlFor="floatingToolbarBackgroundColorPanelPalette">
				{Liferay.Language.get('background-color')}
			</label>
			<ClayColorPicker
				label={Liferay.Language.get('background-color')}
				name="colorPicker"
				onColorsChange={setCustoms}
				onValueChange={setColor}
				showHex
				value={color}
			/>
		</div>
	);
};
