package de.tsgscraft.voicechat_interaction_neoforge.mixin.sculkSensor;

import de.tsgscraft.voicechat_interaction_neoforge.AudioUtils;
import de.tsgscraft.voicechat_interaction_neoforge.VoicechatInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.level.block.entity.SculkSensorBlockEntity$VibrationUser")
public abstract class SculkSensorVibrationUserMixin {
    /**
     * Modifies the redstone strength based on the audio level of the player who triggered the vibration event.
     * @param distance The distance of the vibration.
     * @param originalRadius The original radius of the vibration.
     * @param level The server level.
     * @param pos The position of the vibration.
     * @param gameEvent The game event that triggered the vibration.
     * @param sourceEntity The entity that triggered the vibration.
     * @param projectileOwner The owner of the projectile that triggered the vibration.
     * @param vibrationDistance The distance of the vibration.
     * @return The modified redstone strength.
     */
    @Redirect(
            method = "onReceiveVibration",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem;getRedstoneStrengthForDistance(FI)I"
            )
    )
    private int voicechat_interaction_neoforge$modifyRedstoneStrength(
            float distance,
            int originalRadius,
            ServerLevel level,
            BlockPos pos,
            Holder<GameEvent> gameEvent,
            @Nullable Entity sourceEntity,
            @Nullable Entity projectileOwner,
            float vibrationDistance
    ) {
        System.out.println("Vibration received: distance=" + distance + ", originalRadius=" + originalRadius + ", gameEvent=" + gameEvent);
        if (!gameEvent.is(ResourceLocation.parse(VoicechatInteraction.VOICE_GAME_EVENT_NAME))) return VibrationSystem.getRedstoneStrengthForDistance(distance, originalRadius);
        System.out.println("Using voicechat_interaction_neoforge:voice event, modifying radius based on audio level.");
        Entity player = sourceEntity;

        if (player == null || !(player instanceof Player)) {
            player = projectileOwner;
        }

        int redstone = 0;

        if (player instanceof ServerPlayer serverPlayer) {
            double decibels = AudioUtils.getDecibels(serverPlayer);
            redstone = AudioUtils.calculateRedstoneStrength(decibels);
        }

        return redstone;
    }
}
