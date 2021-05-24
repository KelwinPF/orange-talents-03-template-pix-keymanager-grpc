package br.com.zup.chave

import br.com.zup.ConsultaChaveRequest
import br.com.zup.KeymanagerConsultaChaveServiceGrpc
import br.com.zup.KeymanagerListaServiceGrpc
import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.client.BcbClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.After
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CarregaChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcCliente:KeymanagerConsultaChaveServiceGrpc.KeymanagerConsultaChaveServiceBlockingStub) {

    @Inject
    lateinit var bcbClient: BcbClient

    @BeforeEach
    fun setUp(){
        val conta = Conta(
            agencia = "1",
            numero = "1",
            istituicao = "teste",
            nomeTitular = "testado",
            cpfTitular = "09871143489",
            ispb = "1234")
        repository.save(ChavePix("1","09871143489",TipoChave.CPF,TipoConta.CONTA_CORRENTE,conta))
        repository.save(ChavePix("2","+5585938714098",TipoChave.CELULAR,TipoConta.CONTA_CORRENTE,conta))
        repository.save(ChavePix("1","teste@email.com",TipoChave.EMAIL,TipoConta.CONTA_CORRENTE,conta))
        repository.save(ChavePix("1","RANDOM",TipoChave.RANDOM,TipoConta.CONTA_CORRENTE,conta))
    }

    @AfterEach
    fun cleanUp(){
        repository.deleteAll()
    }

    @Test
    fun devecarregarchaveporPixIdEClientId(){
        val chaveexistente = repository.findByChave("+5585938714098").get()
        val response = grpcCliente.consulta(ConsultaChaveRequest.newBuilder().setPixId(
            ConsultaChaveRequest.FiltroPorPixId.newBuilder()
                .setPixId(chaveexistente.id.toString())
                .setClientId(chaveexistente.clientId.toString()).build()).build())
        with(response){
            assertEquals(chaveexistente.id.toString(),this.pixId)
            assertEquals(chaveexistente.clientId.toString(),this.clientId)
            assertEquals(chaveexistente.chave,this.chave.chave)
        }
    }
    @Test
    fun naodevecarregarchaveporpixideclientidquandofiltroinvalido(){
        val thrown = assertThrows<StatusRuntimeException> {
            grpcCliente.consulta(ConsultaChaveRequest.newBuilder().setPixId(
                ConsultaChaveRequest.FiltroPorPixId.newBuilder()
                    .setPixId("932482394823948")
                    .setClientId("id").build()).build())
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("chave nao encontrada",status.description)
        }
    }

    @Test
    fun devecarregarporchavequandoexistirnobanco(){
        val chaveexistente = repository.findByChave("teste@email.com").get()
        val response = grpcCliente.consulta(ConsultaChaveRequest.newBuilder().setChave("teste@email.com").build())
        with(response){
            assertEquals("",this.pixId)
            assertEquals("",this.clientId)
            assertEquals(chaveexistente.chave,this.chave.chave)
        }
    }

    @Test
    fun naodevecarregarporchavequandoregistronaoexistirnobcb(){
        Mockito.`when`(bcbClient.consultarChave(key = "naoexiste")).thenReturn(HttpResponse.notFound());
        val thrown = assertThrows<StatusRuntimeException> {
            grpcCliente.consulta(ConsultaChaveRequest.newBuilder().setChave("naoexiste").build())
        }
        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("chave nao encontrada no bcb",status.description)
        }
    }

    @Test
    fun naodevecarregarchaveporchavecomfiltroinvalido(){
        val thrown = assertThrows<StatusRuntimeException> {
            grpcCliente.consulta(ConsultaChaveRequest.newBuilder().setChave("").build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave: não deve estar em branco",status.description)
        }

    }

    @Test
    fun naodevecarregarchavequandofiltroinvalido(){
        val thrown = assertThrows<StatusRuntimeException> {
            grpcCliente.consulta(ConsultaChaveRequest.newBuilder().build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("chave pix invalida ou nao informada",status.description)
        }
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient?{
        return mock(BcbClient::class.java)
    }

    @Factory
    class Clients{
        @Bean
        fun listblockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):KeymanagerConsultaChaveServiceGrpc.KeymanagerConsultaChaveServiceBlockingStub{
            return KeymanagerConsultaChaveServiceGrpc.newBlockingStub(channel)
        }
    }

}