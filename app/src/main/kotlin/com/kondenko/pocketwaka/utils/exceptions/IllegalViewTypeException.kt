package com.kondenko.pocketwaka.utils.exceptions

class IllegalViewTypeException(message: String = "This veiw type is not supported")
    : IllegalArgumentException(message)