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

import ClayLabel from '@clayui/label';
import ClayIcon from '@clayui/icon';
// import ClayDropDown from '@clayui/drop-down';
import {PagesVisitor} from 'dynamic-data-mapping-form-renderer';
// import dom from 'metal-dom';
import {maxPageIndex, pageOptions} from '../../util/pageSupport.es';
import {getFieldProperty} from '../LayoutProvider/util/fields.es';
import RulesSupport from '../RuleBuilder/RulesSupport.es';
import React from 'react';

/**
 * RuleList.
 * @extends React.Component
 */

class RuleList extends React.Component {
	componentDidMount() {
        document.addEventListener('mouseup', this._handleDocumentMouseUp.bind(this), true);

        this.setState(states => {
            const rules = RulesSupport.formatRules(
                this.pages,
                this._setDataProviderNames(states)
            );

            return {
                ...states,
                rules: rules.map((rule) => {
                    let logicalOperator;
                    let invalidRule = false;
    
                    if (rule['logical-operator']) {
                        logicalOperator = rule['logical-operator'].toLowerCase();
                    }
                    else if (rule.logicalOperator) {
                        logicalOperator = rule.logicalOperator.toLowerCase();
                    }
    
                    invalidRule = RulesSupport.findInvalidRule(rule);
    
                    return {
                        ...rule,
                        actions: rule.actions.map((action) => {
                            let newAction;
    
                            if (action.action === 'auto-fill') {
                                const {inputs, outputs} = action;
    
                                const inputLabel = Object.values(
                                    inputs
                                ).map((input) => this._getFieldLabel(input));
                                const outputLabel = Object.values(
                                    outputs
                                ).map((output) => this._getFieldLabel(output));
    
                                newAction = {
                                    ...action,
                                    inputLabel,
                                    outputLabel,
                                };
                            }
                            else if (action.action == 'jump-to-page') {
                                newAction = {
                                    ...action,
                                    label: this._getJumpToPageLabel(rule, action),
                                };
                            }
                            else {
                                newAction = {
                                    ...action,
                                    label: this._getFieldLabel(action.target),
                                };
                            }
    
                            return newAction;
                        }),
                        conditions: rule.conditions.map((condition) => {
                            return {
                                ...condition,
                                operands: condition.operands.map(
                                    (operand, index) => {
                                        const label = this._getOperandLabel(
                                            condition.operands,
                                            index
                                        );
    
                                        return {
                                            ...operand,
                                            label,
                                            value: label,
                                        };
                                    }
                                ),
                            };
                        }),
                        invalidRule,
                        logicalOperator,
                        rulesCardOptions: this._getRulesCardOptions(rule),
                    };
                }),
            };

        });
	}

	componentWillUnmount() {
        document.removeEventListener('mouseup', this._handleDocumentMouseUp.bind(this));
	}
    
    render(props) {
        return (
            <div className="form-builder-rule-builder-container">
                <h1 className="form-builder-section-title text-default">{Liferay.Language.get('rule-builder')}</h1>
        
                <div className="liferay-ddm-form-rule-rules-list-container">
                    <RuleList.List {...props} />
                </div>
            </div>
        );
    }

	_getDataProviderName(id) {
		const {dataProvider} = this;

		return dataProvider.find((data) => data.uuid == id).label;
	}

	_getFieldLabel(fieldName) {
		const pages = this.pages;

		return getFieldProperty(pages, fieldName, 'label') || fieldName;
	}

	_getJumpToPageLabel(rule, action) {
		const {pages} = this;
		let pageLabel = '';

		const fieldTarget = (parseInt(action.target, 10) + 1).toString();
		const maxPageIndexRes = maxPageIndex(rule.conditions, pages);
		const pageOptionsList = pageOptions(pages, maxPageIndexRes);
		const selectedPage = pageOptionsList.find((option) => {
			return option.value == fieldTarget;
		});

		if (selectedPage) {
			pageLabel = selectedPage.label;
		}

		return pageLabel;
	}

	_getOperandLabel(operands, index) {
		let label = '';
		const operand = operands[index];

		if (operand.type === 'field') {
			label = this._getFieldLabel(operand.value);
		}
		else if (operand.type === 'user') {
			label = Liferay.Language.get('user');
		}
		else if (operand.type !== 'field') {
			const fieldType = RulesSupport.getFieldType(
				operands[0].value,
				this.pages
			);

			if (
				fieldType === 'checkbox_multiple' ||
				fieldType === 'radio' ||
				fieldType === 'select'
			) {
				label = this._getOptionLabel(operands[0].value, operand.value);
			}
			else if (operand.type === 'json') {
				label = '';

				const operandValueJSON = JSON.parse(operand.value);

				for (const key in operandValueJSON) {
					const keyLabel = this._getPropertyLabel(
						operands[0].value,
						'rows',
						key
					);

					const valueLabel = this._getPropertyLabel(
						operands[0].value,
						'columns',
						operandValueJSON[key]
					);

					label += keyLabel + ':' + valueLabel + ', ';
				}

				const lastCommaPosition = label.lastIndexOf(', ');

				if (lastCommaPosition != -1) {
					label = label.substr(0, lastCommaPosition);
				}
			}
			else {
				label = operand.value;
			}
		}
		else {
			label = operand.value;
		}

		return label;
	}

