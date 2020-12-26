package com.github.bosamatheus.bank.service

import com.github.bosamatheus.bank.exception.AccountNotFoundException
import com.github.bosamatheus.bank.model.Account
import com.github.bosamatheus.bank.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.util.*

@Service
class AccountServiceImpl(private val repository: AccountRepository) : AccountService {

    override fun create(account: Account): Account {
        validate(account)
        return repository.save(account)
    }

    override fun getAll(): List<Account> {
        return repository.findAll()
    }

    override fun getById(id: Long): Optional<Account> {
        return repository.findById(id)
    }

    override fun update(id: Long, account: Account): Optional<Account> {
        validate(account)
        val optional = getById(id)
        if (optional.isEmpty) Optional.empty<Account>()
        return optional.map {
            val accountToUpdate = it.copy(
                name = account.name,
                document = account.document,
                phone = account.phone
            )
            repository.save(accountToUpdate)
        }
    }

    override fun delete(id: Long) {
        getById(id).map {
            repository.delete(it)
        }.orElseThrow { throw AccountNotFoundException("Account not found for ID $id") }
    }

    private fun validate(account: Account) {
        Assert.hasLength(account.name, "[name] cannot be empty")
        Assert.isTrue(account.name.length >= 5, "[name] should have at least 5 characters")

        Assert.hasLength(account.document, "[document] cannot be empty")
        Assert.isTrue(account.document.length == 11, "[document] should have exactly 11 characters")

        Assert.hasLength(account.phone, "[phone] cannot be empty")
        Assert.isTrue(account.phone.length == 17, "[phone] should have exactly 17 characters")
    }
}
