/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.multi.factor.authentication.checker.email.otp.service.impl;

import com.liferay.multi.factor.authentication.checker.email.otp.exception.DuplicateMFAEmailOTPEntryException;
import com.liferay.multi.factor.authentication.checker.email.otp.model.MFAEmailOTPEntry;
import com.liferay.multi.factor.authentication.checker.email.otp.service.base.MFAEmailOTPEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;

import java.util.Date;

import org.osgi.service.component.annotations.Component;

/**
 * @author Arthur Chan
 * @see MFAEmailOTPEntryLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.multi.factor.authentication.checker.email.otp.model.MFAEmailOTPEntry",
	service = AopService.class
)
public class MFAEmailOTPEntryLocalServiceImpl
	extends MFAEmailOTPEntryLocalServiceBaseImpl {

	public MFAEmailOTPEntry addMFAEmailOTPEntry(long userId) throws PortalException {
		MFAEmailOTPEntry emailOTPEntry =
			emailOTPEntryLocalService.fetchEntryByUserId(userId);

		if (emailOTPEntry != null) {
			throw new DuplicateMFAEmailOTPEntryException("User ID " + userId);
		}

		emailOTPEntry = emailOTPEntryPersistence.create(
			counterLocalService.increment());

		User user = userLocalService.getUserById(userId);

		emailOTPEntry.setCompanyId(user.getCompanyId());
		emailOTPEntry.setUserId(user.getUserId());
		emailOTPEntry.setUserName(user.getFullName());

		emailOTPEntry.setCreateDate(new Date());
		emailOTPEntry.setModifiedDate(new Date());

		emailOTPEntryPersistence.update(emailOTPEntry);

		return emailOTPEntry;
	}

	public MFAEmailOTPEntry fetchEntryByUserId(long userId) {
		return emailOTPEntryPersistence.fetchByUserId(userId);
	}

	public void resetFailedAttempts(long userId) {
		MFAEmailOTPEntry emailOTPEntry =
			emailOTPEntryLocalService.fetchEntryByUserId(userId);

		if (emailOTPEntry == null) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Reset failed attempts on non existent user: " + userId);
			}

			return;
		}

		emailOTPEntry.setFailedAttempts(0);
		emailOTPEntry.setLastFailDate(null);
		emailOTPEntry.setLastFailIP(null);

		emailOTPEntryPersistence.update(emailOTPEntry);
	}

	public void updateAttempts(long userId, String userIP, boolean success) {
		MFAEmailOTPEntry emailOTPEntry =
			emailOTPEntryLocalService.fetchEntryByUserId(userId);

		if (emailOTPEntry == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Update attempts on non existent user: " + userId);
			}

			return;
		}

		if (success) {
			emailOTPEntry.setFailedAttempts(0);
			emailOTPEntry.setLastSuccessDate(new Date());
			emailOTPEntry.setLastSuccessIP(userIP);
			emailOTPEntry.setLastFailDate(null);
			emailOTPEntry.setLastFailIP(null);
		}
		else {
			emailOTPEntry.setFailedAttempts(
				emailOTPEntry.getFailedAttempts() + 1);
			emailOTPEntry.setLastFailDate(new Date());
			emailOTPEntry.setLastFailIP(userIP);
		}

		emailOTPEntryPersistence.update(emailOTPEntry);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFAEmailOTPEntryLocalServiceImpl.class);

}