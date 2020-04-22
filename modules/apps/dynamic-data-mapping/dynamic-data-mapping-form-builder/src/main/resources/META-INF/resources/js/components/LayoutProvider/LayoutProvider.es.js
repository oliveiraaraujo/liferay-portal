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

import {
	FormSupport,
	PagesVisitor,
	RulesVisitor,
	generateName,
	getRepeatedIndex,
} from 'dynamic-data-mapping-form-renderer';
import handlePaginationItemClicked from 'dynamic-data-mapping-form-renderer/js/store/actions/handlePaginationItemClicked.es';
import Component from 'metal-jsx';
import {Config} from 'metal-state';

import {pageStructure, ruleStructure} from '../../util/config.es';
import {
	generateInstanceId,
	getFieldProperties,
	localizeField,
} from '../../util/fieldSupport.es';
import {setLocalizedValue} from '../../util/i18n.es';
import handleColumnResized from './handlers/columnResizedHandler.es';
import handleElementSetAdded from './handlers/elementSetAddedHandler.es';
import handleFieldAdded from './handlers/fieldAddedHandler.es';
import handleFieldBlurred from './handlers/fieldBlurredHandler.es';
import handleFieldClicked from './handlers/fieldClickedHandler.es';
import handleFieldDeleted from './handlers/fieldDeletedHandler.es';
import handleFieldDuplicated from './handlers/fieldDuplicatedHandler.es';
import handleFieldEdited from './handlers/fieldEditedHandler.es';
import handleFieldMoved from './handlers/fieldMovedHandler.es';
import handleFieldSetAdded from './handlers/fieldSetAddedHandler.es';
import handleFocusedFieldEvaluationEnded from './handlers/focusedFieldEvaluationEndedHandler.es';
import handleLanguageIdDeleted from './handlers/languageIdDeletedHandler.es';
import handleSectionAdded from './handlers/sectionAddedHandler.es';
import {shouldAutoGenerateName} from './util/defaults.es';
import {generateFieldName} from './util/fields.es';

/**
 * LayoutProvider listens to your children's events to
 * control the `pages` and make manipulations.
 * @extends Component
 */

class LayoutProvider extends Component {
	createNewPage() {
		const languageId = this.props.editingLanguageId;
		const page = {
			description: '',
			enabled: true,
			rows: [FormSupport.implAddRow(12, [])],
			showRequiredFieldsWarning: true,
			title: '',
		};

		setLocalizedValue(page, languageId, 'title', '');
		setLocalizedValue(page, languageId, 'description', '');

		return page;
	}

	dispatch(event, payload) {
		this.emit(event, payload);
	}

	getChildContext() {
		return {
			dispatch: this.dispatch.bind(this),
			store: this,
		};
	}

	getEvents() {
		return {
			activePageUpdated: this._handleActivePageUpdated.bind(this),
			columnResized: this._handleColumnResized.bind(this),
			elementSetAdded: this._handleElementSetAdded.bind(this),
			fieldAdded: this._handleFieldAdded.bind(this),
			fieldBlurred: this._handleFieldBlurred.bind(this),
			fieldChangesCanceled: this._handleFieldChangesCanceled.bind(this),
			fieldClicked: this._handleFieldClicked.bind(this),
			fieldDeleted: this._handleFieldDeleted.bind(this),
			fieldDuplicated: this._handleFieldDuplicated.bind(this),
			fieldEdited: this._handleFieldEdited.bind(this),
			fieldMoved: this._handleFieldMoved.bind(this),
			fieldSetAdded: this._handleFieldSetAdded.bind(this),
			focusedFieldEvaluationEnded: this._handleFocusedFieldEvaluationEnded.bind(
				this
			),
			languageIdDeleted: this._handleLanguageIdDeleted.bind(this),
			pageAdded: this._handlePageAdded.bind(this),
			pageDeleted: this._handlePageDeleted.bind(this),
			pageReset: this._handlePageReset.bind(this),
			pagesUpdated: this._handlePagesUpdated.bind(this),
			paginationItemClicked: this._handlePaginationItemClicked.bind(this),
			paginationModeUpdated: this._handlePaginationModeUpdated.bind(this),
			paginationNextClicked: this._handlePaginationNextClicked.bind(this),
			paginationPreviousClicked: this._handlePaginationPreviousClicked.bind(
				this
			),
			ruleAdded: this._handleRuleAdded.bind(this),
			ruleDeleted: this._handleRuleDeleted.bind(this),
			ruleSaved: this._handleRuleSaved.bind(this),
			sectionAdded: this._handleSectionAdded.bind(this),
			sidebarFieldBlurred: this._handleSidebarFieldBlurred.bind(this),
			successPageChanged: this._handleSuccessPageChanged.bind(this),
		};
	}

