package br.com.zup.chave

import io.micronaut.validation.Validated
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

enum class TipoChave(s: String) {
    RANDOM("RANDOM"){
        override fun isValid(chave: String?): Boolean {
            if(chave.isNullOrBlank()){
                return false
            }
            return true;
        }
    },CELULAR("CELULAR") {
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
}