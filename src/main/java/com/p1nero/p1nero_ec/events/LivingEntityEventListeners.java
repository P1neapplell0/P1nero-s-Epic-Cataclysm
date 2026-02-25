package com.p1nero.p1nero_ec.events;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Blast_Entity;
import com.github.L_Ender.cataclysm.init.ModEffect;
import com.p1nero.p1nero_ec.PECConfig;
import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

@Mod.EventBusSubscriber(modid = PEpicCataclysmMod.MOD_ID)
public class LivingEntityEventListeners {

    /**
     * 灾变硬直buff兼容
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.hasEffect(ModEffect.EFFECTSTUN.get())) {
            LivingEntityPatch<?> livingEntityPatch = EpicFightCapabilities.getEntityPatch(living, LivingEntityPatch.class);
            if (livingEntityPatch != null) {
                livingEntityPatch.applyStun(StunType.HOLD, 0.15F);
            }
        }
    }

    @SubscribeEvent
    public static void onMobGrief(EntityMobGriefingEvent event) {
        if(event.getEntity() instanceof Abyss_Blast_Entity && PECConfig.DISABLE_ABYSS_BLAST_BREAK.get()) {
            event.setResult(Event.Result.DENY);
        }
    }

}
