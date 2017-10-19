package com.kondenko.pocketwaka.domain

/**
 * The base class for all UUseCases in the application.
 */
abstract class UseCase<PARAMS, RESULT> {

    abstract fun execute(params: PARAMS): RESULT

}