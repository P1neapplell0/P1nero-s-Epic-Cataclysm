package com.p1nero.p1nero_ec.events;

import com.github.L_Ender.cataclysm.init.ModEntities;
import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.entitypatch.DrownedHostPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

import java.util.List;

@Mod.EventBusSubscriber(modid = PEpicCataclysmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EpicFightEvents {

    @SubscribeEvent
    public static void onGuardSkillCreate(SkillBuildEvent.ModRegistryWorker.SkillCreateEvent<GuardSkill.Builder> event) {
        if (event.getRegistryName().equals(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "parrying"))) {
            GuardSkill.Builder builder = event.getSkillBuilder();
            builder.addAdvancedGuardMotion(WeaponCategories.SPEAR, (itemCap, playerpatch) ->
                            List.of(Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2))
                    .addAdvancedGuardMotion(WeaponCategories.GREATSWORD, (itemCap, playerpatch) ->
                            List.of(Animations.SWORD_GUARD_ACTIVE_HIT1, Animations.SWORD_GUARD_ACTIVE_HIT2));
        }
    }


    @SubscribeEvent
    public static void setPatch(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(ModEntities.DROWNED_HOST.get(), (entity) -> DrownedHostPatch::new);
    }

    @SubscribeEvent
    public static void setArmature(FMLCommonSetupEvent event) {
        Armatures.registerEntityTypeArmature(ModEntities.DROWNED_HOST.get(), Armatures.BIPED);
    }

}
