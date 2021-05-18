package br.com.zup.chave

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name="chaves_pix")
data class ChavePix(
    val clientId:String,
    @field:Column(nullable = false)
    val chave:String,
    @field:Enumerated(EnumType.STRING)
    val tipoChave: TipoChave,
    @field:Enumerated(EnumType.STRING)
    val tipoConta: TipoConta,
    @Embedded
    val conta: Conta
){
    @Id
    @GeneratedValue
    var id:Long? =null
    val criadoEm: LocalDateTime = LocalDateTime.now()
}
