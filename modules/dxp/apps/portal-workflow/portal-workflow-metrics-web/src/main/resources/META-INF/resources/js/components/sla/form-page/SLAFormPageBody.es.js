/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import React, {useCallback, useContext} from 'react';

import ContentView from '../../../shared/components/content-view/ContentView.es';
import FormGroupWithStatus from '../../../shared/components/form/FormGroupWithStatus.es';
import ReloadButton from '../../../shared/components/list/ReloadButton.es';
import {useToaster} from '../../../shared/components/toaster/hooks/useToaster.es';
import {AppContext} from '../../AppContext.es';
import {SLAContext} from '../SLAContainer.es';
import {
	ALERT_MESSAGE,
	DURATION,
	HOURS,
	NAME,
	PAUSE_NODE_KEYS,
	START_NODE_KEYS,
	STOP_NODE_KEYS,
} from './SLAFormConstants.es';
import {AlertChange, AlertMessage} from './SLAFormPageAlerts.es';
import {SLAFormContext} from './SLAFormPageProvider.es';
import {DurationSection} from './sections/DurationSection.es';
import {TimeFrameSection} from './sections/TimeFrameSection.es';
import {
	hasErrors,
	validateDuration,
	validateHours,
	validateName,
	validateNodeKeys,
} from './util/slaFormUtil.es';

const Body = ({history, id, processId, query}) => {
	const {defaultDelta} = useContext(AppContext);
	const {setSLAUpdated} = useContext(SLAContext);
	const {
		changeValue,
		errors,
		fetchNodes,
		resetNodes,
		saveSLA,
		setErrors,
		sla,
	} = useContext(SLAFormContext);
	const toaster = useToaster();

	const handleErrors = error => {
		const {data} = error.response || {};

		if (Array.isArray(data)) {
			data.forEach(({fieldName, message}) => {
				errors[fieldName || ALERT_MESSAGE] = message;
			});

			const nodeKeys = [PAUSE_NODE_KEYS, START_NODE_KEYS, STOP_NODE_KEYS];
			const nodeErrors = data.filter(({fieldName}) =>
				nodeKeys.includes(fieldName)
			);

			if (nodeErrors.length) {
				resetNodes();
				fetchNodes(processId);
			}

			setErrors({...errors});
		}
		else {
			toaster.danger(Liferay.Language.get('your-request-has-failed'));
		}
	};

	const handleSubmit = useCallback(() => {
		const newErrors = {
			...errors,
			[ALERT_MESSAGE]: '',
			[DURATION]: validateDuration(sla.days, sla.hours),
			[HOURS]: validateHours(sla.hours),
			[NAME]: validateName(sla.name),
			[PAUSE_NODE_KEYS]: '',
			[START_NODE_KEYS]: validateNodeKeys(sla.startNodeKeys.nodeKeys),
			[STOP_NODE_KEYS]: validateNodeKeys(sla.stopNodeKeys.nodeKeys),
		};

		if (
			(!sla.hours || sla.hours === '00:00') &&
			sla.days &&
			Number(sla.days) > 0
		) {
			newErrors[HOURS] = '';
		}

		if (hasErrors(newErrors)) {
			setErrors({
				...newErrors,
				[ALERT_MESSAGE]: Liferay.Language.get(
					'please-fill-in-the-required-fields'
				),
			});
		}
		else {
			saveSLA()
				.then(() => {
					if (id) {
						setSLAUpdated(true);

						history.goBack();

						toaster.success(
							Liferay.Language.get('sla-was-updated')
						);
					}
					else {
						history.push({
							pathname: `/sla/${processId}/list/${defaultDelta}/1`,
							search: query,
						});

						toaster.success(Liferay.Language.get('sla-was-saved'));
					}
				})
				.catch(handleErrors);
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [id, processId, saveSLA, sla]);

	const onChangeHandler = validateFunc => ({target: {name, value = ''}}) => {
		changeValue(name, value);

		if (typeof validateFunc === 'function') {
			validateFunc(value);
		}
	};

	const onNameChanged = newName => {
		setErrors({
			...errors,
			[ALERT_MESSAGE]: '',
			[NAME]: validateName(newName),
		});
	};

	const statesProps = {
		errorProps: {
			actionButton: <ReloadButton />,
			className: 'pb-5 pt-6 sheet sheet-lg',
			hideAnimation: true,
			message: Liferay.Language.get(
				'there-was-a-problem-retrieving-data-please-try-reloading-the-page'
			),
		},
		loadingProps: {className: 'py-5 sheet sheet-lg'},
	};

	return (
		<ContentView {...statesProps}>
			<div className="alert-container">
				{errors[ALERT_MESSAGE] && (
					<Body.AlertMessage message={errors[ALERT_MESSAGE]} />
				)}

				{sla.status === 2 && <Body.AlertChange />}
			</div>

			<ClayForm className="sheet sheet-lg">
				<div className="mb-0 sheet-header">
					<h2 className="sheet-title" data-testid="sheetTitle">
						{Liferay.Language.get('sla-definition')}
					</h2>
				</div>

				<div className="mb-0 sheet-section">
					<div className="row">
						<FormGroupWithStatus
							className="col col-sm-5 form-group"
							data-testid="nameField"
							error={errors[NAME]}
							htmlFor="sla_name"
							label={Liferay.Language.get('name')}
							requiredLabel
						>
							<ClayInput
								autoFocus
								className="form-control"
								id="sla_name"
								maxLength={75}
								name="name"
								onChange={onChangeHandler(onNameChanged)}
								type="text"
								value={sla.name}
							/>
						</FormGroupWithStatus>

						<FormGroupWithStatus
							className="col col-sm-7 form-group"
							data-testid="descriptionField"
							htmlFor="sla_description"
							label={Liferay.Language.get('description')}
						>
							<ClayInput
								id="sla_description"
								name="description"
								onChange={onChangeHandler()}
								type="text"
								value={sla.description}
							/>
						</FormGroupWithStatus>
					</div>

					<Body.TimeFrameSection />

					<Body.DurationSection onChangeHandler={onChangeHandler} />
				</div>

				<div className="sheet-footer sheet-footer-btn-block-sm-down">
					<ClayButton.Group spaced>
						<ClayButton
							data-testid="saveButton"
							onClick={handleSubmit}
						>
							{id
								? Liferay.Language.get('update')
								: Liferay.Language.get('save')}
						</ClayButton>

						<ClayButton
							data-testid="cancelButton"
							displayType="secondary"
							onClick={() => history.goBack()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayButton.Group>
				</div>
			</ClayForm>
		</ContentView>
	);
};

Body.AlertChange = AlertChange;
Body.AlertMessage = AlertMessage;
Body.DurationSection = DurationSection;
Body.TimeFrameSection = TimeFrameSection;

export {Body};
