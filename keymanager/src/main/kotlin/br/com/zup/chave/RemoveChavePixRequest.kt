package br.com.zup.chave

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemoveChavePixRequest(@field:NotBlank val pixId:String,
@field:NotBlank val clientId:String) {

}
