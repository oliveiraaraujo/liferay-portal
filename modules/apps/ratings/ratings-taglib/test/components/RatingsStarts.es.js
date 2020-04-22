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

import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import RatingsStars from '../../src/main/resources/META-INF/resources/js/components/RatingsStars.es';
import {formDataToObj} from '../utils';

const defaultProps = {
	className: 'com.liferay.model.RateableEntry',
	classPK: 'classPK',
	enabled: true,
	numberOfStars: 5,
	signedIn: true,
	url: 'http://url',
};

const renderComponent = (props = defaultProps) =>
	render(<RatingsStars {...props} />);

describe('RatingsStars', () => {
	afterEach(cleanup);

	describe('when rendered with the default props', () => {
		let starsDropdownToggle;

		beforeEach(() => {
			starsDropdownToggle = renderComponent().getAllByRole('button')[0];
		});

		it('is enabled', () => {
			expect(starsDropdownToggle.disabled).toBe(false);
		});

		it('has default user score', () => {
			expect(starsDropdownToggle.value).toBe('0');
		});
	});

	describe('when rendered with enabled = false', () => {
		it('is disabled', () => {
			const starsDropdownToggle = renderComponent({
				...defaultProps,
				enabled: false,
			}).getAllByRole('button')[0];

			expect(starsDropdownToggle.disabled).toBe(true);
		});
	});

	describe('when there is no server response', () => {
		beforeEach(() => {
			fetch.mockResponse(JSON.stringify({}));
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('and the user votes 2/5 stars', () => {
			let starsDropdownToggle;
			let starsButtons;

			beforeEach(() => {
				starsButtons = renderComponent({
					...defaultProps,
					userScore: 0.4,
				}).getAllByRole('button');
				starsDropdownToggle = starsButtons[0];

				act(() => {
					fireEvent.click(starsButtons[2]);
				});
			});

			it('increases the user score', () => {
				expect(starsDropdownToggle.value).toBe('2');
			});

			describe('and the user vote 5/5 stars', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(starsButtons[5]);
					});
				});

				it('increase the user score', () => {
					expect(starsDropdownToggle.value).toBe('5');
				});
			});
		});

		describe('with 5/5 user user score', () => {
			let starsDropdownToggle;
			let starsButtons;

			beforeEach(() => {
				starsButtons = renderComponent({
					...defaultProps,
					userScore: 1,
				}).getAllByRole('button');
				starsDropdownToggle = starsButtons[0];
			});

			it('initial user score', () => {
				expect(starsDropdownToggle.value).toBe('5');
			});

			describe('and the user vote 3/5 stars', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(starsButtons[3]);
					});
				});

				it('decreases the user score', () => {
					expect(starsDropdownToggle.value).toBe('3');
				});
			});
		});
	});

	describe('when there is a valid server response', () => {
		beforeEach(() => {
			fetch.mockResponseOnce(
				JSON.stringify({averageScore: 4, score: 0.2, totalEntries: 2})
			);
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('and the user vote 2/5 stars', () => {
			let starsDropdownToggle;
			let starsButtons;

			beforeEach(async () => {
				starsButtons = renderComponent({
					...defaultProps,
					userScore: 0.6,
				}).getAllByRole('button');
				starsDropdownToggle = starsButtons[0];

				await act(async () => {
					fireEvent.click(starsButtons[2]);
				});
			});

			it('sends a POST request to the server', () => {
				const [url, {body}] = fetch.mock.calls[0];
				const objFormData = formDataToObj(body);

				expect(url).toBe(defaultProps.url);
				expect(objFormData.className).toBe(defaultProps.className);
				expect(objFormData.score).toBe('0.4');
			});

			it('updates the user score with the one from the server', () => {
				expect(starsDropdownToggle.value).toBe('1');
			});
		});
	});
});
