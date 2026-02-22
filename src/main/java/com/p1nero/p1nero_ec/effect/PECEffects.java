package com.p1nero.p1nero_ec.effect;

import com.p1nero.p1nero_ec.PECMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PECEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, PECMod.MOD_ID);
    public static final RegistryObject<MobEffect> SOUL_INCINERATOR = REGISTRY.register("soul_incinerator", () -> new SimpleEffect(MobEffectCategory.BENEFICIAL, 0x6A5ACD));
}
