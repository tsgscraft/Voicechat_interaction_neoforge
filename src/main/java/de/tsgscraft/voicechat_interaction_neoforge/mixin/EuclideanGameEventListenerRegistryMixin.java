package de.tsgscraft.voicechat_interaction_neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.tsgscraft.voicechat_interaction_neoforge.AudioUtils;
import de.tsgscraft.voicechat_interaction_neoforge.VoicechatInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(EuclideanGameEventListenerRegistry.class)
public class EuclideanGameEventListenerRegistryMixin {
    /**
     * This method modifies the listener position for game events, specifically for the voicechat_interaction_neoforge:voice event.
     * @param level level
     * @param pos position
     * @param listener listener
     * @param context context
     * @param event event
     * @return the modified listener position
     */
    @Redirect(
            method = "visitInRangeListeners",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/gameevent/EuclideanGameEventListenerRegistry;getPostableListenerPosition(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEventListener;)Ljava/util/Optional;",
                    remap = false
            ),
            remap = false
    )
    private Optional<Vec3> voicechat_interaction_neoforge$modifyvisitInRangeListeners(
            ServerLevel level,
            Vec3 pos,
            GameEventListener listener,
            @Local(argsOnly = true) GameEvent.Context context,
            @Local(argsOnly = true) Holder<GameEvent> event
    ) {
        return voicechat_interaction_neoforge$modifyListenerPosition(level, pos, listener, context, event.getRegisteredName());
    }

    /**
     * This method modifies the listener position for game events, specifically for the voicechat_interaction_neoforge:voice event.
     * @param level The server level.
     * @param pos The position of the game event.
     * @param listener The game event listener.
     * @param context The context of the game event.
     * @param registeredName The name of the registered game event.
     * @return An Optional containing the modified listener position, or an empty Optional if the listener is out of range.
     */
    @Unique
    private static Optional<Vec3> voicechat_interaction_neoforge$modifyListenerPosition(ServerLevel level, Vec3 pos, GameEventListener listener, GameEvent.Context context, String registeredName) {
        Optional<Vec3> optional = listener.getListenerSource().getPosition(level);
        if (optional.isEmpty()) {
            return Optional.empty();
        } else {
            double d0 = BlockPos.containing((Position) optional.get()).distSqr(BlockPos.containing(pos));
            if (listener instanceof VibrationSystem.Listener && context.sourceEntity() instanceof ServerPlayer serverPlayer && registeredName.equals(VoicechatInteraction.VOICE_GAME_EVENT_NAME)) {
                int radius = AudioUtils.calculateListenerRadius(AudioUtils.getDecibels(serverPlayer), listener.getListenerRadius());
                serverPlayer.sendSystemMessage(Component.literal("Listener radius: " + radius + " (original: " + listener.getListenerRadius() + ")"));
                return d0 > (double) (radius * radius) ? Optional.empty() : optional;
            } else {
                int i = listener.getListenerRadius() * listener.getListenerRadius();
                return d0 > (double) i ? Optional.empty() : optional;
            }
        }
    }
}
