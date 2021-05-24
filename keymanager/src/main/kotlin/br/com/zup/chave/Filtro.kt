package br.com.zup.chave

import br.com.zup.client.BankAccount
import br.com.zup.client.BcbClient
import br.com.zup.client.Owner
import br.com.zup.client.PixKeyDetailsResponse
import br.com.zup.configuration.ChaveNaoExistenteException
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.lang.IllegalArgumentException
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro{

    abstract fun filtra(repository:ChavePixRepository,bcbClient:BcbClient): PixKeyDetailsResponse;

    @Introspected
    data class PelaKey(@field:NotBlank val clientId:String,
                        @field:NotBlank val pixId:String):Filtro(){

        override fun filtra(repository: ChavePixRepository, bcbClient: BcbClient): PixKeyDetailsResponse {
            val chave = repository.findById(pixId.toLong());

            println("pesquisando no banco pela key")

            if(chave.isPresent && clientId.equals(chave.get().clientId)){
                return PixKeyDetailsResponse(chave.get())
            }
            throw ChaveNaoExistenteException();
        }

    }

    @Introspected
    data class PeloClient(@field:NotBlank @field:Size(max = 77) val chave:String):Filtro(){
        override fun filtra(repository: ChavePixRepository, bcbClient: BcbClient): PixKeyDetailsResponse {

            val chavepix:Optional<ChavePix> = repository.findByChave(chave);

            if(chavepix.isPresent){
                println("para microsservicos pegando do proprio banco")
                return PixKeyDetailsResponse(chavepix.get())
            }

            println("para microsservicos pegando do BCB")

            val consultar = bcbClient.consultarChave(chave)

            if(consultar?.status != HttpStatus.OK || consultar == null){
                throw ChaveNaoExistenteException("chave nao encontrada no bcb")
            }
            return consultar.body()
        }
    }

    @Introspected
    class Invalido():Filtro(){
        override fun filtra(repository: ChavePixRepository, bcbClient: BcbClient): PixKeyDetailsResponse {
            throw IllegalArgumentException("chave pix invalida ou nao informada")
        }
    }

}
