package com.kondenko.pocketwaka.utils.exceptions

class IllegalViewTypeException(message: String = "This view type is not supported")
    : IllegalArgumentException(message)