package com.example.whatsapp_sim.domain.repository

import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.domain.model.Chat
import com.example.whatsapp_sim.domain.model.Message

interface ChatRepository {
    fun getAllChats(): List<Chat>
    fun getConversation(conversationId: String): Conversation?
    fun getMessages(conversationId: String): List<Message>
    fun getContactForConversation(conversationId: String): Contact?
    fun getAllContacts(): List<Contact>
    fun getCurrentUserAccount(): Account?
    fun findOrCreateConversationForContact(contact: Contact): String
    fun findOrCreateConversationForCurrentUser(): String
    fun sendMessage(conversationId: String, content: String): Message
    fun sendImageMessage(conversationId: String, imagePath: String, caption: String? = null): Message
    fun createGroupConversation(groupName: String, memberIds: List<String>, memberNames: List<String>): String
}
