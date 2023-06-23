package we.hate.chatreports.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.ChatType;
import com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
import com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_1;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.crypto.MessageSignData;
import com.github.retrooper.packetevents.util.crypto.SaltSignature;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import we.hate.chatreports.NoChatReports;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChatPacketListener implements PacketListener {

    private final NoChatReports plugin;

    public ChatPacketListener(NoChatReports plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketTypeCommon type = event.getPacketType();

        if (type == PacketType.Play.Server.CHAT_MESSAGE) {
            WrapperPlayServerChatMessage wrapper = new WrapperPlayServerChatMessage(event);
            ChatMessage message = wrapper.getMessage();

            Component chatContent = message.getChatContent();
            Instant timestampKiller = Instant.ofEpochMilli(0L);
            UUID senderUUID;
            if (message instanceof ChatMessage_v1_19 chatMessage) {
//                chatMessage.setSignature(null);
//                chatMessage.setSalt(0L);
//                chatMessage.setTimestamp(timestampKiller);
                Component unsignedChatContent = chatMessage.getUnsignedChatContent();
                if (unsignedChatContent != null) {
                    chatContent = unsignedChatContent;
                }
                senderUUID = chatMessage.getSenderUUID();
            }
            else if (message instanceof ChatMessage_v1_19_1 chatMessage) {
//                chatMessage.setSignature(null);
//                chatMessage.setSalt(0L);
//                chatMessage.setLastSeenMessages(LastSeenMessages.EMPTY);
//                chatMessage.setPreviousSignature(null);
//                chatMessage.setTimestamp(timestampKiller);
//                chatMessage.setFilterMask(FilterMask.PASS_THROUGH);
                Component unsignedChatContent = chatMessage.getUnsignedChatContent();
                if (unsignedChatContent != null) {
                    chatContent = unsignedChatContent;
                }
                senderUUID = chatMessage.getSenderUUID();
            }
            else if (message instanceof ChatMessage_v1_19_3 chatMessage) {
                Optional<Component> unsignedChatContent = chatMessage.getUnsignedChatContent();
                if (unsignedChatContent.isPresent()) {
                    chatContent = unsignedChatContent.get();
                }
                senderUUID = chatMessage.getSenderUUID();
            }
            else {
                return;
            }

            System.out.println(chatContent);

            event.setCancelled(true);

            String name = Bukkit.getPlayer(senderUUID).getName();

            ChatType chatType = message.getType();
            ResourceLocation chatTypeName = chatType.getName();
            TranslatableComponent translatable = Component.translatable(
                    chatTypeName.getNamespace() + ":" + chatTypeName.getKey(),
                    Component.text(name),
                    chatContent
            );
            WrapperPlayServerSystemChatMessage newWrapper = new WrapperPlayServerSystemChatMessage(
                    false, translatable
            );
            event.getUser().sendPacketSilently(newWrapper);
        }

        else if (type == PacketType.Play.Server.PLAYER_CHAT_HEADER) {
            event.setCancelled(true);
        }

        else if (type == PacketType.Play.Server.SERVER_DATA) {
            // Config check
            if (!plugin.getConfig().getBoolean("hide-scary-popup", true)) return;

            WrapperPlayServerServerData wrapper = new WrapperPlayServerServerData(event);
            wrapper.setEnforceSecureChat(true);
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        PacketTypeCommon type = event.getPacketType();

        if (type == PacketType.Play.Client.CHAT_MESSAGE) {

            System.out.println("hi");

            // Config check
            if (!plugin.getConfig().getBoolean("strip-client-chat-signatures", true)) return;

            System.out.println("hi x2");

            WrapperPlayClientChatMessage wrapper = new WrapperPlayClientChatMessage(event);
//            wrapper.setLastSeenMessages(null);
//            wrapper.setMessageSignData(new MessageSignData(new SaltSignature(0L, new byte[0]), Instant.ofEpochMilli(0L), false));
//            wrapper.setLegacyLastSeenMessages(new LastSeenMessages.LegacyUpdate(LastSeenMessages.EMPTY, null));
//            wrapper.setMessage("CUSTOM - HAHA");

//            event.
        }

    }
}
