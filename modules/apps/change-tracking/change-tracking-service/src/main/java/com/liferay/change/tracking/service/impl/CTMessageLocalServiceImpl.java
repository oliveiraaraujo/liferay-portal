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

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.model.CTMessage;
import com.liferay.change.tracking.service.base.CTMessageLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.messaging.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The implementation of the ct message local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>com.liferay.change.tracking.service.CTMessageLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTMessageLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.model.CTMessage",
	service = AopService.class
)
public class CTMessageLocalServiceImpl extends CTMessageLocalServiceBaseImpl {

	@Override
	public CTMessage addCTMessage(long ctCollectionId, Message message)
		throws PortalException {

		long ctMessageId = counterLocalService.increment(
			CTMessage.class.getName());

		CTMessage ctMessage = ctMessagePersistence.create(ctMessageId);

		ctMessage.setCtCollectionId(ctCollectionId);
		ctMessage.setMessageContent(_jsonFactory.serialize(message));

		return ctMessagePersistence.update(ctMessage);
	}

	@Override
	public List<Message> getMessages(long ctCollectionId) {
		List<CTMessage> ctMessages = ctMessagePersistence.findByCTCollectionId(
			ctCollectionId);

		if (ctMessages.isEmpty()) {
			Collections.emptyList();
		}

		List<Message> messages = new ArrayList<>(ctMessages.size());

		for (CTMessage ctMessage : ctMessages) {
			messages.add(
				(Message)_jsonFactory.deserialize(
					ctMessage.getMessageContent()));
		}

		return messages;
	}

	@Reference
	private JSONFactory _jsonFactory;

}