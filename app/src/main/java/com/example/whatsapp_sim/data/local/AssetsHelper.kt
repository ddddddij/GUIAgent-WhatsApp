package com.example.whatsapp_sim.data.local

import android.content.Context
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.model.Call
import com.example.whatsapp_sim.domain.model.Channel
import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.domain.model.GroupDetail
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.Status
import com.example.whatsapp_sim.domain.model.UserStatus
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.File

class AssetsHelper(private val context: Context) {
    private val gson = Gson()
    private val prettyGson = GsonBuilder().setPrettyPrinting().create()
    private val runtimeDataDir: File by lazy {
        File(context.filesDir, RUNTIME_DATA_DIR_NAME).apply { mkdirs() }
    }

    init {
        bootstrapRuntimeDataIfNeeded()
    }

    fun loadConversations(): List<Conversation> {
        val file = File(runtimeDataDir, RUNTIME_CONVERSATIONS_FILE)
        file.bufferedReader().use { reader ->
            val root = JsonParser.parseReader(reader)
            if (!root.isJsonArray) {
                return emptyList()
            }
            return root.asJsonArray.mapNotNull(::parseConversation)
        }
    }

    fun loadMessages(): List<Message> {
        val type = object : TypeToken<List<Message>>() {}.type
        return readRuntimeJson(RUNTIME_MESSAGES_FILE, type)
    }

    fun loadAccounts(): List<Account> {
        val type = object : TypeToken<List<Account>>() {}.type
        return readRuntimeJson(RUNTIME_ACCOUNTS_FILE, type)
    }

    fun loadCalls(): List<Call> {
        val type = object : TypeToken<List<Call>>() {}.type
        return readRuntimeJson(RUNTIME_CALLS_FILE, type)
    }

    fun loadChannels(): List<Channel> {
        val type = object : TypeToken<List<Channel>>() {}.type
        return readRuntimeJson(RUNTIME_CHANNELS_FILE, type)
    }

    fun loadContacts(): List<Contact> {
        val type = object : TypeToken<List<Contact>>() {}.type
        return readRuntimeJson(RUNTIME_CONTACTS_FILE, type)
    }

    fun loadGroupDetails(): List<GroupDetail> {
        val type = object : TypeToken<List<GroupDetail>>() {}.type
        return readRuntimeJson(RUNTIME_GROUP_DETAILS_FILE, type)
    }

    fun loadCommunities(): List<Community> {
        val type = object : TypeToken<List<Community>>() {}.type
        return readRuntimeJson(RUNTIME_COMMUNITIES_FILE, type)
    }

    fun loadStatuses(): List<Status> {
        val type = object : TypeToken<List<Status>>() {}.type
        return readRuntimeJson(RUNTIME_STATUSES_FILE, type)
    }

    fun loadUserStatuses(): List<UserStatus> {
        val type = object : TypeToken<List<UserStatus>>() {}.type
        return readRuntimeJson(RUNTIME_USER_STATUSES_FILE, type)
    }

    fun saveConversations(conversations: List<Conversation>) {
        writeRuntimeJson(RUNTIME_CONVERSATIONS_FILE, conversations)
    }

    fun saveMessages(messages: List<Message>) {
        writeRuntimeJson(RUNTIME_MESSAGES_FILE, messages)
    }

    fun saveCalls(calls: List<Call>) {
        writeRuntimeJson(RUNTIME_CALLS_FILE, calls)
    }

    fun saveAccounts(accounts: List<Account>) {
        writeRuntimeJson(RUNTIME_ACCOUNTS_FILE, accounts)
    }

    fun saveChannels(channels: List<Channel>) {
        writeRuntimeJson(RUNTIME_CHANNELS_FILE, channels)
    }

    fun saveContacts(contacts: List<Contact>) {
        writeRuntimeJson(RUNTIME_CONTACTS_FILE, contacts)
    }

    fun saveGroupDetails(groupDetails: List<GroupDetail>) {
        writeRuntimeJson(RUNTIME_GROUP_DETAILS_FILE, groupDetails)
    }

    fun saveCommunities(communities: List<Community>) {
        writeRuntimeJson(RUNTIME_COMMUNITIES_FILE, communities)
    }

    fun saveStatuses(statuses: List<Status>) {
        writeRuntimeJson(RUNTIME_STATUSES_FILE, statuses)
    }

    fun saveUserStatuses(statuses: List<UserStatus>) {
        writeRuntimeJson(RUNTIME_USER_STATUSES_FILE, statuses)
    }

