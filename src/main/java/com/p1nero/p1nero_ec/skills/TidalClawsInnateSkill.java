package com.p1nero.p1nero_ec.skills;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.client.KeyMappings;
import com.p1nero.p1nero_ec.gameassets.PECAnimations;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.List;

public class TidalClawsInnateSkill extends PECWeaponInnateSkillBase {
    public TidalClawsInnateSkill(SkillBuilder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        if(PECDataManager.TIDAL_CLAW_LOCK.get(container.getExecutor().getOriginal())) {
            container.getExecutor().getOriginal().displayClientMessage(PEpicCataclysmMod.tidalClawLock, true);
            return;
        }
        super.executeOnServer(container, args);
    }

    @Override
    protected void tryExecuteSkill1(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
    }

    @Override
    public void executeSkill1(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
    }

    @Override
    public void executeSkill2(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.CLAW_SKILL2, 0.15F);
    }

    @Override
    protected void tryExecuteSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        Vec3 center = serverPlayerPatch.getOriginal().position();
        if(serverPlayerPatch.getOriginal().level().getEntitiesOfClass(LivingEntity.class, (new AABB(center, center)).inflate(10)).size() <= 1) {
            serverPlayerPatch.getOriginal().displayClientMessage(Component.translatable("info.p1nero_ec.need_target"), true);
            return;
        }
        super.tryExecuteSkill3(serverPlayerPatch, container);
    }

    @Override
    public void executeSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.CLAW_SKILL3, 0.15F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected List<KeyMapping> getAvailableKeys() {
        return List.of(KeyMappings.SKILL_2, KeyMappings.SKILL_3);
    }
}
