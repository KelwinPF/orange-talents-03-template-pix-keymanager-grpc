package br.com.zup.chave

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix,Long> {
    fun existsByChave(chave: String): Boolean
    fun findByChave(chave: String): Optional<ChavePix>
    fun findAllByClientId(clientId: String): List<ChavePix>
}