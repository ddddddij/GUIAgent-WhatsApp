package com.example.whatsapp_sim.data.local

import android.content.Context
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.model.Call
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.domain.model.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AssetsHelper(private val context: Context) {
    private val gson = Gson()

    fun loadConversations(): List<Conversation> {
        val json = context.assets.open("data/conversations.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Conversation>>() {}.type
        return gson.fromJson(json, type)
    }

    fun loadMessages(): List<Message> {
        val json = context.assets.open("data/messages.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Message>>() {}.type
        return gson.fromJson(json, type)
    }

    fun loadAccounts(): List<Account> {
        val json = context.assets.open("data/accounts.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Account>>() {}.type
        return gson.fromJson(json, type)
    }

    fun loadCalls(): List<Call> {
        val json = context.assets.open("data/calls.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Call>>() {}.type
        return gson.fromJson(json, type)
    }

    fun loadContacts(): List<Contact> {
        val json = context.assets.open("data/contacts.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Contact>>() {}.type
        return gson.fromJson(json, type)
    }
}
