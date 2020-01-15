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
import ClayForm, {ClayInput} from '@clayui/form';
import {ItemSelectorDialog} from 'frontend-js-web';
import React, {useState, useEffect} from 'react';

import getConnectedReactComponentAdapter from '../util/ReactComponentAdapter.es';
import templates from './ImagePickerAdapter.soy.js';

const ReactImagePicker = ({
	dispatch,
	inputValue = '',
	itemSelectorURL,
	name,
	portletNamespace,
	readOnly
}) => {
	const [imageDescription, setImageDescription] = useState('');
	const [imageTitle, setImageTitle] = useState('');
	const [imageURL, setImageURL] = useState('');

	useEffect(() => {
		const valueJSON = JSON.parse(inputValue || '{}');

		setImageDescription(valueJSON.description || '');
		setImageTitle(valueJSON.title || '');
		setImageURL(valueJSON.url || '');
	}, [inputValue]);

	const _dispatchValue = (value, clear) => {
		const newValue = {
			...JSON.parse(value || inputValue),
			description: imageDescription
		};

		dispatch({
			payload: clear ? '' : JSON.stringify(newValue),
			type: 'value'
		});
	};

	const _handleClearClick = () => {
		setImageDescription('');
		setImageTitle('');
		setImageURL('');

		_dispatchValue(null, true);
	};

	const _handleDescriptionChange = event => {
		const description = event.target.value;

		setImageDescription(description);

		_dispatchValue();
	};

	const _handleFieldChanged = event => {
		var selectedItem = event.selectedItem;

		if (selectedItem) {
			_dispatchValue(selectedItem.value);
		}
	};

	const _handleItemSelectorTriggerClick = event => {
		event.preventDefault();

		const itemSelectorDialog = new ItemSelectorDialog({
			eventName: `${portletNamespace}selectDocumentLibrary`,
			singleSelect: true,
			url: itemSelectorURL
		});

		itemSelectorDialog.on('selectedItemChange', _handleFieldChanged);

		itemSelectorDialog.open();
	};

	return (
		<>
			<ClayForm.Group>
				<ClayInput.Group>
					<ClayInput.GroupItem className="d-none d-sm-block" prepend>
						<input name={name} type="hidden" value={inputValue} />

						<ClayInput
							className="bg-light"
							disabled={readOnly}
							onClick={_handleItemSelectorTriggerClick}
							readOnly
							type="text"
							value={imageTitle}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem append shrink>
						<ClayButton
							disabled={readOnly}
							displayType="secondary"
							onClick={_handleItemSelectorTriggerClick}
							type="button"
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayInput.GroupItem>

					{imageURL && (
						<ClayInput.GroupItem shrink>
							<ClayButton
								disabled={readOnly}
								displayType="secondary"
								onClick={_handleClearClick}
								type="button"
							>
								{Liferay.Language.get('clear')}
							</ClayButton>
						</ClayInput.GroupItem>
					)}
				</ClayInput.Group>
			</ClayForm.Group>

			{imageURL && (
				<>
					<img
						alt=""
						className="img-fluid mb-2 rounded"
						src={imageURL}
					/>

					<ClayForm.Group>
						<ClayInput
							disabled={readOnly}
							name={`${name}-description`}
							onChange={_handleDescriptionChange}
							placeholder={Liferay.Language.get(
								'add-image-description'
							)}
							type="text"
							value={imageDescription}
						/>
					</ClayForm.Group>
				</>
			)}
		</>
	);
};

const ReactImagePickerAdapter = getConnectedReactComponentAdapter(
	ReactImagePicker,
	templates
);

export {ReactImagePickerAdapter};
export default ReactImagePickerAdapter;
