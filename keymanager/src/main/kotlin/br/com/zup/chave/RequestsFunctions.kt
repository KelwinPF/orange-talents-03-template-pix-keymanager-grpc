import br.com.zup.RemoveKeyRequest
import br.com.zup.chave.RemoveChavePixRequest

fun RemoveKeyRequest.convert():RemoveChavePixRequest{
    return RemoveChavePixRequest(this.pixId,this.clientId)
}