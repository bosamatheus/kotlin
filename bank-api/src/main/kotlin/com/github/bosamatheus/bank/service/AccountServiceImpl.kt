package com.github.bosamatheus.bank.service

import com.github.bosamatheus.bank.exception.AccountNotFoundException
import com.github.bosamatheus.bank.model.Account
import com.github.bosamatheus.bank.repository.AccountRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountServiceImpl(private val repository: AccountRepository) : AccountService {

    override fun create(account: Account): Account {
        return repository.save(account)
    }

    override fun getAll(): List<Account> {
        return repository.findAll()
    }

    override fun getById(id: Long): Optional<Account> {
        return repository.findById(id)
    }

    override fun update(id: Long, account: Account): Optional<Account> {
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
}
