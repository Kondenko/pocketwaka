package com.kondenko.pocketwaka.utils.exceptions

data class UnauthorizedException(override val message: String = "This user is unauthorized") : WakatimeException(message)