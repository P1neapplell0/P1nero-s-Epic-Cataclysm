package com.p1nero.p1nero_ec.client;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class PECSounds {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PEpicCataclysmMod.MOD_ID);
    public static final RegistryObject<SoundEvent> GAIN_ABILITY_POINTS = createEvent("gain_ability_points");

    private static RegistryObject<SoundEvent> createEvent(String sound) {
        return REGISTRY.register(sound, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(PEpicCataclysmMod.MOD_ID, sound)));
    }
}
