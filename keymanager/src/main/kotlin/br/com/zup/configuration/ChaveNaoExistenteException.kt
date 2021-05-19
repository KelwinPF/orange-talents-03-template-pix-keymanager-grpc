package br.com.zup.configuration

class ChaveNaoExistenteException(message:String? = "chave nao encontrada"): RuntimeException(message) {
}