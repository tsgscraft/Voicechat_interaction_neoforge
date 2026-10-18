package de.tsgscraft.voicechat_interaction_neoforge.mixin;

import de.tsgscraft.voicechat_interaction_neoforge.Config;
import net.minecraft.core.Holder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VibrationSystem.class)
public interface VibrationSystemMixin {
    /**
     * Adds the custom frequency for the voicechat_interaction_neoforge:voice game event to the vibration system.
     * @param gameEvent The game event to get the frequency for.
     * @param cir The callback info returnable to set the return value and cancel the original method.
     */
    @Inject(
            method = "getGameEventFrequency(Lnet/minecraft/core/Holder;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void voicechat_interaction_neoforge$getGameEventFrequency(Holder<GameEvent> gameEvent, CallbackInfoReturnable<Integer> cir) {
        if (gameEvent.is(net.minecraft.resources.ResourceLocation.parse("voicechat_interaction_neoforge:voice"))) {
            cir.setReturnValue(Config.voiceSculkFrequency);
            cir.cancel();
        }
    }
}
