package br.com.zup.chave

import javax.persistence.Embeddable

@Embeddable
class Conta(
    val agencia:String,
    val numero:String,
    val istituicao: String,
    val nomeTitular: String,
    val cpfTitular:String
)
