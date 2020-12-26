package com.github.bosamatheus.bank.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.bosamatheus.bank.model.Account
import com.github.bosamatheus.bank.repository.AccountRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@AutoConfigureMockMvc
@SpringBootTest
internal class AccountControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var repository: AccountRepository

    val baseUri = "/accounts"

    @Test
    fun `test find all`() {
        repository.save(Account(name = "Testing", document = "12345678910", phone = "+55 41 91234-1234"))
        mockMvc.perform(MockMvcRequestBuilders.get(baseUri))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].name").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].document").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].phone").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test find by id`() {
        val account = repository.save(Account(name = "Testing", document = "12345678910", phone = "+55 41 91234-1234"))
        mockMvc.perform(MockMvcRequestBuilders.get("$baseUri/${account.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(account.id))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create`() {
        val account = Account(name = "Testing", document = "12345678910", phone = "+55 41 91234-1234")
        val json = ObjectMapper().writeValueAsString(account)
        repository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())
        Assertions.assertFalse(repository.findAll().isEmpty())
    }

    @Test
    fun `test update`() {
        val account = repository
            .save(Account(name = "Testing", document = "12345678910", phone = "+55 41 91234-1234"))
            .copy(name = "Updated")
        val json = ObjectMapper().writeValueAsString(account)
        mockMvc.perform(MockMvcRequestBuilders.put("$baseUri/${account.id}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())

        val accountFound = repository.findById(account.id!!)
        Assertions.assertTrue(accountFound.isPresent)
        Assertions.assertEquals(account.name, accountFound.get().name)
    }

    @Test
    fun `test delete`() {
        val account = repository.save(Account(name = "Testing", document = "12345678910", phone = "+55 41 91234-1234"))
        mockMvc.perform(MockMvcRequestBuilders.delete("$baseUri/${account.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

        val accountFound = repository.findById(account.id!!)
        Assertions.assertFalse(accountFound.isPresent)
    }

    @Test
    fun `test create account validation error empty name`() {
        val account = Account(name = "", document = "12345678910", phone = "+55 41 91234-1234")
        val json = ObjectMapper().writeValueAsString(account)
        repository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[name] cannot be empty"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error name should have at least 5 characters`() {
        val account = Account(name = "Test", document = "12345678910", phone = "+55 41 91234-1234")
        val json = ObjectMapper().writeValueAsString(account)
        repository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[name] should have at least 5 characters"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error empty document`() {
        val account = Account(name = "Testing", document = "", phone = "+55 41 91234-1234")
        val json = ObjectMapper().writeValueAsString(account)
        repository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] cannot be empty"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error document should have exactly 11 characters`() {
        val account = Account(name = "Testing", document = "123456789100", phone = "+55 41 91234-1234")
        val json = ObjectMapper().writeValueAsString(account)
        repository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] should have exactly 11 characters"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error empty phone`() {
        val account = Account(name = "Testing", document = "12345678910", phone = "")
        val json = ObjectMapper().writeValueAsString(account)
        repository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[phone] cannot be empty"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error phone should have exactly 17 characters`() {
        val account = Account(name = "Testing", document = "12345678910", phone = "41 91234-1234")
        val json = ObjectMapper().writeValueAsString(account)
        repository.deleteAll()
        mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[phone] should have exactly 17 characters"))
            .andDo(MockMvcResultHandlers.print())
    }
}