	_getOptionLabel(fieldName, optionValue) {
		return this._getPropertyLabel(fieldName, 'options', optionValue);
	}

	_getPropertyLabel(fieldName, propertyName, propertyValue) {
		const pages = this.pages;

		let fieldLabel = null;

		if (pages && propertyValue) {
			const visitor = new PagesVisitor(pages);

			visitor.findField((field) => {
				let found = false;

				if (field.fieldName === fieldName && field.options) {
					field[propertyName].some((property) => {
						if (property.value == propertyValue) {
							fieldLabel = property.label;

							found = true;
						}

						return found;
					});
				}

				return found;
			});
		}

		return fieldLabel ? fieldLabel : propertyValue;
	}

	_getRulesCardOptions(rule) {
		const hasNestedCondition = this._hasNestedCondition(rule);

		const rulesCardOptions = [
			{
				disabled: hasNestedCondition,
				label: Liferay.Language.get('edit'),
				settingsItem: 'edit',
			},
			{
				confirm: hasNestedCondition,
				label: Liferay.Language.get('delete'),
				settingsItem: 'delete',
			},
		];

		return rulesCardOptions;
	}

	_handleDocumentMouseUp({target}) {
		const dropdownSettings = target.closest('.ddm-rule-list-settings');

		if (dropdownSettings) {
			return;
		}

		this.setState({
			dropdownExpandedIndex: -1,
		});
	}

	_handleDropdownClicked(event) {
		event.preventDefault();

		const {dropdownExpandedIndex} = this;
		const ruleNode = event.delegateTarget.closest('.component-action');

		let ruleIndex = parseInt(ruleNode.dataset.ruleIndex, 10);

		if (ruleIndex === dropdownExpandedIndex) {
			ruleIndex = -1;
		}

		this.setState({
			dropdownExpandedIndex: ruleIndex,
		});
	}

	_handleRuleCardClicked({data, target}) {
		const cardElement = target.element.closest('[data-card-id]');
		const cardId = parseInt(cardElement.getAttribute('data-card-id'), 10);

		if (data.item.settingsItem == 'edit') {
			this.emit('ruleEdited', {
				ruleId: cardId,
			});
		}
		else if (data.item.settingsItem == 'delete') {
			if (
				!data.item.confirm ||
				confirm(
					Liferay.Language.get(
						'you-cannot-create-rules-with-nested-functions.-are-you-sure-you-want-to-delete-this-rule'
					)
				)
			) {
				this.emit('ruleDeleted', {
					ruleId: cardId,
				});
			}
		}
	}

	_hasNestedCondition(rule) {
		return (
			rule.conditions.find((condition) =>
				condition.operands.find((operand) =>
					operand.value.match(/[aA-zZ]+[(].*[,]+.*[)]/)
				)
			) !== undefined
		);
	}

	_setDataProviderNames(states) {
		const {rules} = states;

		if (this.dataProvider) {
			for (let rule = 0; rule < rules.length; rule++) {
				const actions = rules[rule].actions;

				actions.forEach((action) => {
					if (action.action === 'auto-fill') {
						const dataProviderName = this._getDataProviderName(
							action.ddmDataProviderInstanceUUID
						);

						action.dataProviderName = dataProviderName;
					}
				});
			}
		}

		return rules;
	}
}

/**
 * Prints the DDM form card rule.
 */
const EmptyList = ({message}) => {
    return (
        <div className="main-content-body">
            <div className="sheet taglib-empty-result-message">
                <div className="taglib-empty-result-message-header"></div>

                {message && (
                    <div className="sheet-text text-center text-muted">
                        <p className="text-default">{message}</p>
                    </div>
                )}
            </div>
        </div>
    )
}

/**
 * Prints the DDM form card rule.
 */
const Label = ({content}) => {
    return (
        <span className="label label-lg label-secondary" data-original-title={content} title={content}>
            <span className="text-truncate-inline">
                <span className="text-truncate">{content}</span>
            </span>
	    </span>
    )
}


/**
 * Prints Rules Conditions.
 */
const Condition = ({
    operandType,
    operandLabel,
    operandValue
}) => {
    const SubCondition = ({operandType}) => {
        if(['double', 'integer', 'json', 'option', 'string', 'text', 'numeric'].includes(operandType)){
            return  (<span>{Liferay.Language.get('value')} </span>);
        }
    
        if(operandType === 'user'){
            return (<span>{Liferay.Language.get('user')} </span>);
        }

        if(operandType === 'field'){
            return (<span>{Liferay.Language.get('field')} </span>);
        }

        if(operandType === 'list'){
            return (<span>{Liferay.Language.get('list')} </span>);
        }

        return null;
    }

    return (
        <div className="ddm-condition-container">
            <SubCondition operandType={operandType}/>

            <Label content={operandLabel || operandValue} />
	    </div>
    );
}

