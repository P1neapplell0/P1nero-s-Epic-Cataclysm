package com.p1nero.p1nero_ec.gameassets;

import com.github.L_Ender.cataclysm.capabilities.RenderRushCapability;
import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.Options.RoarParticleOptions;
import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Blast_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Blast_Portal_Entity;
import com.github.L_Ender.cataclysm.entity.effect.*;
import com.github.L_Ender.cataclysm.entity.projectile.*;
import com.github.L_Ender.cataclysm.init.*;
import com.github.L_Ender.cataclysm.items.Ceraunus;
import com.github.L_Ender.cataclysm.items.Cursed_bow;
import com.github.L_Ender.cataclysm.items.Wrath_of_the_desert;
import com.hm.efn.EFN;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffectEntityInvoker;
import com.hm.efn.util.ParticleEffectInvoker;
import com.hm.efn.util.WeaponTrailGroundSplitter;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.particle.AvalonParticles;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import com.p1nero.invincible.api.animation.types.ScanAttackAnimation;
import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.effect.PECEffects;
import com.p1nero.p1nero_ec.utils.PECEffectConditionParticleTrail;
import com.p1nero.p1nero_ec.utils.PECParticleEffectInvoker;
import com.p1nero.p1nero_ec.utils.ScyllaEffectInvoker;
import com.p1nero.p1nero_ec.utils.VoidEffectInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.*;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.hm.efn.gameasset.animations.EFNClawAnimations_N.CLAW_AUTO3;
import static com.hm.efn.gameasset.animations.EFNDualSwordAnimations.DUAL_SWORD_AUTO_3;
import static com.hm.efn.gameasset.animations.EFNDualSwordAnimations.DUAL_SWORD_AUTO_4;
import static com.hm.efn.gameasset.animations.EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABBB_HIT;
import static com.hm.efn.gameasset.animations.EFNLanceAnimations.MEEN_LANCE_1;
import static com.hm.efn.gameasset.animations.EFNLanceAnimations.MEEN_LANCE_CHARGE3;
import static com.merlin204.avalon.util.AvalonAnimationUtils.createSimplePhase;
import static com.p1nero.p1nero_ec.utils.AxeBladeInvoker.*;
import static com.p1nero.p1nero_ec.utils.ScyllaEffectInvoker.createDirectionalLightningStormLine;
import static com.p1nero.p1nero_ec.utils.VoidEffectInvoker.createForwardVoidRuneCluster;
import static java.lang.Integer.MAX_VALUE;

