package com.kondenko.pocketwaka.utils.exceptions

data class RemoteConfigFetchingException(
      override val message: String = "Couldn't fetch and activate RemoteConfig values",
      override val cause: Throwable?
) : Exception(message, cause)