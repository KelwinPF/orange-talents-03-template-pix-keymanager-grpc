package br.com.zup.chave

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name="chaves_pix")
data class ChavePix(
    @field:Column(nullable = false)
    val clientId:String,
    @field:Column(nullable = false,unique = true)
    var chave:String,
    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    val tipoChave: TipoChave,
    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    val tipoConta: TipoConta,
    @Embedded
    val conta: Conta
){
    @Id
    @GeneratedValue
    var id:Long? =null
    val criadoEm: LocalDateTime = LocalDateTime.now()

    fun atualizarChave(chaveBcb:String){
        this.chave = chaveBcb
    }
}
