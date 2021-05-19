package br.com.zup.chave

import br.com.zup.configuration.ChaveNaoExistenteException
import br.com.zup.configuration.ContaNaoEncontradaException
import br.com.zup.configuration.ErrorHandler
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService(@Inject private val repository:ChavePixRepository) {

    @Transactional
    fun remove(@Valid request: RemoveChavePixRequest):ChavePix{

        val chave:Optional<ChavePix> = repository.findById(request.pixId.toLong())

        if(chave.isPresent && request.clientId.equals(chave.get().clientId)){
            repository.delete(chave.get())
            return chave.get()
        }

        throw ChaveNaoExistenteException()

    }
}
