package br.com.zup.chave

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
internal class TipoChaveTest {

    @Test
    fun validacpf(){
        with(TipoChave.CPF){
            assertTrue(TipoChave.CPF.isValid("09852279893"))
        }
    }

    @Test
    fun invalidacpf(){
        with(TipoChave.CPF){
            assertFalse(
                TipoChave.CPF.isValid("0985227989333333")
            )
            assertFalse(
                TipoChave.CPF.isValid(null)
            )
            assertFalse(
                TipoChave.CPF.isValid("")
            )
        }
    }

    @Test
    fun validacelular(){
        with(TipoChave.CELULAR){
            assertTrue(TipoChave.CELULAR.isValid("+5585988714077"))
        }
    }

    @Test
    fun invalidacelular(){
        with(TipoChave.CELULAR){
            assertFalse(
                TipoChave.CELULAR.isValid("0985227989333333")
            )
            assertFalse(
                TipoChave.CELULAR.isValid(null)
            )
            assertFalse(
                TipoChave.CELULAR.isValid("")
            )
        }
    }

    @Test
    fun validaemail(){
        with(TipoChave.EMAIL){
            assertTrue(TipoChave.EMAIL.isValid("teste@email.com"))
        }
    }

    @Test
    fun invalidaemail(){
        with(TipoChave.EMAIL){
            assertFalse(
                TipoChave.EMAIL.isValid("0985227989333333")
            )
            assertFalse(
                TipoChave.EMAIL.isValid(null)
            )
            assertFalse(
                TipoChave.EMAIL.isValid("")
            )
        }
    }

    @Test
    fun validaaleatoria(){
        with(TipoChave.RANDOM){
            assertTrue(TipoChave.RANDOM.isValid(UUID.randomUUID().toString()))
        }
    }

    @Test
    fun invalidaUUID(){
        with(TipoChave.RANDOM){
            assertFalse(
                TipoChave.RANDOM.isValid(null)
            )
            assertFalse(
                TipoChave.RANDOM.isValid("")
            )
        }
    }
}