@Mod.EventBusSubscriber(modid = PEpicCataclysmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PECAnimations {
    public static final Collider CERAUNUS_SKILL = new OBBCollider(1.7, 1.7, 1.7, 0.0, 0.0, 0.0);
    public static final Collider ASTRAPE_SKILL = new OBBCollider(1, 1, 1.4, 0.0, 1.0, -0.5);
    public static final Collider INFERNAL_SKILL2_BOX = new OBBCollider(2, 2, 5.5, 0.0, 0.8, -4);
    public static final Collider INFERNAL_SKILL3_BOX = new OBBCollider(2.5, 2.5, 2.5, 0.0, 0.0, 0.0);
    public static final Collider BEDIVERE_1 = new OBBCollider(0.95, 0.95, 1, 0.0, 0.8, -0.8);
    public static final Collider BEDIVERE_SKILL_A_HIT = new OBBCollider(3, 3, 3, 0.0, 0.0, 0.0);
    public static final Collider BEDIVERE_SKILL_B_HIT = new OBBCollider(1.5, 1.5, 3, 0.0, 1.25, -2);
    public static final Collider CLAW = new OBBCollider(1, 1.4, 1, 0.0, 0.0, 0.0);
    public static AnimationManager.AnimationAccessor<StaticAnimation> CLAW_SHOOT;
    public static AnimationManager.AnimationAccessor<StaticAnimation> MEAT_CUTTER_LOOP;
    public static AnimationManager.AnimationAccessor<ScanAttackAnimation> BOW_1;
    public static AnimationManager.AnimationAccessor<ScanAttackAnimation> BOW_2;
    public static AnimationManager.AnimationAccessor<ScanAttackAnimation> BOW_3;
    public static AnimationManager.AnimationAccessor<DashAttackAnimation> INFERNAL_AUTO3;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ANNIHILATOR_AUTO1;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ANNIHILATOR_AUTO2;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ANNIHILATOR_AUTO3;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ANNIHILATOR_AUTO4;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ANNIHILATOR_SKILL_1;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ANNIHILATOR_SKILL_2;
    public static AnimationManager.AnimationAccessor<AttackAnimation> ASTRAPE_AUTO3;
    public static AnimationManager.AnimationAccessor<AttackAnimation> ASTRAPE_DASH;
    public static AnimationManager.AnimationAccessor<AttackAnimation> ASTRAPE_SKILL1;
    public static AnimationManager.AnimationAccessor<AttackAnimation> ASTRAPE_SKILL2;
    public static AnimationManager.AnimationAccessor<StaticAnimation> BEDIVERE_IDLE;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> BEDIVERE_AUTO1;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> BEDIVERE_AUTO2;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> BEDIVERE_AUTO3;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> BEDIVERE_AUTO4;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> BEDIVERE_AUTO5;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> BEDIVERE_SKILL_A;
    public static AnimationManager.AnimationAccessor<AttackAnimation> BEDIVERE_SKILL_B;
    public static AnimationManager.AnimationAccessor<ScanAttackAnimation> BOW_SKILL1;
    public static AnimationManager.AnimationAccessor<ScanAttackAnimation> BOW_SKILL2;
    public static AnimationManager.AnimationAccessor<AttackAnimation> BOW_SKILL3;
    public static AnimationManager.AnimationAccessor<AttackAnimation> BOW_DASH_ATTACK;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ARC_AUTO1;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ARC_AUTO2;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> ARC_AUTO3;
    public static AnimationManager.AnimationAccessor<ActionAnimation> CERAUNUS_SKILL1;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> CERAUNUS_SKILL2;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> CERAUNUS_SKILL3;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> SOUL_RENDER_SKILL1;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> SOUL_RENDER_SKILL2;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> SOUL_RENDER_SKILL3;
    public static AnimationManager.AnimationAccessor<AttackAnimation> INFERNAL_SKILL1;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> INFERNAL_SKILL2;
    public static AnimationManager.AnimationAccessor<AttackAnimation> INFERNAL_SKILL3;
    public static AnimationManager.AnimationAccessor<ActionAnimation> THE_INCINERATOR_SKILL2;
    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> THE_INCINERATOR_SKILL3;
    public static AnimationManager.AnimationAccessor<ActionAnimation> CLAW_SKILL2;
    public static AnimationManager.AnimationAccessor<ActionAnimation> CLAW_SKILL3;

    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(PEpicCataclysmMod.MOD_ID, (builder) -> {

            CLAW_SHOOT = builder.nextAccessor("living/claw_shoot", (accessor) -> new StaticAnimation(0.15F, true, accessor, Armatures.BIPED));

            MEAT_CUTTER_LOOP = builder.nextAccessor("living/meat_cutter_loop", (accessor) ->
                    new StaticAnimation(0.15F, true, accessor, Armatures.BIPED)
                            .setResourceLocation(EFN.MODID, "biped/thornwheel/thornwheel_loop"));

            BOW_1 = builder.nextAccessor("bow/bow_auto1", (accessor) ->
                    new ScanAttackAnimation(0.15F, 0, 0.15F, 65 / 60F, 65 / 60F,
                            InteractionHand.MAIN_HAND, PECWeaponPresets.BOW_SCAN, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                            .addEvents(setFullBowUseTime(20 / 60F), AnimationEvent.InTimeEvent.create(0.49F,
                                            shootPhantomArrow(1.0F), AnimationEvent.Side.SERVER),
                                    resetBowUseTime(0.6F),
                                    setFullBowUseTime(50 / 60F),
                                    resetBowUseTime(1.1F),
                                    AnimationEvent.InTimeEvent.create(1.0F,
                                            shootPhantomArrow(1.0F), AnimationEvent.Side.SERVER))
                            .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
            );
            BOW_2 = builder.nextAccessor("bow/bow_auto2", (accessor) ->
                    new ScanAttackAnimation(0.15F, 0, 0.15F, 65 / 60F, 65 / 60F,
                            InteractionHand.MAIN_HAND, PECWeaponPresets.BOW_SCAN, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                            .addEvents(setFullBowUseTime(20 / 60F), AnimationEvent.InTimeEvent.create(0.49F,
                                            shootPhantomArrow(1.5F), AnimationEvent.Side.SERVER),
                                    resetBowUseTime(0.6F),
                                    setFullBowUseTime(50 / 60F),
                                    resetBowUseTime(1.1F),
                                    AnimationEvent.InTimeEvent.create(1.0F,
                                            shootPhantomArrow(1.5F), AnimationEvent.Side.SERVER))
                            .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
            );
            BOW_3 = builder.nextAccessor("bow/bow_auto3", (accessor) ->
                    new ScanAttackAnimation(0.15F, 0, 0.15F, 100 / 60F, 120 / 60F,
                            InteractionHand.MAIN_HAND, PECWeaponPresets.BOW_SCAN, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                            .addEvents(
                                    setFullBowUseTime(1.0F),
                                    AnimationEvent.InTimeEvent.create(80F / 60F,
                                            shootPhantomArrow(0.7F, false), AnimationEvent.Side.SERVER),
                                    AnimationEvent.InTimeEvent.create(90F / 60F,
                                            shootPhantomArrow(0.7F, false), AnimationEvent.Side.SERVER),
                                    AnimationEvent.InTimeEvent.create(100F / 60F,
                                            shootPhantomArrow(0.7F), AnimationEvent.Side.SERVER)
                                    , resetBowUseTime(110F / 60F))
                            .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
            );

            BOW_SKILL1 = builder.nextAccessor("skill/bow_skill1", (accessor) ->
                    new ScanAttackAnimation(0.15F, 0, 0.15F, 65 / 60F, 65 / 60F,
                            InteractionHand.MAIN_HAND, PECWeaponPresets.BOW_SCAN, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                            .newTimePair(0.0F, 1.1F)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                            .addEvents(
                                    setFullBowUseTime(0.33F),
                                    AnimationEvent.InTimeEvent.create(0.49F,
                                            shootSandstorm(0.8F, true), AnimationEvent.Side.SERVER),
                                    resetBowUseTime(0.6F),
                                    setFullBowUseTime(0.8F),
                                    AnimationEvent.InTimeEvent.create(1.0F,
                                            shootSandstorm(0.8F, true), AnimationEvent.Side.SERVER),
                                    resetBowUseTime(1.1F)));

            BOW_SKILL2 = builder.nextAccessor("skill/bow_skill2", (accessor) ->
                    new ScanAttackAnimation(0.15F, 0, 0.15F, 65 / 60F, 65 / 60F,
                            InteractionHand.MAIN_HAND, PECWeaponPresets.BOW_SCAN, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                            .newTimePair(0.0F, 1.1F)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                            .addEvents(
                                    setFullBowUseTime(0.33f),
                                    AnimationEvent.InTimeEvent.create(0.5F,
                                            shootCursedSandstorm(1.1F, true), AnimationEvent.Side.SERVER),
                                    resetBowUseTime(0.6F),
                                    setFullBowUseTime(0.9F),
                                    resetBowUseTime(1.1F),
                                    AnimationEvent.InTimeEvent.create(1.0F,
                                            shootCursedSandstorm(1.1F, true), AnimationEvent.Side.SERVER)));

            BOW_SKILL3 = builder.nextAccessor("skill/bow_skill3", (accessor) ->
                    new AttackAnimation(0.15F, 20 / 60F, 20 / 60F, 40 / 60F, 50 / 60F,
                            InteractionHand.MAIN_HAND, PECWeaponPresets.BOW_ELBOW, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED) {
                        @Override
                        protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> animation) {
                            return super.getCoordVector(entitypatch, animation).scale(0);
                        }
                    }
                            .newTimePair(0.0F, 1.0F)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.2F, shootThreeStorm(false), AnimationEvent.Side.SERVER),
                                    AvalonEventUtils.simpleCameraShake((int) (2.2 * 60), 60, 7, 6, 6),
                                    AnimationEvent.InTimeEvent.create(0.2F,
                                            shootRain(true), AnimationEvent.Side.BOTH)
                            ));


            BOW_DASH_ATTACK = builder.nextAccessor("bow/bow_dash_attack", accessor ->
                    new AttackAnimation(0.15F, 0, 0, 40 / 60F, 60 / 60F,
                            PECWeaponPresets.BOW_DASH, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                            .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                            .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0f))
                            .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true));

            BEDIVERE_IDLE = builder.nextAccessor("living/bedivere_idle", (accessor) -> new StaticAnimation(true, accessor, Armatures.BIPED));

            BEDIVERE_AUTO1 = builder.nextAccessor("combat/bedivere_auto1", accessor -> new AvalonAttackAnimation(0.07F, accessor, Armatures.BIPED, 1F, 1
                    , createSimplePhase(26, 35, 45, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().rootJoint, BEDIVERE_1))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AvalonEventUtils.simpleCameraShake(26, 5, 3, 1, 3),
                            AnimationEvent.InTimeEvent.create(0.7F, (entityPatch, self, params) -> {
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                caster.getLookAngle();
                                VoidEffectInvoker.spawnVoidVortexInFront(world, caster, 3, 50);
                            }, AnimationEvent.Side.BOTH))
            );
            BEDIVERE_AUTO2 = builder.nextAccessor("combat/bedivere_auto2", accessor -> new AvalonAttackAnimation(0.07F, accessor, Armatures.BIPED, 1F, 1
                    , createSimplePhase(31, 40, 50, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().rootJoint, CLAW_AUTO3))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AvalonEventUtils.simpleCameraShake(40, 7, 2, 2, 3),
                            ParticleEffectInvoker.simpleGroundSplit(40, -0.5, 0, 0F, 0, 3F, true),
                            PECParticleEffectInvoker.createVoidRingEffect(31, 40),
                            AnimationEvent.InTimeEvent.create(0.65F, (entityPatch, self, params) -> {
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                createForwardVoidRuneCluster(world, caster.getX(), caster.getY(), caster.getZ(), caster, caster.getYRot());
                            }, AnimationEvent.Side.BOTH))
            );
            BEDIVERE_AUTO3 = builder.nextAccessor("combat/bedivere_auto3", accessor -> new AvalonAttackAnimation(0.07F, accessor, Armatures.BIPED, 1F, 1
                    , createSimplePhase(23, 29, 34, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().handR, CLAW))
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
            );
            BEDIVERE_AUTO4 = builder.nextAccessor("combat/bedivere_auto4", accessor -> new AvalonAttackAnimation(0.07F, accessor, Armatures.BIPED, 1F, 1
                    , createSimplePhase(23, 28, 42, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().handL, CLAW))
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
            );
            BEDIVERE_AUTO5 = builder.nextAccessor("combat/bedivere_auto5", accessor -> new AvalonAttackAnimation(0.07F, accessor, Armatures.BIPED, 1F, 1
                    , createSimplePhase(29, 38, 55, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().rootJoint, CLAW_AUTO3))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AvalonEventUtils.simpleCameraShake(29, 15, 4, 3, 5),
                            ParticleEffectInvoker.simpleGroundSplit(29, 0.5, 0, 0F, 0, 3F, true),
                            PECParticleEffectInvoker.createVoidRingEffect(22, 40),
                            AnimationEvent.InTimeEvent.create(0.85F, (entityPatch, self, params) -> {
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                VoidEffectInvoker.createSimpleVoidRuneRing(world, caster.getX(), caster.getY(), caster.getZ(), caster, 2, 5);
                            }, AnimationEvent.Side.BOTH))
            );
            BEDIVERE_SKILL_A = builder.nextAccessor("skill/bedivere_skill_a", accessor -> new AvalonAttackAnimation(0.07F, accessor, Armatures.BIPED, 1F, 1
                    , createSimplePhase(25, 35, 55, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_A_HIT)
                    , createSimplePhase(55, 60, 75, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_A_HIT))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                    .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.9F))
                    .addEvents(AvalonEventUtils.simpleCameraShake(25, 7, 2, 2, 3),
                            ParticleEffectInvoker.simpleGroundSplit(25, 0.5, 0, 0F, 0, 3F, true),
                            ParticleEffectInvoker.simpleGroundSplit(60, 1, 0, 0F, 0, 6F, true),
                            AnimationEvent.InTimeEvent.create(0.4F, (entitypatch, self, params) -> {
                                if (!entitypatch.getOriginal().level().isClientSide()) {
                                    LivingEntity attacker = entitypatch.getOriginal();
                                    ServerLevel level = (ServerLevel) attacker.level();
                                    // 强化冲击波参数
                                    double centerX = attacker.getX();
                                    double centerY = attacker.getY() + 0.2; // 稍微抬升中心点
                                    double centerZ = attacker.getZ();
                                    double baseRadius = 5.0; // 扩大基础半径
                                    double maxRadius = 10.0; // 扩大最大半径
                                    int waveCount = 6; // 增加波次
                                    int particlesPerWave = 100; // 增加每波粒子数
                                    double speed = 0.5; // 提高速度
                                    // 多层冲击波
                                    for (int wave = 0; wave < waveCount; wave++) {
                                        double progress = wave / (double) (waveCount - 1);
                                        double radius = Mth.lerp(progress, baseRadius, maxRadius);
                                        double verticalScale = 1.8 * Math.sin(progress * Math.PI); // 增强垂直波动
                                        for (int i = 0; i < particlesPerWave; i++) {
                                            double angle = 2 * Math.PI * i / particlesPerWave;
                                            double randomSpread = 0.5 * (level.random.nextDouble() - 0.5);
                                            // 粒子位置计算（带螺旋效果）
                                            Vec3 pos = new Vec3(
                                                    radius * Math.cos(angle + progress * 1.5 * Math.PI) + randomSpread,
                                                    verticalScale * Math.sin(angle * 3 + wave * 0.7),
                                                    radius * Math.sin(angle + progress * 1.5 * Math.PI) + randomSpread
                                            ).add(centerX, centerY, centerZ);
                                            // 动态速度（向外扩散）
                                            Vec3 motion = pos.subtract(centerX, centerY, centerZ).normalize()
                                                    .scale(speed * (0.4 + 0.6 * (1 - progress)))
                                                    .add(0, 0.2, 0);
                                            // 紫色系粒子 - 主要效果
                                            if (level.random.nextDouble() < 0.6) {
                                                // 传送门粒子（紫色）
                                                level.sendParticles(
                                                        ParticleTypes.PORTAL,
                                                        pos.x, pos.y, pos.z,
                                                        2, // 每组2个粒子
                                                        motion.x * 0.4, motion.y * 0.9, motion.z * 0.4,
                                                        1.0
                                                );
                                            } else {
                                                // 反转传送门粒子（深紫色）
                                                level.sendParticles(
                                                        ParticleTypes.REVERSE_PORTAL,
                                                        pos.x, pos.y, pos.z,
                                                        2,
                                                        motion.x * 0.3, motion.y * 0.8, motion.z * 0.3,
                                                        0.8
                                                );
                                            }
                                            // 30%概率生成白色系附加粒子
                                            if (level.random.nextDouble() < 0.3) {
                                                if (level.random.nextBoolean()) {
                                                    // END_ROD 粒子（白色光点）
                                                    level.sendParticles(
                                                            ParticleTypes.END_ROD,
                                                            pos.x, pos.y + 0.3, pos.z,
                                                            1,
                                                            motion.x * 1.2, motion.y * 1.8, motion.z * 1.2,
                                                            0.4
                                                    );
                                                } else {
                                                    // 末影人粒子（紫色烟雾）
                                                    level.sendParticles(
                                                            ParticleTypes.DRAGON_BREATH,
                                                            pos.x, pos.y + 0.2, pos.z,
                                                            1,
                                                            motion.x * 0.8, motion.y * 1.2, motion.z * 0.8,
                                                            0.3
                                                    );
                                                }
                                            }
                                        }
                                    }
                                    level.sendParticles(
                                            ParticleTypes.END_ROD,
                                            centerX, centerY + 0.5, centerZ,
                                            50,
                                            1.8, 0.8, 1.8,
                                            1.2 // 提高速度
                                    );
                                    level.sendParticles(
                                            ParticleTypes.PORTAL,
                                            centerX, centerY + 0.5, centerZ,
                                            60,
                                            2.2, 1.2, 2.2,
                                            1.0
                                    );
                                    level.sendParticles(
                                            ParticleTypes.REVERSE_PORTAL,
                                            centerX, centerY + 0.5, centerZ,
                                            40,
                                            1.5, 0.6, 1.5,
                                            0.9
                                    );
                                    level.sendParticles(
                                            ParticleTypes.DRAGON_BREATH,
                                            centerX, centerY + 0.5, centerZ,
                                            25,
                                            1.2, 0.5, 1.2,
                                            0.6
                                    );
                                    level.sendParticles(
                                            ParticleTypes.SOUL,
                                            centerX, centerY + 0.3, centerZ,
                                            20,
                                            0.8, 0.3, 0.8,
                                            0.4
                                    );
                                }
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(0.2F, (entitypatch, self, params) ->
                            {
                                entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), 1.5F, 0, 0);
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(0.95F, (entitypatch, self, params) ->
                            {
                                entitypatch.playSound(SoundEvents.ENDER_DRAGON_GROWL, 1.5F, 0, 0);
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(1.15F, (entitypatch, self, params) ->
                            {
                                entitypatch.playSound(SoundEvents.GENERIC_EXPLODE, 1F, 0, 0);
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(0.95F, summonEnhanceAbyssPortal(), AnimationEvent.Side.SERVER),
                            PECParticleEffectInvoker.spawnExpParticle(65, 0.8F, 0, -0.7F),
                            AnimationEvent.InTimeEvent.create(0.95F, (entityPatch, self, params) -> {
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                VoidEffectInvoker.createVoidRuneRing(world, caster.getX(), caster.getY(), caster.getZ(), caster);
                            }, AnimationEvent.Side.BOTH))
                    .newTimePair(0.0F, 1.5F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
            );

            BEDIVERE_SKILL_B = builder.nextAccessor("skill/bedivere_skill_b", (accessor) ->
                    new AttackAnimation(0.05F, accessor, Armatures.BIPED,
                            new AttackAnimation.Phase(0.0F, 0.016F, 0.066F, 0.133F, 0.133F, InteractionHand.OFF_HAND, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_B_HIT),
                            new AttackAnimation.Phase(0.133F, 0.133F, 0.183F, 0.25F, 0.25F, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_B_HIT),
                            new AttackAnimation.Phase(0.25F, 0.25F, 0.3F, 0.366F, 0.366F, InteractionHand.OFF_HAND, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_B_HIT),
                            new AttackAnimation.Phase(0.366F, 0.366F, 0.416F, 0.483F, 0.483F, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_B_HIT),
                            new AttackAnimation.Phase(0.483F, 0.483F, 0.533F, 0.6F, 0.6F, InteractionHand.OFF_HAND, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_B_HIT),
                            new AttackAnimation.Phase(0.6F, 0.6F, 0.65F, 0.716F, 0.716F, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_B_HIT),
                            new AttackAnimation.Phase(0.716F, 0.716F, 0.766F, 0.833F, 0.833F, InteractionHand.OFF_HAND, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_B_HIT),
                            new AttackAnimation.Phase(0.833F, 0.833F, 0.883F, 1.1F, 1.1F, Armatures.BIPED.get().rootJoint, BEDIVERE_SKILL_B_HIT))
                            .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F))
                            .addEvents(PECParticleEffectInvoker.createForwardEnderJetParticles(10, 60),
                                    AnimationEvent.InTimeEvent.create(0.25F, (entityPatch, self, params) -> {
                                        Level world = entityPatch.getOriginal().level();
                                        LivingEntity caster = entityPatch.getOriginal();
                                        caster.getLookAngle();
                                        VoidEffectInvoker.createVoidRuneInGaps(world, caster);
                                    }, AnimationEvent.Side.BOTH))
                            .newTimePair(0.0F, 1.5F)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
            );

            ARC_AUTO1 =
                    builder.nextAccessor("combat/arc_auto1", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 1F, 1
                                    , createSimplePhase(27, 50, 55, InteractionHand.MAIN_HAND, 0.8F, 1F, Armatures.BIPED.get().toolR, null)
                            )
                                    .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.9F)
                    );

            ARC_AUTO2 =
                    builder.nextAccessor("combat/arc_auto2", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 1F, 1
                                    , createSimplePhase(29, 53, 55, InteractionHand.MAIN_HAND, 0.8F, 1F, Armatures.BIPED.get().toolR, null)
                            )
                                    .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.9F)
                                    .addEvents(AvalonEventUtils.simpleCameraShake(35, 15, 3, 1, 2),
                                            AnimationEvent.InTimeEvent.create(0.35F, (entityPatch, self, params) -> {
                                                LivingEntity caster = entityPatch.getOriginal();
                                                caster.getLookAngle();
                                                ScyllaEffectInvoker.createFocusedSpearBeam(caster, 7, 15.0f);
                                            }, AnimationEvent.Side.BOTH),
                                            AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                                entityPatch.getOriginal().level();
                                                LivingEntity caster = entityPatch.getOriginal();
                                                ScyllaEffectInvoker.createForwardLightningStormLine(caster, 1.5F, 8, 8, 1);
                                            }, AnimationEvent.Side.BOTH))
                    );
            ARC_AUTO3 =
                    builder.nextAccessor("combat/arc_auto3", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 1F, 1
                                    , createSimplePhase(27, 54, 60, InteractionHand.MAIN_HAND, 0.8F, 1F, Armatures.BIPED.get().toolR, null)
                            )
                                    .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.9F)
                                    .addEvents(AvalonEventUtils.simpleCameraShake(35, 15, 3, 1, 2),
                                            AnimationEvent.InTimeEvent.create(0.18F, (entityPatch, self, params) -> {
                                                entityPatch.getOriginal().level();
                                                LivingEntity caster = entityPatch.getOriginal();
                                                ScyllaEffectInvoker.createFanSpearBarrage(caster, 3, 5, 100.0f, 15.0f, 4.0f);
                                            }, AnimationEvent.Side.BOTH),
                                            AnimationEvent.InTimeEvent.create(0.18F, (entityPatch, self, params) -> {
                                                entityPatch.getOriginal().level();
                                                LivingEntity caster = entityPatch.getOriginal();
                                                ScyllaEffectInvoker.createFanLightningStorms(caster, 3.5, 7, 120);
                                            }, AnimationEvent.Side.BOTH),
                                            AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                                entityPatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), 0, 0);
                                            }, AnimationEvent.Side.BOTH))

                    );

            CERAUNUS_SKILL1 = builder.nextAccessor("skill/ceraunus_skill1", (accessor) ->
                    new ActionAnimation(0.15F, 1.5F, accessor, Armatures.BIPED)
                            .addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
                            .newTimePair(0.5F, 1.5F)
                            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                            .newTimePair(0.0F, 1.5F)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.5F, shootCeraunus(), AnimationEvent.Side.SERVER),
                                    AnimationEvent.InTimeEvent.create(0.35F, (entityPatch, self, params) -> {
                                        entityPatch.getOriginal().level();
                                        LivingEntity caster = entityPatch.getOriginal();
                                        ScyllaEffectInvoker.createForwardLightningStormLine(caster, 2.5F, 25, 25, 1);
                                    }, AnimationEvent.Side.BOTH))
            );

            CERAUNUS_SKILL2 = builder.nextAccessor("skill/ceraunus_skill2", accessor -> new AvalonAttackAnimation(0.15F, accessor, Armatures.BIPED, 2F, 2F, createSimplePhase(45, 53, 80, InteractionHand.MAIN_HAND, 1.2F, 1.2F, Armatures.BIPED.get().toolR, null))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                    .newTimePair(0.0F, 1.5F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AnimationEvent.InTimeEvent.create(0.35F, (entitypatch, animation, params) -> {
                                LivingEntity entity = entitypatch.getOriginal();
                                entity.level().addParticle(AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get()
                                        , entity.getX()
                                        , entity.getY()
                                        , entity.getZ()
                                        , Double.longBitsToDouble(entity.getId())
                                        , 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AvalonEventUtils.simpleCameraShake(40, 50, 5, 4, 5),
                            AnimationEvent.InPeriodEvent.create(0.3F, 0.65F, (entityPatch, self, params) -> {
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                ScyllaEffectInvoker.createFocusedSpearBeam(caster, 2, 35.0f);
                            }, AnimationEvent.Side.BOTH),
                            AnimationEvent.InTimeEvent.create(0.6F, (entitypatch, self, params) -> {
                                if (!entitypatch.getOriginal().level().isClientSide()) {
                                    LivingEntity attacker = entitypatch.getOriginal();
                                    ServerLevel level = (ServerLevel) attacker.level();
                                    // 冲击波参数
                                    double centerX = attacker.getX();
                                    double centerY = attacker.getY(); // 脚底位置
                                    double centerZ = attacker.getZ();
                                    double baseRadius = 8.0; // 基础半径
                                    double maxRadius = 15.0; // 最大半径
                                    int waveCount = 3; // 冲击波层数
                                    int particlesPerWave = 80; // 每层粒子数
                                    double speed = 0.4; // 粒子速度
                                    // 生成多层冲击波
                                    for (int wave = 0; wave < waveCount; wave++) {
                                        double radius = baseRadius + (maxRadius - baseRadius) * wave / (waveCount - 1);
                                        for (int i = 0; i < particlesPerWave; i++) {
                                            double angle = 2 * Math.PI * i / particlesPerWave;
                                            double randomOffset = 0.3 * (level.random.nextDouble() - 0.5);
                                            double xOffset = radius * Math.cos(angle) + randomOffset;
                                            double zOffset = radius * Math.sin(angle) + randomOffset;
                                            double motionX = xOffset * speed / radius;
                                            double motionZ = zOffset * speed / radius;
                                            //高度变化
                                            double yOffset = 0.5 * Math.sin(angle * 2 + wave * 0.5);
                                            level.sendParticles(ParticleTypes.SMOKE, centerX + xOffset, centerY + 0.1 + yOffset, centerZ + zOffset, 1, motionX, 0.05, motionZ, 0.8);
                                        }
                                    }

                                    //海浪
                                    level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSounds.HEAVY_SMASH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                                    float yawRadians = (float) Math.toRadians(90.0F + entitypatch.getYRot());
                                    double vecX = Math.cos(yawRadians);
                                    double vecZ = Math.sin(yawRadians);
                                    double vec = 2.0F;
                                    double spawnX = attacker.getX() + vecX * vec;
                                    double spawnY = attacker.getY();
                                    double spawnZ = attacker.getZ() + vecZ * vec;
                                    int numberOfWaves = 4;
                                    float angleStep = 25.0F;
                                    double firstAngleOffset = (double) (numberOfWaves - 1) / (double) 2.0F * (double) angleStep;
                                    for (int k = 0; k < numberOfWaves; ++k) {
                                        double angle = (double) attacker.getYRot() - firstAngleOffset + (double) ((float) k * angleStep);
                                        double rad = Math.toRadians(angle);
                                        double dx = -Math.sin(rad);
                                        double dz = Math.cos(rad);
                                        Wave_Entity WaveEntity = new Wave_Entity(level, attacker, 60, (float) CMCommonConfig.Ceraunus.waveDamage);
                                        WaveEntity.setPos(spawnX, spawnY, spawnZ);
                                        WaveEntity.setState(1);
                                        WaveEntity.setYRot(-((float) (Mth.atan2(dx, dz) * (180D / Math.PI))));
                                        level.addFreshEntity(WaveEntity);
                                    }
                                    for (int k = 0; k < numberOfWaves; ++k) {
                                        double angle = (double) attacker.getYRot() - firstAngleOffset + (double) ((float) k * angleStep);
                                        double rad = Math.toRadians(angle);

                                        double directionX = -Math.sin(rad);
                                        double directionZ = Math.cos(rad);

                                        createDirectionalLightningStormLine(level, attacker,
                                                spawnX, spawnZ,
                                                directionX, directionZ,
                                                15.0,
                                                15,
                                                1.0
                                        );
                                    }
                                }
                            }, AnimationEvent.Side.SERVER),
                            ParticleEffectInvoker.simpleGroundSplit(40, 2, 0, 0, 0, 3F, true),
                            AvalonEventUtils.simpleCameraShake(40, 40, 3, 3, 3)
                    )
            );

            CERAUNUS_SKILL3 = builder.nextAccessor("skill/ceraunus_skill3", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 1, 1
                    , createSimplePhase(29, 39, 79, InteractionHand.MAIN_HAND, 0.8F, 0.8F, Armatures.BIPED.get().toolR, null)
                    , createSimplePhase(79, 85, 120, InteractionHand.MAIN_HAND, 0.8F, 0.8F, Armatures.BIPED.get().rootJoint, CERAUNUS_SKILL))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                    .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .newTimePair(0.0F, 1.5F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                    .addEvents(AnimationEvent.InTimeEvent.create(0.6F, (entityPatch, self, params) -> {
                                LivingEntity attacker = entityPatch.getOriginal();
                                if (attacker.level() instanceof ServerLevel level) {
                                    // 冲击波参数
                                    double centerX = attacker.getX();
                                    double centerY = attacker.getY(); // 脚底位置
                                    double centerZ = attacker.getZ();
                                    double baseRadius = 8.0; // 基础半径
                                    double maxRadius = 15.0; // 最大半径
                                    int waveCount = 3; // 冲击波层数
                                    int particlesPerWave = 80; // 每层粒子数
                                    double speed = 0.4; // 粒子速度
                                    // 生成多层冲击波
                                    for (int wave = 0; wave < waveCount; wave++) {
                                        double radius = baseRadius + (maxRadius - baseRadius) * wave / (waveCount - 1);
                                        for (int i = 0; i < particlesPerWave; i++) {
                                            double angle = 2 * Math.PI * i / particlesPerWave;
                                            double randomOffset = 0.3 * (level.random.nextDouble() - 0.5);
                                            double xOffset = radius * Math.cos(angle) + randomOffset;
                                            double zOffset = radius * Math.sin(angle) + randomOffset;
                                            double motionX = xOffset * speed / radius;
                                            double motionZ = zOffset * speed / radius;
                                            //高度变化
                                            double yOffset = 0.5 * Math.sin(angle * 2 + wave * 0.5);
                                            level.sendParticles(ParticleTypes.SMOKE, centerX + xOffset, centerY + 0.1 + yOffset, centerZ + zOffset, 1, motionX, 0.05, motionZ, 0.8);
                                        }
                                    }

                                    int rune = 7;
                                    int time = 4;
                                    for (int i = 0; i < rune; ++i) {
                                        float throwAngle = (float) i * (float) Math.PI / (float) (rune / 2);

                                        for (int k = 0; k < 5; ++k) {
                                            double d2 = (double) 1.75F * (double) (k + 1);
                                            int d3 = time * (k + 1) - 9;
                                            spawnLightning(attacker, attacker.getX() + (double) Mth.cos(throwAngle) * (double) 1.25F * d2, attacker.getZ() + (double) Mth.sin(throwAngle) * (double) 1.25F * d2, attacker.getY() - 5, attacker.getY() + (double) 2.0F, throwAngle, d3, 2.0F);
                                        }
                                    }
                                }
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(1.4F, (entityPatch, self, params) -> {
                                LivingEntity attacker = entityPatch.getOriginal();
                                if (attacker.level() instanceof ServerLevel level) {
                                    level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSounds.HEAVY_SMASH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                    float yawRadians = (float) Math.toRadians(90.0F + entityPatch.getYRot());
                                    double vecX = Math.cos(yawRadians);
                                    double vecZ = Math.sin(yawRadians);
                                    double vec = 2.0F;
                                    double spawnX = attacker.getX() + vecX * vec;
                                    double spawnY = attacker.getY();
                                    double spawnZ = attacker.getZ() + vecZ * vec;
                                    int numberOfWaves = 12;
                                    float angleStep = 30.0F;
                                    double firstAngleOffset = (double) (numberOfWaves - 1) / (double) 2.0F * (double) angleStep;
                                    for (int k = 0; k < numberOfWaves; ++k) {
                                        double angle = (double) attacker.getYRot() - firstAngleOffset + (double) ((float) k * angleStep);
                                        double rad = Math.toRadians(angle);
                                        double dx = -Math.sin(rad);
                                        double dz = Math.cos(rad);
                                        Wave_Entity WaveEntity = new Wave_Entity(level, attacker, 60, (float) CMCommonConfig.Ceraunus.waveDamage);
                                        WaveEntity.setPos(spawnX, spawnY, spawnZ);
                                        WaveEntity.setState(1);
                                        WaveEntity.setYRot(-((float) (Mth.atan2(dx, dz) * (180D / Math.PI))));
                                        level.addFreshEntity(WaveEntity);
                                    }
                                    for (int k = 0; k < numberOfWaves; ++k) {
                                        double angle = (double) attacker.getYRot() - firstAngleOffset + (double) ((float) k * angleStep);
                                        double rad = Math.toRadians(angle);

                                        double directionX = -Math.sin(rad);
                                        double directionZ = Math.cos(rad);

                                        createDirectionalLightningStormLine(level, attacker,
                                                spawnX, spawnZ,
                                                directionX, directionZ,
                                                15.0,
                                                15,
                                                1.0
                                        );
                                    }
                                }
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(0.3F, FRACTURE_GROUNDSLAM_NOSMOKE, AnimationEvent.Side.CLIENT).params(new Vec3f(0.0F, 0.3F, 0.0F), Armatures.BIPED.get().toolR, 2D, 0.5F),
                            AnimationEvent.InTimeEvent.create(1.4F, FRACTURE_GROUNDSLAM_NOSMOKE, AnimationEvent.Side.CLIENT).params(new Vec3f(0.0F, 0.3F, 0.0F), Armatures.BIPED.get().rootJoint, 4D, 0.55F),
                            AnimationEvent.InTimeEvent.create(1.4F, ScyllaEffectInvoker.SUMMON_THUNDER_UPGRADED, AnimationEvent.Side.SERVER),
                            AvalonEventUtils.simpleCameraShake(80, 40, 4, 4, 4)
                    )
            );

            INFERNAL_AUTO3 = builder.nextAccessor("skill/infernal_auto3", (accessor) -> new DashAttackAnimation(0.25F, 0.2F, 0.35F, 0.6F, 1.2F, null, Armatures.BIPED.get().toolR, accessor, Armatures.BIPED, false)
                    .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER))
                    .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
                    .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, false)
                    .addEvents(AnimationEvent.InTimeEvent.create(0.2F, (entitypatch, self, params) -> {
                                entitypatch.playSound(SoundEvents.BLAZE_SHOOT, 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            PECParticleEffectInvoker.createForwardMagmaEruption(27),
                            ParticleEffectInvoker.simpleGroundSplit(27, 2, 0, 0, 0, 2, true),
                            ParticleEffectInvoker.simpleGroundSplit(27, 4, 0, 0, 0, 2, true),
                            AnimationEvent.InTimeEvent.create(0.3F, (entityPatch, self, params) -> {
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                createMiniForwardFlameJet(world, caster.getX(), caster.getY(), caster.getZ(), caster, caster.getYRot());
                            }, AnimationEvent.Side.BOTH))
            );

            ASTRAPE_AUTO3 = builder.nextAccessor("combat/astrape_auto3", (accessor) ->
                    new AttackAnimation(0.05F, 0.0F, 0.5F, 0.6F, 0.95F, null, Armatures.BIPED.get().toolR, accessor, Armatures.BIPED)
                            .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                            .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                            .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 1F)
                            .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
                            .addEvents(AnimationEvent.InTimeEvent.create(0.55F, (entityPatch, self, params) -> {
                                        entityPatch.getOriginal().level();
                                        LivingEntity caster = entityPatch.getOriginal();
                                        ScyllaEffectInvoker.createFanSpearBarrage(caster, 3, 5, 100.0f, 15.0f, 4.0f);
                                    }, AnimationEvent.Side.BOTH),
                                    AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                        entityPatch.getOriginal().level();
                                        LivingEntity caster = entityPatch.getOriginal();
                                        ScyllaEffectInvoker.createFanLightningStorms(caster, 3.5, 7, 120);
                                    }, AnimationEvent.Side.BOTH),
                                    AnimationEvent.InTimeEvent.create(0.2F, (entitypatch, animation, params) -> {
                                        entitypatch.playSound(SoundEvents.TRIDENT_RIPTIDE_2, 0, 0);
                                    }, AnimationEvent.Side.CLIENT))
            );

            ASTRAPE_DASH = builder.nextAccessor("combat/astrape_dash", (accessor) ->
                    new AttackAnimation(0.05F, 0.2F, 0.35F, 1.0F, 1.2F, ASTRAPE_SKILL, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                            .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(10))
                            .addProperty(AnimationProperty.ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false)
                            .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
                            .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, null)
                            .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                            .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.2F, 1.1F))
                            .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.4F))
                            .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create(Animations.ReusableSources.RESTORE_BOUNDING_BOX, AnimationEvent.Side.BOTH))
                            .addEvents(AnimationProperty.StaticAnimationProperty.TICK_EVENTS, AnimationEvent.SimpleEvent.create(Animations.ReusableSources.RESIZE_BOUNDING_BOX, AnimationEvent.Side.BOTH).params(EntityDimensions.scalable(0.6F, 1.0F)))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.05F, (entityPatch, self, params) -> {
                                        entityPatch.getOriginal().level();
                                        LivingEntity caster = entityPatch.getOriginal();
                                        ScyllaEffectInvoker.createForwardLightningStorm(caster, 0.5);
                                    }, AnimationEvent.Side.BOTH),
                                    AnimationEvent.InTimeEvent.create(1F, (entityPatch, self, params) -> {
                                        entityPatch.getOriginal().level();
                                        LivingEntity caster = entityPatch.getOriginal();
                                        ScyllaEffectInvoker.createForwardLightningStorm(caster, 1);
                                    }, AnimationEvent.Side.BOTH),
                                    AnimationEvent.InPeriodEvent.create(0.35F, 1.0F, (entitypatch, animation, params) -> {
                                        Vec3 pos = entitypatch.getOriginal().position();
                                        for (int x = -1; x <= 1; x += 2) {
                                            for (int z = -1; z <= 1; z += 2) {
                                                Vec3 rand = new Vec3(Math.random() * x, Math.random(), Math.random() * z).normalize().scale(2.0D);
                                                entitypatch.getOriginal().level().addParticle(EpicFightParticles.TSUNAMI_SPLASH.get(), pos.x + rand.x, pos.y + rand.y - 1.0D, pos.z + rand.z, rand.x * 0.1D, rand.y * 0.1D, rand.z * 0.1D);
                                            }
                                        }
                                    }, AnimationEvent.Side.CLIENT))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.35F, (entitypatch, animation, params) -> {
                                entitypatch.playSound(SoundEvents.TRIDENT_RIPTIDE_3, 0, 0);
                            }, AnimationEvent.Side.CLIENT), AnimationEvent.InTimeEvent.create(0.35F, (entitypatch, animation, params) -> {
                                entitypatch.setAirborneState(true);
                            }, AnimationEvent.Side.SERVER))
            );

            ASTRAPE_SKILL1 = builder.nextAccessor("skill/astrape_skill_1", (accessor) ->
                    new AttackAnimation(0.05F, 0.2F, 0.35F, 0.65F, 0.75F, ASTRAPE_SKILL, Armatures.BIPED.get().rootJoint, accessor, Armatures.BIPED)
                            .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(10))
                            .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD_WITH_X_ROT)
                            .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, null)
                            .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                            .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.15F, 0.85F))
                            .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F))
                            .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.ROOT_X_MODIFIER)
                            .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create(Animations.ReusableSources.RESTORE_BOUNDING_BOX, AnimationEvent.Side.BOTH))
                            .addEvents(AnimationProperty.StaticAnimationProperty.TICK_EVENTS, AnimationEvent.SimpleEvent.create(Animations.ReusableSources.RESIZE_BOUNDING_BOX, AnimationEvent.Side.BOTH).params(EntityDimensions.scalable(0.6F, 1.0F)))
                            .addEvents(AnimationEvent.InPeriodEvent.create(0.3F, 0.65F, (entityPatch, self, params) -> {
                                        LivingEntity caster = entityPatch.getOriginal();
                                        caster.getLookAngle();
                                        ScyllaEffectInvoker.createFocusedSpearBeam(caster, 2, 35.0f);
                                    }, AnimationEvent.Side.BOTH),
                                    AnimationEvent.InPeriodEvent.create(0F, 1F, (entityPatch, self, params) -> {
                                        LivingEntity caster = entityPatch.getOriginal();
                                        caster.getLookAngle();
                                        ScyllaEffectInvoker.createForwardLightningStorm(caster, 1);
                                    }, AnimationEvent.Side.BOTH),
                                    AnimationEvent.InPeriodEvent.create(0.35F, 1.0F, (entitypatch, animation, params) -> {
                                        Vec3 pos = entitypatch.getOriginal().position();

                                        for (int x = -1; x <= 1; x += 2) {
                                            for (int z = -1; z <= 1; z += 2) {
                                                Vec3 rand = new Vec3(Math.random() * x, Math.random(), Math.random() * z).normalize().scale(2.0D);
                                                entitypatch.getOriginal().level().addParticle(EpicFightParticles.TSUNAMI_SPLASH.get(), pos.x + rand.x, pos.y + rand.y - 1.0D, pos.z + rand.z, rand.x * 0.1D, rand.y * 0.1D, rand.z * 0.1D);
                                            }
                                        }
                                    }, AnimationEvent.Side.CLIENT))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.35F, (entitypatch, animation, params) -> {
                                entitypatch.playSound(SoundEvents.TRIDENT_RIPTIDE_3, 0, 0);
                            }, AnimationEvent.Side.CLIENT), AnimationEvent.InTimeEvent.create(0.35F, (entitypatch, animation, params) -> {
                                entitypatch.setAirborneState(true);
                            }, AnimationEvent.Side.SERVER))
                            .newTimePair(0.0F, 1.5F)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
            );

            ASTRAPE_SKILL2 = builder.nextAccessor("skill/astrape_skill_2", (accessor) ->
                    new AttackAnimation(0.15F, accessor, Armatures.BIPED,
                            new AttackAnimation.Phase(0.0F, 0.0F, 0.3F, 0.36F, 1.0F, Float.MAX_VALUE, Armatures.BIPED.get().toolR, null),
                            new AttackAnimation.Phase(InteractionHand.MAIN_HAND, Armatures.BIPED.get().rootJoint, null))
                            .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(10))
                            .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.25F))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.05F, (entityPatch, self, params) -> {
                                        LivingEntity target = entityPatch.getTarget();
                                        LivingEntity caster = entityPatch.getOriginal();

                                        if (target == null) {
                                            List<Entity> nearbyEntities = caster.level().getEntities(caster,
                                                    caster.getBoundingBox().inflate(15.0D),
                                                    (e) -> e instanceof LivingEntity && !e.isAlliedTo(caster)
                                            );

                                            if (!nearbyEntities.isEmpty()) {
                                                HitEntityList hitList = new HitEntityList(entityPatch, nearbyEntities, HitEntityList.Priority.HOSTILITY);
                                                if (hitList.next()) {
                                                    target = (LivingEntity) hitList.getEntity();
                                                }
                                            }
                                        }

                                        if (target != null) {
                                            if (!caster.level().isClientSide()) {
                                                ServerLevel level = (ServerLevel) caster.level();
                                                double centerX = target.getX();
                                                double centerY = target.getY();
                                                double centerZ = target.getZ();
                                                double baseRadius = 8.0;
                                                double maxRadius = 15.0;
                                                int waveCount = 3;
                                                int particlesPerWave = 80;
                                                double speed = 0.4;

                                                for (int wave = 0; wave < waveCount; wave++) {
                                                    double radius = baseRadius + (maxRadius - baseRadius) * wave / (waveCount - 1);
                                                    for (int i = 0; i < particlesPerWave; i++) {
                                                        double angle = 2 * Math.PI * i / particlesPerWave;
                                                        double randomOffset = 0.3 * (level.random.nextDouble() - 0.5);
                                                        double xOffset = radius * Math.cos(angle) + randomOffset;
                                                        double zOffset = radius * Math.sin(angle) + randomOffset;
                                                        double motionX = xOffset * speed / radius;
                                                        double motionZ = zOffset * speed / radius;

                                                        double yOffset = 0.5 * Math.sin(angle * 2 + wave * 0.5);
                                                        level.sendParticles(ParticleTypes.SMOKE, centerX + xOffset, centerY + 0.1 + yOffset, centerZ + zOffset, 1, motionX, 0.05, motionZ, 0.8);
                                                    }
                                                }

                                                int rune = 7;
                                                int time = 4;
                                                for (int i = 0; i < rune; ++i) {
                                                    float throwAngle = (float) i * (float) Math.PI / (float) (rune / 2);

                                                    for (int k = 0; k < 5; ++k) {
                                                        double d2 = (double) 1.75F * (double) (k + 1);
                                                        int d3 = time * (k + 1) - 9;
                                                        spawnLightning(target, target.getX() + (double) Mth.cos(throwAngle) * (double) 1.25F * d2, target.getZ() + (double) Mth.sin(throwAngle) * (double) 1.25F * d2, target.getY() - 5, target.getY() + (double) 2.0F, throwAngle, d3, 2.0F);
                                                    }
                                                }
                                            }
                                        }
                                    }, AnimationEvent.Side.SERVER),

                                    AnimationEvent.InTimeEvent.create(0.35F, (entityPatch, self, params) -> {
                                        LivingEntity target = entityPatch.getTarget();
                                        LivingEntity caster = entityPatch.getOriginal();

                                        if (target == null) {
                                            List<Entity> nearbyEntities = caster.level().getEntities(caster,
                                                    caster.getBoundingBox().inflate(15.0D),
                                                    (e) -> e instanceof LivingEntity && !e.isAlliedTo(caster)
                                            );

                                            if (!nearbyEntities.isEmpty()) {
                                                HitEntityList hitList = new HitEntityList(entityPatch, nearbyEntities, HitEntityList.Priority.HOSTILITY);
                                                if (hitList.next()) {
                                                    target = (LivingEntity) hitList.getEntity();
                                                }
                                            }
                                        }

                                        if (target != null) {
                                            if (!caster.level().isClientSide()) {
                                                ServerLevel level = (ServerLevel) caster.level();
                                                float yawRadians = (float) Math.toRadians(90.0F + entityPatch.getYRot());
                                                double vecX = Math.cos(yawRadians);
                                                double vecZ = Math.sin(yawRadians);
                                                double vec = 2.0F;
                                                double spawnX = target.getX() + vecX * vec;
                                                double spawnY = target.getY();
                                                double spawnZ = target.getZ() + vecZ * vec;
                                                int numberOfWaves = 12;
                                                float angleStep = 30.0F;
                                                double firstAngleOffset = (double) (numberOfWaves - 1) / (double) 2.0F * (double) angleStep;

                                                createRandomDirectionalSpearBarrage(level, caster, target,
                                                        spawnX, spawnY, spawnZ,
                                                        numberOfWaves, angleStep, firstAngleOffset);

                                                for (int k = 0; k < numberOfWaves; ++k) {
                                                    double angle = (double) target.getYRot() - firstAngleOffset + (double) ((float) k * angleStep);
                                                    double rad = Math.toRadians(angle);

                                                    double directionX = -Math.sin(rad);
                                                    double directionZ = Math.cos(rad);

                                                    createDirectionalLightningStormLine(level, caster,
                                                            spawnX, spawnZ,
                                                            directionX, directionZ,
                                                            15.0,
                                                            15,
                                                            1.0
                                                    );
                                                }
                                            }
                                        }
                                    }, AnimationEvent.Side.SERVER),
                                    AnimationEvent.InTimeEvent.create(0.35F, ScyllaEffectInvoker.SUMMON_THUNDER, AnimationEvent.Side.SERVER),
                                    AnimationEvent.InTimeEvent.create(0.35F, FRACTURE_TARGET_GROUNDSLAM_NOSMOKE, AnimationEvent.Side.BOTH)
                                            .params(new Vec3f(0.0F, 0.1F, 0.0F), Armatures.BIPED.get().toolR, 4D, 0.5F),
                                    AnimationEvent.InTimeEvent.create(0.1F, (entitypatch, animation, params) -> {
                                        entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), 0, 0);
                                    }, AnimationEvent.Side.CLIENT),
                                    AnimationEvent.InTimeEvent.create(0.35F, (entitypatch, animation, params) -> {
                                        entitypatch.playSound(SoundEvents.TRIDENT_THUNDER, 0, 0);
                                    }, AnimationEvent.Side.CLIENT))
                            .newTimePair(0.0F, 1.5F)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
            );

            ANNIHILATOR_AUTO1 = builder.nextAccessor("combat/annihilator_auto1", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 0.7F, 1
                    , createSimplePhase(15, 21, 24, InteractionHand.MAIN_HAND, 0.5F, 1F, Armatures.BIPED.get().toolL, null)
                    , createSimplePhase(25, 31, 36, InteractionHand.MAIN_HAND, 0.5F, 1F, Armatures.BIPED.get().toolR, null))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
            );
            ANNIHILATOR_AUTO2 = builder.nextAccessor("combat/annihilator_auto2", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 0.7F, 1
                    , createSimplePhase(16, 22, 25, InteractionHand.MAIN_HAND, 0.5F, 1F, Armatures.BIPED.get().toolR, null)
                    , createSimplePhase(25, 31, 36, InteractionHand.MAIN_HAND, 0.5F, 1F, Armatures.BIPED.get().toolL, null))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
            );
            ANNIHILATOR_AUTO3 = builder.nextAccessor("combat/annihilator_auto3", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 0.8F, 1
                    , createSimplePhase(19, 27, 34, InteractionHand.MAIN_HAND, 1.1F, 1.5F, Armatures.BIPED.get().rootJoint, DUAL_SWORD_AUTO_3))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, ModSounds.MALEDICTUS_MACE_SWING.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AnimationEvent.InTimeEvent.create(0.4F, (entityPatch, self, params) -> {
                        entityPatch.getOriginal().level();
                        LivingEntity caster = entityPatch.getOriginal();
                        caster.getLookAngle();
                        createForwardAxeBlade(caster);
                    }, AnimationEvent.Side.BOTH))
            );
            ANNIHILATOR_AUTO4 = builder.nextAccessor("combat/annihilator_auto4", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 0.7F, 1
                    , createSimplePhase(24, 33, 43, InteractionHand.MAIN_HAND, 1.2F, 1.2F, Armatures.BIPED.get().rootJoint, DUAL_SWORD_AUTO_4))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, ModSounds.MALEDICTUS_MACE_SWING.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AnimationEvent.InTimeEvent.create(0.45F, (entitypatch, self, params) ->
                            {
                                entitypatch.playSound(EpicFightSounds.WHOOSH_SHARP.get(), 1, 1, 1);
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(0.45F, (entityPatch, self, params) -> {
                                entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                createClawAxeBlades(caster);
                            }, AnimationEvent.Side.BOTH))
            );
            ANNIHILATOR_SKILL_1 = builder.nextAccessor("skill/annihilator_skill1", accessor -> new AvalonAttackAnimation(0.15F, accessor, Armatures.BIPED, 1F, 1
                    , createSimplePhase(31, 42, 47, InteractionHand.MAIN_HAND, 1.0F, 1F,
                    AttackAnimation.JointColliderPair.of(Armatures.BIPED.get().toolR, null),
                    AttackAnimation.JointColliderPair.of(Armatures.BIPED.get().toolL, null)))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, ModSounds.MALEDICTUS_MACE_SWING.get())
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AnimationEvent.InTimeEvent.create(0.55F, (entitypatch, self, params) ->
                            {
                                entitypatch.playSound(ModSounds.MALEDICTUS_SHORT_ROAR.get(), 1, 1, 1);
                            }, AnimationEvent.Side.SERVER),
                            AvalonEventUtils.simpleGroundSplit(46, 2, 0, 0, 0, 4, true),
                            AvalonEventUtils.simpleCameraShake(46, 12, 5, 5, 5),
                            AnimationEvent.InTimeEvent.create(0.73F, (entitypatch, self, params) -> {
                                if (!entitypatch.getOriginal().level().isClientSide()) {
                                    LivingEntity attacker = entitypatch.getOriginal();
                                    ServerLevel level = (ServerLevel) attacker.level();
                                    // 冲击波参数
                                    double centerX = attacker.getX();
                                    double centerY = attacker.getY(); // 脚底位置
                                    double centerZ = attacker.getZ();
                                    double baseRadius = 8.0; // 基础半径
                                    double maxRadius = 15.0; // 最大半径
                                    int waveCount = 3; // 冲击波层数
                                    int particlesPerWave = 80; // 每层粒子数
                                    double speed = 0.4; // 粒子速度
                                    // 生成多层冲击波
                                    for (int wave = 0; wave < waveCount; wave++) {
                                        double radius = baseRadius + (maxRadius - baseRadius) * wave / (waveCount - 1);
                                        for (int i = 0; i < particlesPerWave; i++) {
                                            double angle = 2 * Math.PI * i / particlesPerWave;
                                            double randomOffset = 0.3 * (level.random.nextDouble() - 0.5);
                                            double xOffset = radius * Math.cos(angle) + randomOffset;
                                            double zOffset = radius * Math.sin(angle) + randomOffset;
                                            double motionX = xOffset * speed / radius;
                                            double motionZ = zOffset * speed / radius;
                                            //高度变化
                                            double yOffset = 0.5 * Math.sin(angle * 2 + wave * 0.5);
                                            level.sendParticles(
                                                    ModParticle.PHANTOM_WING_FLAME.get(),
                                                    centerX + xOffset,
                                                    centerY + 0.1 + yOffset,
                                                    centerZ + zOffset,
                                                    1,
                                                    motionX,
                                                    0.05,
                                                    motionZ,
                                                    0.8
                                            );
                                        }
                                    }
                                    // 中心爆炸效果
                                    level.sendParticles(
                                            ModParticle.SOUL_LAVA.get(),
                                            centerX,
                                            centerY + 0.5,
                                            centerZ,
                                            50,
                                            1.5, 0.5, 1.5,
                                            0.7
                                    );
                                }
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(0.73F, (entityPatch, self, params) -> {
                                entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                createRadialAxeBlades(caster);
                            }, AnimationEvent.Side.BOTH))
                    .newTimePair(0.0F, 1.5F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
            );
            ANNIHILATOR_SKILL_2 =
                    builder.nextAccessor("skill/annihilator_skill2", accessor -> new AvalonAttackAnimation(0.15F, accessor, Armatures.BIPED, 1F, 1
                            , createSimplePhase(32, 42, 47, InteractionHand.MAIN_HAND, 1.0F, 1F,
                            AttackAnimation.JointColliderPair.of(Armatures.BIPED.get().rootJoint, EXSILIUMGLADIUS_ABBB_HIT),
                            AttackAnimation.JointColliderPair.of(Armatures.BIPED.get().rootJoint, EXSILIUMGLADIUS_ABBB_HIT)))
                            .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                            .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, ModSounds.MALEDICTUS_MACE_SWING.get())
                            .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(MAX_VALUE))
                            .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.55F, (entitypatch, self, params) ->
                                    {
                                        entitypatch.playSound(ModSounds.MALEDICTUS_SHORT_ROAR.get(), 1, 1, 1);
                                    }, AnimationEvent.Side.SERVER),
                                    AvalonEventUtils.simpleGroundSplit(37, 2, 0, 0, 0, 3, true),
                                    AvalonEventUtils.simpleGroundSplit(37, 6, 0, 0, 0, 2, true),
                                    AvalonEventUtils.simpleGroundSplit(37, 10, 0, 0, 0, 2, true),
                                    AvalonEventUtils.simpleGroundSplit(37, 14, 0, 0, 0, 2, true),
                                    AvalonEventUtils.simpleCameraShake(37, 12, 5, 5, 5),
                                    AnimationEvent.InTimeEvent.create(0.55F, (entitypatch, self, params) -> {
                                        if (!entitypatch.getOriginal().level().isClientSide()) {
                                            LivingEntity attacker = entitypatch.getOriginal();
                                            ServerLevel level = (ServerLevel) attacker.level();
                                            Vec3 lookVec = attacker.getLookAngle(); // 获取玩家朝向向量

                                            double centerX = attacker.getX();
                                            double centerY = attacker.getY() + 0.5; // 调整为腰部高度
                                            double centerZ = attacker.getZ();

                                            double baseRadius = 8.0;
                                            double maxRadius = 15.0;
                                            int waveCount = 3;
                                            int particlesPerWave = 80;
                                            double speed = 0.4;

                                            for (int wave = 0; wave < waveCount; wave++) {
                                                double radius = baseRadius + (maxRadius - baseRadius) * wave / (waveCount - 1);
                                                for (int i = 0; i < particlesPerWave; i++) {
                                                    double angle = 2 * Math.PI * i / particlesPerWave;
                                                    double randomOffset = 0.3 * (level.random.nextDouble() - 0.5);
                                                    double xOffset = radius * Math.cos(angle) + randomOffset;
                                                    double zOffset = radius * Math.sin(angle) + randomOffset;
                                                    double motionX = xOffset * speed / radius;
                                                    double motionZ = zOffset * speed / radius;
                                                    double yOffset = 0.5 * Math.sin(angle * 2 + wave * 0.5);

                                                    level.sendParticles(
                                                            ModParticle.SOUL_LAVA.get(),
                                                            centerX + xOffset,
                                                            centerY + yOffset,
                                                            centerZ + zOffset,
                                                            1,
                                                            motionX,
                                                            0.05,
                                                            motionZ,
                                                            0.8
                                                    );
                                                }
                                            }

                                            level.sendParticles(
                                                    ModParticle.PHANTOM_WING_FLAME.get(),
                                                    centerX,
                                                    centerY,
                                                    centerZ,
                                                    50,
                                                    1.5, 0.5, 1.5,
                                                    0.7
                                            );

                                            int segments = 5; // 共5段(10格距离)
                                            double height = 6.0;
                                            int verticalParticles = 25; // 垂直粒子数
                                            int ringParticles = 15; // 每段顶部环形粒子数

                                            for (int seg = 1; seg <= segments; seg++) {
                                                double forwardDist = seg * 2.0; // 每2格一段
                                                double segX = centerX + lookVec.x * forwardDist;
                                                double segZ = centerZ + lookVec.z * forwardDist;

                                                // 3.1 垂直粒子柱
                                                for (int i = 0; i < verticalParticles; i++) {
                                                    double yPos = centerY + (height * i / verticalParticles);
                                                    double randomSpread = 0.4 * (level.random.nextDouble() - 0.5);

                                                    // 垂直于视角方向的扩散
                                                    double spreadX = lookVec.z * randomSpread;
                                                    double spreadZ = -lookVec.x * randomSpread;

                                                    level.sendParticles(
                                                            ModParticle.SOUL_LAVA.get(),
                                                            segX + spreadX,
                                                            yPos,
                                                            segZ + spreadZ,
                                                            1,
                                                            0, 0.08, 0, // 粒子向上飘散
                                                            0.6
                                                    );
                                                }

                                                // 3.2 顶部环形效果
                                                for (int i = 0; i < ringParticles; i++) {
                                                    double angle = 2 * Math.PI * i / ringParticles;
                                                    double radius = 1.5;
                                                    double xOffset = radius * Math.cos(angle);
                                                    double zOffset = radius * Math.sin(angle);

                                                    // 使环形始终面向玩家
                                                    double worldX = segX + lookVec.z * xOffset - lookVec.x * zOffset;
                                                    double worldZ = segZ + lookVec.x * xOffset + lookVec.z * zOffset;

                                                    level.sendParticles(
                                                            ModParticle.PHANTOM_WING_FLAME.get(),
                                                            worldX,
                                                            centerY + height,
                                                            worldZ,
                                                            1,
                                                            0, 0.15, 0, // 更强的上浮速度
                                                            0.7
                                                    );
                                                }
                                            }
                                        }
                                    }, AnimationEvent.Side.SERVER),
                                    AnimationEvent.InTimeEvent.create(0.73F, (entitypatch, self, params) -> {
                                        if (!entitypatch.getOriginal().level().isClientSide()) {
                                            LivingEntity attacker = entitypatch.getOriginal();
                                            ServerLevel level = (ServerLevel) attacker.level();
                                            // 冲击波参数
                                            double centerX = attacker.getX();
                                            double centerY = attacker.getY(); // 脚底位置
                                            double centerZ = attacker.getZ();
                                            double baseRadius = 8.0; // 基础半径
                                            double maxRadius = 15.0; // 最大半径
                                            int waveCount = 3; // 冲击波层数
                                            int particlesPerWave = 80; // 每层粒子数
                                            double speed = 0.4; // 粒子速度
                                            // 生成多层冲击波
                                            for (int wave = 0; wave < waveCount; wave++) {
                                                double radius = baseRadius + (maxRadius - baseRadius) * wave / (waveCount - 1);
                                                for (int i = 0; i < particlesPerWave; i++) {
                                                    double angle = 2 * Math.PI * i / particlesPerWave;
                                                    double randomOffset = 0.3 * (level.random.nextDouble() - 0.5);
                                                    double xOffset = radius * Math.cos(angle) + randomOffset;
                                                    double zOffset = radius * Math.sin(angle) + randomOffset;
                                                    double motionX = xOffset * speed / radius;
                                                    double motionZ = zOffset * speed / radius;
                                                    //高度变化
                                                    double yOffset = 0.5 * Math.sin(angle * 2 + wave * 0.5);
                                                    level.sendParticles(
                                                            ModParticle.PHANTOM_WING_FLAME.get(),
                                                            centerX + xOffset,
                                                            centerY + 0.1 + yOffset,
                                                            centerZ + zOffset,
                                                            1,
                                                            motionX,
                                                            0.05,
                                                            motionZ,
                                                            0.8
                                                    );
                                                }
                                            }
                                            // 中心爆炸效果
                                            level.sendParticles(
                                                    ModParticle.SOUL_LAVA.get(),
                                                    centerX,
                                                    centerY + 0.5,
                                                    centerZ,
                                                    50,
                                                    1.5, 0.5, 1.5,
                                                    0.7
                                            );
                                        }
                                    }, AnimationEvent.Side.SERVER),
                                    AnimationEvent.InTimeEvent.create(0.73F, (entityPatch, self, params) -> {
                                        entityPatch.getOriginal().level();
                                        LivingEntity caster = entityPatch.getOriginal();
                                        caster.getLookAngle();
                                        createFiveClawAxeBlades(caster);
                                    }, AnimationEvent.Side.BOTH)
                            )
                            .newTimePair(0.0F, 1.5F)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                    );

            INFERNAL_SKILL1 = builder.nextAccessor("skill/infernal_skill1", (accessor) -> new AttackAnimation(0.15F, accessor, Armatures.BIPED,
                    new AttackAnimation.Phase(0.0F, 0.0F, 0.0F, 0.2F, 0.45F, 0.45F, Armatures.BIPED.get().rootJoint, ColliderPreset.STEEL_WHIRLWIND),
                    new AttackAnimation.Phase(0.45F, 0.45F, 0.45F, 0.65F, 1.0F, 1.0F, Armatures.BIPED.get().rootJoint, ColliderPreset.STEEL_WHIRLWIND),
                    new AttackAnimation.Phase(1.0F, 1.0F, 1.0F, 1.2F, 2.55F, Float.MAX_VALUE, Armatures.BIPED.get().rootJoint, ColliderPreset.STEEL_WHIRLWIND))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER))
                    .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 4)
                    .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
                    .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, null)
                    .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, (animation, entitypatch, transformSheet) -> {
                        if (!animation.isLinkAnimation()) {
                            transformSheet.readFrom(animation.getCoord().copyAll().extendsZCoord(1.8F, 0, 3));
                        } else {
                            MoveCoordFunctions.RAW_COORD.set(animation, entitypatch, transformSheet);
                        }
                    })
                    .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, false)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F))
                    .addEvents(AnimationEvent.InTimeEvent.create(0.3F, (entitypatch, self, params) -> {
                                entitypatch.playSound(SoundEvents.BLAZE_SHOOT, 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AnimationEvent.InTimeEvent.create(0.1F, (entitypatch, self, params) -> {
                                entitypatch.playSound(SoundEvents.FIRECHARGE_USE, 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AvalonEventUtils.particleTrail(20, 75, InteractionHand.MAIN_HAND, new Vec3(0, 0, -1.5F), new Vec3(0, 0, -1.8F), 4, 2, ParticleTypes.SMALL_FLAME, 0.3F),
                            AnimationEvent.InTimeEvent.create(0.95F, (entityPatch, self, params) -> {
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();

                                Vec3 lookVec = caster.getLookAngle();

                                double spawnX = caster.getX() + lookVec.x * 2.2;
                                double spawnY = caster.getY() + caster.getEyeHeight() * 0.8;
                                double spawnZ = caster.getZ() + lookVec.z * 2.2;

                                Flare_Bomb_Entity bomb = spawnFlareBomb(world, spawnX, spawnY, spawnZ, caster);

                                if (bomb != null) {
                                    bomb.setDeltaMovement(0, -0.35, 0);
                                }
                            }, AnimationEvent.Side.BOTH))
                    .newTimePair(0.0F, 1.5F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
            );

            INFERNAL_SKILL2 = builder.nextAccessor("skill/infernal_skill2", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED, 1F, 1
                    , createSimplePhase(33, 44, 70, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().rootJoint, INFERNAL_SKILL2_BOX))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AnimationEvent.InTimeEvent.create(0.1F, (entitypatch, self, params) -> {
                                entitypatch.playSound(SoundEvents.FIRECHARGE_USE, 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AnimationEvent.InTimeEvent.create(0.55F, (entitypatch, self, params) -> {
                                entitypatch.playSound(SoundEvents.GENERIC_EXPLODE, 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            ParticleEffectInvoker.simpleGroundSplit(33, 2, 0, 0, 0, 3, true),
                            ParticleEffectInvoker.simpleGroundSplit(33, 6, 0, 0, 0, 2, true),
                            ParticleEffectInvoker.simpleGroundSplit(33, 10, 0, 0, 0, 2, true),
                            ParticleEffectInvoker.simpleGroundSplit(33, 14, 0, 0, 0, 2, true),
                            AvalonEventUtils.simpleCameraShake(33, 12, 5, 5, 5),
                            PECParticleEffectInvoker.createForwardFlameJetParticles(10, 25),
                            AnimationEvent.InTimeEvent.create(0.38F, (entityPatch, self, params) -> {
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                caster.getLookAngle();
                                createForwardFlameJet(world, caster.getX(), caster.getY(), caster.getZ(), caster, caster.getYRot());
                            }, AnimationEvent.Side.BOTH)
                    )
                    .newTimePair(0.0F, 1.5F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
            );

            INFERNAL_SKILL3 = builder.nextAccessor("skill/infernal_skill3", (accessor) -> (new AttackAnimation(0.1F, accessor, Armatures.BIPED,
                    new AttackAnimation.Phase(0.0F, 0.8F, 1.16F, 1.16F, 1.8F, Armatures.BIPED.get().toolR, INFERNAL_SKILL3_BOX)))
                    .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                    .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
                    .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(30.0F))
                    .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F))
                    .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                    .addEvents(AvalonEventUtils.simpleCameraShake(60, 40, 4, 4, 7),
                            PECParticleEffectInvoker.createLavaRingEffect(30, 70),
                            PECParticleEffectInvoker.createMagmaEruption(65),
                            AnimationEvent.InTimeEvent.create(0.9F, FRACTURE_GROUNDSLAM_NOSMOKE, AnimationEvent.Side.CLIENT).params(new Vec3f(0.0F, 0.0F, 0.0F), Armatures.BIPED.get().toolR, 5D, 0F),
                            AnimationEvent.InTimeEvent.create(0.1F, (entitypatch, self, params) -> {
                                entitypatch.playSound(SoundEvents.FIRECHARGE_USE, 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AnimationEvent.InTimeEvent.create(0.8F, (entityPatch, self, params) -> {
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();

                                Vec3 lookVec = caster.getLookAngle();

                                double spawnX = caster.getX() + lookVec.x * 1.15;
                                double spawnY = caster.getY() + caster.getEyeHeight() * 0.8;
                                double spawnZ = caster.getZ() + lookVec.z * 1.15;

                                Flare_Bomb_Entity bomb = spawnFlareBomb(world, spawnX, spawnY, spawnZ, caster);

                                if (bomb != null) {
                                    bomb.setDeltaMovement(0, -0.35, 0);
                                }
                                createEnhancedFlameJetBurst(world, caster.getX(), caster.getY(), caster.getZ(), caster);
                            }, AnimationEvent.Side.BOTH))
                    .newTimePair(0.0F, 1.5F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
            );

            SOUL_RENDER_SKILL1 = builder.nextAccessor("skill/soul_render_skill1", accessor -> new AvalonAttackAnimation(0.15F, accessor, Armatures.BIPED, 1.1F, 1,
                    createSimplePhase(38, 56, 75, InteractionHand.MAIN_HAND, 1.5F, 1.5F, Armatures.BIPED.get().toolR, null))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                    .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(80))
                    .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
                    .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
                    .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                    .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                    .newTimePair(0.0F, 1.3F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                    .newTimePair(0.0F, Float.MAX_VALUE)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(
                            EffectEntityInvoker.clearFireWind(75),
                            AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                entityPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY.get(), 20, 10, false, false, false));
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, animation, params) -> {
                                LivingEntity entity = entityPatch.getOriginal();
                                entity.level().addParticle(AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AvalonEventUtils.simpleCameraShake(43, 40, 3, 3, 3),
                            PECEffectConditionParticleTrail.buffedParticleTrail(0, 55, InteractionHand.MAIN_HAND, new Vec3(0, 0, -1.29F), new Vec3(0, 0, -1.3F), 8, 2, ModParticle.PHANTOM_WING_FLAME.get(), 0F),
                            AnimationEvent.InTimeEvent.create(0.6F, (entityPatch, self, params) -> {
                                RenderRushCapability.IRenderRushCapability ChargeCapability = ModCapabilities.getCapability(entityPatch.getOriginal(), ModCapabilities.RENDER_RUSH_CAPABILITY);
                                if (ChargeCapability != null) {
                                    ChargeCapability.setRush(true);
                                    ChargeCapability.setTimer(30 / 2);
                                    ChargeCapability.setdamage(0.1F);
                                }
                            }, AnimationEvent.Side.BOTH)
                    )
            );

            SOUL_RENDER_SKILL2 = builder.nextAccessor("skill/soul_render_skill2", accessor -> new AvalonAttackAnimation(0.15F, accessor, Armatures.BIPED, 1.2F, 1,
                    createSimplePhase(52, 75, 90, InteractionHand.MAIN_HAND, 2.0F, 8.0F, Armatures.BIPED.get().toolR, MEEN_LANCE_1))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(120))
                    .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                    .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                    .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE, EpicFightDamageTypeTags.BYPASS_DODGE))
                    .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                    .newTimePair(0.0F, 1.0F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                    .newTimePair(0.0F, Float.MAX_VALUE)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(EffectEntityInvoker.clearFireWind(80),
                            AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, animation, params) -> {
                                LivingEntity entity = entityPatch.getOriginal();
                                entity.level().addParticle(AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                entityPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY.get(), 20, 10, false, false, false));
                            }, AnimationEvent.Side.SERVER),
                            AvalonEventUtils.simpleCameraShake(53, 70, 7, 6, 6),
                            PECEffectConditionParticleTrail.buffedParticleTrail(56, 70, InteractionHand.MAIN_HAND
                                    , new Vec3(0, 0, -2.25F), new Vec3(0, 0, -2.3F), 12, 3, ModParticle.SOUL_LAVA.get(), 0.3F),
                            PECEffectConditionParticleTrail.buffedParticleTrail(52, 70, InteractionHand.MAIN_HAND
                                    , new Vec3(-0.2F, 0, -2.25F), new Vec3(-0.2F, 0, -2.3F), 8, 10, ModParticle.PHANTOM_WING_FLAME.get(), 0.3F),
                            WeaponTrailGroundSplitter.create(54, 80, InteractionHand.MAIN_HAND
                                    , new Vec3(0, 0, -2.25F), new Vec3(0, 0, -2.3F),
                                    1.8F, ModParticle.PHANTOM_WING_FLAME.get(), 0, 3F, 6, 15),
                            AnimationEvent.InTimeEvent.create(1.0F, (entityPatch, self, params) -> {
                                double radius = 6.0F;
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                ScreenShake_Entity.ScreenShake(world, caster.position(), 30.0F, 0.1F, 0, 30);
                                world.playSound(null, caster.getX(), caster.getY(), caster.getZ(), ModSounds.MALEDICTUS_SHORT_ROAR.get(), SoundSource.PLAYERS, 2.0F, 1.0F);
                                world.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.5F, 1.0F / (caster.getRandom().nextFloat() * 0.4F + 0.8F));

                                for (Entity entity : world.getEntities(caster, caster.getBoundingBox().inflate(radius, radius, radius))) {
                                    if (entity instanceof LivingEntity living && living.isAlive()) {
                                        living.addEffect(new MobEffectInstance(ModEffect.EFFECTSTUN.get(), 60, 0));
                                    }
                                }
                                if (world.isClientSide) {
                                    world.addParticle(new RingParticleOptions(0.0F, ((float)Math.PI / 2F), 30, 86, 236, 204, 1.0F, 85.0F, false, 0), caster.getX(), caster.getY() + (double)0.03F, caster.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
                                }
                            }, AnimationEvent.Side.BOTH)
                    )
            );

            SOUL_RENDER_SKILL3 = builder.nextAccessor("skill/soul_render_skill3", accessor -> new AvalonAttackAnimation(0.15F, accessor, Armatures.BIPED, 1, 1,
                    createSimplePhase(59, 100, 120, InteractionHand.MAIN_HAND, 2.0F, 0, Armatures.BIPED.get().rootJoint, MEEN_LANCE_CHARGE3))
                    .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100))
                    .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                    .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10))
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                    .newTimePair(0.0F, 1.5F)
                    .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                    .newTimePair(0.0F, Float.MAX_VALUE)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(// 第一阶段：火焰爆发效果（客户端）
                            AnimationEvent.InTimeEvent.create(1.1F, (entityPatch, self, params) -> {
                                if (entityPatch.getOriginal().level().isClientSide()) {
                                    LivingEntity attacker = entityPatch.getOriginal();
                                    Level level = attacker.level();

                                    // ===== 强化参数配置 =====
                                    float intensity = 2.5f; // 增强强度系数
                                    int coneParticles = (int) (150 * intensity); // 增加锥形粒子数

                                    // ===== 改进版：完整形状的扩散火圈 =====
                                    Vec3 centerPos = attacker.position().add(0, 0.05, 0); // 中心位置
                                    int totalRings = 5; // 总圈数
                                    int particlesPerRing = 36; // 每圈粒子数(36个粒子形成完整圆)
                                    float maxRadius = 5.0f; // 最大半径
                                    float duration = 0.5f; // 扩散持续时间(秒)
                                    float currentTime = 0.0f; // 当前时间(用于动画)

                                    currentTime += 0.05f; // 假设每帧0.05秒
                                    float progress = Math.min(currentTime / duration, 1.0f);

                                    for (int ring = 0; ring < totalRings; ring++) {
                                        // 当前圈的半径(从内到外)
                                        float radius = maxRadius * (ring + progress) / totalRings;

                                        // 完整圆形生成
                                        for (int i = 0; i < particlesPerRing; i++) {
                                            float angle = (float) (6.283185307179586 * i / particlesPerRing);
                                            float xOffset = radius * Mth.cos(angle);
                                            float zOffset = radius * Mth.sin(angle);

                                            // 基础火焰粒子
                                            level.addParticle(
                                                    ModParticle.PHANTOM_WING_FLAME.get(),
                                                    centerPos.x + xOffset,
                                                    centerPos.y + 0.05f, // 紧贴地面
                                                    centerPos.z + zOffset,
                                                    xOffset * 0.1f, // 轻微的向外速度
                                                    0.08f, // 轻微上浮
                                                    zOffset * 0.1f
                                            );

                                            // 每5个粒子添加一个强化粒子
                                            if (i % 5 == 0) {
                                                level.addParticle(
                                                        ModParticle.SOUL_LAVA.get(), // 使用灵魂火增加视觉效果
                                                        centerPos.x + xOffset,
                                                        centerPos.y + 0.07f,
                                                        centerPos.z + zOffset,
                                                        xOffset * 0.15f,
                                                        0.12f,
                                                        zOffset * 0.15f
                                                );
                                            }
                                        }
                                    }


                                    // ===== 第一阶段：武器球状粒子 =====
                                    OpenMatrix4f transformMatrix = entityPatch.getArmature()
                                            .getBoundTransformFor(entityPatch.getAnimator().getPose(0.0F), Armatures.BIPED.get().toolR);

                                    transformMatrix.translate(new Vec3f(-0.2F, 0.0F, 0.4F));
                                    OpenMatrix4f.mul(
                                            new OpenMatrix4f().rotate(-(float) Math.toRadians(attacker.yBodyRotO + 180.0F),
                                                    new Vec3f(0.0F, 1.0F, 0.0F)),
                                            transformMatrix,
                                            transformMatrix
                                    );
                                    // 70个球状粒子（完全复刻原版）
                                    for (int i = 0; i < 70; ++i) {
                                        double theta = 6.283185307179586 * Math.random();
                                        double thetax = Math.acos(2.0 * Math.random() - 1.0);
                                        float dx = (float) (0.1 * Math.sin(thetax) * Math.cos(theta));
                                        float dy = (float) (0.1 * Math.sin(thetax) * Math.sin(theta));
                                        float dz = (float) (0.1 * Math.cos(thetax));

                                        level.addParticle(
                                                ModParticle.PHANTOM_WING_FLAME.get(),
                                                transformMatrix.m30 + attacker.getX(),
                                                transformMatrix.m31 + attacker.getY() + (float) (Math.random() * 2.9F),
                                                transformMatrix.m32 + attacker.getZ(),
                                                dx, dy, dz
                                        );

                                        if (i % 2 == 0) {
                                            level.addParticle(
                                                    ModParticle.SOUL_LAVA.get(),
                                                    transformMatrix.m30 + attacker.getX(),
                                                    transformMatrix.m31 + attacker.getY() + (float) (Math.random() * 2.9F),
                                                    transformMatrix.m32 + attacker.getZ(),
                                                    dx, dy, dz
                                            );
                                        }
                                    }


                                    // ===== 双锥形冲击波 =====
                                    transformMatrix = entityPatch.getArmature()
                                            .getBoundTransformFor(entityPatch.getAnimator().getPose(0.0F), Armatures.BIPED.get().chest);
                                    OpenMatrix4f.mul(
                                            new OpenMatrix4f().rotate(-(float) Math.toRadians(attacker.yBodyRotO + 180.0F),
                                                    new Vec3f(0.0F, 1.0F, 0.0F)),
                                            transformMatrix,
                                            transformMatrix
                                    );
                                    transformMatrix.translate(new Vec3f(0.0F, 0.3F, -0.5F)); // 调整发射位置

                                    double r = 0.5 * intensity; // 扩大半径
                                    double t = 0.006; // 调整锥形角度

                                    // 强化双锥形（每组粒子数增加）
                                    for (int group = 0; group < 2; group++) {
                                        float angle = group == 0 ? 110.0f : 70.0f;

                                        for (int i = 0; i < coneParticles; ++i) {
                                            double theta = 6.283185307179586 * Math.random();
                                            double phi = (Math.random() - 0.2) * Math.PI * t / r; // 调整分布

                                            Vec3f direction = new Vec3f(
                                                    (float) (r * Math.cos(phi) * Math.cos(theta)),
                                                    (float) (r * Math.cos(phi) * Math.sin(theta)) * 1.5f, // 增强垂直效果
                                                    (float) (r * Math.sin(phi))
                                            );

                                            OpenMatrix4f rotation = new OpenMatrix4f()
                                                    .rotate((float) Math.toRadians(-attacker.yBodyRotO + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F))
                                                    .rotate((float) Math.toRadians(angle), new Vec3f(1.0F, 0.0F, 0.0F));
                                            OpenMatrix4f.transform3v(rotation, direction, direction);

                                            float speedVariation = 0.3f + 0.3f * (float) Math.random();
                                            direction.scale(speedVariation);

                                            // 添加粒子多样性
                                            ParticleOptions particle;
                                            if (Math.random() < 0.7) {
                                                particle = ModParticle.PHANTOM_WING_FLAME.get();
                                            } else {
                                                particle = ParticleTypes.END_ROD; // 添加火星效果
                                            }

                                            level.addParticle(
                                                    particle,
                                                    transformMatrix.m30 + attacker.getX(),
                                                    transformMatrix.m31 + attacker.getY() + 0.7f,
                                                    transformMatrix.m32 + attacker.getZ(),
                                                    direction.x * 1.3f,
                                                    direction.y * 1.5f, // 增强上升效果
                                                    direction.z * 1.3f
                                            );

                                            // 随机熔岩尾迹
                                            if (Math.random() < 0.2f) {
                                                Vec3f lavaDir = new Vec3f(direction.x, direction.y, direction.z).scale(0.7f);
                                                level.addParticle(
                                                        ModParticle.SOUL_LAVA.get(),
                                                        transformMatrix.m30 + attacker.getX(),
                                                        transformMatrix.m31 + attacker.getY(),
                                                        transformMatrix.m32 + attacker.getZ(),
                                                        lavaDir.x, lavaDir.y * 1.4f, lavaDir.z
                                                );
                                            }
                                        }
                                    }

                                    // ===== 地面燃烧效果 =====
                                    for (int i = 0; i < 120 * intensity; i++) {
                                        double angle = Math.random() * 6.283185307179586;
                                        double radius = 4.0 * intensity * Math.random();
                                        Vec3 pos = new Vec3(
                                                radius * Math.cos(angle),
                                                0.1,
                                                radius * Math.sin(angle)
                                        ).add(attacker.position());

                                        // 主火焰粒子
                                        level.addParticle(
                                                Math.random() < 0.6 ? ParticleTypes.SOUL_FIRE_FLAME : ModParticle.PHANTOM_WING_FLAME.get(),
                                                pos.x, pos.y, pos.z,
                                                (Math.random() - 0.5) * 0.25,
                                                0.15 + Math.random() * 0.4,
                                                (Math.random() - 0.5) * 0.25
                                        );

                                        // 熔岩斑块
                                        if (i % 4 == 0) {
                                            level.addParticle(
                                                    ModParticle.SOUL_LAVA.get(),
                                                    pos.x, pos.y + 0.05, pos.z,
                                                    0, 0.03, 0
                                            );
                                        }

                                        // 烟雾效果
                                        if (i % 3 == 0) {
                                            level.addParticle(
                                                    ParticleTypes.ENCHANT,
                                                    pos.x, pos.y + 0.1, pos.z,
                                                    (Math.random() - 0.5) * 0.1,
                                                    0.1,
                                                    (Math.random() - 0.5) * 0.1
                                            );
                                        }
                                    }
                                }
                            }, AnimationEvent.Side.CLIENT),
                            // 第二阶段：冲击波效果（服务端）
                            AnimationEvent.InTimeEvent.create(1.85F, (entityPatch, self, params) -> {
                                if (!entityPatch.getOriginal().level().isClientSide()) {
                                    LivingEntity attacker = entityPatch.getOriginal();
                                    ServerLevel level = (ServerLevel) attacker.level();

                                    // 强化冲击波参数
                                    double centerX = attacker.getX();
                                    double centerY = attacker.getY() + 0.2; // 稍微抬升中心点
                                    double centerZ = attacker.getZ();
                                    double baseRadius = 10.0; // 扩大基础半径
                                    double maxRadius = 18.0; // 扩大最大半径
                                    int waveCount = 4; // 增加波次
                                    int particlesPerWave = 100; // 增加每波粒子数
                                    double speed = 0.5; // 提高速度

                                    // 多层冲击波
                                    for (int wave = 0; wave < waveCount; wave++) {
                                        double progress = wave / (double) (waveCount - 1);
                                        double radius = Mth.lerp(progress, baseRadius, maxRadius);
                                        double verticalScale = 1.8 * Math.sin(progress * Math.PI); // 增强垂直波动

                                        for (int i = 0; i < particlesPerWave; i++) {
                                            double angle = 2 * Math.PI * i / particlesPerWave;
                                            double randomSpread = 0.5 * (level.random.nextDouble() - 0.5);

                                            // 粒子位置计算（带螺旋效果）
                                            Vec3 pos = new Vec3(
                                                    radius * Math.cos(angle + progress * 1.5 * Math.PI) + randomSpread,
                                                    verticalScale * Math.sin(angle * 3 + wave * 0.7),
                                                    radius * Math.sin(angle + progress * 1.5 * Math.PI) + randomSpread
                                            ).add(centerX, centerY, centerZ);

                                            // 动态速度（向外扩散）
                                            Vec3 motion = pos.subtract(centerX, centerY, centerZ).normalize()
                                                    .scale(speed * (0.4 + 0.6 * (1 - progress)))
                                                    .add(0, 0.2, 0);

                                            // 核心火焰粒子
                                            level.sendParticles(
                                                    ModParticle.PHANTOM_WING_FLAME.get(),
                                                    pos.x, pos.y, pos.z,
                                                    2, // 每组2个粒子
                                                    motion.x * 0.4, motion.y * 0.9, motion.z * 0.4,
                                                    1.0
                                            );

                                            // 50%概率生成附加火星
                                            if (level.random.nextDouble() < 0.5) {
                                                level.sendParticles(
                                                        ParticleTypes.ENCHANT,
                                                        pos.x, pos.y + 0.3, pos.z,
                                                        1,
                                                        motion.x * 1.5, motion.y * 2.0, motion.z * 1.5,
                                                        0.5
                                                );
                                            }
                                        }
                                    }

                                    // 中心爆炸强化
                                    level.sendParticles(
                                            ModParticle.PHANTOM_WING_FLAME.get(),
                                            centerX, centerY + 0.5, centerZ,
                                            30,
                                            1.8, 0.8, 1.8,
                                            0.9
                                    );

                                    level.sendParticles(
                                            ParticleTypes.ENCHANT,
                                            centerX, centerY + 0.5, centerZ,
                                            80,
                                            2.0, 1.0, 2.0,
                                            0.8
                                    );
                                }
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(0.0F, (entityPatch, animation, params) -> {
                                LivingEntity entity = entityPatch.getOriginal();
                                entity.level().addParticle(AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get()
                                        , entity.getX()
                                        , entity.getY()
                                        , entity.getZ()
                                        , Double.longBitsToDouble(entity.getId())
                                        , 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AnimationEvent.InTimeEvent.create(0.0F, (entityPatch, self, params) -> {
                                entityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 5, false, false, false));
                            }, AnimationEvent.Side.BOTH),
                            AnimationEvent.InTimeEvent.create(0.2F, (entityPatch, self, params) -> {
                                entityPatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), 150, 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AnimationEvent.InTimeEvent.create(1.1F, (entityPatch, self, params) -> {
                                entityPatch.playSound(SoundEvents.GENERIC_EXPLODE, 150, 0, 0);
                            }, AnimationEvent.Side.CLIENT),
                            AvalonEventUtils.simpleGroundSplit(57, 0, 0, 0, 0, 5F, true),
                            AvalonEventUtils.simpleCameraShake(59, 60, 4, 4, 4),
                            AnimationEvent.InTimeEvent.create(0.9F, (entityPatch, self, params) -> {
                                StrikeWindmillHalberd(entityPatch.getOriginal().level(), entityPatch.getOriginal(), 3, 3, 1.0F, 1.0F, 0.4, 1);
                                Level world = entityPatch.getOriginal().level();
                                LivingEntity caster = entityPatch.getOriginal();
                                for (Entity entity : world.getEntities(caster, caster.getBoundingBox().inflate(6, 6, 6))) {
                                    if (entity instanceof LivingEntity living && living.isAlive()) {
                                        living.addEffect(new MobEffectInstance(ModEffect.EFFECTSTUN.get(), 30, 0));
                                    }
                                }
                            }, AnimationEvent.Side.BOTH),
                            AnimationEvent.InTimeEvent.create(1.2F, (entityPatch, self, params) -> {
                                StrikeWindmillHalberd(entityPatch.getOriginal().level(), entityPatch.getOriginal(), 5, 3, 1.0F, 1.0F, 0.4, 1);
                            }, AnimationEvent.Side.BOTH),
                            AnimationEvent.InTimeEvent.create(1.5F, (entityPatch, self, params) -> {
                                StrikeWindmillHalberd(entityPatch.getOriginal().level(), entityPatch.getOriginal(), 5, 5, 1.4F, 1.4F, 0.4, 1);
                                entityPatch.playSound(EpicFightSounds.BIG_ENTITY_MOVE.get(), 1.0F, 1.0F);
                            }, AnimationEvent.Side.BOTH)
                    )
                    .newTimePair(0.0F, Float.MAX_VALUE)
                    .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                    .addStateRemoveOld(EntityState.CAN_SWITCH_HAND_ITEM, false)
                    .addStateRemoveOld(EntityState.INACTION, true)
            );

            CLAW_SKILL2 = builder.nextAccessor("skill/claw_skill2", accessor ->
                    new ActionAnimation(0.15F, accessor, Armatures.BIPED)
                            .newTimePair(0.0F, Float.MAX_VALUE)
                            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.01F, shootBeam(), AnimationEvent.Side.SERVER))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.3F, ((livingEntityPatch, assetAccessor, animationParameters) -> {
                                livingEntityPatch.playSound(ModSounds.ABYSS_BLAST_ONLY_CHARGE.get(), 4.0F, 0.0F, 0.0F);
                            }), AnimationEvent.Side.SERVER))
                            .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                                AnimationPlayer player = livingEntityPatch.getAnimator().getPlayerFor(null);
                                if (player == null || dynamicAnimation.isLinkAnimation()) {
                                    return 1.0F;
                                }
                                float elapsed = player.getElapsedTime();
                                return (elapsed > 0.3F || elapsed < 1.0F) ? 0.3F : 1.0F;
                            }))
            );

            CLAW_SKILL3 = builder.nextAccessor("skill/claw_skill3", accessor ->
                    new ActionAnimation(0.15F, accessor, Armatures.BIPED)
                            .newTimePair(0.0F, Float.MAX_VALUE)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                            .addEvents(AnimationEvent.InTimeEvent.create(0.3F, summonAbyssPortal(), AnimationEvent.Side.SERVER))
                            .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))
            );

            THE_INCINERATOR_SKILL2 = builder.nextAccessor("skill/the_incinerator_skill2", (accessor) ->
                    new ActionAnimation(0.0F, accessor, Armatures.BIPED)
                            .addState(EntityState.PHASE_LEVEL, 1)
                            .addState(EntityState.CAN_BASIC_ATTACK, true)
                            .addState(EntityState.CAN_SKILL_EXECUTION, true)
                            .addState(EntityState.TURNING_LOCKED, true)
                            .addState(EntityState.MOVEMENT_LOCKED, true)
                            .addState(EntityState.INACTION, false)
                            .newTimePair(0.0F, Float.MAX_VALUE)
                            .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.BLOCKED))
                            .addEvents(AnimationProperty.ActionAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_FIRST)), AnimationEvent.Side.SERVER))
                            .addEvents(AnimationEvent.InTimeEvent.create(0.2F, summonFireBall(), AnimationEvent.Side.SERVER))
            );

            THE_INCINERATOR_SKILL3 = builder.nextAccessor("skill/the_incinerator_skill3", accessor -> new AvalonAttackAnimation(0F, accessor, Armatures.BIPED, 1, 1
                    , createSimplePhase(60, 80, 85, InteractionHand.MAIN_HAND, 1, 1F, Armatures.BIPED.get().toolR, MEEN_LANCE_1)
                    , createSimplePhase(89, 115, 120, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().toolR, MEEN_LANCE_1)
                    , createSimplePhase(160, 166, 180, InteractionHand.MAIN_HAND, 0.2F, 1F, Armatures.BIPED.get().rootJoint, MEEN_LANCE_1)
                    , createSimplePhase(214, 223, 224, InteractionHand.MAIN_HAND, 2F, 2F, Armatures.BIPED.get().rootJoint, MEEN_LANCE_CHARGE3)
                    , createSimplePhase(225, 241, 310, InteractionHand.MAIN_HAND, 1F, 1F, Armatures.BIPED.get().rootJoint, MEEN_LANCE_CHARGE3))
                    .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10))
                    .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100))
                    .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                    .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                    .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE, EpicFightDamageTypeTags.BYPASS_DODGE))
                    .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                    .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                    .newTimePair(0.9F, Float.MAX_VALUE)
                    .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                    .newTimePair(0.0F, Float.MAX_VALUE)
                    .addState(EntityState.ATTACK_RESULT, (damageSource) -> AttackResult.ResultType.BLOCKED)
                    .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1F))
                    .addEvents(AnimationEvent.InTimeEvent.create(2.6F, (entityPatch, self, params) -> {
                                boolean hasEffect = entityPatch.getOriginal().hasEffect(PECEffects.SOUL_INCINERATOR.get());
                                ParticleOptions flame = hasEffect ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
                                ParticleOptions lava = hasEffect ? ModParticle.SOUL_LAVA.get() : ParticleTypes.LAVA;
                                if (!entityPatch.getOriginal().level().isClientSide()) {
                                    LivingEntity attacker = entityPatch.getOriginal();
                                    ServerLevel level = (ServerLevel) attacker.level();
                                    // 强化冲击波参数
                                    double centerX = attacker.getX();
                                    double centerY = attacker.getY() + 0.2; // 稍微抬升中心点
                                    double centerZ = attacker.getZ();
                                    double baseRadius = 5.0; // 扩大基础半径
                                    double maxRadius = 10.0; // 扩大最大半径
                                    int waveCount = 6; // 增加波次
                                    int particlesPerWave = 100; // 增加每波粒子数
                                    double speed = 0.5; // 提高速度

                                    // 多层冲击波
                                    for (int wave = 0; wave < waveCount; wave++) {
                                        double progress = wave / (double) (waveCount - 1);
                                        double radius = Mth.lerp(progress, baseRadius, maxRadius);
                                        double verticalScale = 1.8 * Math.sin(progress * Math.PI); // 增强垂直波动

                                        for (int i = 0; i < particlesPerWave; i++) {
                                            double angle = 2 * Math.PI * i / particlesPerWave;
                                            double randomSpread = 0.5 * (level.random.nextDouble() - 0.5);

                                            // 粒子位置计算（带螺旋效果）
                                            Vec3 pos = new Vec3(
                                                    radius * Math.cos(angle + progress * 1.5 * Math.PI) + randomSpread,
                                                    verticalScale * Math.sin(angle * 3 + wave * 0.7),
                                                    radius * Math.sin(angle + progress * 1.5 * Math.PI) + randomSpread
                                            ).add(centerX, centerY, centerZ);

                                            // 动态速度（向外扩散）
                                            Vec3 motion = pos.subtract(centerX, centerY, centerZ).normalize()
                                                    .scale(speed * (0.4 + 0.6 * (1 - progress)))
                                                    .add(0, 0.2, 0);

                                            // 核心火焰粒子
                                            level.sendParticles(
                                                    flame,
                                                    pos.x, pos.y, pos.z,
                                                    2, // 每组2个粒子
                                                    motion.x * 0.4, motion.y * 0.9, motion.z * 0.4,
                                                    1.0
                                            );

                                            // 50%概率生成附加火星
                                            if (level.random.nextDouble() < 0.5) {
                                                level.sendParticles(
                                                        lava,
                                                        pos.x, pos.y + 0.3, pos.z,
                                                        1,
                                                        motion.x * 1.5, motion.y * 2.0, motion.z * 1.5,
                                                        0.5
                                                );
                                            }
                                        }
                                    }

                                    // 中心爆炸强化
                                    level.sendParticles(
                                            ParticleTypes.END_ROD,
                                            centerX, centerY + 0.5, centerZ,
                                            30,
                                            1.8, 0.8, 1.8,
                                            0.9
                                    );

                                    level.sendParticles(
                                            flame,
                                            centerX, centerY + 0.5, centerZ,
                                            80,
                                            2.0, 1.0, 2.0,
                                            0.8
                                    );
                                }
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(3.7F, (entityPatch, self, params) -> {
                                boolean hasEffect = entityPatch.getOriginal().hasEffect(PECEffects.SOUL_INCINERATOR.get());
                                ParticleOptions flame = hasEffect ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
                                ParticleOptions lava = hasEffect ? ModParticle.SOUL_LAVA.get() : ParticleTypes.LAVA;
                                if (entityPatch.getOriginal().level().isClientSide()) {
                                    LivingEntity attacker = entityPatch.getOriginal();
                                    Level level = attacker.level();

                                    // ===== 强化参数配置 =====
                                    float intensity = 2.5f; // 增强强度系数
                                    int sphereParticles = (int) (200 * intensity); // 增加球状粒子数
                                    int coneParticles = (int) (150 * intensity); // 增加锥形粒子数
                                    float lavaRatio = 0.4f; // 提高熔岩粒子比例

                                    // ===== 改进版：完整形状的扩散火圈 =====
                                    Vec3 centerPos = attacker.position().add(0, 0.05, 0); // 中心位置
                                    int totalRings = 5; // 总圈数
                                    int particlesPerRing = 36; // 每圈粒子数(36个粒子形成完整圆)
                                    float maxRadius = 5.0f; // 最大半径
                                    float duration = 0.5f; // 扩散持续时间(秒)
                                    float currentTime = 0.0f; // 当前时间(用于动画)

                                    currentTime += 0.05f; // 假设每帧0.05秒
                                    float progress = Math.min(currentTime / duration, 1.0f);

                                    for (int ring = 0; ring < totalRings; ring++) {
                                        // 当前圈的半径(从内到外)
                                        float radius = maxRadius * (ring + progress) / totalRings;

                                        // 完整圆形生成
                                        for (int i = 0; i < particlesPerRing; i++) {
                                            float angle = (float) (6.283185307179586 * i / particlesPerRing);
                                            float xOffset = radius * Mth.cos(angle);
                                            float zOffset = radius * Mth.sin(angle);

                                            // 基础火焰粒子
                                            level.addParticle(
                                                    flame,
                                                    centerPos.x + xOffset,
                                                    centerPos.y + 0.05f, // 紧贴地面
                                                    centerPos.z + zOffset,
                                                    xOffset * 0.1f, // 轻微的向外速度
                                                    0.08f, // 轻微上浮
                                                    zOffset * 0.1f
                                            );

                                            // 每5个粒子添加一个强化粒子
                                            if (i % 5 == 0) {
                                                level.addParticle(
                                                        lava, // 使用灵魂火增加视觉效果
                                                        centerPos.x + xOffset,
                                                        centerPos.y + 0.07f,
                                                        centerPos.z + zOffset,
                                                        xOffset * 0.15f,
                                                        0.12f,
                                                        zOffset * 0.15f
                                                );
                                            }
                                        }

                                        // 最外圈添加火星效果
                                        if (ring == totalRings - 1) {
                                            for (int i = 0; i < particlesPerRing / 2; i++) {
                                                float angle = (float) (6.283185307179586 * i / ((double) particlesPerRing / 2));
                                                float xOffset = radius * Mth.cos(angle);
                                                float zOffset = radius * Mth.sin(angle);

                                                level.addParticle(
                                                        ParticleTypes.FIREWORK,
                                                        centerPos.x + xOffset,
                                                        centerPos.y + 0.1f,
                                                        centerPos.z + zOffset,
                                                        xOffset * 0.3f,
                                                        0.2f,
                                                        zOffset * 0.3f
                                                );
                                            }
                                        }
                                    }


                                    // ===== 第一阶段：武器球状粒子 =====
                                    OpenMatrix4f transformMatrix = entityPatch.getArmature()
                                            .getBoundTransformFor(entityPatch.getAnimator().getPose(0.0F), Armatures.BIPED.get().handR);

                                    transformMatrix.translate(new Vec3f(-0.2F, 0.0F, 0.4F));
                                    OpenMatrix4f.mul(
                                            new OpenMatrix4f().rotate(-(float) Math.toRadians(attacker.yBodyRotO + 180.0F),
                                                    new Vec3f(0.0F, 1.0F, 0.0F)),
                                            transformMatrix,
                                            transformMatrix
                                    );
                                    // 70个球状粒子（完全复刻原版）
                                    for (int i = 0; i < 70; ++i) {
                                        double theta = 6.283185307179586 * Math.random();
                                        double thetax = Math.acos(2.0 * Math.random() - 1.0);
                                        float dx = (float) (0.1 * Math.sin(thetax) * Math.cos(theta));
                                        float dy = (float) (0.1 * Math.sin(thetax) * Math.sin(theta));
                                        float dz = (float) (0.1 * Math.cos(thetax));

                                        level.addParticle(
                                                ParticleTypes.SMALL_FLAME,
                                                transformMatrix.m30 + attacker.getX(),
                                                transformMatrix.m31 + attacker.getY() + (float) (Math.random() * 2.9F),
                                                transformMatrix.m32 + attacker.getZ(),
                                                dx, dy, dz
                                        );

                                        if (i % 2 == 0) {
                                            level.addParticle(
                                                    lava,
                                                    transformMatrix.m30 + attacker.getX(),
                                                    transformMatrix.m31 + attacker.getY() + (float) (Math.random() * 2.9F),
                                                    transformMatrix.m32 + attacker.getZ(),
                                                    dx, dy, dz
                                            );
                                        }
                                    }


                                    // ===== 双锥形冲击波 =====
                                    transformMatrix = entityPatch.getArmature()
                                            .getBoundTransformFor(entityPatch.getAnimator().getPose(0.0F), Armatures.BIPED.get().handR);
                                    OpenMatrix4f.mul(
                                            new OpenMatrix4f().rotate(-(float) Math.toRadians(attacker.yBodyRotO + 180.0F),
                                                    new Vec3f(0.0F, 1.0F, 0.0F)),
                                            transformMatrix,
                                            transformMatrix
                                    );
                                    transformMatrix.translate(new Vec3f(0.0F, 0.3F, -0.5F)); // 调整发射位置

                                    double r = 0.5 * intensity; // 扩大半径
                                    double t = 0.006; // 调整锥形角度

                                    // 强化双锥形（每组粒子数增加）
                                    for (int group = 0; group < 2; group++) {
                                        float angle = group == 0 ? 110.0f : 70.0f;

                                        for (int i = 0; i < coneParticles; ++i) {
                                            double theta = 6.283185307179586 * Math.random();
                                            double phi = (Math.random() - 0.2) * Math.PI * t / r; // 调整分布

                                            Vec3f direction = new Vec3f(
                                                    (float) (r * Math.cos(phi) * Math.cos(theta)),
                                                    (float) (r * Math.cos(phi) * Math.sin(theta)) * 1.5f, // 增强垂直效果
                                                    (float) (r * Math.sin(phi))
                                            );

                                            OpenMatrix4f rotation = new OpenMatrix4f()
                                                    .rotate((float) Math.toRadians(-attacker.yBodyRotO + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F))
                                                    .rotate((float) Math.toRadians(angle), new Vec3f(1.0F, 0.0F, 0.0F));
                                            OpenMatrix4f.transform3v(rotation, direction, direction);

                                            float speedVariation = 0.3f + 0.3f * (float) Math.random();
                                            direction.scale(speedVariation);

                                            // 添加粒子多样性
                                            ParticleOptions particle;
                                            if (Math.random() < 0.7) {
                                                particle = flame;
                                            } else if (Math.random() < 0.9) {
                                                particle = ParticleTypes.SMALL_FLAME;
                                            } else {
                                                particle = ParticleTypes.END_ROD; // 添加火星效果
                                            }

                                            level.addParticle(
                                                    particle,
                                                    transformMatrix.m30 + attacker.getX(),
                                                    transformMatrix.m31 + attacker.getY() + 0.7f,
                                                    transformMatrix.m32 + attacker.getZ(),
                                                    direction.x * 1.3f,
                                                    direction.y * 1.5f, // 增强上升效果
                                                    direction.z * 1.3f
                                            );

                                            // 随机熔岩尾迹
                                            if (Math.random() < 0.2f) {
                                                Vec3f lavaDir = new Vec3f(direction.x, direction.y, direction.z).scale(0.7f);
                                                level.addParticle(
                                                        ParticleTypes.LANDING_LAVA,
                                                        transformMatrix.m30 + attacker.getX(),
                                                        transformMatrix.m31 + attacker.getY(),
                                                        transformMatrix.m32 + attacker.getZ(),
                                                        lavaDir.x, lavaDir.y * 1.4f, lavaDir.z
                                                );
                                            }
                                        }
                                    }

                                    // ===== 地面燃烧效果 =====
                                    for (int i = 0; i < 120 * intensity; i++) {
                                        double angle = Math.random() * 6.283185307179586;
                                        double radius = 4.0 * intensity * Math.random();
                                        Vec3 pos = new Vec3(
                                                radius * Math.cos(angle),
                                                0.1,
                                                radius * Math.sin(angle)
                                        ).add(attacker.position());

                                        // 主火焰粒子
                                        level.addParticle(
                                                Math.random() < 0.6 ? flame : ParticleTypes.SMALL_FLAME,
                                                pos.x, pos.y, pos.z,
                                                (Math.random() - 0.5) * 0.25,
                                                0.15 + Math.random() * 0.4,
                                                (Math.random() - 0.5) * 0.25
                                        );

                                        // 熔岩斑块
                                        if (i % 4 == 0) {
                                            level.addParticle(
                                                    lava,
                                                    pos.x, pos.y + 0.05, pos.z,
                                                    0, 0.03, 0
                                            );
                                        }

                                        // 烟雾效果
                                        if (i % 3 == 0) {
                                            level.addParticle(
                                                    ParticleTypes.ENCHANT,
                                                    pos.x, pos.y + 0.1, pos.z,
                                                    (Math.random() - 0.5) * 0.1,
                                                    0.1,
                                                    (Math.random() - 0.5) * 0.1
                                            );
                                        }
                                    }
                                }
                            }, AnimationEvent.Side.CLIENT),
                            AnimationEvent.InTimeEvent.create(0.2F, (entitypatch, self, params) ->
                            {
                                entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), 150, 0, 0);
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(2.6F, (entitypatch, self, params) ->
                            {
                                entitypatch.playSound(SoundEvents.TRIDENT_RIPTIDE_1, 140, 0, 0);
                            }, AnimationEvent.Side.SERVER),
                            AnimationEvent.InTimeEvent.create(3.6F, (entitypatch, self, params) ->
                            {
                                entitypatch.playSound(ModSounds.FLAME_BURST.get(), 150, 0, 0);
                            }, AnimationEvent.Side.SERVER),
                            incineratorParticleTrail(56, 166, InteractionHand.MAIN_HAND
                                    , new Vec3(0, 0, -2.25F), new Vec3(0, 0, -2.6F), 8, 8, 0.6F)
                            , WeaponTrailGroundSplitter.create(60, 80, InteractionHand.MAIN_HAND
                                    , new Vec3(0, 0, -2.25F), new Vec3(0, 0, -2.3F)
                                    , 1.2f, ParticleTypes.ENCHANT, 1, 3F, 4, 7),
                            WeaponTrailGroundSplitter.create(89, 115, InteractionHand.MAIN_HAND
                                    , new Vec3(0, 0, -2.25F), new Vec3(0, 0, -2.3F)
                                    , 1.2f, ParticleTypes.ENCHANT, 1, 3F, 4, 7)
                            , AvalonEventUtils.simpleGroundSplit(160, 0, 0, 0, 0, 4, true)
                            , AvalonEventUtils.simpleCameraShake(160, 30, 3, 3, 3)
                            , AvalonEventUtils.simpleGroundSplit(214, 0, 0, 0, 0, 5, true)
                            , AvalonEventUtils.simpleCameraShake(214, 60, 8, 4, 8)
                            , AvalonEventUtils.simpleGroundSplit(225, 0, 0, 0, 0, 7, true),
                            AnimationEvent.InTimeEvent.create(1.0F, (entitypatch, self, params) ->
                            {
                                LivingEntity player = entitypatch.getOriginal();
                                double headY = player.getY() + (double) 1.0F;
                                int standingOnY = Mth.floor(player.getY()) - 2;
                                float yawRadians = (float) Math.toRadians(90.0F + player.getYRot());
                                boolean hasSucceeded = false;
                                for (int l = 0; l < 10; ++l) {
                                    double d2 = (double) 2.25F * (double) (l + 1);
                                    int j2 = (int) (1.5F * (float) l);
                                    if (spawnFlameStrike(player.getX() + (double) Mth.cos(yawRadians) * d2, player.getZ() + (double) Mth.sin(yawRadians) * d2, standingOnY, headY, yawRadians, 40, j2, j2, player.level(), 1.0F, player)) {
                                        hasSucceeded = true;
                                    }
                                }

                                if (hasSucceeded) {
                                    ScreenShake_Entity.ScreenShake(player.level(), player.position(), 30.0F, 0.15F, 0, 30);
                                    player.playSound(ModSounds.SWORD_STOMP.get(), 1.0F, 1.0F);
                                }
                            }, AnimationEvent.Side.SERVER)
                    )
            );


        });
    }

    public static AnimationEvent.InPeriodEvent incineratorParticleTrail(int startFrame, int endFrame, InteractionHand hand, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, float random) {
        float start = (float)startFrame / 60.0F;
        float end = (float)endFrame / 60.0F;
        Joint joint = null;
        switch (hand) {
            case MAIN_HAND -> joint = Armatures.BIPED.get().toolR;
            case OFF_HAND -> joint = Armatures.BIPED.get().toolL;
        }

        Joint finalJoint = joint;
        return AnimationEvent.InPeriodEvent.create(start, end, (entityPatch, self, params) -> {
            boolean hasEffect = entityPatch.getOriginal().hasEffect(PECEffects.SOUL_INCINERATOR.get());
            ParticleOptions flame = hasEffect ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = player.getPrevElapsedTime();
            float elapsedTime = player.getElapsedTime();
            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3 trailStartOffset = startOffset;
            Vec3f trailDirection = new Vec3f((float)(endOffset.x - startOffset.x), (float)(endOffset.y - startOffset.y), (float)(endOffset.z - startOffset.z));

            for(float f = prevElapsedTime; f <= elapsedTime; f += step) {
                for(int i = 0; i <= particleCount; ++i) {
                    float ratio = (float)i / (float)particleCount;
                    Vec3f pointOffset = new Vec3f((float)(trailStartOffset.x + (double)(trailDirection.x * ratio)), (float)(trailStartOffset.y + (double)(trailDirection.y * ratio)), (float)(trailStartOffset.z + (double)(trailDirection.z * ratio)));
                    double randX = (Math.random() - (double)0.5F) * (double)random;
                    double randY = (Math.random() - (double)0.5F) * (double)random;
                    double randZ = (Math.random() - (double)0.5F) * (double)random;
                    Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                    if ((entityPatch.getOriginal()).level().isClientSide) {
                        (entityPatch.getOriginal()).level().addParticle(flame, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0F, 0.0F, 0.0F);
                    }
                }
            }

        }, AnimationEvent.Side.CLIENT);
    }

    public static AnimationEvent.InTimeEvent<?> setFullBowUseTime(float time) {
        return AnimationEvent.InTimeEvent.create(time, (livingEntityPatch, assetAccessor, animationParameters) -> {
            ItemStack itemStack = livingEntityPatch.getOriginal().getMainHandItem();
            if (itemStack.is(ModItems.WRATH_OF_THE_DESERT.get())) {
                Wrath_of_the_desert.setUseTime(itemStack, 20);
            }
            if (itemStack.is(ModItems.CURSED_BOW.get())) {
                Cursed_bow.setUseTime(itemStack, 20);
            }
        }, AnimationEvent.Side.CLIENT);
    }

    public static AnimationEvent.InTimeEvent<?> resetBowUseTime(float time) {
        return AnimationEvent.InTimeEvent.create(time, (livingEntityPatch, assetAccessor, animationParameters) -> {
            ItemStack itemStack = livingEntityPatch.getOriginal().getMainHandItem();
            if (itemStack.is(ModItems.WRATH_OF_THE_DESERT.get())) {
                Wrath_of_the_desert.setUseTime(itemStack, 0);
            }
            if (itemStack.is(ModItems.CURSED_BOW.get())) {
                Cursed_bow.setUseTime(itemStack, 0);
            }
        }, AnimationEvent.Side.CLIENT);
    }

    private static boolean spawnFlameStrike(double x, double z, double minY, double maxY, float rotation, int duration, int wait, int delay, Level world, float radius, LivingEntity player) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0F;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = world.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while ((double) blockpos.getY() >= minY);

        if (flag) {
            world.addFreshEntity(new Flame_Strike_Entity(world, x, (double) blockpos.getY() + d0, z, rotation, duration, wait, delay, radius, 6.0F, 2.0F, false, player));
            return true;
        } else {
            return false;
        }
    }

    public static AnimationEvent.E0 summonFireBall() {
        return (entityPatch, animation, params) -> {
            entityPatch.playSound(ModSounds.ABYSS_BLAST_ONLY_CHARGE.get(), 4.0F, 1.0F, 1.0F);
            LivingEntity entity = entityPatch.getOriginal();
            boolean hasSoulEffect = entity.hasEffect(PECEffects.SOUL_INCINERATOR.get());
            if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                if (serverPlayerPatch.getTarget() == null) {
                    ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
                    List<LivingEntity> list = serverPlayer.serverLevel().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, serverPlayer, new AABB(serverPlayer.position(), serverPlayer.position()).inflate(10));
                    if (!list.isEmpty()) {
                        list.sort(Comparator.comparingDouble((e) -> e.distanceTo(serverPlayer)));
                        serverPlayerPatch.setAttackTarget(list.get(0));
                    }
                }
            }
            entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON, entity.getSoundSource(), 5.0F, 1.4F + entity.getRandom().nextFloat() * 0.1F, false);
            int random = hasSoulEffect ? 5 : entity.getRandom().nextInt(5);
            switch (random) {
                case 0:
                    shootAbyssFireball(entity, new Vec3(-5.0F, 3.0F, 0.0F), 109 - 45);
                    shootFireball(entity, new Vec3(-2.0F, 3.0F, 0.0F), 0);
                    shootFireball(entity, new Vec3(0.0F, 3.0F, 0.0F), 61 - 45);
                    shootFireball(entity, new Vec3(2.0F, 3.0F, 0.0F), 77 - 45);
                    shootFireball(entity, new Vec3(5.0F, 3.0F, 0.0F), 93 - 45);
                    break;
                case 1:
                    shootFireball(entity, new Vec3(-5.0F, 3.0F, 0.0F), 0);
                    shootAbyssFireball(entity, new Vec3(-2.0F, 3.0F, 0.0F), 109 - 45);
                    shootFireball(entity, new Vec3(0.0F, 3.0F, 0.0F), 61 - 45);
                    shootFireball(entity, new Vec3(2.0F, 3.0F, 0.0F), 77 - 45);
                    shootFireball(entity, new Vec3(5.0F, 3.0F, 0.0F), 93 - 45);
                    break;
                case 2:
                    shootFireball(entity, new Vec3(-5.0F, 3.0F, 0.0F), 0);
                    shootFireball(entity, new Vec3(-2.0F, 3.0F, 0.0F), 61 - 45);
                    shootAbyssFireball(entity, new Vec3(0.0F, 3.0F, 0.0F), 109 - 45);
                    shootFireball(entity, new Vec3(2.0F, 3.0F, 0.0F), 77 - 45);
                    shootFireball(entity, new Vec3(5.0F, 3.0F, 0.0F), 93 - 45);
                    break;
                case 3:
                    shootFireball(entity, new Vec3(-5.0F, 3.0F, 0.0F), 0);
                    shootFireball(entity, new Vec3(-2.0F, 3.0F, 0.0F), 61 - 45);
                    shootFireball(entity, new Vec3(0.0F, 3.0F, 0.0F), 77 - 45);
                    shootAbyssFireball(entity, new Vec3(2.0F, 3.0F, 0.0F), 109 - 45);
                    shootFireball(entity, new Vec3(5.0F, 3.0F, 0.0F), 93 - 45);
                    break;
                case 4:
                    shootFireball(entity, new Vec3(-5.0F, 3.0F, 0.0F), 0);
                    shootFireball(entity, new Vec3(-2.0F, 3.0F, 0.0F), 61 - 45);
                    shootFireball(entity, new Vec3(0.0F, 3.0F, 0.0F), 77 - 45);
                    shootFireball(entity, new Vec3(2.0F, 3.0F, 0.0F), 93 - 45);
                    shootAbyssFireball(entity, new Vec3(5.0F, 3.0F, 0.0F), 109 - 45);
                    break;
                case 5:
                    shootAbyssFireball(entity, new Vec3(-5.0F, 3.0F, 0.0F), 0);
                    shootAbyssFireball(entity, new Vec3(-2.0F, 3.0F, 0.0F), 61 - 45);
                    shootAbyssFireball(entity, new Vec3(0.0F, 3.0F, 0.0F), 77 - 45);
                    shootAbyssFireball(entity, new Vec3(2.0F, 3.0F, 0.0F), 93 - 45);
                    shootAbyssFireball(entity, new Vec3(5.0F, 3.0F, 0.0F), 109 - 45);
            }
        };
    }

    private static void shootAbyssFireball(LivingEntity entity, Vec3 shotAt, int timer) {
        shotAt = shotAt.yRot(-entity.getYRot() * ((float) Math.PI / 180F));
        Ignis_Abyss_Fireball_Entity shot = new Ignis_Abyss_Fireball_Entity(entity.level(), entity);
        shot.setPos(entity.getX() - (double) (entity.getBbWidth() + 1.0F) * 0.15 * (double) Mth.sin(entity.yBodyRot * ((float) Math.PI / 180F)), entity.getY() + (double) 1.0F, entity.getZ() + (double) (entity.getBbWidth() + 1.0F) * 0.15 * (double) Mth.cos(entity.yBodyRot * ((float) Math.PI / 180F)));
        double d0 = shotAt.x;
        double d1 = shotAt.y;
        double d2 = shotAt.z;
        float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.35F;
        shot.shoot(d0, d1 + (double) f, d2, 0.25F, 3.0F);
        shot.setUp(timer + 21);
        entity.level().addFreshEntity(shot);
    }

    private static void shootFireball(LivingEntity entity, Vec3 shotAt, int timer) {
        shotAt = shotAt.yRot(-entity.getYRot() * ((float) Math.PI / 180F));
        Ignis_Fireball_Entity shot = new Ignis_Fireball_Entity(entity.level(), entity);
        shot.setPos(entity.getX() - (double) (entity.getBbWidth() + 1.0F) * 0.15 * (double) Mth.sin(entity.yBodyRot * ((float) Math.PI / 180F)), entity.getY() + (double) 1.0F, entity.getZ() + (double) (entity.getBbWidth() + 1.0F) * 0.15 * (double) Mth.cos(entity.yBodyRot * ((float) Math.PI / 180F)));
        double d0 = shotAt.x;
        double d1 = shotAt.y;
        double d2 = shotAt.z;
        float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.35F;
        shot.shoot(d0, d1 + (double) f, d2, 0.25F, 3.0F);
        shot.setUp(timer + 21);

        entity.level().addFreshEntity(shot);
    }

    public static AnimationEvent.E0 shootBeam() {
        return (entityPatch, animation, params) -> {
            LivingEntity entity = entityPatch.getOriginal();
            Vec3 startPos = AvalonAnimationUtils.getJointWorldPos(entityPatch, Armatures.BIPED.get().rootJoint);
            startPos = startPos.add(entity.getViewVector(1.0F).normalize());
            Abyss_Blast_Entity deathBeam = new Abyss_Blast_Entity(ModEntities.ABYSS_BLAST.get(), entity.level(), entity, startPos.x, startPos.y, startPos.z, (float) ((entityPatch.getYRot() + 90) * Math.PI / 180.0), (float) ((double) (-entity.getXRot()) * Math.PI / (double) 180.0F), 60, 90, (float) 5, (float) 0.04);
            entity.level().playSound(null, entity, ModSounds.ABYSS_BLAST_ONLY_SHOOT.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
            entity.level().addFreshEntity(deathBeam);
        };
    }

    public static AnimationEvent.E0 summonAbyssPortal() {
        return (entityPatch, animation, params) -> {
            LivingEntity entity = entityPatch.getOriginal();
            Vec3 center = entity.position();
            entity.level().getEntitiesOfClass(LivingEntity.class, (new AABB(center, center)).inflate(10)).forEach(target -> {
                float rotation = (float) Mth.atan2(target.getZ() - entity.getZ(), target.getX() - entity.getX());
                if (target != entity) {
                    entity.level().addFreshEntity(new Abyss_Blast_Portal_Entity(entity.level(), target.getX(), target.getY(), target.getZ(), rotation, 0, (float) 5, (float) 0.04, entity));
                }
                entityPatch.playSound(SoundEvents.END_PORTAL_SPAWN, 0.5F, 1.0F, 1.0F);
            });
        };
    }

    public static AnimationEvent.E0 summonEnhanceAbyssPortal() {
        return (entityPatch, animation, params) -> {
            LivingEntity entity = entityPatch.getOriginal();
            Level world = entity.level();

            Vec3 lookVec = entity.getLookAngle();

            double distance = 1.0;
            Vec3 baseSpawnPos = entity.getEyePosition()
                    .add(lookVec.x * distance, -1.0, lookVec.z * distance);

            BlockPos spawnBlockPos = BlockPos.containing(baseSpawnPos);
            BlockState blockState = world.getBlockState(spawnBlockPos.below());

            if (!blockState.isFaceSturdy(world, spawnBlockPos.below(), Direction.UP)) {
                for (int i = 0; i < 3; i++) {
                    BlockPos checkPos = spawnBlockPos.below(i + 1);
                    BlockState checkState = world.getBlockState(checkPos);
                    if (checkState.isFaceSturdy(world, checkPos, Direction.UP)) {
                        baseSpawnPos = new Vec3(baseSpawnPos.x, checkPos.getY() + 1.0, baseSpawnPos.z);
                        break;
                    }
                }
            }

            float rotation = entity.getYRot() * ((float) Math.PI / 180F);

            world.addFreshEntity(new Abyss_Blast_Portal_Entity(
                    world,
                    baseSpawnPos.x,
                    baseSpawnPos.y,
                    baseSpawnPos.z,
                    rotation,
                    0,
                    (float) 5,
                    (float) 0.05,
                    entity
            ));

            entityPatch.playSound(SoundEvents.END_PORTAL_SPAWN, 0.5F, 1.0F, 1.0F);
        };
    }


    protected static void spawnLightning(LivingEntity entity, double x, double z, double minY, double maxY, float rotation, int delay, float size) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0F;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = entity.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(entity.level(), blockpos1, Direction.UP)) {
                if (!entity.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = entity.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(entity.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (flag) {
            entity.level().addFreshEntity(new Lightning_Storm_Entity(entity.level(), x, (double) blockpos.getY() + d0, z, rotation, delay, (float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage, entity, size));
        }

    }


    public static void StrikeWindmillHalberd(Level level, LivingEntity player, int numberOfBranches, int particlesPerBranch, double initialRadius, double radiusIncrement, double curveFactor, int delay) {
        float angleIncrement = (float) ((Math.PI * 2D) / (double) numberOfBranches);

        for (int branch = 0; branch < numberOfBranches; ++branch) {
            float baseAngle = angleIncrement * (float) branch;

            for (int i = 0; i < particlesPerBranch; ++i) {
                double currentRadius = initialRadius + (double) i * radiusIncrement;
                float currentAngle = (float) ((double) baseAngle + (double) ((float) i * angleIncrement) / initialRadius + (double) ((float) ((double) i * curveFactor)));
                double xOffset = currentRadius * Math.cos(currentAngle);
                double zOffset = currentRadius * Math.sin(currentAngle);
                double spawnX = player.getX() + xOffset;
                double spawnY = player.getY() + 0.3;
                double spawnZ = player.getZ() + zOffset;
                int d3 = delay * (i + 1);
                double deltaX = level.getRandom().nextGaussian() * 0.007;
                double deltaY = level.getRandom().nextGaussian() * 0.007;
                double deltaZ = level.getRandom().nextGaussian() * 0.007;
                if (level.isClientSide) {
                    level.addParticle(ModParticle.PHANTOM_WING_FLAME.get(), spawnX, spawnY, spawnZ, deltaX, deltaY, deltaZ);
                }

                spawnHalberd(spawnX, spawnZ, player.getY() - 5.0F, player.getY() + 3.0F, currentAngle, d3, level, player);
            }
        }

    }


    public static void spawnHalberd(double x, double z, double minY, double maxY, float rotation, int delay, Level world, LivingEntity player) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0F;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = world.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (flag) {
            world.addFreshEntity(new Phantom_Halberd_Entity(world, x, (double) blockpos.getY() + d0, z, rotation, delay, player, (float) CMCommonConfig.Maledictus.PhantomHalberdDamage));
        }

    }

    public static AnimationEvent.E0 shootPhantomArrow(float damageRate) {
        return shootPhantomArrow(damageRate, true);
    }

    public static AnimationEvent.E0 shootPhantomArrow(float damageRate, boolean shouldClear) {
        return (entityPatch, animation, params) -> {
            LivingEntity living = entityPatch.getOriginal();
            LivingEntity target = ScanAttackAnimation.getTarget(entityPatch);
            if (target == null) {
                if (!entityPatch.getCurrentlyAttackTriedEntities().isEmpty() && entityPatch.getCurrentlyAttackTriedEntities().get(0) instanceof LivingEntity living1) {
                    target = living1;
                }
            }
            Level level = living.level();
            Phantom_Arrow_Entity hommingArrowEntity = new Phantom_Arrow_Entity(level, living, target);
            float base = (float) entityPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE);
            hommingArrowEntity.setBaseDamage(entityPatch.getModifiedBaseDamage(base) * damageRate);
            hommingArrowEntity.pickup = AbstractArrow.Pickup.DISALLOWED;
            hommingArrowEntity.setCritArrow(true);
            Vec3 playerPosition = living.position();
            float finalYal = entityPatch.getYRot();
            if (target != null) {
                Vec3 targetPosition = target.position();
                finalYal = (float) MathUtils.getYRotOfVector(targetPosition.subtract(playerPosition));
            }
            hommingArrowEntity.shootFromRotation(living, 0, finalYal, 0.0F, 3.3F, 1.0F);
            hommingArrowEntity.setPos(getJointWorldPos(entityPatch, Armatures.BIPED.get().toolL));
            level.addFreshEntity(hommingArrowEntity);
            level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + damageRate * 0.4F);
            entityPatch.getOriginal().stopUsingItem();
            if (shouldClear) {
                entityPatch.removeHurtEntities();
            }
        };
    }

    public static AnimationEvent.E0 shootSandstormByView(float damageRate, boolean shouldClear) {
        return (entityPatch, animation, params) -> {
            LivingEntity self = entityPatch.getOriginal();
            Level level = self.level();
            Vec3 startPos = getJointWorldPos(entityPatch, Armatures.BIPED.get().toolL);
            float d7 = entityPatch.getYRot();
            float xRot = entityPatch.getOriginal().getViewXRot(1.0F);
            for (int j = -1; j <= 1; ++j) {
                float yaw = d7 + (float) (j * 15);
                float directionX = -Mth.sin(yaw * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
                float directionY = -Mth.sin(xRot * ((float) Math.PI / 180F));
                float directionZ = Mth.cos(yaw * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
                Sandstorm_Projectile sandstormProjectile = new Sandstorm_Projectile(self, directionX, directionY, directionZ, self.level(), 6.0F);
                sandstormProjectile.setState(1);
                sandstormProjectile.setPos(startPos);
                level.addFreshEntity(sandstormProjectile);
            }
            level.playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + damageRate * 0.4F);
            entityPatch.getOriginal().stopUsingItem();
            if (shouldClear) {
                entityPatch.removeHurtEntities();
            }
        };
    }

    public static AnimationEvent.E0 shootSandstorm(float damageRate, boolean shouldClear) {
        return (entityPatch, animation, params) -> {
            LivingEntity self = entityPatch.getOriginal();
            Level level = self.level();
            Vec3 startPos = getJointWorldPos(entityPatch, Armatures.BIPED.get().toolL);
            float d7 = entityPatch.getYRot();
            float xRot = -3;
            for (int j = -1; j <= 1; ++j) {
                float yaw = d7 + (float) (j * 15);
                float directionX = -Mth.sin(yaw * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
                float directionY = -Mth.sin(xRot * ((float) Math.PI / 180F));
                float directionZ = Mth.cos(yaw * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
                Sandstorm_Projectile sandstormProjectile = new Sandstorm_Projectile(self, directionX, directionY, directionZ, self.level(), 6.0F);
                sandstormProjectile.setState(1);
                sandstormProjectile.setPos(startPos);
                level.addFreshEntity(sandstormProjectile);
            }
            level.playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + damageRate * 0.4F);
            entityPatch.getOriginal().stopUsingItem();
            if (shouldClear) {
                entityPatch.removeHurtEntities();
            }
        };
    }

    public static AnimationEvent.E0 shootCursedSandstorm(float damageRate, boolean shouldClear) {
        return (entityPatch, animation, params) -> {
            LivingEntity self = entityPatch.getOriginal();
            LivingEntity target = ScanAttackAnimation.getTarget(entityPatch);
            if (target == null) {
                if (!entityPatch.getCurrentlyAttackTriedEntities().isEmpty() && entityPatch.getCurrentlyAttackTriedEntities().get(0) instanceof LivingEntity living1) {
                    target = living1;
                }
            }
            Level level = self.level();
            float baseYaw = entityPatch.getYRot();
            float pitch = -3;
            Vec3 startPos = getJointWorldPos(entityPatch, Armatures.BIPED.get().toolL);
            for (int j = -1; j <= 1; ++j) {
                float yaw = baseYaw + (float) (j * 15);
                float directionX = -Mth.sin(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));
                float directionY = -Mth.sin(pitch * ((float) Math.PI / 180F));
                float directionZ = Mth.cos(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));

                if (target != null && !target.isAlliedTo(self)) {
                    Cursed_Sandstorm_Entity cursedSandstormEntity = new Cursed_Sandstorm_Entity(self, directionX, directionY, directionZ, level, (float) CMCommonConfig.WrathOfTheDesert.damage * damageRate, target);
                    cursedSandstormEntity.setPos(startPos);
                    cursedSandstormEntity.setUp(15);
                    level.addFreshEntity(cursedSandstormEntity);
                    continue;
                }

                Cursed_Sandstorm_Entity cursedSandstormEntity = new Cursed_Sandstorm_Entity(self, directionX, directionY, directionZ, level, (float) CMCommonConfig.WrathOfTheDesert.damage * damageRate, null);
                cursedSandstormEntity.setPos(startPos);
                cursedSandstormEntity.setUp(15);
                level.addFreshEntity(cursedSandstormEntity);
            }
            level.playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + damageRate * 0.4F);
            entityPatch.getOriginal().stopUsingItem();
            if (shouldClear) {
                entityPatch.removeHurtEntities();
            }
        };
    }

    private static void roarParticle(LivingEntity entity, float vec, float math, float y, int duration, int r, int g, int b, float a, float start, float inc, float end) {
        if (entity.level().isClientSide) {
            float f = Mth.cos(entity.yBodyRot * ((float) Math.PI / 180F));
            float f1 = Mth.sin(entity.yBodyRot * ((float) Math.PI / 180F));
            double theta = (double) entity.yBodyRot * (Math.PI / 180D);
            ++theta;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            entity.level().addParticle(new RoarParticleOptions(duration, r, g, b, a, start, inc, end), entity.getX() + (double) vec * vecX + (double) (f * math), entity.getY() + (double) y, entity.getZ() + (double) vec * vecZ + (double) (f1 * math), 0.0F, 0.0F, 0.0F);
        }

    }

    public static AnimationEvent.E0 shootThreeStorm(boolean shouldClear) {
        return (entityPatch, animation, params) -> {
            LivingEntity self = entityPatch.getOriginal();
            Level level = self.level();
            for (int i = 0; i < 3; ++i) {
                float angle = (float) i * (float) Math.PI / 1.5F;
                double sx = self.getX() + (double) (Mth.cos(angle) * 8.0F);
                double sy = self.getY();
                double sz = self.getZ() + (double) (Mth.sin(angle) * 8.0F);
                if (!self.level().isClientSide()) {
                    Sandstorm_Entity projectile = new Sandstorm_Entity(self.level(), sx, sy, sz, 140, angle, self);
                    self.level().addFreshEntity(projectile);
                }
            }
            level.playSound(null, self.getX(), self.getY(), self.getZ(), ModSounds.SANDSTORM.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            if (shouldClear) {
                entityPatch.removeHurtEntities();
            }
        };
    }

    public static AnimationEvent.E0 shootRain(boolean shouldClear) {
        return (entityPatch, animation, params) -> {
            LivingEntity self = entityPatch.getOriginal();
            LivingEntity target = entityPatch.getTarget();
            Level level = self.level();
            double d1;
            if (target != null) {
                d1 = target.getY();
            } else {
                d1 = self.getY();
            }
            float f = (float) Mth.atan2(target != null ? (target.getZ() - self.getZ()) : 0, target != null ? target.getX() - self.getX() : 0);
            StrikeWindmillMonolith(self, 8, 16, 2.0F, 0.75F, 0.6, d1, 1);

            for (int l = 0; l < 16; ++l) {
                double d2 = (double) 1.25F * (double) (l + 1);
                int j = (int) (5.0F + 1.5F * (float) l);
                spawnSpikeLine(self, self.getX() + (double) Mth.cos(f) * d2, self.getZ() + (double) Mth.sin(f) * d2, d1, f, j);
            }
            level.playSound(null, self.getX(), self.getY(), self.getZ(), ModSounds.IGNIS_EARTHQUAKE.get(), SoundSource.PLAYERS, 2.0F, 1.0F);
            if (shouldClear) {
                entityPatch.removeHurtEntities();
            }
        };
    }

    private static void StrikeWindmillMonolith(LivingEntity entity, int numberOfBranches, int particlesPerBranch, double initialRadius, double radiusIncrement, double curveFactor, double spawnY, int delay) {
        float angleIncrement = (float) ((Math.PI * 2D) / (double) numberOfBranches);

        for (int branch = 0; branch < numberOfBranches; ++branch) {
            float baseAngle = angleIncrement * (float) branch;

            for (int i = 0; i < particlesPerBranch; ++i) {
                double currentRadius = initialRadius + (double) i * radiusIncrement;
                float currentAngle = (float) ((double) baseAngle + (double) ((float) i * angleIncrement) / initialRadius + (double) ((float) ((double) i * curveFactor)));
                double xOffset = currentRadius * Math.cos(currentAngle);
                double zOffset = currentRadius * Math.sin(currentAngle);
                double spawnX = entity.getX() + xOffset;
                double spawnZ = entity.getZ() + zOffset;
                int d3 = delay * (i + 1);
                spawnSpikeLine(entity, spawnX, spawnZ, spawnY, currentAngle, d3);
            }
        }

    }

    private static void spawnSpikeLine(LivingEntity entity, double posX, double posZ, double posY, float rotation, int delay) {
        BlockPos blockpos = BlockPos.containing(posX, posY, posZ);
        double d0 = 0.0F;

        do {
            BlockPos blockpos1 = blockpos.above();
            BlockState blockstate = entity.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(entity.level(), blockpos1, Direction.DOWN)) {
                if (!entity.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = entity.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(entity.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                break;
            }

            blockpos = blockpos.above();
        } while (blockpos.getY() < Math.min(entity.level().getMaxBuildHeight(), entity.getBlockY() + 20));

        entity.level().addFreshEntity(new Ancient_Desert_Stele_Entity(entity.level(), posX, (double) blockpos.getY() + d0 - (double) 3.0F, posZ, rotation, delay, (float) CMCommonConfig.AncientRemnant.AncientDesertSteledamage, entity));
    }

    public static AnimationEvent.E0 shootCeraunus() {
        return (entityPatch, animation, params) -> {
            LivingEntity living = entityPatch.getOriginal();
            if (living.getMainHandItem().getItem() instanceof Ceraunus) {
                Level level = living.level();
                Player_Ceraunus_Entity launchedBlock = new Player_Ceraunus_Entity(level, living);
                launchedBlock.setBaseDamage((float) living.getAttributeValue(Attributes.ATTACK_DAMAGE) + 15);
                float xRot = living.getXRot() - 5;
                if (xRot < -5) {
                    xRot = -5;
                } else if (xRot > 20) {
                    xRot = 20;
                }
                launchedBlock.shootFromRotation(living, xRot, entityPatch.getYRot(), 0.0F, 2.5F, 1.0F);
                if (level.addFreshEntity(launchedBlock)) {
                    living.getMainHandItem().getOrCreateTag().putUUID("thrown_anchor", launchedBlock.getUUID());
                }
                level.playSound(null, living.getX(), living.getY(), living.getZ(), EpicFightSounds.ENTITY_MOVE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                entityPatch.removeHurtEntities();
            }
        };
    }

    public static boolean spawnFlameJet(Level world, double x, double y, double z, float rotation, int delay, float damage, @Nullable LivingEntity caster) {
        if (world == null) {
            return false;
        }

        world.addFreshEntity(new Flame_Jet_Entity(world, x, y, z, rotation, delay, damage, caster));
        return true;
    }

    public static boolean spawnFlameJetOnGround(Level world, double x, double z, double minY, double maxY, float rotation, int delay, float damage, @Nullable LivingEntity caster) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double groundY = 0.0D;

        do {
            BlockPos belowPos = blockpos.below();
            BlockState blockstate = world.getBlockState(belowPos);

            if (blockstate.isFaceSturdy(world, belowPos, Direction.UP)) {
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState currentState = world.getBlockState(blockpos);
                    VoxelShape voxelshape = currentState.getCollisionShape(world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        groundY = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= minY);

        if (flag) {
            world.addFreshEntity(new Flame_Jet_Entity(world, x, (double) blockpos.getY() + groundY, z, rotation, delay, damage, caster));
            return true;
        } else {
            return false;
        }
    }

    public static Flare_Bomb_Entity spawnFlareBomb(Level world, double x, double y, double z, @Nullable LivingEntity thrower) {
        if (world == null) {
            return null;
        }

        Flare_Bomb_Entity bomb = null;
        if (thrower != null) {
            bomb = new Flare_Bomb_Entity(ModEntities.FLARE_BOMB.get(), world, thrower);
        }
        bomb.setPos(x, y, z);
        world.addFreshEntity(bomb);
        return bomb;
    }

    public static Flare_Bomb_Entity spawnFlareBombWithMotion(Level world, double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable LivingEntity thrower) {
        if (world == null) {
            return null;
        }

        Flare_Bomb_Entity bomb = null;
        if (thrower != null) {
            bomb = new Flare_Bomb_Entity(ModEntities.FLARE_BOMB.get(), world, thrower);
        }
        bomb.setPos(x, y, z);
        bomb.setDeltaMovement(motionX, motionY, motionZ);
        world.addFreshEntity(bomb);
        return bomb;
    }

    private static void createEnhancedFlameJetBurst(Level world, double centerX, double centerY, double centerZ, LivingEntity caster) {
        if (world.isClientSide()) return;

        float damage = (float) CMCommonConfig.NetheriteMonstrosity.FlareBombDamage;
        float yaw = caster.getYRot();

        for (int i = 0; i < 2; i++) {
            spawnFlameJetOnGround(world, centerX, centerZ, centerY - 1, centerY + 3,
                    yaw * ((float) Math.PI / 180F),
                    i * 5, damage, caster);
        }

        createMultiAngleBurst(world, centerX, centerY, centerZ, yaw, 8, 2, damage, caster);
    }

    private static void createMultiAngleBurst(Level world, double centerX, double centerY, double centerZ,
                                              float yaw, int directions, int rings, float damage, LivingEntity caster) {
        for (int ring = 1; ring <= rings; ring++) {
            for (int i = 0; i < directions; i++) {

                float baseAngle = (float) (2 * Math.PI * i / directions);
                float offsetAngle = baseAngle + (ring * 0.3f);

                double distance = 0.8D + (ring * 0.8D);
                int delay = ring * 2;

                double jetX = centerX + Math.cos(offsetAngle) * distance;
                double jetZ = centerZ + Math.sin(offsetAngle) * distance;

                spawnFlameJetOnGround(world, jetX, jetZ, centerY - 1, centerY + 2,
                        offsetAngle, delay, damage, caster);

                if (ring == 2 && i % 2 == 0) {
                    float midAngle = offsetAngle + (float) (Math.PI / directions);
                    double midJetX = centerX + Math.cos(midAngle) * (distance * 0.9);
                    double midJetZ = centerZ + Math.sin(midAngle) * (distance * 0.9);

                    spawnFlameJetOnGround(world, midJetX, midJetZ, centerY - 1, centerY + 2,
                            midAngle, delay + 1, damage, caster);
                }
            }
        }
    }

    private static void createForwardFlameJet(Level world, double startX, double startY, double startZ,
                                              LivingEntity caster, float yaw) {
        if (world.isClientSide()) return;

        float damage = (float) CMCommonConfig.NetheriteMonstrosity.FlareBombDamage;

        Vec3 lookVec = caster.getLookAngle();

        int totalLength = 15;
        double startDistance = 2.5;
        double interval = 1;

        int count = (int) ((totalLength - startDistance) / interval) + 1;

        for (int i = 0; i < count; i++) {
            double distance = startDistance + i * interval;

            double jetX = startX + lookVec.x * distance;
            double jetZ = startZ + lookVec.z * distance;

            int delay = i * 2;

            spawnFlameJetOnGround(world, jetX, jetZ, startY - 1, startY + 2,
                    yaw * ((float) Math.PI / 180F), delay, damage, caster);

            int sideCount = world.random.nextInt(2) + 1;

            for (int j = 0; j < sideCount; j++) {

                double sideOffset = (world.random.nextDouble()) + 0.5;

                boolean isLeft = world.random.nextBoolean();
                double offsetMultiplier = isLeft ? -1 : 1;

                Vec3 perpendicularVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();

                double sideJetX = jetX + perpendicularVec.x * sideOffset * offsetMultiplier;
                double sideJetZ = jetZ + perpendicularVec.z * sideOffset * offsetMultiplier;

                int sideDelay = delay + world.random.nextInt(3);

                spawnFlameJetOnGround(world, sideJetX, sideJetZ, startY - 1, startY + 2,
                        yaw * ((float) Math.PI / 180F), sideDelay, damage, caster);
            }
        }
    }

    private static void createMiniForwardFlameJet(Level world, double startX, double startY, double startZ,
                                                  LivingEntity caster, float yaw) {
        if (world.isClientSide()) return;

        float damage = (float) CMCommonConfig.NetheriteMonstrosity.FlareBombDamage;

        Vec3 lookVec = caster.getLookAngle();

        int totalLength = 5;
        double startDistance = 1.5;
        double interval = 1;

        int count = (int) ((totalLength - startDistance) / interval) + 1;

        for (int i = 0; i < count; i++) {
            double distance = startDistance + i * interval;

            double jetX = startX + lookVec.x * distance;
            double jetZ = startZ + lookVec.z * distance;

            int delay = i * 2;

            spawnFlameJetOnGround(world, jetX, jetZ, startY - 1, startY + 2,
                    yaw * ((float) Math.PI / 180F), delay, damage, caster);

            int sideCount = world.random.nextInt(2) + 1;

            for (int j = 0; j < sideCount; j++) {

                double sideOffset = (world.random.nextDouble()) + 0.5;

                boolean isLeft = world.random.nextBoolean();
                double offsetMultiplier = isLeft ? -1 : 1;

                Vec3 perpendicularVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();

                double sideJetX = jetX + perpendicularVec.x * sideOffset * offsetMultiplier;
                double sideJetZ = jetZ + perpendicularVec.z * sideOffset * offsetMultiplier;

                int sideDelay = delay + world.random.nextInt(3);

                spawnFlameJetOnGround(world, sideJetX, sideJetZ, startY - 1, startY + 2,
                        yaw * ((float) Math.PI / 180F), sideDelay, damage, caster);
            }
        }
    }

    public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint) {
        Animator animator = entityPatch.getAnimator();
        Armature armature = entityPatch.getArmature();

        Pose pose = animator.getPose(0.5f);

        Vec3 pos = entityPatch.getOriginal().position();
        OpenMatrix4f modelTf = OpenMatrix4f.createTranslation((float) pos.x, (float) pos.y, (float) pos.z)
                .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                        .mulBack(entityPatch.getModelMatrix(1)));

        OpenMatrix4f JointTf = new OpenMatrix4f(armature.getBoundTransformFor(pose, joint)).mulFront(modelTf);

        return OpenMatrix4f.transform(JointTf, Vec3.ZERO);
    }

    private static void createRandomDirectionalSpearBarrage(ServerLevel level, LivingEntity caster, LivingEntity target,
                                                            double startX, double startY, double startZ,
                                                            int numberOfDirections, float angleStep, double firstAngleOffset) {
        float baseDamage = (float) 3;
        double baseDistance = 6.0;

        for (int k = 0; k < numberOfDirections; ++k) {
            double angle = (double) target.getYRot() - firstAngleOffset + (double) ((float) k * angleStep);
            double rad = Math.toRadians(angle);

            double spawnX = target.getX() + Math.cos(rad) * baseDistance;
            double spawnZ = target.getZ() + Math.sin(rad) * baseDistance;
            double spawnY = target.getY() + target.getBbHeight() * 0.7;

            Vec3 direction = new Vec3(
                    target.getX() - spawnX,
                    target.getY(0.5) - spawnY,
                    target.getZ() - spawnZ
            ).normalize();

            boolean isWaterSpear = level.random.nextBoolean();

            double offsetX = spawnX + (level.random.nextDouble() - 0.5) * 1.5;
            double offsetY = spawnY + (level.random.nextDouble() - 0.5) * 1.0;
            double offsetZ = spawnZ + (level.random.nextDouble() - 0.5) * 1.5;

            float yRot = (float) (Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) + 90F;
            float xRot = (float) -(Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180F / Math.PI));

            if (isWaterSpear) {
                Water_Spear_Entity waterSpear = ScyllaEffectInvoker.spawnWaterSpear(level, caster, direction, offsetX, offsetY, offsetZ, baseDamage);
                if (waterSpear != null) {
                    waterSpear.setYRot(yRot);
                    waterSpear.setXRot(xRot);
                    waterSpear.accelerationPower = 0.25D;
                    waterSpear.setTotalBounces(2);
                }
            } else {
                Lightning_Spear_Entity lightningSpear = ScyllaEffectInvoker.spawnLightningSpear(level, caster, direction, offsetX, offsetY, offsetZ, baseDamage,
                        (float) 2,
                        (float) 0.02,
                        2F);
                if (lightningSpear != null) {
                    lightningSpear.setYRot(yRot);
                    lightningSpear.setXRot(xRot);
                    lightningSpear.accelerationPower = 0.25D;
                }
            }

            if (level.random.nextFloat() < 0.25f) {
                double extraOffsetX = spawnX + (level.random.nextDouble() - 0.5) * 1.0;
                double extraOffsetY = spawnY + (level.random.nextDouble() - 0.5) * 0.5;
                double extraOffsetZ = spawnZ + (level.random.nextDouble() - 0.5) * 1.0;

                if (!isWaterSpear) {
                    Water_Spear_Entity extraWaterSpear = ScyllaEffectInvoker.spawnWaterSpear(level, caster, direction, extraOffsetX, extraOffsetY, extraOffsetZ, baseDamage * 0.8f);
                    if (extraWaterSpear != null) {
                        extraWaterSpear.setYRot(yRot);
                        extraWaterSpear.setXRot(xRot);
                        extraWaterSpear.accelerationPower = 0.2D;
                    }
                } else {
                    Lightning_Spear_Entity extraLightningSpear = ScyllaEffectInvoker.spawnLightningSpear(level, caster, direction, extraOffsetX, extraOffsetY, extraOffsetZ, baseDamage * 0.8f,
                            (float) 2,
                            (float) 0.02,
                            2F);
                    if (extraLightningSpear != null) {
                        extraLightningSpear.setYRot(yRot);
                        extraLightningSpear.setXRot(xRot);
                        extraLightningSpear.accelerationPower = 0.2D;
                    }
                }
            }
        }

        createVerticalSpearBarrage(level, caster, target, baseDamage);
    }

    private static void createVerticalSpearBarrage(ServerLevel level, LivingEntity caster, LivingEntity target, float baseDamage) {
        int verticalSpearCount = 6;
        double height = 8.0;

        for (int i = 0; i < verticalSpearCount; i++) {
            double offsetX = target.getX() + (level.random.nextDouble() - 0.5) * 3.0;
            double offsetY = target.getY() + height;
            double offsetZ = target.getZ() + (level.random.nextDouble() - 0.5) * 3.0;

            Vec3 direction = new Vec3(0, -1, 0);

            float yRot = 0;
            float xRot = 90.0F;

            if (i % 2 == 0) {
                Water_Spear_Entity waterSpear = ScyllaEffectInvoker.spawnWaterSpear(level, caster, direction, offsetX, offsetY, offsetZ, baseDamage);
                if (waterSpear != null) {
                    waterSpear.setYRot(yRot);
                    waterSpear.setXRot(xRot);
                    waterSpear.accelerationPower = 0.3D;
                }
            } else {
                Lightning_Spear_Entity lightningSpear = ScyllaEffectInvoker.spawnLightningSpear(level, caster, direction, offsetX, offsetY, offsetZ, baseDamage,
                        (float) 2,
                        (float) 0.01,
                        2.0F);
                if (lightningSpear != null) {
                    lightningSpear.setYRot(yRot);
                    lightningSpear.setXRot(xRot);
                    lightningSpear.accelerationPower = 0.3D;
                }
            }
        }
    }


    public static final AnimationEvent.E4<Vec3f, Joint, Double, Float> FRACTURE_GROUNDSLAM_NOSMOKE = (entitypatch, animation, params) -> {
        Vec3 position = ((LivingEntity) entitypatch.getOriginal()).position();
        OpenMatrix4f modelTransform = entitypatch.getArmature().getBoundTransformFor(((StaticAnimation) animation.get()).getPoseByTime(entitypatch, (Float) params.fourth(), 1.0F), (Joint) params.second()).mulFront(OpenMatrix4f.createTranslation((float) position.x, (float) position.y, (float) position.z).mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(entitypatch.getModelMatrix(1.0F))));
        Level level = ((LivingEntity) entitypatch.getOriginal()).level();
        Vec3 weaponEdge = OpenMatrix4f.transform(modelTransform, ((Vec3f) params.first()).toDoubleVector());
        BlockHitResult hitResult = level.clip(new ClipContext(position.add(0.0, 0.1, 0.0), weaponEdge, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entitypatch.getOriginal()));
        Vec3 slamStartPos;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            Direction direction = hitResult.getDirection();
            BlockPos collidePos = hitResult.getBlockPos().offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            if (!LevelUtil.canTransferShockWave(level, collidePos, level.getBlockState(collidePos))) {
                collidePos = collidePos.below();
            }

            slamStartPos = new Vec3((double) collidePos.getX(), (double) collidePos.getY(), (double) collidePos.getZ());
        } else {
            slamStartPos = weaponEdge.subtract(0.0, 1.0, 0.0);
        }

        LevelUtil.circleSlamFracture((LivingEntity) entitypatch.getOriginal(), level, slamStartPos, (Double) params.third(), false, true);
    };

    public static final AnimationEvent.E4<Vec3f, Joint, Double, Float> FRACTURE_TARGET_GROUNDSLAM_NOSMOKE = (entitypatch, animation, params) -> {
        LivingEntity target = entitypatch.getTarget();
        if (target != null) {
            Vec3 position = ((LivingEntity) entitypatch.getTarget()).position();
            OpenMatrix4f modelTransform = entitypatch.getArmature().getBoundTransformFor(((StaticAnimation) animation.get()).getPoseByTime(entitypatch, (Float) params.fourth(), 1.0F), (Joint) params.second()).mulFront(OpenMatrix4f.createTranslation((float) position.x, (float) position.y, (float) position.z).mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(entitypatch.getModelMatrix(1.0F))));
            Level level = ((LivingEntity) entitypatch.getOriginal()).level();
            Vec3 weaponEdge = OpenMatrix4f.transform(modelTransform, ((Vec3f) params.first()).toDoubleVector());
            BlockHitResult hitResult = level.clip(new ClipContext(position.add(0.0, 0.1, 0.0), weaponEdge, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entitypatch.getOriginal()));
            Vec3 slamStartPos;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Direction direction = hitResult.getDirection();
                BlockPos collidePos = hitResult.getBlockPos().offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
                if (!LevelUtil.canTransferShockWave(level, collidePos, level.getBlockState(collidePos))) {
                    collidePos = collidePos.below();
                }

                slamStartPos = new Vec3((double) collidePos.getX(), (double) collidePos.getY(), (double) collidePos.getZ());
            } else {
                slamStartPos = weaponEdge.subtract(0.0, 1.0, 0.0);
            }

            LevelUtil.circleSlamFracture((LivingEntity) entitypatch.getOriginal(), level, slamStartPos, (Double) params.third(), false, true);
        }
    };

}
