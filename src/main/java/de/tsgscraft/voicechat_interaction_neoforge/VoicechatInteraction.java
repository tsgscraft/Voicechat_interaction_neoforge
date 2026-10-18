package de.tsgscraft.voicechat_interaction_neoforge;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(VoicechatInteraction.MODID)
public class VoicechatInteraction {

    public static final String MODID = "voicechat_interaction_neoforge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<GameEvent> GAME_EVENTS =
            DeferredRegister.create(BuiltInRegistries.GAME_EVENT, MODID);

    public static final DeferredHolder<GameEvent, GameEvent> VOICE_GAME_EVENT =
            GAME_EVENTS.register("voice", () -> new GameEvent(128));
    public static String VOICE_GAME_EVENT_NAME = "voicechat_interaction_neoforge:voice";

    public VoicechatInteraction(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);

        GAME_EVENTS.register(modEventBus);
    }
}
