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

import '../FieldBase/FieldBase.es';

import './GeolocationRegister.soy.js';

import dom from 'metal-dom';
import L from 'leaflet';
import Component from 'metal-component';
import MapOpenStreetMap from 'map-openstreetmap/js/MapOpenStreetMap.es';
import Soy from 'metal-soy';
import { Config } from 'metal-state';

import { setJSONArrayValue } from '../util/setters.es';
import templates from './Geolocation.soy.js';

import 'leaflet/dist/leaflet.css';


/**
 * Geolocation.
 * @extends Component
 */

class Geolocation extends Component {
	attached() {
		const { readOnly } = this;

		this.setState({
			geolocateTitle: Liferay.Language.get('geolocate'),
			pathThemeImages: Liferay.ThemeDisplay.getPathThemeImages()
		});

		if (!readOnly) {
			setTimeout(() => {
				window['L'] = L;
				const mapcomp = new MapOpenStreetMap({
					boundingBox: '#targetGeo1', 
					controls: ['home', 'pan', 'search', 'type', 'zoom'], 
					geolocation: true,
					position: { location: { lat: 0, lng: 0 } }
				});
				// console.log(mapcomp);
		}, 2000);
	}
}


prepareStateForRender(state) {
	// console.log('prepareStateForRender--> 1', {readOnly: state.readOnly});

	const { predefinedValue } = state;
	const predefinedValueArray = this._getArrayValue(predefinedValue);

	return {
		...state,
		predefinedValue: predefinedValueArray[0] || '',
		...{
			geolocateTitle: Liferay.Language.get('geolocate'),
			pathThemeImages: Liferay.ThemeDisplay.getPathThemeImages()
		}
	};
}

_getArrayValue(value) {
	let newValue = value || '';

	if (!Array.isArray(newValue)) {
		newValue = [newValue];
	}

	return newValue;
}

_handleFieldBlurred() {
	// this.emit('fieldBlurred', {
	// 	fieldInstance: this,
	// 	originalEvent: window.event,
	// 	value: window.event.target.value
	// });
}

_handleFieldFocused(event) {
	// this.emit('fieldFocused', {
	// 	fieldInstance: this,
	// 	originalEvent: event
	// });
}

_handleValueChanged(event) {
	// this.emit('fieldEdited', {
	// 	fieldInstance: this,
	// 	originalEvent: event,
	// 	value: event.target.value
	// });
}
}

Geolocation.STATE = {
	/**
	 * @default 'string'
	 * @instance
	 * @memberof Text
	 * @type {?(string|undefined)}
	 */

	dataType: Config.string().value('string'),

	/**
	 * @default undefined
	 * @instance
	 * @memberof FieldBase
	 * @type {?bool}
	 */

	evaluable: Config.bool().value(false),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string|undefined)}
	 */

	fieldName: Config.string(),

	/**
	 * @default ''
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string)}
	 */

	geolocateTitle: Config.string().value(''),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string|undefined)}
	 */

	inline: Config.bool().value(false),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string|undefined)}
	 */

	label: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string|undefined)}
	 */

	options: Config.arrayOf(
		Config.shapeOf({
			label: Config.string(),
			name: Config.string(),
			value: Config.string()
		})
	).value([
		{
			label: 'Option 1'
		},
		{
			label: 'Option 2'
		}
	]),

	/**
	 * @default ''
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string)}
	 */

	pathThemeImages: Config.string().value(''),

	/**
	 * @default Choose an Option
	 * @instance
	 * @memberof Geolocation
	 * @type {?string}
	 */

	predefinedValue: Config.oneOfType([
		Config.array(),
		Config.object(),
		Config.string()
	])
		.setter(setJSONArrayValue)
		.value([]),

	/**
	 * @default false
	 * @instance
	 * @memberof Geolocation
	 * @type {?bool}
	 */

	readOnly: Config.bool().value(false),

	/**
	 * @default undefined
	 * @instance
	 * @memberof FieldBase
	 * @type {?(bool|undefined)}
	 */

	repeatable: Config.bool(),

	/**
	 * @default false
	 * @instance
	 * @memberof Geolocation
	 * @type {?bool}
	 */

	required: Config.bool().value(false),

	/**
	 * @default true
	 * @instance
	 * @memberof Geolocation
	 * @type {?bool}
	 */

	showLabel: Config.bool().value(true),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string|undefined)}
	 */

	spritemap: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string|undefined)}
	 */

	tip: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Text
	 * @type {?(string|undefined)}
	 */

	type: Config.string().value('geolocation'),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Geolocation
	 * @type {?(string|undefined)}
	 */

	value: Config.string()
};

Soy.register(Geolocation, templates);

export default Geolocation;
