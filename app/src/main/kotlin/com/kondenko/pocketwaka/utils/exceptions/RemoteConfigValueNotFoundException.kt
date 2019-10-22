package com.kondenko.pocketwaka.utils.exceptions

data class RemoteConfigValueNotFoundException(
      val key: String,
      override val cause: Throwable?
) : Exception("Value for key $key wasn't found in RemoteConfig")
