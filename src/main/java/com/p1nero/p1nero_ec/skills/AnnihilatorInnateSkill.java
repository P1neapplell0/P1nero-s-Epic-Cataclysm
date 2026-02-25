package com.p1nero.p1nero_ec.skills;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.client.KeyMappings;
import com.p1nero.p1nero_ec.gameassets.PECAnimations;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.List;

public class AnnihilatorInnateSkill extends PECWeaponInnateSkillBase {
    public AnnihilatorInnateSkill(SkillBuilder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        if(PECDataManager.DUAL_ANNIHILATOR_LOCK.get(container.getExecutor().getOriginal())) {
            container.getExecutor().getOriginal().displayClientMessage(PEpicCataclysmMod.dualAnnihilatorLock, true);
            return;
        }
        super.executeOnServer(container, args);
    }

    @Override
    protected void tryExecuteSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
    }

    @Override
    public void executeSkill1(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.ANNIHILATOR_SKILL_1, 0.1F);

    }

    @Override
    public void executeSkill2(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.ANNIHILATOR_SKILL_2, 0.1F);

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

