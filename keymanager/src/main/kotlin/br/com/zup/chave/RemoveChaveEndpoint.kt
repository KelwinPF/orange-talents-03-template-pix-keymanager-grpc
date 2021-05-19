package br.com.zup.chave

import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.RemoveKeyRequest
import br.com.zup.RemoveKeyResponse
import br.com.zup.configuration.ErrorHandler
import convert
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoveChaveEndpoint(@Inject private val service:ChavePixService)
    :KeymanagerServiceGrpc.KeymanagerServiceImplBase(){

    override fun remove(request: RemoveKeyRequest, responseObserver: StreamObserver<RemoveKeyResponse>) {
        val removerequest:RemoveChavePixRequest = request.convert();
        service.remove(removerequest)

        val response = RemoveKeyResponse.newBuilder().setMessage("sucesso").build()
        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}