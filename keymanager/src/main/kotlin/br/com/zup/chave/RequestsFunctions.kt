import br.com.zup.KeyRequest
import br.com.zup.RemoveKeyRequest
import br.com.zup.chave.ChavePixRequest
import br.com.zup.chave.RemoveChavePixRequest
import br.com.zup.chave.TipoChave
import java.util.*

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