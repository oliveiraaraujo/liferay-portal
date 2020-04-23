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

import {fireEvent} from '@testing-library/react';

import Options from '../../../src/main/resources/META-INF/resources/Options/Options.es';
import withContextMock from '../__mocks__/withContextMock.es';

let component;
const spritemap = 'icons.svg';

const OptionsWithContextMock = withContextMock(Options);

const optionsValue = {
	[themeDisplay.getLanguageId()]: [
		{
			label: 'Option 1',
			value: 'Option1',
		},
		{
			label: 'Option 2',
			value: 'Option2',
		},
	],
};

describe('Options', () => {
	beforeEach(() => jest.useFakeTimers());

	afterEach(() => {
		if (component) {
			component.dispose();
		}
	});

	it('shows the options', () => {
		component = new OptionsWithContextMock({
			name: 'options',
			spritemap,
			value: optionsValue,
		});

		expect(component).toMatchSnapshot();
	});

	it('shows an empty option when value is an array of size 1', () => {
		component = new OptionsWithContextMock({
			name: 'options',
			spritemap,
			value: {
				[themeDisplay.getLanguageId()]: [
					{
						label: 'Option',
						value: 'Option',
					},
				],
			},
		});

		jest.runAllTimers();

		const {element} = component;
		const labelInputs = element.querySelectorAll('.ddm-field-text');

		expect(labelInputs.length).toEqual(2);
		expect(labelInputs[0].value).toEqual('Option');
		expect(labelInputs[1].value).toEqual('');

		const valueInputs = element.querySelectorAll('.key-value-input');

		expect(valueInputs.length).toEqual(2);
		expect(valueInputs[0].value).toEqual('Option');
		expect(valueInputs[1].value).toEqual('');
	});

	it('does not show an empty option when translating', () => {
		component = new OptionsWithContextMock({
			defaultLanguageId: themeDisplay.getLanguageId(),
			editingLanguageId: 'pt_BR',
			name: 'options',
			spritemap,
			value: {
				[themeDisplay.getLanguageId()]: [
					{
						label: 'Option',
						value: 'Option',
					},
				],
				pt_BR: [
					{
						label: 'Option',
						value: 'Option',
					},
				],
			},
		});

		jest.runAllTimers();

		const {element} = component;
		const labelInputs = element.querySelectorAll('.ddm-field-text');

		expect(labelInputs.length).toEqual(1);
	});

	it('edits the value of an option based on the label', () => {
		component = new OptionsWithContextMock({
			name: 'options',
			spritemap,
			value: {
				[themeDisplay.getLanguageId()]: [
					{
						label: 'Option',
						value: 'Option',
					},
				],
			},
		});

		jest.runAllTimers();

		const {element} = component;
		const labelInputs = element.querySelectorAll('.ddm-field-text');

		fireEvent.change(labelInputs[0], {
			target: {
				value: 'Hello',
			},
		});

		jest.runAllTimers();

		const valueInputs = element.querySelectorAll('.key-value-input');

		expect(valueInputs[0].value).toEqual('Hello');
	});

	it('inserts a new empty option when editing the last option', () => {
		component = new OptionsWithContextMock({
			name: 'options',
			spritemap,
			value: {
				[themeDisplay.getLanguageId()]: [
					{
						label: 'Option',
						value: 'Option',
					},
				],
			},
		});

		jest.runAllTimers();

		const {element} = component;
		const labelInputs = element.querySelectorAll('.ddm-field-text');

		fireEvent.change(labelInputs[1], {
			target: {
				value: 'Hello',
			},
		});

		jest.runAllTimers();

		const valueInputs = element.querySelectorAll('.key-value-input');

		expect(valueInputs.length).toEqual(labelInputs.length + 1);
	});

	it('does not insert a new empty option automatically if translating', () => {
		component = new OptionsWithContextMock({
			defaultLanguageId: themeDisplay.getLanguageId(),
			editingLanguageId: 'pt_BR',
			name: 'options',
			spritemap,
			value: {
				[themeDisplay.getLanguageId()]: [
					{
						label: 'Option',
						value: 'Option',
					},
				],
				pt_BR: [
					{
						label: 'Option',
						value: 'Option',
					},
				],
			},
		});

		jest.runAllTimers();

		const {element} = component;
		const labelInputs = element.querySelectorAll('.ddm-field-text');

		fireEvent.input(labelInputs[0], {target: {value: 'Hello'}});

		jest.runAllTimers();

		const valueInputs = element.querySelectorAll('.key-value-input');

		expect(valueInputs.length).toEqual(labelInputs.length);
	});

	it('deduplication of value when adding a new option', () => {
		component = new OptionsWithContextMock({
			name: 'options',
			spritemap,
			value: {
				[themeDisplay.getLanguageId()]: [
					{
						label: 'Foo',
						value: 'Foo',
					},
				],
			},
		});

		jest.runAllTimers();

		const {element} = component;
		const labelInputs = element.querySelectorAll('.ddm-field-text');

		fireEvent.input(labelInputs[1], {target: {value: 'Foo'}});

		const valueInputs = element.querySelectorAll('.key-value-input');

		expect(valueInputs[1].value).toEqual('Foo1');
	});

	it('deduplication of the value when editing the value', () => {
		component = new OptionsWithContextMock({
			name: 'options',
			spritemap,
			value: {
				[themeDisplay.getLanguageId()]: [
					{
						label: 'Bar',
						value: 'Bar',
					},
					{
						label: 'Foo',
						value: 'Foo',
					},
				],
			},
		});

		jest.runAllTimers();

		const {element} = component;
		const labelInputs = element.querySelectorAll('.ddm-field-text');

		fireEvent.input(labelInputs[1], {target: {value: 'Bar'}});

		const valueInputs = element.querySelectorAll('.key-value-input');

		expect(valueInputs[1].value).toEqual('Bar1');
	});

	it('adds a value to the value property when the label is empty', () => {
		component = new OptionsWithContextMock({
			name: 'options',
			spritemap,
			value: {
				[themeDisplay.getLanguageId()]: [
					{
						label: 'Bar',
						value: 'Bar',
					},
				],
			},
		});

		jest.runAllTimers();

		const {element} = component;
		const labelInput = element.querySelector('.ddm-field-text');

		fireEvent.input(labelInput, {target: {value: ''}});

		const valueInput = element.querySelector('.key-value-input');

		expect(valueInput.value).toBe('option');
	});
});
