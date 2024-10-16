package org.raku.comma.metadata.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Support(
    val email: String? = null,
    @SerialName(value = "maillinglist")
    val mailingList: String? = null,
    @SerialName(value = "bugtracker")
    val bugTracker: String? = null,
    val source: String? = null,
    val irc: String? = null,
    val phone: String? = null
)
