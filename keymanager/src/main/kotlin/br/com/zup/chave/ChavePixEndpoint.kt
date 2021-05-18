package br.com.zup.chave

import br.com.zup.KeyRequest
import br.com.zup.KeyResponse
import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.client.ContaResponse
import br.com.zup.client.ErpClient
import br.com.zup.configuration.ChaveExistenteException
import br.com.zup.configuration.ContaNaoEncontradaException
import br.com.zup.configuration.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid


@Singleton
@Validated
@ErrorHandler
class ChavePixEndpoint(@Inject val pixRepository: ChavePixRepository,
                            @Inject val erpClient: ErpClient): KeymanagerServiceGrpc.KeymanagerServiceImplBase() {

    override fun send(request: KeyRequest, responseObserver: StreamObserver<KeyResponse>?) {

        var chave: String? = request.chave;

        if(TipoChave.valueOf(request.tipoChave.toString()) == TipoChave.RANDOM){
            chave = UUID.randomUUID().toString();
        }

        val chavepix: ChavePixRequest = ChavePixRequest(request.idCliente,chave!!
            ,request.tipoConta.toString(),request.tipoChave.toString())

        validar(chavepix,request)

        val response = KeyResponse.newBuilder().setMessage("sucesso").build()
        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }

    @Transactional
    private fun validar(@Valid chavePixRequest: ChavePixRequest,request: KeyRequest): ChavePixRequest{

        var consultar: HttpResponse<ContaResponse>
        try{
            consultar = erpClient.getConta(request.tipoConta.toString(),request.idCliente)
        }catch(e:Exception){
            throw ContaNaoEncontradaException();
        }

        if(pixRepository.existsByChave(chavePixRequest.chave)){
            throw ChaveExistenteException();
        }

        pixRepository.save(chavePixRequest.toChavePix(consultar.body().toConta()))
        return chavePixRequest
    }
}