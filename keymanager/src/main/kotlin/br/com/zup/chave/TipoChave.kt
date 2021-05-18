package br.com.zup.chave

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

enum class TipoChave(s: String) {
    RANDOM("RANDOM"){
        override fun isValid(chave: String?): Boolean {
            return true;
        }
    },CELULAR("CELULAR") {
        override fun isValid(@NotBlank chave: String?): Boolean {
            if (chave != null) {
                return chave.matches(Regex("^\\+[1-9][0-9]\\d{1,14}\$"))
            }else{
                return false;
            }
        }
    },EMAIL("EMAIL") {
        override fun isValid(@NotBlank @Email chave: String?): Boolean {
            return true;
        }
    },CPF("CPF") {
        override fun isValid(@NotBlank chave: String?): Boolean {
            if (chave != null) {
                return chave.matches(Regex("^[0-9]{11}\$"))
            }
            return false;
        }
    };
    abstract fun isValid(chave:String?):Boolean
}