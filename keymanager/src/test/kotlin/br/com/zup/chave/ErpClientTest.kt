package br.com.zup.chave

import br.com.zup.KeyRequest
import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.client.ContaResponse
import br.com.zup.client.ErpClient
import br.com.zup.client.Instituicao
import br.com.zup.client.Titular
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ErpClientTest(val ggcliente: KeymanagerServiceGrpc.KeymanagerServiceBlockingStub) {
    @Inject
    lateinit var itauerp: ErpClient
    @Test
    fun erpclientmock(){
        Mockito.`when`(itauerp.getConta("CONTA_CORRENTE"
            ,"c56dfef4-7901-44fb-84e2-a2cefb157890")).thenReturn(
            HttpResponse.ok(
                ContaResponse(
                    "CONTA_CORRENTE",
                    Instituicao("itau","43242"),
                    "21312312",
                    "0001",
                    Titular("c56dfef4-7901-44fb-84e2-a2cefb157890",
                        "asddasd","12345678901")
                )
            ))

        val request: KeyRequest = KeyRequest.newBuilder().setChave("09871154488")
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CPF).setTipoConta(TipoConta.CONTA_CORRENTE).build()
        val tested = ggcliente.send(request)
        Assertions.assertNotNull(tested)
    }

    @MockBean(ErpClient::class)
    fun itauClient(): ErpClient? {
        return Mockito.mock(ErpClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerServiceGrpc.KeymanagerServiceBlockingStub? {
            return KeymanagerServiceGrpc.newBlockingStub(channel)
        }
    }
}