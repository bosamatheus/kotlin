package com.github.bosamatheus.bank.controller

import com.github.bosamatheus.bank.model.Account
import com.github.bosamatheus.bank.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/accounts")
@RestController
class AccountController(private val service: AccountService) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@RequestBody account: Account): Account = service.create(account)

    @GetMapping
    fun getAll(): List<Account> = service.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Account> =
        service.getById(id).map {
            ResponseEntity.ok(it)
        }.orElse(ResponseEntity.notFound().build())

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody account: Account): ResponseEntity<Account> =
        service.update(id, account).map {
            ResponseEntity.ok(it)
        }.orElse(ResponseEntity.notFound().build())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            service.delete(id)
            ResponseEntity<Void>(HttpStatus.OK)
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }
}
