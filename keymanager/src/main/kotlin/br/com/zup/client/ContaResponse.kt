package br.com.zup.client

import br.com.zup.chave.Conta
import io.micronaut.core.annotation.Introspected

@Introspected
data class ContaResponse(val tipo:String,
                    val instituicao:Instituicao,
                    val numero:String,
                    val agencia:String,
                    val titular: Titular) {
    fun toConta():Conta{
        return Conta(agencia,numero,instituicao.nome,titular.nome,titular.cpf,instituicao.ispb)
    }
}

@Introspected
data class Instituicao(val nome:String,val ispb:String) {
}
@Introspected
data class Titular(val id:String,val nome:String,val cpf:String) {
}
