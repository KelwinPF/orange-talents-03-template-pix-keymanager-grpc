package br.com.zup.client

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client("http://localhost:9091/api/v1")
open interface ErpClient {

    @Get("/clientes/{clienteId}/contas")
    open fun getConta(@QueryValue tipo:String,@PathVariable clienteId:String): HttpResponse<ContaResponse>
}