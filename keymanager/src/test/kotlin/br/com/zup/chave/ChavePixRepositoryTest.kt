package br.com.zup.chave

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject
import org.junit.jupiter.api.Assertions.*

@MicronautTest(transactional = false)
internal class ChavePixRepositoryTest {
    @Inject
    lateinit var repository: ChavePixRepository

    @BeforeEach
    fun setUp(){
        repository.deleteAll()
    }

    @Test
    fun testesave(){
        val chave_pix = ChavePix("5260263c-a3c1-4727-ae32-3bdb2538841b","09241554688",
            br.com.zup.chave.TipoChave.CPF,br.com.zup.chave.TipoConta.CONTA_CORRENTE,
            Conta("0001","21312312","teste","tituteste","09241554688","123123")
        )
        val chave: ChavePix = repository.save(chave_pix)
        assertNotNull(chave)
        println(chave.id)
        assertEquals(chave.chave,"09241554688")
        assertEquals(chave.id,1L)
        assertEquals(chave.clientId,"5260263c-a3c1-4727-ae32-3bdb2538841b")
    }

    @Test
    fun testExistsByChave(){
        val chave_pix = ChavePix("5260263c-a3c1-4727-ae32-3bdb2538841b","09241554688",
            br.com.zup.chave.TipoChave.CPF,br.com.zup.chave.TipoConta.CONTA_CORRENTE,
            Conta("0001","21312312","teste","tituteste","09241554688","123123")
        )
        val chave: ChavePix = repository.save(chave_pix)
        assertTrue(repository.existsByChave("09241554688"))
    }
}