package de.tsgscraft.voicechat_interaction_neoforge.mixin;

import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VibrationSystem.Listener.class)
public interface VibrationSystemListenerAccessor {
    @Accessor("system")
    VibrationSystem system();
}
