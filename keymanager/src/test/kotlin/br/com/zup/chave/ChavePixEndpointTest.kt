package br.com.zup.chave

import br.com.zup.KeyRequest
import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.client.ErpClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ChavePixEndpointTest(val grpcClient: KeymanagerServiceGrpc.KeymanagerServiceBlockingStub) {

    @Inject
    lateinit var repository: ChavePixRepository

    @Inject
    lateinit var itauerp: ErpClient

    @BeforeEach
    fun setUp(){
        repository.deleteAll()
    }

    @Test
    fun requestchavecpfvalida(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("09241554688")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.CPF).setTipoConta(TipoConta.CONTA_CORRENTE).build()
        val tested = grpcClient.send(request)
        assertNotNull(tested);
    }

    @Test
    fun requestchaverepetida(){
        val chave_pix = ChavePix("5260263c-a3c1-4727-ae32-3bdb2538841b","09241554688",
            br.com.zup.chave.TipoChave.CPF,br.com.zup.chave.TipoConta.CONTA_CORRENTE,
            Conta("0001","21312312","teste","tituteste","09241554688")
        )
        repository.save(chave_pix)
        val request: KeyRequest = KeyRequest.newBuilder().setChave("09241554688")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.CPF).setTipoConta(TipoConta.CONTA_CORRENTE).build()
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.send(request);
        }
        with(thrown){
            assertEquals(Status.ALREADY_EXISTS.code,status.code)
            assertEquals("chave ja existente",status.description)
        }
    }

    @Test
    fun requestchavecpfinvalida(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("09241554688123123123123")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.CPF).setTipoConta(TipoConta.CONTA_CORRENTE).build()
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.send(request);
        }
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave invalida",status.description)
        }
    }

    @Test
    fun requestchavealeatoria(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.RANDOM).setTipoConta(TipoConta.CONTA_CORRENTE).build()

        val tested = grpcClient.send(request);
        assertNotNull(tested);
    }


    @Test
    fun requestchavecelularvalido(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("+5585988714070")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.CELULAR).setTipoConta(TipoConta.CONTA_CORRENTE).build()

        val tested = grpcClient.send(request);
        assertNotNull(tested);
    }

    @Test
    fun requestchavecelularinvalida(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("09241554688123123123123")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.CELULAR).setTipoConta(TipoConta.CONTA_CORRENTE).build()
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.send(request);
        }
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave invalida",status.description)
        }
    }

    @Test
    fun requestchaveemailvalida(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("teste@email.com")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.EMAIL).setTipoConta(TipoConta.CONTA_CORRENTE).build()

        assertNotNull(grpcClient.send(request))
    }

    @Test
    fun requestchaveemailinvalida(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("testesaddsad")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.EMAIL).setTipoConta(TipoConta.CONTA_CORRENTE).build()
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.send(request);
        }
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave invalida",status.description)
        }
    }

    @Test
    fun requestcontanaoencontrada(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("teste@email.com")
            .setIdCliente("5260263c-a3adsadasdasdasd")
            .setTipoChave(TipoChave.EMAIL).setTipoConta(TipoConta.CONTA_CORRENTE).build()
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.send(request);
        }
        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("conta nao encontrada",status.description)
        }
    }

    @Test
    fun requestchavedesconhecida(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("teste@email.com")
            .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.UNKNOWN_CHAVE).setTipoConta(TipoConta.CONTA_CORRENTE).build()
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.send(request);
        }
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("No enum constant br.com.zup.chave.TipoChave.UNKNOWN_CHAVE",status.description)
        }
    }

    @Test
    fun requestcontadesconhecida(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("teste@email.com")
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.EMAIL).setTipoConta(TipoConta.UNKNOWN_CONTA).build()
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.send(request);
        }
        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("conta nao encontrada",status.description)
        }
    }

    @Test
    fun requestcontachavedesconhecida(){
        val request: KeyRequest = KeyRequest.newBuilder().setChave("teste@email.com")
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.UNKNOWN_CHAVE).setTipoConta(TipoConta.UNKNOWN_CONTA).build()
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.send(request);
        }
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("No enum constant br.com.zup.chave.TipoChave.UNKNOWN_CHAVE",status.description)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerServiceGrpc.KeymanagerServiceBlockingStub? {
            return KeymanagerServiceGrpc.newBlockingStub(channel)
        }
    }
}