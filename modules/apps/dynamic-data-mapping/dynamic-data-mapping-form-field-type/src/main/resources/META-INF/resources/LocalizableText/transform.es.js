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

export const getEditingValue = ({defaultLocale, editingLocale, value}) => {
	const valueJSON = convertValueToJSON(value);

	if (valueJSON) {
		return (
			valueJSON[editingLocale.localeId] ||
			valueJSON[defaultLocale.localeId] ||
			''
		);
	}

	return editingLocale;
}

export const getInitialInternalValue = ({editingLocale, value}) => {
	const valueJSON = convertValueToJSON(value);

	return valueJSON[editingLocale.localeId] || '';
}

const convertValueToString = value => {
	if (value && typeof value === 'object') {
		return JSON.stringify(value);
	}

	return value;
};

export const convertValueToJSON = value => {
	if (value && typeof value === 'string') {
		try {
			return JSON.parse(value);
		}
		catch (e) {
			console.warn('Unable to parse JSON', value);
		}
	}

	return value;
};

const isTranslated = ({localeId, value}) => {
	const valueJSON = convertValueToJSON(value);

	if (valueJSON) {
		return !!valueJSON[localeId];
	}

	return false;
};

const isDefaultLocale = ({defaultLocale, localeId}) => {
	return defaultLocale.localeId === localeId;
};

export const normalizeLocaleId = localeId => {
	if (!localeId || localeId === '') {
		throw new Error(`localeId ${localeId} is invalid`);
	}

	return localeId.replace('_', '-').toLowerCase();
};

export const transformAvailableLocalesAndValue = ({
	availableLocales,
	defaultLocale,
	value,
}) => ({
	availableLocales: availableLocales.map(availableLocale => ({
		...availableLocale,
		icon: normalizeLocaleId(availableLocale.localeId),
		isDefault: isDefaultLocale({
			defaultLocale,
			localeId: availableLocale.localeId,
		}),
		isTranslated: isTranslated({
			localeId: availableLocale.localeId,
			value,
		}),
	})),
	value: convertValueToString(value),
});
