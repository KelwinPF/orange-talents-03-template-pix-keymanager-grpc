package br.com.zup.configuration


import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ContaNaoEncontradaExceptionHandler: ExceptionHandler<ContaNaoEncontradaException> {

    override fun handle(e: ContaNaoEncontradaException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ContaNaoEncontradaException
    }
}