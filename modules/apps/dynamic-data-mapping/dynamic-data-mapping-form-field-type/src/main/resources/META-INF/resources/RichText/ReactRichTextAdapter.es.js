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

import {ClayInput} from '@clayui/form';
import {Editor} from 'frontend-editor-ckeditor-web';
import {cancelDebounce, debounce} from 'frontend-js-web';
import React, {useRef} from 'react';

import getConnectedReactComponentAdapter from '../util/ReactComponentAdapterWithObserver.es';
import templates from './RichTextAdapter.soy';

const CKEDITOR_CONFIG = {
	toolbar: [
		{items: ['Undo', 'Redo'], name: 'clipboard'},
		'/',
		{
			items: [
				'Bold',
				'Italic',
				'Underline',
				'Strike',
				'-',
				'CopyFormatting',
				'RemoveFormat',
			],
			name: 'basicstyles',
		},
		{
			items: [
				'NumberedList',
				'BulletedList',
				'-',
				'Outdent',
				'Indent',
				'-',
				'Blockquote',
				'-',
				'JustifyLeft',
				'JustifyCenter',
				'JustifyRight',
				'JustifyBlock',
			],
			name: 'paragraph',
		},
		{items: ['Link', 'Unlink', 'Anchor'], name: 'links'},
		{
			items: ['Image', 'Table', 'HorizontalRule', 'SpecialChar'],
			name: 'insert',
		},
		'/',
		{items: ['Styles', 'Format', 'Font', 'FontSize'], name: 'styles'},
		{items: ['TextColor', 'BGColor'], name: 'colors'},
		{items: ['Maximize'], name: 'tools'},
		{
			items: ['Source'],
			name: 'document',
		},
	],
};

const useDebounceCallback = (callback, milliseconds) => {
	const callbackRef = useRef(debounce(callback, milliseconds));

	return [callbackRef.current, () => cancelDebounce(callbackRef.current)];
};

const RichText = ({data, dispatch, name, readOnly}) => {
	const editorProps = {
		config: CKEDITOR_CONFIG,
		data,
		editorName: `editor_${name}`,
	};

	const [debouncedInput] = useDebounceCallback(event => {
		dispatch({
			payload: event.editor.getData(),
			type: 'value',
		});
	}, 500);

	if (readOnly) {
		editorProps.readOnly = true;
		editorProps.style = {pointerEvents: 'none'};
	}
	else {
		editorProps.onChange = debouncedInput;
	}

	const style = {
		border: '1px solid #E7E7ED',
		height: '148px',
	};

	return (
		<>
			{readOnly && data && (
				<div
					dangerouslySetInnerHTML={{
						__html: data,
					}}
					name={`html_text_${name}`}
					style={style}
				></div>
			)}

			{readOnly && !data && (
				<div
					name={`html_text_${name}`}
					style={{...style, color: '#A7A9BC', padding: '16px'}}
				>
					{Liferay.Language.get('click-to-add-a-rich-text')}
				</div>
			)}

			{!readOnly && <Editor {...editorProps} />}

			<ClayInput name={name} type="hidden" value={data} />
		</>
	);
};

const ReactRichTextAdapter = getConnectedReactComponentAdapter(
	RichText,
	templates
);

export {ReactRichTextAdapter};
export default ReactRichTextAdapter;
