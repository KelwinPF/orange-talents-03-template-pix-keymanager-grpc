package br.com.zup.chave

import br.com.zup.configuration.ChaveInvalidaException
import io.micronaut.validation.Validated
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

enum class TipoChave(val s: String) {
    RANDOM("RANDOM"){
        override fun isValid(chave: String?): Boolean {
            if(chave.isNullOrBlank()){
                return false
            }
            return true;
        }
    },CELULAR("PHONE") {
        override fun isValid(chave: String?): Boolean {
            if (chave != null) {
                return chave.matches(Regex("^\\+[1-9][0-9]\\d{1,14}\$"))
            }else{
                return false;
            }
        }
    },EMAIL("EMAIL") {
        override fun isValid(chave: String?): Boolean {
            if (chave != null) {
                return chave.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$".toRegex())
            }
            return false;
        }
    },CPF("CPF") {
        override fun isValid(chave: String?): Boolean {
            if (chave != null) {
                return chave.matches(Regex("^[0-9]{11}\$"))
            }
            return false;
        }
    };
    abstract fun isValid(chave:String?):Boolean

    fun get_value():String{
        return s;
    }

    companion object {
        fun getEnum(value:String): TipoChave{
            for(t:TipoChave in values()){
                if(value.equals(t.get_value())){
                    return t;
                }
            }
            throw ChaveInvalidaException("enum invalido")
        }
    }
}