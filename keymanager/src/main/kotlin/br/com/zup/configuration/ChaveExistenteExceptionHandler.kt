package br.com.zup.configuration

import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveExistenteExceptionHandler : ExceptionHandler<ChaveExistenteException> {

    override fun handle(e: ChaveExistenteException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )

    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveExistenteException
    }

}