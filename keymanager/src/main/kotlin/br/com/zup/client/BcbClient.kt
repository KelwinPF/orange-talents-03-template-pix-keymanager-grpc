package br.com.zup.client

import br.com.zup.chave.ChavePix
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import java.time.LocalDateTime

@Client("http://localhost:8082/api/v1/pix/keys")
interface BcbClient {
    @Post
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun cadastrarChave(@Body request: CreatePixKeyRequest):HttpResponse<CreatePixKeyResponse>

    @Delete(value = "/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun deletarChave(@PathVariable key:String,
                     @Body request: DeletePixKeyRequest):HttpResponse<DeletePixKeyResponse>
}

data class DeletePixKeyRequest(
    val key:String,
    val participant: String) {

    constructor(chave: ChavePix):
            this(
                key = chave.chave,
                participant = chave.conta.ispb
            )
}

class DeletePixKeyResponse(
    val key:String,
    val participant: String,
    val deletedAt:LocalDateTime){

}

data class CreatePixKeyRequest(
    val keyType:String,
    val key:String,
    val bankAccount:BankAccount,
    val owner:Owner){

    constructor(chavepix: ChavePix):
            this(
                owner = Owner(
                    name = chavepix.conta.nomeTitular,
                    taxIdNumber = chavepix.conta.cpfTitular),
                bankAccount = BankAccount(
                    participant = chavepix.conta.ispb,
                    branch = chavepix.conta.agencia,
                    accountNumber = chavepix.conta.numero,
                    accountType = chavepix.tipoConta.get_value(),
                ),
                key = chavepix.chave,
                keyType = chavepix.tipoChave.get_value()
            )
}

data class Owner(
    val type:String = "NATURAL_PERSON",
    val name:String,
    val taxIdNumber:String
) {}

data class BankAccount(
    val participant:String,
    val branch:String,
    val accountNumber:String,
    val accountType: String
) {}

data class CreatePixKeyResponse(
    val keyType:String,
    val key:String,
    val bankAccount:BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
){

}

