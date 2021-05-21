package br.com.zup.chave

import br.com.zup.KeymanagerRemoveServiceGrpc
import br.com.zup.RemoveKeyRequest
import br.com.zup.client.BcbClient
import br.com.zup.client.DeletePixKeyRequest
import br.com.zup.client.ErpClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.mockito.Mockito

@MicronautTest(transactional = false)
class RemoveChaveEndpointTest(val gweClient:KeymanagerRemoveServiceGrpc.KeymanagerRemoveServiceBlockingStub) {

    @Inject
    lateinit var repository:ChavePixRepository

    @Inject
    lateinit var bcbClient: BcbClient

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
                ,istituicao = "itau",nomeTitular = "fulano","09822922992","123123")
        )
    }

    @Test
    fun chaveexistente(){
        val chave_salva = repository.save(chave)
        val request =
           RemoveKeyRequest
                .newBuilder()
                .setPixId(chave_salva.id.toString())
                .setClientId(chave_salva.clientId).build()

        Mockito.`when`(bcbClient.deletarChave
            (chave_salva.chave, DeletePixKeyRequest(chave_salva))).thenReturn(HttpResponse.ok())

        val response = gweClient.remove(request)
        assertNotNull(response)
        assertEquals(response.message,"sucesso")
    }

    @Test
    fun chavenaoencontrada(){
        val chave2 = ChavePix(
            tipoChave = TipoChave.CELULAR,
            tipoConta = TipoConta.CONTA_CORRENTE,
            chave = "+5585988714066",
            clientId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            conta = Conta(agencia = "1111",numero="11111"
                ,istituicao = "itau",nomeTitular = "fulano","09822922992","123123")
        )

        Mockito.`when`(bcbClient.deletarChave
            ("+5585988714066", DeletePixKeyRequest(chave2))).thenReturn(HttpResponse.notFound())

        val request =
            RemoveKeyRequest
                .newBuilder()
                .setPixId("458734793")
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

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class Clients{
        @Singleton
        fun RemoveblockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRemoveServiceGrpc.KeymanagerRemoveServiceBlockingStub{
            return KeymanagerRemoveServiceGrpc.newBlockingStub(channel)
        }
    }
}