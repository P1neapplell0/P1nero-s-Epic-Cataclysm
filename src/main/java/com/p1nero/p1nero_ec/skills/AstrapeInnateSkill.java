package com.p1nero.p1nero_ec.skills;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.capability.PECPlayer;
import com.p1nero.p1nero_ec.client.KeyMappings;
import com.p1nero.p1nero_ec.gameassets.PECAnimations;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.List;

public class AstrapeInnateSkill extends PECWeaponInnateSkillBase {
    public AstrapeInnateSkill(SkillBuilder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        if(PECDataManager.ASTRAPE_LOCK.get(container.getExecutor().getOriginal())) {
            container.getExecutor().getOriginal().displayClientMessage(PEpicCataclysmMod.astrapeLock, true);
            return;
        }
        super.executeOnServer(container, args);
    }

    @Override
    protected void tryExecuteSkill1(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        if (PECPlayer.consumeSkillPoint(serverPlayerPatch.getOriginal(), 2)) {
            executeSkill1(serverPlayerPatch, container);
        } else {
            onSkillPointNotEnough(container, 2);
        }
    }

    @Override
    protected void tryExecuteSkill2(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        ServerPlayer caster = serverPlayerPatch.getOriginal();
        List<Entity> nearbyEntities = caster.level().getEntities(caster,
                caster.getBoundingBox().inflate(15.0D),
                (e) -> e instanceof LivingEntity && !e.isAlliedTo(caster)
        );

        if(nearbyEntities.isEmpty() && serverPlayerPatch.getTarget() == null) {
            caster.displayClientMessage(Component.translatable("info.p1nero_ec.need_target"), true);
            return;
        }

        if (PECPlayer.consumeSkillPoint(serverPlayerPatch.getOriginal(), 3)) {
            executeSkill2(serverPlayerPatch, container);
        } else {
            onSkillPointNotEnough(container, 3);
        }
    }

    @Override
    protected void tryExecuteSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
    }

    @Override
    public void executeSkill1(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.ASTRAPE_SKILL1, 0.05F);

    }

    @Override
    public void executeSkill2(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.ASTRAPE_SKILL2, 0.1F);

    }

    @Override
    public void executeSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected List<KeyMapping> getAvailableKeys() {
        return List.of(KeyMappings.SKILL_1, KeyMappings.SKILL_2);
    }
}
