package com.p1nero.p1nero_ec.capability.item;

import com.asanginxst.epicfightx.gameassets.EpicFightSkillsX;
import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.asanginxst.epicfightx.gameassets.animations.ExtraAnimations;
import com.google.common.collect.Maps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BrontesCapability extends RangedWeaponCapability {
    private final Map<Style, Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>>> styleLivingMotionModifiers;
    private final List<AnimationAccessor<? extends AttackAnimation>> mountAttackMotion;

    public BrontesCapability(CapabilityItem.Builder builder) {
        super(builder);

        this.mountAttackMotion = List.of(AnimationsX.SPEAR_MOUNT_ATTACK);

        this.styleLivingMotionModifiers = Maps.newHashMap();
        initializeLivingMotionModifiers();
    }

    private void initializeLivingMotionModifiers() {
        Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>> commonModifiers = Maps.newHashMap();
        commonModifiers.put(LivingMotions.IDLE, AnimationsX.BIPED_HOLD_SPEAR);
        commonModifiers.put(LivingMotions.WALK, AnimationsX.BIPED_WALK_SPEAR);
        commonModifiers.put(LivingMotions.RUN, AnimationsX.BIPED_RUN_SPEAR);
        commonModifiers.put(LivingMotions.AIM, AnimationsX.BIPED_JAVELIN_AIM);
        commonModifiers.put(LivingMotions.SHOT, AnimationsX.BIPED_JAVELIN_THROW);
        this.styleLivingMotionModifiers.put(Styles.TWO_HAND, commonModifiers);
    }

    @Override
    public Style getStyle(LivingEntityPatch<?> entityPatch) {
        return Styles.TWO_HAND;
    }

    @Override
    public List<AnimationAccessor<? extends AttackAnimation>> getAutoAttackMotion(PlayerPatch<?> playerpatch) {
        return List.of(
                AnimationsX.AXE_AUTO1,
                AnimationsX.AXE_AUTO2,
                ExtraAnimations.AXE_AUTO3,
                ExtraAnimations.AXE_AUTO4,
                ExtraAnimations.AXE_AUTO5,
                AnimationsX.AXE_DASH,
                AnimationsX.AXE_AIRSLASH
        );
    }

    @Override
    public List<AnimationAccessor<? extends AttackAnimation>> getMountAttackMotion() {
        return this.mountAttackMotion;
    }

    @Override
    public Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>> getLivingMotionModifier(LivingEntityPatch<?> playerdata, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            Style currentStyle = this.getStyle(playerdata);
            return this.styleLivingMotionModifiers.getOrDefault(currentStyle, Maps.newHashMap());
        }
        return super.getLivingMotionModifier(playerdata, hand);
    }

    @Override
    public LivingMotion getLivingMotion(LivingEntityPatch<?> entityPatch, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return null;
        }

        if (!entityPatch.getOriginal().isUsingItem()) {
            return null;
        }

        ItemStack useItem = entityPatch.getOriginal().getUseItem();
        if (useItem.getUseAnimation() != UseAnim.SPEAR) {
            return null;
        }

        return LivingMotions.AIM;
    }

    @Nullable
    @Override
    public Skill getInnateSkill(PlayerPatch<?> playerpatch, ItemStack itemstack) {
        if (EnchantmentHelper.getRiptide(itemstack) > 0) {
            return EpicFightSkillsX.TSUNAMI;
        } else if (EnchantmentHelper.hasChanneling(itemstack)) {
            return EpicFightSkillsX.WRATHFUL_LIGHTING;
        } else if (EnchantmentHelper.getLoyalty(itemstack) > 0) {
            return EpicFightSkillsX.EVERLASTING_ALLEGIANCE;
        } else {
            return null;
        }
    }
}
