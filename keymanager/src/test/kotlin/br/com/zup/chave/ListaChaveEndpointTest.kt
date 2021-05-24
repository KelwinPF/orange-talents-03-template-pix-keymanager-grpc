package br.com.zup.chave

import br.com.zup.KeymanagerListaServiceGrpc
import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.ListaKeyRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
internal class ListaChaveEndpointTest(val repository: ChavePixRepository,
                                      val grpgcCliente: KeymanagerListaServiceGrpc.KeymanagerListaServiceBlockingStub)
{
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
    fun limpaUp(){
        repository.deleteAll()
    }

    @Test
    fun develistartodasaschavesdocliente(){
        val response = grpgcCliente.lista(ListaKeyRequest.newBuilder().setClienteId("1").build())
        with(response.chavesList){
            assertEquals(3,this.size)
        }
    }

    @Test
    fun naodevelistarquandonaopossuirchave(){
        val response = grpgcCliente.lista(ListaKeyRequest.newBuilder().setClienteId("3").build())
        assertEquals(0,response.chavesCount)
    }

    @Test
    fun naodevelistarquandoclienteinvalido(){
        val thrown = assertThrows<StatusRuntimeException> {
            grpgcCliente.lista(ListaKeyRequest.newBuilder().setClienteId("").build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("ClienteId não pode ser vazio ou nulo",status.description)
        }
    }

    @Factory
    class Clients{
        @Bean
        fun listablockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerListaServiceGrpc.KeymanagerListaServiceBlockingStub{
            return KeymanagerListaServiceGrpc.newBlockingStub(channel)
        }
    }

}