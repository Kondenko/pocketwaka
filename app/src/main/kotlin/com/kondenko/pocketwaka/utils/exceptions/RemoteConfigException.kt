package com.kondenko.pocketwaka.utils.exceptions

data class RemoteConfigException(val key: String, override val message: String = "Value for key $key wasn't found in RemoteConfig") : Exception(message)