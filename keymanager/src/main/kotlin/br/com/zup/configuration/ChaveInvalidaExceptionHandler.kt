package br.com.zup.configuration

import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveInvalidaExceptionHandler:ExceptionHandler<ChaveInvalidaException> {
    override fun handle(e: ChaveInvalidaException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveInvalidaException
    }
}