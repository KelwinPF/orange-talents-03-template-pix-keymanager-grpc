package br.com.zup.chave
import br.com.zup.configuration.ChaveInvalidaException
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class ChavePixRequest(
    @field:NotBlank val clientId:String = "",
    @field:NotBlank @Size(max= 77) val chave:String = "",
    @field:NotBlank val tipoConta:String = "",
    @field:NotBlank val tipoChave:String = "",

    ){

    fun toChavePix(conta: Conta):ChavePix{

        if(!TipoChave.valueOf(tipoChave).isValid(chave)){
            throw ChaveInvalidaException();
        };

        return ChavePix(clientId,chave,
            TipoChave.valueOf(tipoChave),
            TipoConta.valueOf(tipoConta),
            conta)
    }

}

