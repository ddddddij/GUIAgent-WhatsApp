package com.example.whatsapp_sim.domain.model

enum class CallType { VOICE, VIDEO }

enum class CallStatus { MISSED, INCOMING, OUTGOING }

enum class ContactStatus { SAVED, INVITED, BLOCKED }

enum class InviteStatus { PENDING, SENT, ACCEPTED }

enum class CommunityRole { OWNER, ADMIN, MEMBER }

enum class MembershipStatus { JOINED, LEFT, KICKED }

enum class MessageType { TEXT, IMAGE, AUDIO, DOCUMENT, LOCATION }

enum class MessageStatus { SENDING, SENT, DELIVERED, READ, FAILED }

enum class PrivacyLevel { EVERYONE, MY_CONTACTS, NOBODY }

enum class NotifLevel { ALL, MENTIONS_ONLY, MUTED }
