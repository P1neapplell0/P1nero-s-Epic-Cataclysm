package com.p1nero.p1nero_ec.skills;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.gameassets.PECAnimations;
import net.minecraft.network.FriendlyByteBuf;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CeraunusInnateSkill extends PECWeaponInnateSkillBase {
    public CeraunusInnateSkill(SkillBuilder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        if(PECDataManager.CERAUNUS_LOCK.get(container.getExecutor().getOriginal())) {
            container.getExecutor().getOriginal().displayClientMessage(PEpicCataclysmMod.ceraunusLock, true);
            return;
        }
        super.executeOnServer(container, args);
    }

    @Override
    public void executeSkill1(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.CERAUNUS_SKILL1, 0.15F);
    }

    @Override
    public void executeSkill2(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.CERAUNUS_SKILL2, 0.15F);
    }

    @Override
    public void executeSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.CERAUNUS_SKILL3, 0.15F);
    }
}
