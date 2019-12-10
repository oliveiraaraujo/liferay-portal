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
import {PagesVisitor} from 'dynamic-data-mapping-form-renderer/js/util/visitors.es';
import React, {
	useEffect,
	useRef,
	useState,
	useContext,
	useLayoutEffect
} from 'react';

import DataLayoutBuilderContext from './DataLayoutBuilderContext.es';
import DataLayoutBuilderViewContext from './DataLayoutBuilderViewContext.es';
import {dropLayoutBuilderField, EVALUATION_ERROR} from './actions.es';
import Button from './components/button/Button.es';
import FieldTypeList from './components/field-types/FieldTypeList.es';
import Sidebar from './components/sidebar/Sidebar.es';
import {useSidebarContent} from './hooks/index.es';
import renderSettingsForm, {
	getFilteredSettingsContext
} from './renderSettingsForm.es';
import isClickOutside from './utils/clickOutside.es';

const DefaultSidebarBody = ({keywords}) => {
	const [dataLayoutBuilder] = useContext(DataLayoutBuilderContext);

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
					rowIndex: pages[activePage].rows.length
				}
			})
		);
	};

	const fieldTypes = dataLayoutBuilder
		.getFieldTypes()
		.filter(({group}) => group === 'basic');

	fieldTypes.sort(({displayOrder: a}, {displayOrder: b}) => a - b);

	return (
		<>
			<Sidebar.Tab tabs={[Liferay.Language.get('fields')]} />

			<Sidebar.TabContent>
				<FieldTypeList
					fieldTypes={fieldTypes}
					keywords={keywords}
					onDoubleClick={onDoubleClick}
				/>
			</Sidebar.TabContent>
		</>
	);
};

const SettingsSidebarBody = () => {
	const [dataLayoutBuilder] = useContext(DataLayoutBuilderContext);
	const [state, dispatch] = useContext(DataLayoutBuilderViewContext);
	const {focusedField} = state;
	const {settingsContext: fieldSettingsContext} = focusedField;
	const formRef = useRef();
	const [form, setForm] = useState(null);
	const settingsContext = fieldSettingsContext;

	useEffect(() => {
		const filteredSettingsContext = getFilteredSettingsContext(
			settingsContext
		);

		if (form === null || form.isDisposed()) {
			const dispatchEvent = (type, payload) => {
				dataLayoutBuilder.dispatch(type, payload);
			};

			setForm(
				renderSettingsForm(
					{
						dispatchEvent,
						settingsContext: filteredSettingsContext
					},
					formRef.current
				)
			);
		} else {
			const {pages} = filteredSettingsContext;

			form.setState({pages}, () => {
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
	}, [dataLayoutBuilder, dispatch, form, formRef, settingsContext]);

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
	const [{fieldTypes, focusedField}] = useContext(
		DataLayoutBuilderViewContext
	);
	const {settingsContext} = focusedField;

	const visitor = new PagesVisitor(settingsContext.pages);
	const typeField = visitor.findField(field => field.fieldName === 'type');

	const fieldType = fieldTypes.find(({name}) => {
		return name === typeField.value;
	});

	const [dataLayoutBuilder] = useContext(DataLayoutBuilderContext);
	const handleFocusedFieldBlur = () => {
		dataLayoutBuilder.dispatch('sidebarFieldBlurred');
	};

	if (!fieldType) return null;

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
	const [{focusedField}] = useContext(DataLayoutBuilderViewContext);
	const [keywords, setKeywords] = useState('');
	const [sidebarClosed, setSidebarClosed] = useState(false);

	useSidebarContent(dataLayoutBuilder.ContainerRef, sidebarClosed);

	const sidebarRef = useRef();

	useEffect(() => {
		const eventHandler = ({target}) => {
			if (isClickOutside(target, sidebarRef.current, '.dropdown-menu')) {
				dataLayoutBuilder.dispatch('sidebarFieldBlurred');
			}
		};

		window.addEventListener('click', eventHandler, true);

		return () => window.removeEventListener('click', eventHandler);
	}, [dataLayoutBuilder, sidebarRef]);

	useEffect(() => {
		const productMenuToggle = document.querySelector(
			'.product-menu-toggle'
		);

		if (productMenuToggle) {
			const sidenav = Liferay.SideNavigation.instance(productMenuToggle);

			if (!sidebarClosed) {
				sidenav.hide();
			}
		}
	}, [sidebarClosed]);

	useLayoutEffect(() => {
		const productMenuToggle = document.querySelector(
			'.product-menu-toggle'
		);

		if (productMenuToggle) {
			Liferay.SideNavigation.hide(productMenuToggle);

			const sidenav = Liferay.SideNavigation.instance(productMenuToggle);
			const openEventListener = sidenav.on(
				'openStart.lexicon.sidenav',
				() => {
					setSidebarClosed(true);
				}
			);

			return () => {
				openEventListener.removeListener();
			};
		}
	}, []);

	const hasFocusedField = Object.keys(focusedField).length > 0;

	return (
		<Sidebar
			closeable={!hasFocusedField || sidebarClosed}
			closed={sidebarClosed}
			onSearch={hasFocusedField ? false : setKeywords}
			onToggle={closed => setSidebarClosed(closed)}
			ref={sidebarRef}
		>
			<>
				{hasFocusedField && <SettingsSidebarHeader />}

				<Sidebar.Body>
					{hasFocusedField ? (
						<SettingsSidebarBody />
					) : (
						<DefaultSidebarBody keywords={keywords} />
					)}
				</Sidebar.Body>
			</>
		</Sidebar>
	);
};
