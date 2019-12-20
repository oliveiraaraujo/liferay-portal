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

import L from 'leaflet';
import MapGoogleMaps from 'map-google-maps/js/MapGoogleMaps.es.js';
import MapOpenStreetMap from 'map-openstreetmap/js/MapOpenStreetMap.es';
import Component from 'metal-component';
import Soy from 'metal-soy';
import {Config} from 'metal-state';

import {setJSONArrayValue} from '../util/setters.es';
import templates from './Geolocation.soy.js';

import 'leaflet/dist/leaflet.css';

/**
 * Geolocation.
 * @extends Component
 */

const GEOLOCATE_CONFIG = {
	geolocateTitle: Liferay.Language.get('geolocate'),
	pathThemeImages: Liferay.ThemeDisplay.getPathThemeImages()
};

const LEAFLET_CDN_IMAGES = 'https://npmcdn.com/leaflet@1.2.0/dist/images/';

const MAP_PROVIDER = {
	googleMaps: 'GoogleMaps',
	openStreetMap: 'OpenStreetMap'
};

const {CONTROLS} = Liferay.MapBase;

const MAP_CONFIG = {
	boundingBox: '#targetGeo1',
	controls: [CONTROLS.HOME, CONTROLS.PAN, CONTROLS.SEACH, CONTROLS.TYPE, CONTROLS.ZOOM],
	geolocation: true,
	position: {location: {lat: 0, lng: 0}},
};

class Geolocation extends Component {
	
	constructor(...args) {
		super(...args);

		this._mapComponent = null;
	}

	attached() {
		const {readOnly} = this;

		this.setState(GEOLOCATE_CONFIG);

		if (!readOnly) {
			switch (this.mapProvider) {
				case MAP_PROVIDER.openStreetMap:
					this._createMapOpenStreetMaps(MAP_CONFIG);
					break;
				
				case MAP_PROVIDER.googleMaps:
					if (Liferay.Maps.gmapsReady) {
						this._createGoogleMaps(MAP_CONFIG);
					} else {
						Liferay.once('gmapsReady', this._createGoogleMaps(MAP_CONFIG));
					}	
					break;
			
				default:
					throw new Error('mapProvider is required!');
			}
		}
	}

	_createMapOpenStreetMaps(mapConfig) {
		window['L'] = L; //Isso é realmente necessário?

		window['L'].Icon.Default.imagePath = LEAFLET_CDN_IMAGES;

		this._mapComponent = new MapOpenStreetMap(
			mapConfig
		);

		Liferay.MapBase.register(
			this.name,
			this._mapComponent,
			'#targetGeo1'
		);
	}

	_createGoogleMaps(mapConfig) {
		this._mapComponent = new MapGoogleMaps(mapConfig);
	
		Liferay.MapBase.register(
			this.name,
			this._mapComponent,
			'#targetGeo1'
		);
	};

	prepareStateForRender(state) {
		const {predefinedValue} = state;
		const predefinedValueArray = this._getArrayValue(predefinedValue);

		return {
			...state,
			predefinedValue: predefinedValueArray[0] || '',
			...GEOLOCATE_CONFIG
		};
	}

	disposed() {
		if (this._mapComponent) {
			this._mapComponent.dispose();
		}
	}

	_getArrayValue(value) {
		let newValue = value || '';

		if (!Array.isArray(newValue)) {
			newValue = [newValue];
		}

		return newValue;
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
	 * @default 'mapProvider'
	 * TODO - Falta esse JSDOC aaa
	 */
	mapProvider: Config.string().value('OpenStreetMap'),

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
