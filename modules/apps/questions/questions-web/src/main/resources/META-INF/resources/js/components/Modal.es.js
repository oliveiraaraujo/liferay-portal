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

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';

export default ({
    visible,
    onClose,
    title,
    body,
    primaryAction,
    status="info",
    textSecondaryButton="Cancel",
    textPrimaryButton="Save"
}) => {
    const {observer, onClose: close} = useModal({
        onClose: onClose
    })

    return(
        <>
        {visible && (
            <ClayModal
                observer={observer}
                status={status}
            >
                <ClayModal.Header>{title}</ClayModal.Header>
                <ClayModal.Body>
                    {body}
                </ClayModal.Body>
                <ClayModal.Footer
                    first={
                        <ClayButton
                            displayType="secondary"
                            onClick={close}
                        >
                                {textSecondaryButton}
                        </ClayButton>
                    }
                    last={
                        <ClayButton
                            displayType="primary"
                            onClick={() =>{primaryAction();close();}}
                        >
                                {textPrimaryButton}
                        </ClayButton>
                }
                />
            </ClayModal>
        )}
        </>
    )
}