package br.com.zup.chave

import br.com.zup.KeyRequest
import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.client.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CadastroChaveEndpointTest(val grpcClient:KeymanagerServiceGrpc.KeymanagerServiceBlockingStub){
    @Inject
    lateinit var repository:ChavePixRepository

    @Inject
    lateinit var erpClient: ErpClient;

    @Inject
    lateinit var bcbClient: BcbClient;

    @BeforeEach
    internal fun setUp(){
        repository.deleteAll()
    }

    @Test
    fun cpfvalidorequest(){
        Mockito.`when`(
            erpClient.getConta(
                clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipo = "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(ContaResponse(
            "CONTA_CORRENTE",
            Instituicao("ITAÚ UNIBANCO S.A.","60701190"),
            "291900","0001", Titular("c56dfef4-7901-44fb-84e2-a2cefb157890"
            ,"Rafael M C Ponte","02467781054")
        )))

        Mockito.`when`(bcbClient.cadastrarChave(
            CreatePixKeyRequest(
                ChavePix("c56dfef4-7901-44fb-84e2-a2cefb157890"
                    ,"09422153311",
                    TipoChave.CPF,
                    TipoConta.CONTA_CORRENTE,
                    Conta("0001",
                        "291900",
                        "ITAÚ UNIBANCO S.A.",
                        "Rafael M C Ponte","02467781054","60701190")))))
            .thenReturn(HttpResponse.created(CreatePixKeyResponse(
                "CPF","09422153311", BankAccount("02467781054",
                    "60701190","291900","CACC"),
                Owner(name = "Rafael M C Ponte",taxIdNumber = "291900"), LocalDateTime.now()
            )))

        val response = grpcClient.send(
            KeyRequest.newBuilder().
            setChave("09422153311").
            setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoConta(br.com.zup.TipoConta.CONTA_CORRENTE)
                .setTipoChave(br.com.zup.TipoChave.CPF).build())
        assertNotNull(response)
        assertEquals(response.message,"sucesso")
    }

    @Test
    fun cpfchaverepetida(){
        Mockito.`when`(
            erpClient.getConta(
                clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipo = "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(ContaResponse(
            "CONTA_CORRENTE",
            Instituicao("ITAÚ UNIBANCO S.A.","60701190"),
            "291900","0001", Titular("c56dfef4-7901-44fb-84e2-a2cefb157890"
                ,"Rafael M C Ponte","02467781054")
        )))

        val key = ChavePix("c56dfef4-7901-44fb-84e2-a2cefb157890"
            ,"09422153311",
            TipoChave.CPF,
            TipoConta.CONTA_CORRENTE,
            Conta("0001",
                "291900",
                "ITAÚ UNIBANCO S.A.",
                "Rafael M C Ponte","02467781054","60701190"))

        Mockito.`when`(bcbClient.cadastrarChave(
            CreatePixKeyRequest(key)))
            .thenReturn(HttpResponse.created(CreatePixKeyResponse(
                "CPF","09422153311", BankAccount("02467781054",
                    "60701190","291900","CACC"),
                Owner(name = "Rafael M C Ponte",taxIdNumber = "291900"), LocalDateTime.now()
            )))

        repository.save(key)

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.send(KeyRequest.newBuilder().
                setChave("09422153311").
                setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                    .setTipoConta(br.com.zup.TipoConta.CONTA_CORRENTE)
                    .setTipoChave(br.com.zup.TipoChave.CPF).build())
        }
        with(thrown){
            assertEquals(Status.ALREADY_EXISTS.code,status.code)
            assertEquals("chave ja existente",status.description)
        }
    }

    @Test
    fun containexistentetest(){

        Mockito.`when`(
            erpClient.getConta(
                clienteId = "nao existente",
                tipo = "asdasdasdad")
        ).thenReturn(HttpResponse.notFound())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.send(KeyRequest.newBuilder().
            setChave("09422153311").
            setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoConta(br.com.zup.TipoConta.CONTA_CORRENTE)
                .setTipoChave(br.com.zup.TipoChave.CPF).build())
        }
        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("conta nao encontrada",status.description)
        }
    }

    @Test
    fun testeerrobcb(){
        Mockito.`when`(
            erpClient.getConta(
                clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipo = "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(ContaResponse(
            "CONTA_CORRENTE",
            Instituicao("ITAÚ UNIBANCO S.A.","60701190"),
            "291900","0001", Titular("c56dfef4-7901-44fb-84e2-a2cefb157890"
                ,"Rafael M C Ponte","02467781054")
        )))

        val key = ChavePix("c56dfef4-7901-44fb-84e2-a2cefb157890"
            ,"09422153311",
            TipoChave.CPF,
            TipoConta.CONTA_CORRENTE,
            Conta("0001",
                "291900",
                "ITAÚ UNIBANCO S.A.",
                "Rafael M C Ponte","02467781054","60701190"))

        Mockito.`when`(bcbClient.cadastrarChave(
            CreatePixKeyRequest(key)))
            .thenReturn(HttpResponse.badRequest())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.send(KeyRequest.newBuilder().
            setChave("09422153311").
            setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoConta(br.com.zup.TipoConta.CONTA_CORRENTE)
                .setTipoChave(br.com.zup.TipoChave.CPF).build())
        }
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("Erro ao cadastrar no banco central",status.description)
        }
    }

    @Test
    fun emailinvalidorequest(){
        Mockito.`when`(
            erpClient.getConta(
                clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipo = "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(ContaResponse(
            "CONTA_CORRENTE",
            Instituicao("ITAÚ UNIBANCO S.A.","60701190"),
            "291900","0001", Titular("c56dfef4-7901-44fb-84e2-a2cefb157890"
                ,"Rafael M C Ponte","02467781054")
        )))

        Mockito.`when`(bcbClient.cadastrarChave(
            CreatePixKeyRequest(
                ChavePix("c56dfef4-7901-44fb-84e2-a2cefb157890"
                    ,"emailerrado",
                    TipoChave.EMAIL,
                    TipoConta.CONTA_CORRENTE,
                    Conta("0001",
                        "291900",
                        "ITAÚ UNIBANCO S.A.",
                        "Rafael M C Ponte","02467781054","60701190")))))
            .thenReturn(HttpResponse.created(CreatePixKeyResponse(
                "EMAIL","094", BankAccount("02467781054",
                    "60701190","291900","CACC"),
                Owner(name = "Rafael M C Ponte",taxIdNumber = "291900"), LocalDateTime.now()
            )))

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.send(KeyRequest.newBuilder().
            setChave("emailerrado").
            setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoConta(br.com.zup.TipoConta.CONTA_CORRENTE)
                .setTipoChave(br.com.zup.TipoChave.EMAIL).build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave invalida",status.description)
        }
    }

    @Test
    fun celularinvalidorequest(){
        Mockito.`when`(
            erpClient.getConta(
                clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipo = "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(ContaResponse(
            "CONTA_CORRENTE",
            Instituicao("ITAÚ UNIBANCO S.A.","60701190"),
            "291900","0001", Titular("c56dfef4-7901-44fb-84e2-a2cefb157890"
                ,"Rafael M C Ponte","02467781054")
        )))

        Mockito.`when`(bcbClient.cadastrarChave(
            CreatePixKeyRequest(
                ChavePix("c56dfef4-7901-44fb-84e2-a2cefb157890"
                    ,"13123",
                    TipoChave.CELULAR,
                    TipoConta.CONTA_CORRENTE,
                    Conta("0001",
                        "291900",
                        "ITAÚ UNIBANCO S.A.",
                        "Rafael M C Ponte","02467781054","60701190")))))
            .thenReturn(HttpResponse.created(CreatePixKeyResponse(
                "EMAIL","094", BankAccount("02467781054",
                    "60701190","291900","CACC"),
                Owner(name = "Rafael M C Ponte",taxIdNumber = "291900"), LocalDateTime.now()
            )))

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.send(KeyRequest.newBuilder().
            setChave("13123").
            setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoConta(br.com.zup.TipoConta.CONTA_CORRENTE)
                .setTipoChave(br.com.zup.TipoChave.CELULAR).build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave invalida",status.description)
        }
    }

    @Test
    fun cpfinvalidorequest(){
        Mockito.`when`(
            erpClient.getConta(
                clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipo = "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(ContaResponse(
            "CONTA_CORRENTE",
            Instituicao("ITAÚ UNIBANCO S.A.","60701190"),
            "291900","0001", Titular("c56dfef4-7901-44fb-84e2-a2cefb157890"
                ,"Rafael M C Ponte","094")
        )))

        Mockito.`when`(bcbClient.cadastrarChave(
            CreatePixKeyRequest(
                ChavePix("c56dfef4-7901-44fb-84e2-a2cefb157890"
                    ,"094",
                    TipoChave.CPF,
                    TipoConta.CONTA_CORRENTE,
                    Conta("0001",
                        "291900",
                        "ITAÚ UNIBANCO S.A.",
                        "Rafael M C Ponte","02467781054","60701190")))))
            .thenReturn(HttpResponse.created(CreatePixKeyResponse(
                "CPF","094", BankAccount("02467781054",
                    "60701190","291900","CACC"),
                Owner(name = "Rafael M C Ponte",taxIdNumber = "291900"), LocalDateTime.now()
            )))

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.send(KeyRequest.newBuilder().
            setChave("094").
            setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoConta(br.com.zup.TipoConta.CONTA_CORRENTE)
                .setTipoChave(br.com.zup.TipoChave.CPF).build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave invalida",status.description)
        }
    }

    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerServiceGrpc.KeymanagerServiceBlockingStub{
            return KeymanagerServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ErpClient::class)
    fun erpClient(): ErpClient?{
        return Mockito.mock(ErpClient::class.java)
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

}