    private fun <T> readRuntimeJson(fileName: String, type: java.lang.reflect.Type): T {
        val file = File(runtimeDataDir, fileName)
        val json = file.bufferedReader().use { it.readText() }
        return gson.fromJson(json, type)
    }

    private fun writeRuntimeJson(fileName: String, data: Any) {
        File(runtimeDataDir, fileName).writeText(prettyGson.toJson(data))
    }

    private fun parseConversation(element: JsonElement): Conversation? {
        val json = element.takeIf { it.isJsonObject }?.asJsonObject ?: return null
        return Conversation(
            id = json.string("id"),
            participantIds = json.stringList("participantIds"),
            participantNames = json.stringList("participantNames"),
            isGroupChat = json.boolean("isGroupChat"),
            groupName = json.nullableString("groupName"),
            lastMessagePreview = json.nullableString("lastMessagePreview"),
            lastMessageAt = json.nullableLong("lastMessageAt"),
            createdAt = json.long("createdAt"),
            creatorId = json.string("creatorId"),
            unreadCount = json.int("unreadCount"),
            memberIds = json.stringList("memberIds"),
            adminIds = json.stringList("adminIds"),
            description = json.nullableString("description"),
            createdBy = json.stringOrDefault("createdBy", "you"),
            createdAtDisplay = json.stringOrDefault("createdAtDisplay", "")
        )
    }

    private fun JsonObject.string(name: String): String =
        stringOrDefault(name, "")

    private fun JsonObject.stringOrDefault(name: String, default: String): String {
        val value = get(name) ?: return default
        if (value.isJsonNull) {
            return default
        }
        return value.asString
    }

    private fun JsonObject.nullableString(name: String): String? {
        val value = get(name) ?: return null
        return if (value.isJsonNull) null else value.asString
    }

    private fun JsonObject.long(name: String): Long =
        nullableLong(name) ?: 0L

    private fun JsonObject.nullableLong(name: String): Long? {
        val value = get(name) ?: return null
        return if (value.isJsonNull) null else value.asLong
    }

    private fun JsonObject.int(name: String): Int {
        val value = get(name) ?: return 0
        return if (value.isJsonNull) 0 else value.asInt
    }

    private fun JsonObject.boolean(name: String): Boolean {
        val value = get(name) ?: return false
        return !value.isJsonNull && value.asBoolean
    }

    private fun JsonObject.stringList(name: String): List<String> {
        val value = get(name) ?: return emptyList()
        if (!value.isJsonArray) {
            return emptyList()
        }
        return value.asJsonArray.toStringList()
    }

    private fun JsonArray.toStringList(): List<String> {
        return mapNotNull { item ->
            if (item.isJsonNull) null else item.asString
        }
    }

    private fun bootstrapRuntimeDataIfNeeded() {
        synchronized(bootstrapLock) {
            if (hasBootstrappedForCurrentProcess) {
                return
            }

            runtimeDataDir.mkdirs()
            runtimeDataDir.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "json") {
                    file.delete()
                }
            }

            context.assets.list(ASSET_DATA_DIR)?.forEach { assetFileName ->
                val destinationFile = File(runtimeDataDir, assetFileName)
                context.assets.open("$ASSET_DATA_DIR/$assetFileName").use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            hasBootstrappedForCurrentProcess = true
        }
    }

    companion object {
        private const val ASSET_DATA_DIR = "data"
        private const val RUNTIME_DATA_DIR_NAME = "data"
        private const val RUNTIME_ACCOUNTS_FILE = "accounts.json"
        private const val RUNTIME_CALLS_FILE = "calls.json"
        private const val RUNTIME_CHANNELS_FILE = "channels.json"
        private const val RUNTIME_COMMUNITIES_FILE = "communities.json"
        private const val RUNTIME_CONTACTS_FILE = "contacts.json"
        private const val RUNTIME_CONVERSATIONS_FILE = "conversations.json"
        private const val RUNTIME_GROUP_DETAILS_FILE = "group_details.json"
        private const val RUNTIME_MESSAGES_FILE = "messages.json"
        private const val RUNTIME_STATUSES_FILE = "statuses.json"
        private const val RUNTIME_USER_STATUSES_FILE = "user_statuses.json"

        private val bootstrapLock = Any()

        @Volatile
        private var hasBootstrappedForCurrentProcess = false
    }
}
