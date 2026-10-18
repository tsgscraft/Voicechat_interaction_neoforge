package de.tsgscraft.voicechat_interaction_neoforge;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AudioUtils {

    /**
     * Calculates the audio level of a signal with specific samples.
     *
     * @param samples the samples of the signal to calculate the audio level of
     * @return the audio level of the specified signal in db
     */
    public static double calculateAudioLevel(short[] samples) {
        double rms = 0D; // root mean square (RMS) amplitude

        for (int i = 0; i < samples.length; i++) {
            double sample = (double) samples[i] / (double) Short.MAX_VALUE;
            rms += sample * sample;
        }

        int sampleCount = samples.length / 2;

        rms = (sampleCount == 0) ? 0 : Math.sqrt(rms / sampleCount);

        double db;

        if (rms > 0D) {
            db = Math.min(Math.max(20D * Math.log10(rms), -127D), 0D);
        } else {
            db = -127D;
        }

        return db;
    }

    public static double calculateMaxAudioLevel(short[] samples) {
        if (samples == null || samples.length == 0) {
            return -127D;
        }

        int maxAmplitude = 0;

        for (short sample : samples) {
            int amplitude = Math.abs((int) sample);

            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude;
            }
        }

        if (maxAmplitude == 0) {
            return -127D;
        }

        double normalized = maxAmplitude / 32768.0D;
        return 20D * Math.log10(normalized);
    }

    public static int calculateListenerRadius(double decibels, int vanillaRadius) {
        return (int) Math.max(1, Math.ceil((Math.pow(Config.radiusFunctionBase, (decibels + 127))+Config.radiusFunctionCorrection)*(vanillaRadius/Config.radiusFunctionInfluence)));
    }

    public static int calculateRedstoneStrength(double decibels) {
        return Math.clamp((long) Math.ceil(Math.pow(1.022, (decibels + 127)) / -1), 1, 15);
    }

    private static final Map<UUID, Double> playerDecibelMap = new ConcurrentHashMap<>();

    public static void setDecibels(ServerPlayer serverPlayer, double decibels) {
        playerDecibelMap.put(serverPlayer.getUUID(), decibels);
    }

    public static double getDecibels(ServerPlayer serverPlayer) {
        return playerDecibelMap.getOrDefault(serverPlayer.getUUID(), 0.0);
    }
}
