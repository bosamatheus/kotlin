package com.github.bosamatheus.bank.repository

import com.github.bosamatheus.bank.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long>
