package com.p1nero.p1nero_ec;

import com.github.L_Ender.cataclysm.items.*;
import com.mojang.logging.LogUtils;
import com.p1nero.p1nero_ec.client.PECSounds;
import com.p1nero.p1nero_ec.effect.PECEffects;
import com.p1nero.p1nero_ec.network.PECPacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import yesman.epicfight.main.EpicFightSharedConstants;

import static yesman.epicfight.client.gui.screen.config.ItemsPreferenceScreen.registerWeaponCategorizedItemClasses;

@Mod(PECMod.MOD_ID)
public class PECMod {

    public static final String MOD_ID = "p1nero_ec";
    private static final Logger LOGGER = LogUtils.getLogger();

    public PECMod(FMLJavaModLoadingContext context) {
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
                    Wrath_of_the_desert.class,
                    infernal_forge.class,
                    Gauntlet_of_Guard.class,
                    The_Annihilator.class
            );

        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PECPacketHandler.register();
    }

}