	getFocusedField() {
		const {defaultLanguageId, editingLanguageId} = this.props;
		let {focusedField} = this.state;

		if (focusedField && focusedField.settingsContext) {
			const settingsContext = {
				...focusedField.settingsContext,
				pages: this.getLocalizedPages(
					focusedField.settingsContext.pages
				),
			};

			focusedField = {
				...focusedField,
				...getFieldProperties(
					settingsContext,
					defaultLanguageId,
					editingLanguageId
				),
				settingsContext,
			};
		}

		return focusedField;
	}

	getLocalizedPages(pages) {
		const {defaultLanguageId, editingLanguageId} = this.props;
		const settingsVisitor = new PagesVisitor(pages);

		return settingsVisitor.mapFields(field =>
			localizeField(field, defaultLanguageId, editingLanguageId)
		);
	}

	getPages() {
		const {defaultLanguageId, editingLanguageId} = this.props;
		const {availableLanguageIds = [editingLanguageId]} = this.props;
		const {focusedField} = this.state;
		let {pages} = this.state;

		const visitor = new PagesVisitor(pages);

		pages = visitor.mapFields(
			field => {
				const {options, settingsContext} = field;

				return {
					...getFieldProperties(
						settingsContext,
						defaultLanguageId,
						editingLanguageId
					),
					name: generateName(field.name, {
						instanceId: field.instanceId || generateInstanceId(),
						repeatedIndex: getRepeatedIndex(field.name),
					}),
					options,
					selected: focusedField.fieldName === field.fieldName,
					settingsContext: {
						...settingsContext,
						availableLanguageIds,
						defaultLanguageId,
						pages: this.getLocalizedPages(settingsContext.pages),
					},
				};
			},
			true,
			true
		);

		visitor.setPages(pages);

		return visitor.mapPages(page => {
			let {description, title} = page;

			if (page.localizedDescription[editingLanguageId]) {
				description = page.localizedDescription[editingLanguageId];
			}
			else if (page.localizedDescription[defaultLanguageId]) {
				description = page.localizedDescription[defaultLanguageId];
			}

			if (page.localizedTitle[editingLanguageId]) {
				title = page.localizedTitle[editingLanguageId];
			}
			else if (page.localizedTitle[defaultLanguageId]) {
				title = page.localizedTitle[defaultLanguageId];
			}

			return {
				...page,
				description,
				title,
			};
		});
	}

	getPaginationMode() {
		const {allowMultiplePages} = this.props;
		const {paginationMode} = this.state;

		if (allowMultiplePages) {
			return paginationMode;
		}

		return 'single-page';
	}

	getRules() {
		let {rules} = this.state;

		if (rules) {
			const visitor = new RulesVisitor(rules);

			rules = visitor.mapConditions(condition => {
				if (condition.operands[0].type == 'list') {
					condition = {
						...condition,
						operands: [
							{
								label: 'user',
								repeatable: false,
								type: 'user',
								value: 'user',
							},
							{
								...condition.operands[0],
								label: condition.operands[0].value,
							},
						],
					};
				}

				return condition;
			});
		}

		return rules;
	}

	render() {
		const {
			allowSuccessPage,
			children,
			defaultLanguageId,
			editingLanguageId,
			fieldActions,
			spritemap,
		} = this.props;
		const {activePage, rules, successPageSettings} = this.state;

		return (
			<span>
				{(children || []).map(child => ({
					...child,
					props: {
						...child.props,
						...this.otherProps(),
						activePage,
						allowSuccessPage,
						defaultLanguageId,
						editingLanguageId,
						fieldActions,
						focusedField: this.getFocusedField(),
						pages: this.getPages(),
						paginationMode: this.getPaginationMode(),
						rules,
						spritemap,
						successPageSettings,
					},
				}))}
			</span>
		);
	}

	_fieldActionsValueFn() {
		return [
			{
				action: fieldName =>
					this.dispatch('fieldDuplicated', {fieldName}),
				label: Liferay.Language.get('duplicate'),
			},
			{
				action: fieldName => this.dispatch('fieldDeleted', {fieldName}),
				label: Liferay.Language.get('delete'),
			},
		];
	}

	_fieldNameGeneratorValueFn() {
		return (desiredName, currentName) => {
			const {pages} = this.state;

			return generateFieldName(pages, desiredName, currentName);
		};
	}

	_handleActivePageUpdated(activePage) {
		this.setState({
			activePage,
		});
	}

	_handleColumnResized({column, container, direction, source}) {
		const {state} = this;

		this.setState(
			handleColumnResized(
				this.props,
				state,
				source,
				container,
				column,
				direction
			)
		);
	}

	_handleElementSetAdded(event) {
		this.setState(handleElementSetAdded(this.props, this.state, event));
	}

	_handleFieldAdded(event) {
		this.setState(handleFieldAdded(this.props, this.state, event));
	}

