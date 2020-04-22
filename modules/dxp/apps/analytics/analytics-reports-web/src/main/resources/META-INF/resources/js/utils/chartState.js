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

import {useReducer} from 'react';

const ADD_DATA_SET_ITEM = 'add-data-key';
const NEXT_TIME_SPAN = 'next-time-span';
const PREV_TIME_SPAN = 'previous-time-span';
const CHANGE_TIME_SPAN_OPTION = 'change-time-span-option';
const SET_LOADING = 'set-loading';

export const useChartState = ({
	defaultTimeSpanOption,
	publishDate,
	validAnalyticsConnection,
}) => {
	const [state, dispatch] = useReducer(reducer, {
		dataSet: {histogram: [], keyList: [], totals: []},
		loading: true,
		publishDate,
		timeSpanOffset: 0,
		timeSpanOption: defaultTimeSpanOption,
	});

	const actions = {
		addDataSetItem: payload =>
			dispatch({
				payload,
				type: ADD_DATA_SET_ITEM,
				validAnalyticsConnection,
			}),
		changeTimeSpanOption: payload =>
			dispatch({payload, type: CHANGE_TIME_SPAN_OPTION}),
		nextTimeSpan: () => dispatch({type: NEXT_TIME_SPAN}),
		previousTimeSpan: () => dispatch({type: PREV_TIME_SPAN}),
		setLoading: () => dispatch({type: SET_LOADING}),
	};

	return {actions, state};
};

/**
 * {
		"loading": false,
		"timeSpanOffset": 1,
		"timeSpanOption": "last-7-days",
		"dataSet": {
			"keyList": [
				"analyticsReportsHistoricalReads",
				"analyticsReportsHistoricalViews"
			],
			"totals": {
				"analyticsReportsHistoricalReads": 225000,
				"analyticsReportsHistoricalViews": 144245
			},
			"histogram": [
				{
					"analyticsReportsHistoricalViews": 5412,
					"label": "2020-02-02T00:00",
					"analyticsReportsHistoricalReads": 5000
				},
				...
			]
		}
	}
 *
 */
function reducer(state, action) {
	let nextState;

	switch (action.type) {
		case ADD_DATA_SET_ITEM:
			nextState = addDataSetItem(
				state,
				action.payload,
				action.validAnalyticsConnection
			);
			break;
		case NEXT_TIME_SPAN:
			nextState = {
				...state,
				timeSpanOffset: state.timeSpanOffset - 1,
			};
			break;
		case PREV_TIME_SPAN:
			nextState = {
				...state,
				timeSpanOffset: state.timeSpanOffset + 1,
			};
			break;
		case CHANGE_TIME_SPAN_OPTION:
			nextState = {
				...state,
				timeSpanOffset: 0,
				timeSpanOption: action.payload.key,
			};
			break;
		case SET_LOADING:
			nextState = setLoadingState(state);
			break;
		default:
			state = nextState;
			break;
	}

	return nextState;
}

/**
 * Declares the state as loading and resets the dataSet histogram values
 */
function setLoadingState(state) {
	/**
	 * The dataSet does not need to be reset
	 */
	if (!state.dataSet) {
		return {...state, loading: true};
	}

	const histogram = state.dataSet.histogram.map(set => {
		const newSet = {};

		const setArray = Object.entries(set);

		for (const index in setArray) {
			const [key, value] = setArray[index];

			if (key === 'label') {
				newSet[key] = value;
			}
			else {
				newSet[key] = null;
			}
		}

		return newSet;
	});

	const arrayTotals = Object.entries(state.dataSet.totals);

	const totals = {};

	for (const index in arrayTotals) {
		const [key] = arrayTotals[index];

		totals[key] = null;
	}

	const nextState = {
		...state,
		dataSet: {
			...state.dataSet,
			histogram,
			totals,
		},
		loading: true,
	};

	return nextState;
}

function mergeDataSets({
	key,
	newData,
	previousDataSet = {histogram: [], keyList: [], totals: []},
	publishDate,
	timeSpanComparator,
	validAnalyticsConnection,
}) {
	const resultDataSet = {};

	resultDataSet.keyList = [...previousDataSet.keyList, key];

	resultDataSet.totals = {
		...previousDataSet.totals,
		[key]: newData.value,
	};

	const publishDateObject = new Date(publishDate);

	const newFormattedHistogram = newData.histogram.map(h => {
		const valueDataObject = new Date(h.key);

		if (
			(valueDataObject < publishDateObject &&
				publishDateObject - valueDataObject > timeSpanComparator) ||
			!validAnalyticsConnection
		) {
			return {
				[key]: null,
				label: h.key,
			};
		}

		return {
			[key]: h.value,
			label: h.key,
		};
	});

	let start = 0;
	const mergeHistogram = [];

	while (start < newData.histogram.length) {
		if (!previousDataSet.histogram[start]) {
			mergeHistogram.push({
				...newFormattedHistogram[start],
			});
		}
		else if (
			newFormattedHistogram[start].label ===
			previousDataSet.histogram[start].label
		) {
			mergeHistogram.push({
				...newFormattedHistogram[start],
				...previousDataSet.histogram[start],
			});
		}

		start = start + 1;
	}

	resultDataSet.histogram = mergeHistogram;

	return resultDataSet;
}

/**
 * Adds dataSetItem to the dataSet
 *
 * payload = {
 * 	dataSet: {
 * 		histogram: Array<{
 *			key: string, // '2020-01-24T00:00'
 *			value: number
 * 		}>
 * 		values: number
 * 	},
 * 	key: string,
 *  timeSpanComparator: number,
 * }
 */
function addDataSetItem(state, payload, validAnalyticsConnection) {
	/**
	 * The dataSetItem is recognized as substitutive when the
	 * previous state was in loading state.
	 */
	const previousDataSet = state.loading === true ? undefined : state.dataSet;

	return {
		...state,
		dataSet: mergeDataSets({
			key: payload.key,
			newData: payload.dataSetItem,
			previousDataSet,
			publishDate: state.publishDate,
			timeSpanComparator: payload.timeSpanComparator,
			validAnalyticsConnection,
		}),
		loading: false,
	};
}
