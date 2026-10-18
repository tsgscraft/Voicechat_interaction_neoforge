package de.tsgscraft.voicechat_interaction_neoforge;

import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ForgeVoicechatPlugin
public class Plugin implements VoicechatPlugin {

    public static VoicechatApi voicechatApi;
    private static ConcurrentHashMap<UUID, Long> cooldowns;

    @Nullable
    public static VoicechatServerApi voicechatServerApi;

    @Nullable
    private OpusDecoder decoder;

    @Override
    public String getPluginId() {
        return "voicechat_interaction_neoforge";
    }

    @Override
    public void initialize(VoicechatApi api) {
        voicechatApi = api;
        cooldowns = new ConcurrentHashMap<>();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicPacket);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatServerApi = event.getVoicechat();
    }

    private void onMicPacket(MicrophonePacketEvent event) {
        VoicechatConnection senderConnection = event.getSenderConnection();
        if (senderConnection == null) {
            return;
        }

        if (event.getPacket().getOpusEncodedData().length <= 0) {
            // Don't trigger any events when stopping to talk
            return;
        }

        if (!Config.groupInteraction) {
            if (senderConnection.isInGroup()) {
                return;
            }
        }

        if (!Config.whisperInteraction) {
            if (event.getPacket().isWhispering()) {
                return;
            }
        }

        if (!(senderConnection.getPlayer().getPlayer() instanceof ServerPlayer player)) {
            VoicechatInteraction.LOGGER.warn("Received microphone packets from non-player");
            return;
        }

        if (!Config.sneakInteraction) {
            if (player.isCrouching()) {
                return;
            }
        }

        if (decoder == null) {
            decoder = event.getVoicechat().createDecoder();
        }

        decoder.resetState();
        short[] decoded = decoder.decode(event.getPacket().getOpusEncodedData());

        double audioLevel = AudioUtils.calculateMaxAudioLevel(decoded);

        if (audioLevel < Config.minActivationThreshold) {
            return;
        }

        player.level().getServer().execute(() -> {
            if (activate(player)) {// decibels are too low, don't trigger the event
                //player.sendSystemMessage(Component.literal("Voicechat interaction triggered! Audio level: " + audioLevel + " dB | " + event.getPacket().isWhispering() + " | " + Config.minActivationThreshold));
                if (audioLevel > Config.minActivationThreshold) {
                    //player.sendSystemMessage(Component.literal("Sent!"));
                    AudioUtils.setDecibels(player, audioLevel);
                    player.gameEvent(VoicechatInteraction.VOICE_GAME_EVENT);
                }
            }
        });
    }

    private boolean activate(ServerPlayer player) {
        Long lastTimestamp = cooldowns.get(player.getUUID());
        long currentTime = player.level().getGameTime();
        if (lastTimestamp == null || currentTime - lastTimestamp > 4L) { // 4 ticks cooldown
            cooldowns.put(player.getUUID(), currentTime);
            return true;
        }
        return false;
    }
}