	_handleFieldBlurred(event) {
		this.setState(handleFieldBlurred(this.state, event));
	}

	_handleFieldChangesCanceled() {
		const {focusedField, pages, previousFocusedField} = this.state;
		const {settingsContext} = previousFocusedField;

		const visitor = new PagesVisitor(settingsContext.pages);

		visitor.mapFields(({fieldName, value}) => {
			this._handleFieldEdited({
				propertyName: fieldName,
				propertyValue: value,
			});
		});

		visitor.setPages(pages);

		this.setState({
			focusedField: previousFocusedField,
			pages: visitor.mapFields(field => {
				if (field.fieldName === focusedField.fieldName) {
					return {
						...field,
						settingsContext,
					};
				}

				return field;
			}),
		});
	}

	_handleFieldClicked(event) {
		this.setState(handleFieldClicked(this.props, this.state, event));
	}

	_handleFieldDeleted(event) {
		this.setState(handleFieldDeleted(this.props, this.state, event));
	}

	_handleFieldDuplicated(event) {
		this.setState(handleFieldDuplicated(this.props, this.state, event));
	}

	_handleFieldEdited(properties) {
		this.setState(handleFieldEdited(this.props, this.state, properties));
	}

	_handleFieldMoved(event) {
		this.setState(handleFieldMoved(this.props, this.state, event));
	}

	_handleFieldSetAdded(event) {
		this.setState(handleFieldSetAdded(this.props, this.state, event));
	}

	_handleFocusedFieldEvaluationEnded({settingsContext}) {
		this.setState(
			handleFocusedFieldEvaluationEnded(
				this.props,
				this.state,
				settingsContext
			)
		);
	}

	_handleLanguageIdDeleted({locale}) {
		const {focusedField, pages} = this.state;

		this.setState(handleLanguageIdDeleted(focusedField, pages, locale));
	}

	_handlePageAdded() {
		const {pages} = this.state;

		this.setState({
			activePage: pages.length,
			pages: [...pages, this.createNewPage()],
		});
	}

	_handlePageDeleted(pageIndex) {
		const {pages} = this.state;

		this.setState({
			activePage: Math.max(0, pageIndex - 1),
			pages: pages.filter((page, index) => index != pageIndex),
		});
	}

	_handlePageReset() {
		this.setState({
			pages: [this.createNewPage()],
		});
	}

	_handlePagesUpdated(pages) {
		this.setState({
			pages: [...pages],
		});
	}

	_handlePaginationItemClicked({pageIndex}) {
		handlePaginationItemClicked({pageIndex}, this.dispatch.bind(this));
	}

	_handlePaginationModeUpdated() {
		const {paginationMode} = this.state;
		let newMode = 'paginated';

		if (paginationMode === newMode) {
			newMode = 'wizard';
		}

		this.setState({
			paginationMode: newMode,
		});
	}

	_handlePaginationNextClicked() {
		const {activePage, pages} = this.state;
		const pageIndex = Math.min(activePage + 1, pages.length - 1);
		handlePaginationItemClicked({pageIndex}, this.dispatch.bind(this));
	}

	_handlePaginationPreviousClicked() {
		const {activePage} = this.state;
		const pageIndex = Math.max(activePage - 1, 0);
		handlePaginationItemClicked({pageIndex}, this.dispatch.bind(this));
	}

	_handleRuleAdded(rule) {
		this.setState({
			rules: [...this.state.rules, rule],
		});

		this.emit('rulesModified');
	}

	_handleRuleDeleted({ruleId}) {
		const {rules} = this.state;

		this.setState({
			rules: rules.filter((rule, index) => index !== ruleId),
		});

		this.emit('rulesModified');
	}

	_handleRuleSaved(event) {
		const {actions, conditions, ruleEditedIndex} = event;
		const logicalOperator = event['logical-operator'];
		const {rules} = this.state;

		const newRule = {
			actions,
			conditions,
			'logical-operator': logicalOperator,
		};

		rules.splice(ruleEditedIndex, 1, newRule);

		this.setState({
			rules,
		});

		this.emit('rulesModified');
	}

	_handleSectionAdded(event) {
		this.setState(handleSectionAdded(this.props, this.state, event));
	}

	_handleSidebarFieldBlurred() {
		this.setState({
			focusedField: {},
		});
	}

	_handleSuccessPageChanged(successPageSettings) {
		this.setState({
			successPageSettings,
		});
	}

	_pagesValueFn() {
		const {initialPages} = this.props;

		return initialPages;
	}

	_paginationModeValueFn() {
		return this.props.initialPaginationMode;
	}

	_rulesValueFn() {
		const {rules} = this.props;

		return rules;
	}

	_setEvents(value) {
		return {
			...this.getEvents(),
			...value,
		};
	}

