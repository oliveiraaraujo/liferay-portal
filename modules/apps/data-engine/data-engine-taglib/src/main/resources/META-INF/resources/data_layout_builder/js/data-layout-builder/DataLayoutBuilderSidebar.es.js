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

import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {PagesVisitor} from 'dynamic-data-mapping-form-renderer';
import React, {
	useContext,
	useEffect,
	useLayoutEffect,
	useRef,
	useState,
} from 'react';

import AppContext from '../AppContext.es';
import {
	EDIT_CUSTOM_OBJECT_FIELD,
	EVALUATION_ERROR,
	dropLayoutBuilderField,
} from '../actions.es';
import Button from '../components/button/Button.es';
import FieldSets from '../components/field-sets/FieldSets.es';
import FieldTypeList from '../components/field-types/FieldTypeList.es';
import RuleList from '../components/rules/RuleList.es';
import Sidebar from '../components/sidebar/Sidebar.es';
import isClickOutside from '../utils/clickOutside.es';
import renderSettingsForm, {
	getEvents,
	getFilteredSettingsContext,
} from '../utils/renderSettingsForm.es';
import DataLayoutBuilderContext from './DataLayoutBuilderContext.es';

const DefaultSidebarBody = ({keywords}) => {
	const [dataLayoutBuilder] = useContext(DataLayoutBuilderContext);

	const [
		{
			config: {allowFieldSets, allowRules},
		},
	] = useContext(AppContext);

	const onDoubleClick = ({name}) => {
		const {activePage, pages} = dataLayoutBuilder.getStore();

		dataLayoutBuilder.dispatch(
			'fieldAdded',
			dropLayoutBuilderField({
				addedToPlaceholder: true,
				dataLayoutBuilder,
				fieldTypeName: name,
				indexes: {
					columnIndex: 0,
					pageIndex: activePage,
					rowIndex: pages[activePage].rows.length,
				},
			})
		);
	};

	const fieldTypes = dataLayoutBuilder
		.getFieldTypes()
		.filter(({group}) => group === 'basic');

	fieldTypes.sort(({displayOrder: a}, {displayOrder: b}) => a - b);

	const tabs = [
		{
			label: Liferay.Language.get('fields'),
			render: () => (
				<FieldTypeList
					fieldTypes={fieldTypes}
					keywords={keywords}
					onDoubleClick={onDoubleClick}
				/>
			),
		},
	];

	if (allowFieldSets) {
		tabs.push({
			label: Liferay.Language.get('fieldsets'),
			render: () => <FieldSets />,
		});
	}

	if (allowRules) {
		tabs.push({
			label: Liferay.Language.get('rules'),
			render: () => <RuleList />,
		});
	}

	return <Sidebar.Tabs tabs={tabs} />;
};

const SettingsSidebarBody = () => {
	const [dataLayoutBuilder] = useContext(DataLayoutBuilderContext);
	const [state, dispatch] = useContext(AppContext);
	const {focusedCustomObjectField, focusedField} = state;
	const {
		settingsContext: customObjectFieldSettingsContext,
	} = focusedCustomObjectField;
	const {settingsContext: fieldSettingsContext} = focusedField;
	const formRef = useRef();
	const [form, setForm] = useState(null);
	const hasFocusedCustomObjectField = !!customObjectFieldSettingsContext;
	const settingsContext = hasFocusedCustomObjectField
		? customObjectFieldSettingsContext
		: fieldSettingsContext;

	useEffect(() => {
		const filteredSettingsContext = getFilteredSettingsContext({
			config: state.config,
			settingsContext,
		});

		const dispatchEvent = (type, payload) => {
			if (hasFocusedCustomObjectField && type === 'fieldEdited') {
				dispatch({payload, type: EDIT_CUSTOM_OBJECT_FIELD});
			}
			else if (!hasFocusedCustomObjectField) {
				dataLayoutBuilder.dispatch(type, payload);
			}
		};

		if (form === null || form.isDisposed()) {
			setForm(
				renderSettingsForm(
					getEvents(dispatchEvent, filteredSettingsContext),
					filteredSettingsContext,
					formRef.current
				)
			);
		}
		else {
			const {pages, rules} = filteredSettingsContext;
			let newState = {pages, rules};

			if (form.activePage > pages.length - 1) {
				newState = {
					...newState,
					activePage: 0,
				};
			}

			newState = {
				...newState,
				events: getEvents(dispatchEvent, filteredSettingsContext),
			};

			form.setState(newState, () => {
				let evaluableForm = false;
				const visitor = new PagesVisitor(pages);

				visitor.mapFields(({evaluable}) => {
					if (evaluable) {
						evaluableForm = true;
					}
				});

				if (evaluableForm) {
					form.evaluate()
						.then(pages => {
							if (form.isDisposed()) {
								return;
							}

							form.setState({pages});
						})
						.catch(error => dispatch(EVALUATION_ERROR, error));
				}
			});
		}
	}, [
		dataLayoutBuilder,
		dispatch,
		focusedField,
		form,
		formRef,
		hasFocusedCustomObjectField,
		settingsContext,
		state.config,
	]);

	useEffect(() => {
		return () => form && form.dispose();
	}, [form]);

	const focusedFieldName = focusedField.name;

	useLayoutEffect(() => {
		if (!form) {
			return;
		}

		form.once('rendered', () => {
			const firstInput = form.element.querySelector('input');

			if (firstInput && !form.element.contains(document.activeElement)) {
				firstInput.focus();

				if (firstInput.select) {
					firstInput.select();
				}
			}
		});
	}, [focusedFieldName, form]);

	return (
		<form onSubmit={event => event.preventDefault()} ref={formRef}></form>
	);
};

