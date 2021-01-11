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

import {render} from '@testing-library/react';
import React from 'react';

import {FormsRuleList} from '../../../../../src/main/resources/META-INF/resources/data_layout_builder/js/components/rule-builder/FormsRuleList.es';

const DEFAULT_PROPS = {
	fields: [
		{
			fieldName: 'Text27344750',
			label: 'Text27344750',
		},
		{
			fieldName: 'Text27344751',
			label: 'Text27344751',
		},
	],
	keywords: '',
	onDelete: () => {},
	onEdit: () => {},
	rules: [
		{
			actions: [
				{
					action: 'enable',
					label: 'Text27344750',
					target: 'Text27344750',
				},
			],
			conditions: [],
			logicalOperator: 'OR',
			name: {
				en_US: 'teste rulers',
			},
		},
	],
};

describe('RuleList', () => {
	describe('basic component render', () => {
		it('mounts component on empty state without data', () => {
			const {container} = render(<FormsRuleList />);
			expect(
				container.querySelector('.sheet-text > .text-default').innerHTML
			).toBe(
				'there-are-no-rules-yet-click-on-plus-icon-below-to-add-the-first'
			);
		});

		it('mounts with operands type field', () => {
			const primaryOperand = {
				label: 'Text',
				repeatable: false,
				source: 0,
				type: 'field',
				value: 'Text27344750',
			};
			const secondaryOperand = {
				type: 'string',
				value: '123',
			};
			const {baseElement} = render(
				<FormsRuleList
					{...DEFAULT_PROPS}
					rules={[
						{
							...DEFAULT_PROPS.rules[0],
							conditions: [
								{
									operands: [
										{
											...primaryOperand,
										},
										{
											...secondaryOperand,
										},
									],
									operator: 'equals-to',
								},
							],
						},
					]}
				/>
			);

			expect(baseElement.textContent).toMatch(
				`if ${primaryOperand.type} ${primaryOperand.value} is-equal-to value ${secondaryOperand.value}`.replace(
					/ /g,
					''
				)
			);
		});

		it('mounts with two operands type field', () => {
			const primaryOperand = {
				label: 'Text',
				repeatable: false,
				source: 0,
				type: 'field',
				value: 'Text27344750',
			};

			const secondaryOperand = {
				label: 'Text',
				repeatable: false,
				source: 0,
				type: 'field',
				value: 'Text27344751',
			};

			const {baseElement} = render(
				<FormsRuleList
					{...DEFAULT_PROPS}
					rules={[
						{
							...DEFAULT_PROPS.rules[0],
							conditions: [
								{
									operands: [
										{
											...primaryOperand,
										},
										{
											...secondaryOperand,
										},
									],
									operator: 'equals-to',
								},
							],
						},
					]}
				/>
			);

			expect(baseElement.textContent).toMatch(
				`if ${primaryOperand.type} ${primaryOperand.value} is-equal-to ${secondaryOperand.type} ${secondaryOperand.value}`.replace(
					/ /g,
					''
				)
			);
		});

		it('mounts with operands type user', () => {
			const primaryOperand = {
				label: 'user',
				repeatable: false,
				type: 'user',
				value: 'user',
			};

			const secondaryOperand = {
				label: 'Guest',
				type: 'list',
				value: 'Guest',
			};

			const {baseElement} = render(
				<FormsRuleList
					{...DEFAULT_PROPS}
					rules={[
						{
							...DEFAULT_PROPS.rules[0],
							conditions: [
								{
									operands: [
										{
											...primaryOperand,
										},
										{
											...secondaryOperand,
										},
									],
									operator: 'belongs-to',
								},
							],
						},
					]}
				/>
			);

			expect(baseElement.textContent).toMatch(
				`if ${primaryOperand.type} ${primaryOperand.value} belongs-to list ${secondaryOperand.value}`.replace(
					/ /g,
					''
				)
			);
		});

		it('mounts with operands type list', () => {
			const primaryOperand = {
				label: 'Text',
				left: {
					field: {
						options: [
							{
								label: 'text',
								value: '1',
							},
						],
					},
				},
				repeatable: false,
				source: 0,
				type: 'list',
				value: '1',
			};

			const secondaryOperand = {
				type: 'list',
				value: '1',
			};

			const {baseElement} = render(
				<FormsRuleList
					{...DEFAULT_PROPS}
					rules={[
						{
							...DEFAULT_PROPS.rules[0],
							conditions: [
								{
									operands: [
										{
											...primaryOperand,
										},
										{
											...secondaryOperand,
										},
									],
									operator: 'belongs-to',
								},
							],
						},
					]}
				/>
			);

			expect(baseElement.textContent).toMatch(
				`if user user belongs-to ${secondaryOperand.type} ${secondaryOperand.value}`.replace(
					/ /g,
					''
				)
			);
		});

		it('mounts with operands type JSON', () => {
			const primaryOperand = {
				label: 'Text',
				left: {
					field: {
						columns: [
							{
								label: 'text',
								value: 123,
							},
						],
						rows: [
							{
								label: 'text',
								value: 'a',
							},
						],
					},
				},
				repeatable: false,
				source: 0,
				type: 'json',
				value: '{"a":123}',
			};

			const secondaryOperand = {
				type: 'json',
				value: '{}',
			};

			const {baseElement} = render(
				<FormsRuleList
					{...DEFAULT_PROPS}
					rules={[
						{
							...DEFAULT_PROPS.rules[0],
							conditions: [
								{
									operands: [
										{
											...primaryOperand,
										},
										{
											...secondaryOperand,
										},
									],
									operator: 'equals-to',
								},
							],
						},
					]}
				/>
			);

			expect(baseElement.textContent).toMatch(
				`if value ${primaryOperand.left.field.columns[0].label}:${primaryOperand.left.field.rows[0].label}`.replace(
					/ /g,
					''
				)
			);
		});

		it('mounts with operands type option', () => {
			const primaryOperand = {
				label: 'Text',
				left: {
					field: {
						options: [
							{
								label: 'text',
								value: '1',
							},
						],
					},
				},
				repeatable: false,
				source: 0,
				type: 'option',
				value: '1',
			};

			const secondaryOperand = {
				type: 'string',
				value: '1',
			};

			const {baseElement} = render(
				<FormsRuleList
					{...DEFAULT_PROPS}
					rules={[
						{
							...DEFAULT_PROPS.rules[0],
							conditions: [
								{
									operands: [
										{
											...primaryOperand,
										},
										{
											...secondaryOperand,
										},
									],
									operator: 'belongs-to',
								},
							],
						},
					]}
				/>
			);

			expect(baseElement.textContent).toMatch(
				`if value ${primaryOperand.left.field.options[0].label} belongs-to value ${secondaryOperand.value}`.replace(
					/ /g,
					''
				)
			);
		});
	});
});
