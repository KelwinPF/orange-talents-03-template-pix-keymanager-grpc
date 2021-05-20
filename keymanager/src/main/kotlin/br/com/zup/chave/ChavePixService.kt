package br.com.zup.chave

import br.com.zup.KeyRequest
import br.com.zup.client.*
import br.com.zup.configuration.ChaveExistenteException
import br.com.zup.configuration.ChaveInvalidaException
import br.com.zup.configuration.ChaveNaoExistenteException
import br.com.zup.configuration.ContaNaoEncontradaException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService(@Inject private val repository:ChavePixRepository,
                      @Inject val erpClient: ErpClient,
                      @Inject val bcbClient: BcbClient
) {

    @Transactional
    fun remove(@Valid request: RemoveChavePixRequest):ChavePix{

        val chave:Optional<ChavePix> = repository.findById(request.pixId.toLong())

        if(chave.isPresent && request.clientId.equals(chave.get().clientId)){

            val chavepix = chave.get()

            try{
                val bcbresponse = bcbClient.deletarChave(
                    key = chavepix.chave,
                    request = DeletePixKeyRequest(chavepix)
                )
            }catch (e:Exception){
                throw ChaveInvalidaException("Erro ao cadastrar no banco central")
            }

            repository.delete(chave.get())
            return chave.get()

        }
        throw ChaveNaoExistenteException()
    }

    @Transactional
    fun cadastra(@Valid chavePixRequest:ChavePixRequest,request: KeyRequest): ChavePix{

        var consultar: HttpResponse<ContaResponse>

        try{
            consultar = erpClient.getConta(request.tipoConta.toString(),request.idCliente)
        }catch(e:Exception){
            throw ContaNaoEncontradaException();
        }

        if(repository.existsByChave(chavePixRequest.chave)){
            throw ChaveExistenteException();
        }

        val chavepix = chavePixRequest.toChavePix(consultar.body().toConta())

        val responsebcb = bcbClient.cadastrarChave(
            CreatePixKeyRequest(chavepix)
        )

        if(responsebcb.status != HttpStatus.CREATED){
            throw ChaveInvalidaException("Erro ao cadastrar no banco central");
        }

        chavepix.atualizarChave(responsebcb.body().key)

        return repository.save(chavepix)
    }
}
