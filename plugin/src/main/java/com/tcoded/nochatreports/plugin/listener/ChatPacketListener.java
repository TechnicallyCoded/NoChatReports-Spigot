package com.tcoded.nochatreports.plugin.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.util.crypto.MessageSignData;
import com.github.retrooper.packetevents.util.crypto.SaltSignature;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerData;
import com.tcoded.nochatreports.nms.PlayerChatPacket;
import com.tcoded.nochatreports.nms.SystemChatPacket;
import com.tcoded.nochatreports.plugin.NoChatReports;
import io.netty.buffer.ByteBuf;

import java.time.Instant;

public class ChatPacketListener implements PacketListener {

    private final NoChatReports plugin;

    public ChatPacketListener(NoChatReports plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketTypeCommon type = event.getPacketType();

        if (type == PacketType.Play.Server.CHAT_MESSAGE) {
            handlePlayerChatMessagePacket(event);
        }
        else if (type == PacketType.Play.Server.PLAYER_CHAT_HEADER) {
            handlePlayerChatHeaderPacket(event);
        }
        else if (type == PacketType.Play.Server.SERVER_DATA) {
            handleServerDataPacket(event);
        }
        else if (type == PacketType.Play.Server.JOIN_GAME) {
            handleJoinGamePacket(event);
        }
    }

    private void handleJoinGamePacket(PacketSendEvent event) {
//        WrapperPlayServerJoinGame wrapper = new WrapperPlayServerJoinGame(event);
//        NBTCompound dimensionCodec = wrapper.getDimensionCodec();
//
//        NBTCompound chatTypeCompound = dimensionCodec.getCompoundTagOrNull("minecraft:chat_type");
//        NBTList<NBTCompound> chatTypeValues = chatTypeCompound.getCompoundListTagOrNull("value");
//
//        for (NBTCompound chatType : chatTypeValues.getTags()) {
//            NBTCompound chatCompound = chatType.getCompoundTagOrNull("chat");
//            NBTCompound narrationCompound = chatType.getCompoundTagOrNull("narration");
//
//            String chatTranslationKey = chatCompound.getStringTagValueOrNull("translation_key");
//            System.out.println("chatTranslationKey = " + chatTranslationKey);
//            NBTCompound chatStyle = chatCompound.getCompoundTagOrNull("style");
//            JsonElement jsonElement = NBTCodec.nbtToJson(chatStyle, false);
//            System.out.println(jsonElement.toString());
//
//            String narrationTranslationKey = narrationCompound.getStringTagValueOrNull("translation_key");
//        }
    }

    private void handlePlayerChatHeaderPacket(PacketSendEvent event) {
        // Config check
        if (!plugin.getConfig().getBoolean("strip-server-chat-signatures", true)) return;
        event.setCancelled(true);
    }

    private void handleServerDataPacket(PacketSendEvent event) {
        // Config check
        if (!plugin.getConfig().getBoolean("hide-scary-popup", true)) return;

        WrapperPlayServerServerData wrapper = new WrapperPlayServerServerData(event);
        wrapper.setEnforceSecureChat(true);
    }

    private void handlePlayerChatMessagePacket(PacketSendEvent event) {
        // Config check
        if (!plugin.getConfig().getBoolean("strip-server-chat-signatures", true)) return;

        PlayerChatPacket wrappedChatPacket = this.plugin.getNmsProvider().wrapChatPacket((ByteBuf) event.getByteBuf());
        SystemChatPacket systemPacket = wrappedChatPacket.toSystem();

        event.setCancelled(true);

        ByteBuf systemPacketByteBuf = systemPacket.toByteBuf();
//        PacketWrapper<WrapperPlayServerSystemChatMessage> wrapper = new PacketWrapper<>(PacketType.Play.Server.SYSTEM_CHAT_MESSAGE);
//        wrapper.buffer = systemPacketByteBuf;
//        WrapperPlayServerSystemChatMessage wrapper = new WrapperPlayServerSystemChatMessage(false, Component.text("CUSTOM"));
        event.getUser().sendPacket(systemPacketByteBuf);

//        net.kyori.adventure.chat.ChatType.chatType(KeyedValue.keyedValue(chatType.getName().getKey()))
//        chatType.
        // chat.type.text
//            TranslatableComponent translatable = Component.translatable(
//                    "chat.type.text",
//                    Component.text(name),
//                    Component.text(AdventureSerializer.asVanilla(chatContent))
//            );
//        String json = AdventureSerializer.toJson(translatable);
//        System.out.println("AdventureSerializer.toJson(translatable) = " + json);
//        WrapperPlayServerSystemChatMessage newWrapper = new WrapperPlayServerSystemChatMessage(
//                false, translatable
//        );

//        User user = event.getUser();
//        UUID receiverId = user.getUUID();
//        BaseComponent[] bungeeComponents = ComponentSerializer.parse(json);
//        Bukkit.getPlayer(receiverId).spigot().sendMessage(bungeeComponents);
//        user.sendPacketSilently(newWrapper);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        PacketTypeCommon type = event.getPacketType();

        if (type == PacketType.Play.Client.CHAT_MESSAGE) {

            // Config check
            if (!plugin.getConfig().getBoolean("strip-client-chat-signatures", true)) return;

            WrapperPlayClientChatMessage wrapper = new WrapperPlayClientChatMessage(event);
//            wrapper.setLastSeenMessages(null);
            wrapper.setMessageSignData(new MessageSignData(new SaltSignature(0L, new byte[0]), Instant.ofEpochMilli(0L), false));
//            wrapper.setLegacyLastSeenMessages(new LastSeenMessages.LegacyUpdate(LastSeenMessages.EMPTY, null));
//            wrapper.setMessage("CUSTOM - HAHA");
        }

    }
}
