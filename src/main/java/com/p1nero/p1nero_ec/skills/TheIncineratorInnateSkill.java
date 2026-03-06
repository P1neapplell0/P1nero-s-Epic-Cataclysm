package com.p1nero.p1nero_ec.skills;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.client.KeyMappings;
import com.p1nero.p1nero_ec.gameassets.PECAnimations;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.List;

public class TheIncineratorInnateSkill extends PECWeaponInnateSkillBase {
    public TheIncineratorInnateSkill(SkillBuilder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        super.executeOnServer(container, args);
    }

    @Override
    protected void tryExecuteSkill1(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {

    }

    @Override
    public void executeSkill1(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {

    }

    @Override
    protected void tryExecuteSkill2(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        if(PECDataManager.THE_INCINERATOR_LOCK.get(container.getExecutor().getOriginal())) {
            container.getExecutor().getOriginal().displayClientMessage(PEpicCataclysmMod.theIncineratorLock, true);
            return;
        }
        if(serverPlayerPatch.getTarget() == null) {
            serverPlayerPatch.getOriginal().displayClientMessage(Component.translatable("info.p1nero_ec.need_target"), true);
            return;
        }
        super.tryExecuteSkill2(serverPlayerPatch, container);
    }

    @Override
    public void executeSkill2(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.THE_INCINERATOR_SKILL2, 0.15F);
    }

    @Override
    protected void tryExecuteSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        if(PECDataManager.THE_INCINERATOR_LOCK.get(container.getExecutor().getOriginal())) {
            container.getExecutor().getOriginal().displayClientMessage(PEpicCataclysmMod.theIncineratorLock, true);
            return;
        }
        super.tryExecuteSkill3(serverPlayerPatch, container);
    }

    @Override
    public void executeSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.THE_INCINERATOR_SKILL3, 0.15F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected List<KeyMapping> getAvailableKeys() {
        return List.of(KeyMappings.SKILL_2, KeyMappings.SKILL_3);
    }
}
