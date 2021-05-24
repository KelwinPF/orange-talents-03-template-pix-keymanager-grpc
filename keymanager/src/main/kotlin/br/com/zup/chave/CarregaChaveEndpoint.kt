package br.com.zup.chave

import br.com.zup.*
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.client.BcbClient
import br.com.zup.configuration.ErrorHandler
import com.google.protobuf.Timestamp
import convert
import io.grpc.stub.StreamObserver
import io.micronaut.validation.validator.Validator
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class CarregaChaveEndpoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val bcbClient: BcbClient,
    @Inject private val validator: Validator
):
    KeymanagerConsultaChaveServiceGrpc.KeymanagerConsultaChaveServiceImplBase() {

    override fun consulta(
        request: ConsultaChaveRequest,
        responseObserver: StreamObserver<ConsultaChaveResponse>)
    {
        val filtro = request.convert(validator);
        val chaveinfo = filtro.filtra(repository,bcbClient)

        responseObserver.onNext(chaveinfo.convert(request))
        responseObserver.onCompleted()
    }

}