package br.com.zup.client

import br.com.zup.ConsultaChaveRequest
import br.com.zup.ConsultaChaveResponse
import br.com.zup.chave.ChavePix
import com.google.protobuf.Timestamp
import br.com.zup.chave.TipoChave
import br.com.zup.chave.TipoConta
import java.time.LocalDateTime

class PixKeyDetailsResponse(
    val keyType:String,
    val key:String,
    val bankAccount:BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
    constructor(chave: ChavePix):
            this(
                owner = Owner(
                name = chave.conta.nomeTitular,
                taxIdNumber = chave.conta.cpfTitular),
                bankAccount = BankAccount(
                    participant = chave.conta.ispb,
                    branch = chave.conta.agencia,
                    accountNumber = chave.conta.numero,
                    accountType = chave.tipoConta.get_value(),
                ),
                key = chave.chave,
                keyType = chave.tipoChave.get_value(),
                createdAt = chave.criadoEm
            )

    fun convert(request: ConsultaChaveRequest): ConsultaChaveResponse {
        return ConsultaChaveResponse.newBuilder()
            .setPixId(request.pixId.pixId)
            .setClientId(request.pixId.clientId)
            .setChave(ConsultaChaveResponse.ChaveResponse.newBuilder()
                .setChave(key)
                .setTipo(TipoChave.getEnum(keyType).toString())
                .setConta(ConsultaChaveResponse.ContaResponse.newBuilder()
                    .setAgencia(bankAccount.branch)
                    .setCpfTitular(owner.taxIdNumber)
                    .setInstituicao(bankAccount.participant)
                    .setNomeTitular(owner.name)
                    .setNumero(bankAccount.accountNumber)
                    .setTipo(TipoConta.getEnum(bankAccount.accountType).toString())
                ).setCriadoEm(Timestamp.newBuilder().setNanos(createdAt.nano).setSeconds(createdAt.second.toLong())))
            .build()
    }
}
