package com.bagas.obrol.ui.screen.call

import com.bagas.obrol.domain.model.device.Contact

enum class CallState {
    SENT_CALL_REQUEST, RECEIVED_CALL_REQUEST, CALL
}

data class CallViewState(
    val callState: CallState? = null, // Can be null initially
    val contact: Contact? = null,      // Can be null initially
    val isSpeakerOn: Boolean = false
)
