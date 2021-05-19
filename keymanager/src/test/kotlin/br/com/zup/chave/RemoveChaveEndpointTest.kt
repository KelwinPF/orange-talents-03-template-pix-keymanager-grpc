package br.com.zup.chave

import br.com.zup.KeymanagerRemoveServiceGrpc
import br.com.zup.RemoveKeyRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

@MicronautTest(transactional = false)
class RemoveChaveEndpointTest(val gweClient:KeymanagerRemoveServiceGrpc.KeymanagerRemoveServiceBlockingStub) {
    @Inject
    lateinit var repository:ChavePixRepository
    lateinit var chave:ChavePix

    @BeforeEach
    internal fun setUp(){
        repository.deleteAll()

        chave = ChavePix(
            tipoChave = TipoChave.CELULAR,
            tipoConta = TipoConta.CONTA_CORRENTE,
            chave = "+5585988714077",
            clientId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            conta = Conta(agencia = "1111",numero="11111"
                ,istituicao = "itau",nomeTitular = "fulano","09822922992")
        )

    }

    @Test
    fun chaveexistente(){
        repository.save(chave)
        val request =
           RemoveKeyRequest
                .newBuilder()
                .setPixId("1")
                .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890").build()

        val response = gweClient.remove(request)
        assertNotNull(response)
        assertEquals(response.message,"sucesso")
    }

    @Test
    fun chavenaoencontrada(){

        val request =
            RemoveKeyRequest
                .newBuilder()
                .setPixId("5234")
                .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890").build()

        val thrown = assertThrows<StatusRuntimeException>{
            gweClient.remove(request);
        }
        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("chave nao encontrada",status.description)
        }
    }

    @Test
    fun clientnaoassociadoachave(){

        val request =
            RemoveKeyRequest
                .newBuilder()
                .setPixId("1")
                .setClientId("usuarioerrado").build()

        val thrown = assertThrows<StatusRuntimeException>{
            gweClient.remove(request);
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("chave nao encontrada",status.description)
        }
    }



    @Factory
    class Clients{
        @Singleton
        fun RemoveblockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRemoveServiceGrpc.KeymanagerRemoveServiceBlockingStub{
            return KeymanagerRemoveServiceGrpc.newBlockingStub(channel)
        }
    }
}