	_setInitialPages(initialPages) {
		const visitor = new PagesVisitor(initialPages);

		return visitor.mapFields(field => {
			const {settingsContext} = field;

			return {
				...field,
				localizedValue: {},
				readOnly: true,
				settingsContext: {
					...this._setInitialSettingsContext(settingsContext),
				},
				value: undefined,
				visible: true,
			};
		});
	}

	_setInitialSettingsContext(settingsContext) {
		const visitor = new PagesVisitor(settingsContext.pages);

		return {
			...settingsContext,
			pages: visitor.mapFields(field => {
				if (field.type === 'options') {
					const getOptions = (languageId, field) => {
						return field.value[languageId].map(option => {
							return {
								...option,
								edited: true,
							};
						});
					};

					Object.keys(field.value).forEach(languageId => {
						field = {
							...field,
							value: {
								...field.value,
								[languageId]: getOptions(languageId, field),
							},
						};
					});
				}

				return field;
			}),
		};
	}

	_setPages(pages) {
		return pages.filter(({contentRenderer}) => {
			return contentRenderer !== 'success';
		});
	}

	_shouldAutoGenerateNameValueFn() {
		return shouldAutoGenerateName;
	}

	_successPageSettingsValueFn() {
		return this.props.initialSuccessPageSettings;
	}
}

LayoutProvider.PROPS = {
	/**
	 * @instance
	 * @memberof LayoutProvider
	 * @type {boolean}
	 */

	allowMultiplePages: Config.bool().value(true),

	/**
	 * @instance
	 * @memberof LayoutProvider
	 * @type {boolean}
	 */

	allowSuccessPage: Config.bool().value(true),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?string}
	 */

	defaultLanguageId: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?string}
	 */

	editingLanguageId: Config.string(),

	/**
	 * @default {}
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?object}
	 */

	events: Config.setter('_setEvents').value({}),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?string}
	 */

	fieldActions: Config.array().valueFn('_fieldActionsValueFn'),

	/**
	 * @default _fieldNameGeneratorValueFn
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?function}
	 */

	fieldNameGenerator: Config.func().valueFn('_fieldNameGeneratorValueFn'),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?string}
	 */

	fieldSetDefinitionURL: Config.string(),

	/**
	 * @default []
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?(array|undefined)}
	 */

	fieldSets: Config.array().value([]),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?(array|undefined)}
	 */

	initialPages: Config.arrayOf(pageStructure)
		.setter('_setInitialPages')
		.value([]),

	/**
	 * @default 'wizard'
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?string}
	 */

	initialPaginationMode: Config.string().value('wizard'),

	/**
	 * @instance
	 * @memberof LayoutProvider
	 * @type {object}
	 */

	initialSuccessPageSettings: Config.shapeOf({
		body: Config.object(),
		enabled: Config.bool(),
		title: Config.object(),
	}),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?(array|undefined)}
	 */

	rules: Config.arrayOf(ruleStructure),

	/**
	 * @default _shouldAutoGenerateNameValueFn
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?function}
	 */

	shouldAutoGenerateName: Config.func().valueFn(
		'_shouldAutoGenerateNameValueFn'
	),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?(array|undefined)}
	 */

	spritemap: Config.string(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?string}
	 */

	view: Config.string(),
};

LayoutProvider.STATE = {
	/**
	 * @instance
	 * @memberof FormPage
	 * @type {?number}
	 */

	activePage: Config.number().value(0),

	/**
	 * @default {}
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?object}
	 */

	focusedField: Config.shapeOf({
		columnIndex: Config.oneOfType([
			Config.bool().value(false),
			Config.number(),
		]).required(),
		pageIndex: Config.number().required(),
		rowIndex: Config.number().required(),
		type: Config.string().required(),
	}).value({}),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?array}
	 */

	pages: Config.arrayOf(pageStructure)
		.setter('_setPages')
		.valueFn('_pagesValueFn'),

	/**
	 * @instance
	 * @memberof LayoutProvider
	 * @type {string}
	 */

	paginationMode: Config.string().valueFn('_paginationModeValueFn'),

	/**
	 * @default {}
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?object}
	 */

	previousFocusedField: Config.shapeOf({
		columnIndex: Config.oneOfType([
			Config.bool().value(false),
			Config.number(),
		]).required(),
		pageIndex: Config.number().required(),
		rowIndex: Config.number().required(),
		type: Config.string().required(),
	}).value({}),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?(array|undefined)}
	 */

	rules: Config.arrayOf(ruleStructure).valueFn('_rulesValueFn'),

	/**
	 * @default undefined
	 * @instance
	 * @memberof LayoutProvider
	 * @type {?(object|undefined)}
	 */

	successPageSettings: Config.object().valueFn('_successPageSettingsValueFn'),
};

export default LayoutProvider;