/**
 * Prints the show action.
 */
const Action = ({
    action,
    dataProviderName,
    outputLabel,
}) => {
    if(action === 'auto-fill'){
        return (
            <>
                {outputLabel.map(output => {
                    <>
                        <Label content={output} />
                        {!isLast(output) ? ',' : ''}
                    </>
                })}
                
                <span>
                    <b>
                        {Liferay.Util.sub(
                            Liferay.Language.get('autofill-x-from-data-provider-x'),
                            dataProviderOutputFields,
                            dataProviderName
                        )}
                    </b>
                </span>
            </>
        );
    }

    if(action === 'calculate'){
        return (
            <>
                <Label content={expression} />

                <Label content={label} />

                <span>
                    <b>
                        {Liferay.Util.sub(
                            Liferay.Language.get('calculate-field-x-as-x'),
                            expressionLabel,
                            targetLabel
                        )}
                    </b>
                </span>                
            </>
        );
    }

    const SubLabel = ({text, label}) => {
        return (<>
            <span>
                <b>
                    {text}
                </b>
            </span>

            <Label content={label} />
        </>);
    }

    if(action === 'enable'){
        return (
            <SubLabel text={Liferay.Language.get(`enable`)} label={label} />
        );
    }

    if(action === 'jump-to-page'){
        return (
            <SubLabel text={Liferay.Language.get(`jump-to-page`)} label={label} />
        );
    }

    if(action === 'require'){
        return (
            <SubLabel text={Liferay.Language.get(`require`)} label={label} />
        );
    }

    if(action === 'show'){
        return (
            <SubLabel text={Liferay.Language.get(`show`)} label={label} />
        );
    }    

    return null;
}

const List = ({
    onDropdownClicked,
    onRuleCardClicked,
    dropdownExpandedIndex,
    rules,
    spritemap
    }) => {
        const msg = "";
        const desc = "";

        if(!rules.length){
            return (<EmptyList message={Liferay.Language.get('there-are-no-rules-yet-click-on-plus-icon-below-to-add-the-first')}/>);
        }

        return (
            <div className="ddm-rule-list-container form-builder-rule-list">
                <ul className="ddm-form-body-content form-builder-rule-builder-rules-list tabular-list-group">
                    {rules.map((rule, ruleIndex) => (
                            <li className="list-group-item">
                            <div className="clamp-horizontal list-group-item-content">
                                <p className="form-builder-rule-builder-rule-description text-default">
                                    <b> {Liferay.Language.get('if')}</b>
                                    {rule.conditions.map(condition => (
                                        <>
                                            <b className="text-lowercase"><em> {$strings[$condition.operator]} </em></b>

                                            {condition.operands[1] && !isLast(condition) && (
                                                <>
                                                    <br />
                                                    <b>
                                                        {' '}
                                                        {
                                                            rule.logicalOperator === 'and' ? 
                                                            Liferay.Language.get('and'):Liferay.Language.get('or')
                                                        }
                                                        {' '}
                                                    </b>                                                    
                                                </>
                                            )}
                                        </>
                                    ))}

                                    <br />

                                    {rule.actions.map(action => {
                                        if(!isLast(action)){
                                            return (<>
                                                    , <br />
                                                    <b> {Liferay.Language.get('and')} </b>
                                            </>);
                                        }
                                        return null;
                                    })}

                                </p>
                            </div>

                            <div className="list-group-item-field">
                                <div className="card-col-field">
                                    {
                                        rule.invalidRule && (
                                        <>
                                            {Liferay.Language.get('due-to-missing-fields')}
                                            {Liferay.Language.get('broken-rule')}

                                            <div className="invalid-rule" title={titleMessage}>
                                                <ClayLabel spritemap={spritemap} displayType="danger">
                                                    {labelMessage}
                                                </ClayLabel>
                                            </div>
                                        </>
                                        )
                                    }

                                    <div className="dropdown dropdown-action">
                                        <div className="ddm-rule-list-settings" data-card-id={ruleIndex}>
                                            {/* <ClayActionsDropdown 
                                                items={rule.rulesCardOptions}
                                                expanded={dropdownExpandedIndex === ruleIndex} 
                                                itemClicked={onRuleCardClicked}
                                                spritemap={spritemap}
                                            /> */}

                                            <button className="component-action cursor-pointer dropdown-toggle" onClick={onDropdownClicked} data-rule-index={ruleIndex}>
                                                <ClayIcon spritemap={spritemap} symbol="ellipsis-v" />
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            </div>
        );
}

RuleList.List = List;

RuleList.displayName = 'RuleList';

export default RuleList;