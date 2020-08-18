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

import {PagesVisitor} from 'dynamic-data-mapping-form-renderer';
import React, {useCallback, useEffect, useState} from 'react';

export function useDDMFormSubmit(ddmForm, onSubmit) {
	useEffect(() => {
		const formNode = ddmForm.reactComponentRef.current.getFormNode();

		formNode.addEventListener('submit', onSubmit);

		return () => formNode.removeEventListener('submit', onSubmit);
	}, [ddmForm, onSubmit]);
}

export function useDDMFormValidation(
	ddmForm,
	onSubmitCallback,
	languageId,
	availableLanguageIds
) {
	return useCallback(
		(event) => {
			if (typeof event.stopImmediatePropagation === 'function') {
				event.stopImmediatePropagation();
			}

			const ddmReactForm = ddmForm.reactComponentRef.current;

			ddmReactForm.validate().then((isValidForm) => {
				if (!isValidForm) {
					return;
				}

				const dataRecordValues = {};

				const visitor = new PagesVisitor(ddmReactForm.get('pages'));

				const setDataRecord = ({
					fieldName,
					repeatable,
					type,
					value,
					visible,
				}) => {
					if (type === 'fieldset') {
						return;
					}

					let _value = value;

					if (!visible) {
						_value = '';
					}

					if (!dataRecordValues[fieldName]) {
						dataRecordValues[fieldName] = {
							[languageId]: [],
						};
					}

					if (repeatable) {
						dataRecordValues[fieldName][languageId].push(_value);
					}
					else {
						dataRecordValues[fieldName] = {
							[languageId]: _value,
						};
					}

					availableLanguageIds.forEach((key) => {
						dataRecordValues[fieldName][key] =
							dataRecordValues[fieldName][languageId];
					});
				};

				visitor.mapFields(
					(field) => {
						setDataRecord(field);
					},
					true,
					true
				);

				onSubmitCallback({dataRecordValues});
			});
		},
		[
			availableLanguageIds,
			ddmForm.reactComponentRef,
			languageId,
			onSubmitCallback,
		]
	);
}

export default function withDDMForm(Component) {
	return ({containerElementId, ...props}) => {
		const [ddmForm, setDDMForm] = useState();

		if (!ddmForm) {
			Liferay.componentReady(containerElementId).then(setDDMForm);
		}

		return ddmForm ? <Component ddmForm={ddmForm} {...props} /> : null;
	};
}
