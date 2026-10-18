package de.tsgscraft.voicechat_interaction_neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = VoicechatInteraction.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue groupInteractionConf = BUILDER.comment("If talking in groups should trigger vibrations").define("group_interaction", false);
    private static final ModConfigSpec.BooleanValue whisperInteractionConf = BUILDER.comment("If whispering should trigger vibrations").define("whisper_interaction", false);
    private static final ModConfigSpec.BooleanValue sneakInteractionConf = BUILDER.comment("If talking while sneaking should trigger vibrations").define("sneak_interaction", false);
    private static final ModConfigSpec.IntValue voiceSculkFrequencyConf = BUILDER.comment("The frequency of the voice vibration").defineInRange("voice_sculk_frequency", 7, 1, 15);
    private static final ModConfigSpec.IntValue minActivationThresholdConf = BUILDER.comment("The audio level threshold to activate the sculk sensor in dB").defineInRange("minimum_activation_threshold", -50, -127, 0);

    private static final ModConfigSpec.DoubleValue radiusFunctionBaseConf = BUILDER.comment("The base value for the speaking radius function e.g. the x in radius = (x^(db+127)+y)*(vanilla_radius/z) (db range from -127 to 0 the output is clamped)").defineInRange("function.range_function_base", 1.031, 1.000001, 2.0);
    private static final ModConfigSpec.DoubleValue radiusFunctionCorrectionConf = BUILDER.comment("The correction value for the speaking radius function e.g. the y in radius = (x^(db+127)+y)*(vanilla_radius/z) (db range from -127 to 0 the output is clamped)").defineInRange("function.range_function_correction", -8.0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue radiusFunctionInfluenceConf = BUILDER.comment("The vanilla_influence value for the speaking radius function e.g. the z in radius = (x^(db+127)+y)*(vanilla_radius/z) (db range from -127 to 0 the output is clamped)").defineInRange("function.range_function_vanilla_influence", 16.0, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean groupInteraction = false;
    public static boolean whisperInteraction = false;
    public static boolean sneakInteraction = false;
    public static int voiceSculkFrequency = 7;
    public static int minActivationThreshold = -50;

    public static double radiusFunctionBase = 1.031;
    public static double radiusFunctionCorrection = -8;
    public static double radiusFunctionInfluence = 16;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        try {
            if (!event.getConfig().getModId().equals(VoicechatInteraction.MODID)) {
                return;
            }
            groupInteraction = groupInteractionConf.get();
            whisperInteraction = whisperInteractionConf.get();
            sneakInteraction = sneakInteractionConf.get();
            voiceSculkFrequency = voiceSculkFrequencyConf.get();
            minActivationThreshold = minActivationThresholdConf.get();

            radiusFunctionBase = radiusFunctionBaseConf.get();
            radiusFunctionCorrection = radiusFunctionCorrectionConf.get();
            radiusFunctionInfluence = radiusFunctionInfluenceConf.get();
        }catch (Exception ignore) {
            // Ignore errors when loading config
        }
    }

    @SubscribeEvent
    public static void onUnload(final ModConfigEvent.Unloading event) {
        if (!event.getConfig().getModId().equals(VoicechatInteraction.MODID)) {
            return;
        }
    }
}
