import br.com.zup.ConsultaChaveRequest
import br.com.zup.KeyRequest
import br.com.zup.RemoveKeyRequest
import br.com.zup.chave.ChavePixRequest
import br.com.zup.chave.Filtro
import br.com.zup.chave.RemoveChavePixRequest
import br.com.zup.chave.TipoChave
import io.micronaut.validation.validator.Validator
import java.util.*
import javax.validation.ConstraintViolationException

fun RemoveKeyRequest.convert():RemoveChavePixRequest{
    return RemoveChavePixRequest(this.pixId,this.clientId)
}

fun KeyRequest.convert(request: KeyRequest): ChavePixRequest {

    var chave: String? = request.chave;

    if(TipoChave.valueOf(request.tipoChave.toString()) == TipoChave.RANDOM){
        chave = UUID.randomUUID().toString();
    }

    return ChavePixRequest(this.idCliente,chave!!,
        this.tipoConta.toString(),this.tipoChave.toString())
}

fun ConsultaChaveRequest.convert(validator: Validator): Filtro {
    val filtro  = when(filtroCase){
        ConsultaChaveRequest.FiltroCase.PIXID -> pixId.let {
            Filtro.PelaKey(clientId = it.clientId,pixId = it.pixId)
        }
        ConsultaChaveRequest.FiltroCase.CHAVE -> Filtro.PeloClient(chave)
        ConsultaChaveRequest.FiltroCase.FILTRO_NOT_SET -> Filtro.Invalido()
    }

    val violation = validator.validate(filtro);

    if(violation.isNotEmpty()){
        throw ConstraintViolationException(violation)
    }

    return filtro;
}