package br.com.zup.chave

import br.com.zup.KeymanagerListaServiceGrpc
import br.com.zup.ListaKeyRequest
import br.com.zup.ListaKeyResponse
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.configuration.ChaveInvalidaException
import br.com.zup.configuration.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ErrorHandler
class ListaChaveEndpoint(@Inject private val repository: ChavePixRepository):KeymanagerListaServiceGrpc.KeymanagerListaServiceImplBase(){

    override fun lista(request: ListaKeyRequest, responseObserver: StreamObserver<ListaKeyResponse>) {
        if(request.clienteId.isNullOrBlank()){
            throw ChaveInvalidaException("ClienteId não pode ser vazio ou nulo")
        }
        val chaves = repository.findAllByClientId(request.clienteId).map {
            println(TipoChave.valueOf(it.tipoChave.toString()))
            println(TipoConta.valueOf(it.tipoConta.toString()))
            ListaKeyResponse.ChavePix.newBuilder()
                .setPixId(it.id.toString())
                .setTipoChave(TipoChave.valueOf(it.tipoChave.toString()))
                .setChave(it.chave).setTipoConta(TipoConta.valueOf(it.tipoConta.toString()))
                .setCriadoEm(it.criadoEm.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano).build()
                }).build()
        }
        println("teste2")
        responseObserver.onNext(
            ListaKeyResponse.newBuilder().setClientId(request.clienteId).addAllChaves(chaves).build())
        responseObserver.onCompleted()
    }
}