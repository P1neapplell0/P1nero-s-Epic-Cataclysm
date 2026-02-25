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

public class GauntletGuardInnateSkill extends PECWeaponInnateSkillBase {
    public GauntletGuardInnateSkill(SkillBuilder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        if(PECDataManager.GAUNTLET_OF_GUARD_LOCK.get(container.getExecutor().getOriginal())) {
            container.getExecutor().getOriginal().displayClientMessage(PEpicCataclysmMod.gauntletOfGuardLock, true);
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
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.BEDIVERE_SKILL_B, 0.1F);
    }

    @Override
    public void executeSkill3(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        serverPlayerPatch.playAnimationSynchronized(PECAnimations.BEDIVERE_SKILL_A, 0.1F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected List<KeyMapping> getAvailableKeys() {
        return List.of(KeyMappings.SKILL_2, KeyMappings.SKILL_3);
    }
}