const SettingsSidebarHeader = () => {
	const [{fieldTypes, focusedCustomObjectField, focusedField}] = useContext(
		AppContext
	);
	let {settingsContext} = focusedField;

	if (focusedCustomObjectField.settingsContext) {
		settingsContext = focusedCustomObjectField.settingsContext;
	}

	const visitor = new PagesVisitor(settingsContext.pages);
	const typeField = visitor.findField(field => field.fieldName === 'type');

	const fieldType = fieldTypes.find(({name}) => {
		return name === typeField.value;
	});

	const [dataLayoutBuilder] = useContext(DataLayoutBuilderContext);
	const handleFocusedFieldBlur = () => {
		dataLayoutBuilder.dispatch('sidebarFieldBlurred');
	};

	if (!fieldType) {
		return null;
	}

	return (
		<Sidebar.Header className="d-flex">
			<Button
				className="mr-2"
				displayType="secondary"
				monospaced={false}
				onClick={handleFocusedFieldBlur}
				symbol="angle-left"
			/>

			<ClayDropDown
				className="d-inline-flex flex-grow-1"
				onActiveChange={() => {}}
				trigger={
					<Button
						className="d-inline-flex flex-grow-1"
						disabled={true}
						displayType="secondary"
					>
						<ClayIcon
							className="mr-2 mt-1"
							symbol={fieldType.icon}
						/>

						{fieldType.label}

						<span className="d-inline-flex ml-auto navbar-breakpoint-down-d-none pt-2">
							<ClayIcon
								className="inline-item inline-item-after"
								symbol="caret-bottom"
							/>
						</span>
					</Button>
				}
			></ClayDropDown>
		</Sidebar.Header>
	);
};

export default () => {
	const [dataLayoutBuilder] = useContext(DataLayoutBuilderContext);
	const [{focusedCustomObjectField, focusedField}] = useContext(AppContext);
	const [keywords, setKeywords] = useState('');

	const sidebarRef = useRef();

	useEffect(() => {
		const eventHandler = ({target}) => {
			if (
				isClickOutside(
					target,
					sidebarRef.current,
					'.data-layout-builder-sidebar',
					'.ddm-field-actions-container',
					'.ddm-form-builder',
					'.dropdown-menu'
				)
			) {
				dataLayoutBuilder.dispatch('sidebarFieldBlurred');
			}
		};

		window.addEventListener('click', eventHandler, true);

		return () => window.removeEventListener('click', eventHandler);
	}, [dataLayoutBuilder, sidebarRef]);

	const hasFocusedField = Object.keys(focusedField).length > 0;
	const hasFocusedCustomObjectField =
		Object.keys(focusedCustomObjectField).length > 0;
	const displaySettings = hasFocusedCustomObjectField || hasFocusedField;

	return (
		<Sidebar
			onSearch={displaySettings ? false : setKeywords}
			ref={sidebarRef}
		>
			<>
				{displaySettings && <SettingsSidebarHeader />}

				<Sidebar.Body>
					{displaySettings ? (
						<SettingsSidebarBody />
					) : (
						<DefaultSidebarBody keywords={keywords} />
					)}
				</Sidebar.Body>
			</>
		</Sidebar>
	);
};
