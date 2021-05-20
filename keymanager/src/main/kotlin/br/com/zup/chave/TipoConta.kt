package br.com.zup.chave

enum class TipoConta(val s: String) {
    CONTA_CORRENTE("CACC"),CONTA_POUPANCA("SVGS");

    fun get_value():String{
        return s;
    }
}