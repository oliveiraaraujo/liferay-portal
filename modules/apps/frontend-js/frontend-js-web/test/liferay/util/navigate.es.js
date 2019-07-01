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

'use strict';

import navigate from '../../../src/main/resources/META-INF/resources/liferay/util/navigate.es';

describe('Liferay.Util.navigate', () => {
	const sampleUrl = 'http://sampleurl.com';

	beforeEach(() => {
		Liferay = {};
	});

	describe('when SPA is enabled', () => {
		beforeEach(() => {
			Liferay.once = jest.fn();

			Liferay.SPA = {
				app: {
					navigate: jest.fn()
				}
			};
		});

		it('navigates to the given url using the provided Liferay.SPA.app.navigate helper', () => {
			navigate(sampleUrl);

			expect(Liferay.SPA.app.navigate).toBeCalledWith(sampleUrl);
		});

		it('setups one-time-only global listeners in the Liferay object if specified', () => {
			const listenerFn = jest.fn();

			navigate(sampleUrl, {
				event1: listenerFn,
				event2: listenerFn
			});

			expect(Liferay.once).toHaveBeenCalledTimes(2);
			expect(Liferay.once).toHaveBeenNthCalledWith(
				1,
				'event1',
				listenerFn
			);
			expect(Liferay.once).toHaveBeenNthCalledWith(
				2,
				'event2',
				listenerFn
			);
		});
	});

	describe('when SPA is disabled', () => {
		it('navigates to the given url using window.location.assign', () => {
			const spy = jest
				.spyOn(console, 'error')
				.mockImplementation(() => undefined);

			navigate(sampleUrl);

			// JSDOM does not allow to mock location.href. Thus, we verify
			// it is called by matching the error they log when an attempt
			// at setting location.href is detected

			expect(spy).toHaveBeenCalled();
			expect(spy.mock.calls[0][0]).toMatch(
				'Error: Not implemented: navigation (except hash changes)'
			);

			spy.mockRestore();
		});
	});
});
