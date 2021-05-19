package br.com.zup.chave

import br.com.zup.KeyRequest
import br.com.zup.KeyResponse
import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.configuration.ErrorHandler
import convert
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@Validated
@ErrorHandler
class ChavePixEndpoint(@Inject private val service:ChavePixService): KeymanagerServiceGrpc.KeymanagerServiceImplBase() {

    override fun send(request: KeyRequest, responseObserver: StreamObserver<KeyResponse>?) {

        val chavepix: ChavePixRequest = request.convert(request);

        service.cadastra(chavepix,request);

        val response = KeyResponse.newBuilder().setMessage("sucesso").build()
        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}