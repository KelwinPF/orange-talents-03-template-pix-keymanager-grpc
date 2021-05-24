package br.com.zup.chave

import br.com.zup.configuration.ChaveInvalidaException

enum class TipoConta(val s: String) {
    CONTA_CORRENTE("CACC"),CONTA_POUPANCA("SVGS");

    fun get_value():String{
        return s;
    }

    companion object {
        fun getEnum(value:String):TipoConta{
            for(t:TipoConta in values()){
                if(value.equals(t.get_value())){
                    return t;
                }
            }
            throw ChaveInvalidaException("enum invalido")
        }
    }
}