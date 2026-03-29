package com.p1nero.p1nero_ec;

import com.github.L_Ender.cataclysm.items.*;
import com.p1nero.p1nero_ec.client.PECSounds;
import com.p1nero.p1nero_ec.effect.PECEffects;
import com.p1nero.p1nero_ec.network.PECPacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import yesman.epicfight.main.EpicFightSharedConstants;

import static yesman.epicfight.client.gui.screen.config.ItemsPreferenceScreen.registerWeaponCategorizedItemClasses;

@Mod(PEpicCataclysmMod.MOD_ID)
public class PEpicCataclysmMod {

    public static final String MOD_ID = "p1nero_ec";

    public static Component tidalClawLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);
    public static Component ceraunusLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);
    public static Component infernalForgeLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);
    public static Component gauntletOfGuardLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);
    public static Component dualAnnihilatorLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);
    public static Component soulRenderLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);
    public static Component astrapeLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);
    public static Component theIncineratorLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);
    public static Component wrathOfTheDesertLock = Component.literal("Locked!").withStyle(ChatFormatting.RED);

    public PEpicCataclysmMod(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        PECEffects.REGISTRY.register(bus);
        PECSounds.REGISTRY.register(bus);
        bus.addListener(this::commonSetup);
        context.registerConfig(ModConfig.Type.COMMON, PECConfig.SPEC);
        if (EpicFightSharedConstants.isPhysicalClient()) {
            registerWeaponCategorizedItemClasses(
                    Tidal_Claws.class,
                    Soul_Render.class,
                    The_Incinerator.class,
                    Ceraunus.class,
                    Astrape.class,
                    Wrath_of_the_desert.class,
                    Infernal_forge.class,
                    Gauntlet_of_Guard.class,
                    The_Annihilator.class
            );

        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PECPacketHandler.register();
    }

}
