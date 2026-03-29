package com.p1nero.p1nero_ec.utils;

import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.entity.effect.Lightning_Storm_Entity;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Lightning_Spear_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Spark_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Storm_Serpent_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Water_Spear_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

import java.util.List;

public class ScyllaEffectInvoker {

    public static final AnimationEvent.E0 SUMMON_THUNDER_UPGRADED = (entitypatch, animation, params) -> {
        if (entitypatch.isLogicalClient()) {
            return;
        }

        if (animation.get() instanceof AttackAnimation attackAnimation) {
            AttackAnimation.Phase phase = attackAnimation.phases[1];

            int strikeCount = (int) ValueModifier.calculator().attach(phase.getProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER).orElse(ValueModifier.setter(3))).getResult(0);
            float damage = ValueModifier.calculator().attach(phase.getProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER).orElse(ValueModifier.setter(8.0F))).getResult(0);

            LivingEntity original = entitypatch.getOriginal();
            ServerLevel level = (ServerLevel) original.level();
            float total = damage + ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create().get(original, original.getItemInHand(InteractionHand.MAIN_HAND), null, damage);

            List<Entity> list = level.getEntities(original, original.getBoundingBox().inflate(10.0D, 4.0D, 10.0D), (e) -> {
                return !(e.distanceToSqr(original) > 100.0D) && !e.isAlliedTo(original) && entitypatch.getOriginal().hasLineOfSight(e);
            });

            list = HitEntityList.Priority.HOSTILITY.sort(entitypatch, list);
            int count = 0;

            while (count < strikeCount && count < list.size()) {
                Entity e = list.get(count++);
                BlockPos blockpos = e.blockPosition();

                createSixDirectionLightningStorms(level, original, blockpos, total);

                createRandomSpearBarrage(level, original, e, 8, total * 0.6f);

                createMultipleLightningBolts(level, original, e, blockpos, entitypatch, attackAnimation, phase, total);

                LightningBolt mainLightning = EntityType.LIGHTNING_BOLT.create(level);
                mainLightning.setVisualOnly(true);
                mainLightning.moveTo(Vec3.atBottomCenterOf(blockpos));
                mainLightning.setDamage(0.0F);
                mainLightning.setCause(entitypatch instanceof ServerPlayerPatch serverPlayerPatch ? serverPlayerPatch.getOriginal() : null);

                DamageSource dmgSource = new DamageSource(e.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT), entitypatch.getOriginal());
                EpicFightDamageSource damageSource = attackAnimation.getEpicFightDamageSource(dmgSource, entitypatch, e, phase).setUsedItem(entitypatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND));
                e.hurt(damageSource, total);
                e.thunderHit(level, mainLightning);

                level.addFreshEntity(mainLightning);

                Vec3 lightningPos = Vec3.atBottomCenterOf(blockpos);
                level.sendParticles(ModParticle.LIGHTNING_EXPLODE.get(),
                        lightningPos.x, lightningPos.y, lightningPos.z,
                        1, 0, 0, 0, 0);
            }

            if (count > 0) {
                if (level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && level.random.nextFloat() < 0.08F && level.getThunderLevel(1.0F) < 1.0F) {
                    level.setWeatherParameters(0, Mth.randomBetweenInclusive(level.random, 12000, 180000), true, true);
                }

                original.playSound(SoundEvents.TRIDENT_THUNDER, 5.0F, 1.0F);

                ScreenShake_Entity.ScreenShake(level, original.position(), 25, 0.15f, 0, 20);
            }
        }
    };

    public static final AnimationEvent.E0 SUMMON_THUNDER = (entitypatch, animation, params) -> {
        if (!entitypatch.isLogicalClient()) {
            Object patt173405$temp = animation.get();
            if (patt173405$temp instanceof AttackAnimation) {
                AttackAnimation attackAnimation = (AttackAnimation)patt173405$temp;
                AttackAnimation.Phase phase = attackAnimation.phases[1];
                int i = (int)ValueModifier.calculator().attach((ValueModifier)phase.getProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER).orElse(ValueModifier.setter(3.0F))).getResult(0.0F);
                float damage = ValueModifier.calculator().attach((ValueModifier)phase.getProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER).orElse(ValueModifier.setter(8.0F))).getResult(0.0F);
                LivingEntity original = (LivingEntity)entitypatch.getOriginal();
                ServerLevel level = (ServerLevel)original.level();
                float total = damage + ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0]).get(original, original.getItemInHand(InteractionHand.MAIN_HAND), (LivingEntity)null, damage);
                List<Entity> list = level.getEntities(original, original.getBoundingBox().inflate(10.0, 4.0, 10.0), (ex) -> {
                    return !(ex.distanceToSqr(original) > 100.0) && !ex.isAlliedTo(original) && ((LivingEntity)entitypatch.getOriginal()).hasLineOfSight(ex);
                });
                list = HitEntityList.Priority.HOSTILITY.sort(entitypatch, list);
                int count = 0;

                while(count < i && count < list.size()) {
                    Entity e = (Entity)list.get(count++);
                    BlockPos blockpos = e.blockPosition();

                    createMultipleLightningBolts(level, original, e, blockpos, entitypatch, attackAnimation, phase, total);
                    LightningBolt lightningbolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
                    lightningbolt.setVisualOnly(true);
                    lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
                    lightningbolt.setDamage(0.0F);
                    ServerPlayer var19;
                    if (entitypatch instanceof ServerPlayerPatch) {
                        ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)entitypatch;
                        var19 = (ServerPlayer)serverPlayerPatch.getOriginal();
                    } else {
                        var19 = null;
                    }

                    lightningbolt.setCause(var19);
                    DamageSource dmgSource = new DamageSource(e.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT), entitypatch.getOriginal());
                    EpicFightDamageSource damageSource = attackAnimation.getEpicFightDamageSource(dmgSource, entitypatch, e, phase).setUsedItem(((LivingEntity)entitypatch.getOriginal()).getItemInHand(InteractionHand.MAIN_HAND));
                    e.hurt(damageSource, total);
                    e.thunderHit(level, lightningbolt);
                    level.addFreshEntity(lightningbolt);
                }

                if (count > 0) {
                    if (level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && level.random.nextFloat() < 0.08F && level.getThunderLevel(1.0F) < 1.0F) {
                        level.setWeatherParameters(0, Mth.randomBetweenInclusive(level.random, 12000, 180000), true, true);
                    }

                    original.playSound(SoundEvents.TRIDENT_THUNDER, 5.0F, 1.0F);
                }
            }

        }
    };

    public static void spawnLightningStorm(Level world, double x, double y, double z, double v, float rotation, int delay, float damage, float hpDamage, LivingEntity caster, float size) {
        if (world == null) return;

        BlockPos blockpos = BlockPos.containing(x, y, z);
        boolean foundGround = false;
        double groundY = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = world.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        groundY = voxelshape.max(Direction.Axis.Y);
                    }
                }
                foundGround = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(y) - 1);

        if (foundGround) {
            Lightning_Storm_Entity storm = new Lightning_Storm_Entity(world, x, (double) blockpos.getY() + groundY, z, rotation, delay, damage, hpDamage, caster, size);
            world.addFreshEntity(storm);
        }
    }

    public static Water_Spear_Entity spawnWaterSpear(Level world, LivingEntity caster, Vec3 direction, double x, double y, double z, float damage) {
        if (world == null) return null;

        Water_Spear_Entity spear = new Water_Spear_Entity(caster, direction, world, damage, 1.0);
        spear.setPos(x, y, z);
        world.addFreshEntity(spear);
        return spear;
    }

    public static Lightning_Spear_Entity spawnLightningSpear(Level world, LivingEntity caster, Vec3 direction, double x, double y, double z, float damage, float areaDamage, float hpDamage, float areaRadius) {
        if (world == null) return null;

        Lightning_Spear_Entity spear = new Lightning_Spear_Entity(caster, direction, world, damage, 1.0);
        spear.setAreaDamage(areaDamage);
        spear.setHpDamage(hpDamage);
        spear.setAreaRadius(areaRadius);
        spear.setPos(x, y, z);
        world.addFreshEntity(spear);
        return spear;
    }

    public static Spark_Entity spawnSpark(Level world, LivingEntity caster, double x, double y, double z, float damage, float areaDamage, float hpDamage, float areaRadius) {
        if (world == null) return null;

        Spark_Entity spark = new Spark_Entity(world, caster);
        spark.setDamage(damage);
        spark.setAreaDamage(areaDamage);
        spark.setHpDamage(hpDamage);
        spark.setAreaRadius(areaRadius);
        spark.setPos(x, y, z);
        world.addFreshEntity(spark);
        return spark;
    }

    public static void spawnStormSerpent(Level world, double x, double y, double z, float rotation, int delay, LivingEntity caster, float damage, LivingEntity target, boolean isFirst) {
        if (world == null) return;

        Storm_Serpent_Entity serpent = new Storm_Serpent_Entity(world, x, y, z, rotation, delay, caster, damage, target, isFirst);
        world.addFreshEntity(serpent);
    }

    public static Wave_Entity spawnWave(Level world, LivingEntity caster, double x, double y, double z, int duration, float speed) {
        if (world == null) return null;

        Wave_Entity wave = new Wave_Entity(world, caster, duration, speed);
        wave.setPos(x, y, z);
        world.addFreshEntity(wave);
        return wave;
    }

    public static void createForwardLightningStorm(Level world, LivingEntity caster, double distance, float damage, float hpDamage, float size) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 lookVec = caster.getLookAngle();
        Vec3 spawnPos = caster.position().add(lookVec.x * distance, 0, lookVec.z * distance);
        float rotation = caster.getYRot() * ((float) Math.PI / 180F);

        spawnLightningStorm(world, spawnPos.x, spawnPos.y, spawnPos.z, caster.getY() + 3, rotation, 0, damage, hpDamage, caster, size);
    }

    public static void createForwardLightningStormLine(Level world, LivingEntity caster, double baseDistance, double maxDistance, int count, double stepDistance, float damage, float hpDamage, float size) {
        if (world == null || world.isClientSide() || caster == null) return;
        Vec3 lookVec = caster.getLookAngle();
        float baseRotation = caster.getYRot() * ((float) Math.PI / 180F);

        double actualBaseDistance = Math.min(baseDistance, maxDistance);

        for (int i = 0; i < count; i++) {
            double currentDistance = actualBaseDistance + (stepDistance * i);
            if (currentDistance > maxDistance) break; // 不超过最大距离

            Vec3 spawnPos = caster.position().add(lookVec.x * currentDistance, 0, lookVec.z * currentDistance);

            spawnLightningStorm(world,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    caster.getY() + 3,
                    baseRotation,
                    0,
                    3,
                    hpDamage,
                    caster,
                    2.5f
            );
        }
    }

    public static void createFanLightningStorms(Level world, LivingEntity caster, double distance, int count, float spreadAngle, float damage, float hpDamage, float size) {
        if (world == null || world.isClientSide() || caster == null) return;

        float baseYaw = caster.getYRot();

        for (int i = 0; i < count; i++) {
            float horizontalAngle = -spreadAngle / 2 + (spreadAngle * i / (count - 1));

            double spawnX = caster.getX();
            double spawnY = caster.getY() + caster.getBbHeight() * 0.7F;
            double spawnZ = caster.getZ();

            double horizontalRad = Math.toRadians(baseYaw + horizontalAngle);
            double verticalRad = 0;

            Vec3 direction = new Vec3(
                    -Math.sin(horizontalRad) * Math.cos(verticalRad),
                    Math.sin(verticalRad),
                    Math.cos(horizontalRad) * Math.cos(verticalRad)
            ).normalize();

            Vec3 spawnPos = caster.position().add(direction.x * distance, 0, direction.z * distance);

            float rotation = (float) Math.atan2(direction.z, direction.x);

            spawnLightningStorm(world,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    caster.getY() + 3,
                    rotation,
                    0,
                    damage,
                    hpDamage,
                    caster,
                    size
            );
        }
    }


    public static void createCircleLightningStorms(Level world, LivingEntity caster, double radius, int count, float damage, float hpDamage, float size) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 center = caster.position();

        for (int i = 0; i < count; i++) {
            float angle = (float) i * (float) Math.PI * 2.0F / count;
            Vec3 offset = new Vec3(Mth.cos(angle) * radius, 0, Mth.sin(angle) * radius);
            Vec3 spawnPos = center.add(offset);

            spawnLightningStorm(world, spawnPos.x, spawnPos.y, spawnPos.z, caster.getY() + 3, angle, 0, damage, hpDamage, caster, size);
        }
    }

    public static void createLightningWall(Level world, LivingEntity caster, double startX, double startZ,
                                           double endX, double endZ, int lightningCount, float damage, float hpDamage) {
        if (world == null || world.isClientSide() || caster == null) return;

        double dx = endX - startX;
        double dz = endZ - startZ;
        double length = Math.sqrt(dx * dx + dz * dz);

        double unitX = dx / length;
        double unitZ = dz / length;

        for (int i = 0; i < lightningCount; i++) {
            double progress = (double) i / (lightningCount - 1);
            double x = startX + dx * progress;
            double z = startZ + dz * progress;

            double perpX = -unitZ;

            for (int side = -1; side <= 1; side += 2) {
                double offsetX = x + perpX * side * 2.0;
                double offsetZ = z + unitX * side * 2.0;

                float rotation = (float) Math.atan2(unitX * side, perpX * side);
                spawnLightningStorm(world, offsetX, offsetZ, caster.getY() - 2, caster.getY() + 5,
                        rotation, 0, damage, hpDamage, caster, 2.0F);
            }
        }
    }

    public static void createArcLightningWall(Level world, LivingEntity caster, double distance,
                                              float arcAngle, int lightningCount, float damage, float hpDamage) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 lookVec = caster.getLookAngle();
        float baseYaw = caster.getYRot();

        for (int i = 0; i < lightningCount; i++) {
            float angle = -arcAngle / 2 + (arcAngle * i / (lightningCount - 1));
            double angleRad = Math.toRadians(angle);

            // 计算弧形位置
            Vec3 direction = lookVec.yRot((float) angleRad).normalize();
            double x = caster.getX() + direction.x * distance;
            double z = caster.getZ() + direction.z * distance;

            float rotation = (baseYaw + angle) * ((float) Math.PI / 180F);
            spawnLightningStorm(world, x, z, caster.getY() - 2, caster.getY() + 5,
                    rotation, i * 2, damage, hpDamage, caster, 2.0F);
        }
    }

    public static void createCircleLightningWall(Level world, LivingEntity caster, double radius,
                                                 int lightningCount, float damage, float hpDamage) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 center = caster.position();

        for (int i = 0; i < lightningCount; i++) {
            float angle = (float) i * (float) Math.PI * 2.0F / lightningCount;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            spawnLightningStorm(world, x, z, caster.getY() - 2, caster.getY() + 5,
                    angle, i * 2, damage, hpDamage, caster, 2.0F);
        }
    }

    public static void createDualSpearAttack(Level world, LivingEntity caster, LivingEntity target, int spearCount, float spreadAngle, float damage) {
        if (world == null || world.isClientSide() || caster == null || target == null) return;

        float f = Mth.cos(caster.getYRot() * ((float) Math.PI / 180F));
        float f1 = Mth.sin(caster.getYRot() * ((float) Math.PI / 180F));

        for (int i = 0; i < spearCount; i++) {
            double offsetAngle = (i - ((spearCount - 1) / 2.0)) * Math.toRadians(spreadAngle);

            double d0 = caster.getX() + f * -0.5;
            double d1 = caster.getY() + caster.getBbHeight() * 0.7F;
            double d2 = caster.getZ() + f1 * -0.5;

            double d3 = target.getX() - d0;
            double d4 = target.getY(0.2D) - d1;
            double d5 = target.getZ() - d2;

            double x = d3 * Math.cos(offsetAngle) + d5 * Math.sin(offsetAngle);
            double y = d4;
            double z = -d3 * Math.sin(offsetAngle) + d5 * Math.cos(offsetAngle);

            Vec3 direction = new Vec3(x, y, z).normalize();
            float yRot = (float) (Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) + 90F;
            float xRot = (float) -(Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180F / Math.PI));

            if (i % 2 == 0) {
                Water_Spear_Entity waterSpear = spawnWaterSpear(world, caster, direction, d0, d1, d2, damage);
                if (waterSpear != null) {
                    waterSpear.setYRot(yRot);
                    waterSpear.setXRot(xRot);
                }
            } else {
                Lightning_Spear_Entity lightningSpear = spawnLightningSpear(world, caster, direction, d0, d1, d2, damage,
                        (float) CMCommonConfig.Scylla.LightningAreaDamage, (float) CMCommonConfig.Scylla.StormHpDamage, 2.0F);
                if (lightningSpear != null) {
                    lightningSpear.setYRot(yRot);
                    lightningSpear.setXRot(xRot);
                }
            }
        }
    }

    public static void createForwardDualSpears(Level world, LivingEntity caster, int spearCount, float spreadAngle, float distance, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;
        Vec3 lookVec = caster.getLookAngle();
        float f = Mth.cos(caster.getYRot() * ((float) Math.PI / 180F));
        float f1 = Mth.sin(caster.getYRot() * ((float) Math.PI / 180F));
        for (int i = 0; i < spearCount; i++) {
            double offsetAngle = (i - ((spearCount - 1) / 2.0)) * Math.toRadians(spreadAngle);

            double spawnX = caster.getX() + f * -0.5;
            double spawnY = caster.getY() + caster.getBbHeight() * 0.7F;
            double spawnZ = caster.getZ() + f1 * -0.5;
            Vec3 baseDirection = lookVec.yRot((float) offsetAngle).normalize();
            Vec3 direction = baseDirection.scale(distance).normalize();
            float yRot = (float) (Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) + 90F;
            float xRot = (float) -(Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180F / Math.PI));
            if (i % 2 == 0) {
                Water_Spear_Entity waterSpear = spawnWaterSpear(world, caster, direction, spawnX, spawnY, spawnZ, damage);
                if (waterSpear != null) {
                    waterSpear.setYRot(yRot);
                    waterSpear.setXRot(xRot);
                    waterSpear.accelerationPower = 0.2D;
                }
            } else {
                Lightning_Spear_Entity lightningSpear = spawnLightningSpear(world, caster, direction, spawnX, spawnY, spawnZ, damage,
                        (float) CMCommonConfig.Scylla.LightningAreaDamage, (float) CMCommonConfig.Scylla.StormHpDamage,
                        2.0F);
                if (lightningSpear != null) {
                    lightningSpear.setYRot(yRot);
                    lightningSpear.setXRot(xRot);
                    lightningSpear.accelerationPower = 0.2D;
                }
            }
        }
    }

    public static void createFanSpearBarrage(Level world, LivingEntity caster, int rows, int spearsPerRow,
                                             float horizontalSpread, float verticalSpread, float baseDistance, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;

        float baseYaw = caster.getYRot();

        for (int row = 0; row < rows; row++) {
            float verticalAngle = -verticalSpread / 2 + (verticalSpread * row / (rows - 1));

            for (int col = 0; col < spearsPerRow; col++) {
                float horizontalAngle = -horizontalSpread / 2 + (horizontalSpread * col / (spearsPerRow - 1));

                double spawnX = caster.getX();
                double spawnY = caster.getY() + caster.getBbHeight() * 0.7F;
                double spawnZ = caster.getZ();

                double horizontalRad = Math.toRadians(baseYaw + horizontalAngle);
                double verticalRad = Math.toRadians(verticalAngle);

                Vec3 direction = new Vec3(
                        -Math.sin(horizontalRad) * Math.cos(verticalRad),
                        Math.sin(verticalRad),
                        Math.cos(horizontalRad) * Math.cos(verticalRad)
                ).normalize();

                float yRot = (float) (Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) + 90F;
                float xRot = (float) -(Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180F / Math.PI));

                boolean isWaterSpear = (row + col) % 2 == 0;

                if (isWaterSpear) {
                    Water_Spear_Entity waterSpear = spawnWaterSpear(world, caster, direction, spawnX, spawnY, spawnZ, damage);
                    if (waterSpear != null) {
                        waterSpear.setYRot(yRot);
                        waterSpear.setXRot(xRot);
                        waterSpear.setTotalBounces(6);
                    }
                } else {
                    Lightning_Spear_Entity lightningSpear = spawnLightningSpear(world, caster, direction, spawnX, spawnY, spawnZ, damage,
                            (float) 3,
                            (float) 0.025,
                            2F);
                    if (lightningSpear != null) {
                        lightningSpear.setYRot(yRot);
                        lightningSpear.setXRot(xRot);
                    }
                }
            }
        }
    }

    public static void createRotatingSpearRing(Level world, LivingEntity caster, double radius, int spearCount,
                                               float rotationSpeed, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;
        Vec3 center = caster.position();
        Vec3 lookVec = caster.getLookAngle();

        Vec3 upVec = new Vec3(0, 1, 0);
        Vec3 rightVec = lookVec.cross(upVec).normalize();
        Vec3 upVecCorrected = rightVec.cross(lookVec).normalize();
        for (int i = 0; i < spearCount; i++) {
            float angle = (float) i * (float) Math.PI * 2.0F / spearCount;

            double offsetX = Math.cos(angle) * radius;
            double offsetY = Math.sin(angle) * radius;

            Vec3 ringOffset = rightVec.scale(offsetX).add(upVecCorrected.scale(offsetY));
            Vec3 spawnPos = center.add(ringOffset).add(0, caster.getBbHeight() * 0.7, 0);

            Vec3 direction = ringOffset.normalize();

            float yRot = (float) (Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) + 90F;
            float xRot = (float) -(Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180F / Math.PI));
            // 交替生成水矛和闪电矛
            if (i % 2 == 0) {
                Water_Spear_Entity waterSpear = spawnWaterSpear(world, caster, direction, spawnPos.x, spawnPos.y, spawnPos.z, damage);
                if (waterSpear != null) {
                    waterSpear.setYRot(yRot);
                    waterSpear.setXRot(xRot);
                }
            } else {
                Lightning_Spear_Entity lightningSpear = spawnLightningSpear(world, caster, direction, spawnPos.x, spawnPos.y, spawnPos.z, damage,
                        (float) CMCommonConfig.Scylla.LightningAreaDamage, (float) CMCommonConfig.Scylla.StormHpDamage,
                        1.8F);
                if (lightningSpear != null) {
                    lightningSpear.setYRot(yRot);
                    lightningSpear.setXRot(xRot);
                }
            }
        }
    }

    public static void createFocusedSpearBeam(Level world, LivingEntity caster, int spearCount, float convergenceDistance, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;
        Vec3 lookVec = caster.getLookAngle();
        Vec3 convergencePoint = caster.position()
                .add(lookVec.x * convergenceDistance,
                        caster.getBbHeight() * 0.7,
                        lookVec.z * convergenceDistance);
        for (int i = 0; i < spearCount; i++) {
            // 在玩家周围随机生成位置
            double angle = Math.random() * 2 * Math.PI;
            double radius = 1.5 + Math.random();
            double spawnX = caster.getX() + Math.cos(angle) * radius;
            double spawnY = caster.getY() + caster.getBbHeight() * 0.7 + (Math.random() - 0.5);
            double spawnZ = caster.getZ() + Math.sin(angle) * radius;

            // 计算朝向汇聚点的方向
            Vec3 direction = convergencePoint.subtract(spawnX, spawnY, spawnZ).normalize();

            float yRot = (float) (Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) + 90F;
            float xRot = (float) -(Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180F / Math.PI));
            // 交替生成水矛和闪电矛
            if (i % 2 == 0) {
                Water_Spear_Entity waterSpear = spawnWaterSpear(world, caster, direction, spawnX, spawnY, spawnZ, damage);
                if (waterSpear != null) {
                    waterSpear.setYRot(yRot);
                    waterSpear.setXRot(xRot);
                }
            } else {
                Lightning_Spear_Entity lightningSpear = spawnLightningSpear(world, caster, direction, spawnX, spawnY, spawnZ, damage,
                        (float) CMCommonConfig.Scylla.LightningAreaDamage, (float) CMCommonConfig.Scylla.StormHpDamage,
                        2.0F);
                if (lightningSpear != null) {
                    lightningSpear.setYRot(yRot);
                    lightningSpear.setXRot(xRot);
                }
            }
        }
    }

    public static void createStormSerpentSwarm(Level world, LivingEntity caster, LivingEntity target, int serpentCount, double spreadDistance, float damage) {
        if (world == null || world.isClientSide() || caster == null || target == null) return;

        float f = Mth.cos(caster.getYRot() * ((float) Math.PI / 180F));
        float f1 = Mth.sin(caster.getYRot() * ((float) Math.PI / 180F));

        for (int i = 0; i < serpentCount; i++) {
            double firstAngleOffset = (serpentCount - 1) / 2.0 * spreadDistance;
            double math = 0 - firstAngleOffset + (i * spreadDistance);

            double d0 = caster.getX() + f * math;
            double d1 = caster.getY() + caster.getBbHeight() * 0.7F;
            double d2 = caster.getZ() + f1 * math;

            double d3 = target.getX() - d0;
            double d4 = target.getY(0.35) - d1;
            double d5 = target.getZ() - d2;
            Vec3 direction = new Vec3(d3, d4, d5).normalize();

            spawnStormSerpent(world, d0, caster.getY(), d2,
                    (float) (Mth.atan2(direction.z, direction.x)), i * 8, caster, damage, target, i == 0);
        }
    }

    public static void createWaveAttack(Level world, LivingEntity caster, int waveCount, float angleStep, double distance, int duration, float speed) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 lookVec = caster.getLookAngle();
        double theta = (caster.getYRot()) * (Math.PI / 180);
        theta += Math.PI / 2;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);

        double firstAngleOffset = (waveCount - 1) / 2.0 * angleStep;

        for (int i = 0; i < waveCount; i++) {
            double angle = caster.getYRot() - firstAngleOffset + (i * angleStep);
            double rad = Math.toRadians(angle);
            double dx = -Math.sin(rad);
            double dz = Math.cos(rad);

            double spawnX = caster.getX() + vecX * distance;
            double spawnY = caster.getY();
            double spawnZ = caster.getZ() + vecZ * distance;

            Wave_Entity wave = spawnWave(world, caster, spawnX, spawnY, spawnZ, duration, speed);
            if (wave != null) {
                wave.setState(1);
                wave.setYRot(-(float) (Mth.atan2(dx, dz) * (180F / Math.PI)));
            }
        }
    }

    public static void createSparkExplosion(Level world, LivingEntity caster, double x, double y, double z, int sparkCount, float damage, float areaDamage, float hpDamage, float areaRadius) {
        if (world == null || world.isClientSide() || caster == null) return;

        for (int i = 0; i < sparkCount; i++) {
            Spark_Entity spark = spawnSpark(world, caster, x, y, z, damage, areaDamage, hpDamage, areaRadius);
            if (spark != null) {
                spark.shoot((world.random.nextDouble() - 0.5) * 0.5F,
                        world.random.nextDouble() * 0.4F + 0.01F,
                        (world.random.nextDouble() - 0.5) * 0.5F,
                        1.0F, 1F);
            }
        }
    }

    public static void createForwardDualSpears(Level world, LivingEntity caster, int spearCount, float spreadAngle, float distance) {
        createForwardDualSpears(world, caster, spearCount, spreadAngle, distance,
                (float) CMCommonConfig.Scylla.SpearDamage);
    }

    public static void createFanSpearBarrage(Level world, LivingEntity caster, int rows, int spearsPerRow,
                                             float horizontalSpread, float verticalSpread, float baseDistance) {
        createFanSpearBarrage(world, caster, rows, spearsPerRow, horizontalSpread, verticalSpread, baseDistance,
                (float) 2);
    }

    public static void createRotatingSpearRing(Level world, LivingEntity caster, double radius, int spearCount, float rotationSpeed) {
        createRotatingSpearRing(world, caster, radius, spearCount, rotationSpeed,
                (float) CMCommonConfig.Scylla.SpearDamage);
    }

    public static void createFocusedSpearBeam(Level world, LivingEntity caster, int spearCount, float convergenceDistance) {
        createFocusedSpearBeam(world, caster, spearCount, convergenceDistance,
                (float) 5);
    }

    public static void createForwardDualSpears(LivingEntity caster, int spearCount, float spreadAngle, float distance) {
        createForwardDualSpears(caster.level(), caster, spearCount, spreadAngle, distance);
    }

    public static void createFanSpearBarrage(LivingEntity caster, int rows, int spearsPerRow,
                                             float horizontalSpread, float verticalSpread, float baseDistance) {
        createFanSpearBarrage(caster.level(), caster, rows, spearsPerRow, horizontalSpread, verticalSpread, baseDistance);
    }

    public static void createRotatingSpearRing(LivingEntity caster, double radius, int spearCount, float rotationSpeed) {
        createRotatingSpearRing(caster.level(), caster, radius, spearCount, rotationSpeed);
    }

    public static void createFocusedSpearBeam(LivingEntity caster, int spearCount, float convergenceDistance) {
        createFocusedSpearBeam(caster.level(), caster, spearCount, convergenceDistance);
    }

    public static void createForwardLightningStorm(Level world, LivingEntity caster, double distance) {
        createForwardLightningStorm(world, caster, distance,
                (float) CMCommonConfig.Scylla.LightningAreaDamage, (float) CMCommonConfig.Scylla.StormHpDamage,
                2.0F);
    }

    public static void createFanLightningStorms(Level world, LivingEntity caster, double distance, int count, float spreadAngle) {
        createFanLightningStorms(world, caster, distance, count, spreadAngle,
                (float) 3,
                (float) 0.02,
                2.5F);
    }

    public static void createForwardLightningStormLine(Level world, LivingEntity caster, double baseDistance, double maxDistance, int count, double stepDistance) {
        createForwardLightningStormLine(world, caster, baseDistance, maxDistance, count, stepDistance,
                (float) 3,
                (float) 0.02,
                2.3F);
    }

    public static void createCircleLightningStorms(Level world, LivingEntity caster, double radius, int count) {
        createCircleLightningStorms(world, caster, radius, count,
                (float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage,
                2.0F);
    }

    public static void createDualSpearAttack(Level world, LivingEntity caster, LivingEntity target, int spearCount, float spreadAngle) {
        createDualSpearAttack(world, caster, target, spearCount, spreadAngle,
                (float) CMCommonConfig.Scylla.SpearDamage);
    }

    public static void createStormSerpentSwarm(Level world, LivingEntity caster, LivingEntity target, int serpentCount, double spreadDistance) {
        createStormSerpentSwarm(world, caster, target, serpentCount, spreadDistance,
                (float) CMCommonConfig.Scylla.SnakeDamage);
    }

    public static void createWaveAttack(Level world, LivingEntity caster, int waveCount, float angleStep, double distance) {
        createWaveAttack(world, caster, waveCount, angleStep, distance, 80, 9.0F);
    }

    public static void createSparkExplosion(Level world, LivingEntity caster, double x, double y, double z, int sparkCount) {
        createSparkExplosion(world, caster, x, y, z, sparkCount,
                (float) CMCommonConfig.Scylla.LightningStormDamage,
                (float) CMCommonConfig.Scylla.LightningAreaDamage,
                (float) CMCommonConfig.Scylla.StormHpDamage,
                2.0F);
    }

    public static void createForwardLightningWall(LivingEntity caster, double distance, int lightningCount) {
        Vec3 lookVec = caster.getLookAngle();
        double startX = caster.getX() + lookVec.x * 2;
        double startZ = caster.getZ() + lookVec.z * 2;
        double endX = caster.getX() + lookVec.x * distance;
        double endZ = caster.getZ() + lookVec.z * distance;

        createLightningWall(caster.level(), caster, startX, startZ, endX, endZ, lightningCount,
                (float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage);
    }

    public static void createCircularLightningWall(LivingEntity caster, double radius, int lightningCount) {
        createCircleLightningWall(caster.level(), caster, radius, lightningCount,
                (float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage);
    }

    public static void createArcingLightningWall(LivingEntity caster, double distance, float arcAngle, int lightningCount) {
        createArcLightningWall(caster.level(), caster, distance, arcAngle, lightningCount,
                (float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage);
    }

    public static void createForwardLightningStorm(LivingEntity caster, double distance) {
        createForwardLightningStorm(caster.level(), caster, distance);
    }

    public static void createFanLightningStorms(LivingEntity caster, double distance, int count, float spreadAngle) {
        createFanLightningStorms(caster.level(), caster, distance, count, spreadAngle);
    }

    public static void createForwardLightningStormLine(LivingEntity caster, double baseDistance, double maxDistance, int count, double stepDistance) {
        createForwardLightningStormLine(caster.level(), caster, baseDistance, maxDistance, count, stepDistance);
    }

    public static void createCircleLightningStorms(LivingEntity caster, double radius, int count) {
        createCircleLightningStorms(caster.level(), caster, radius, count);
    }

    public static void createDualSpearAttack(LivingEntity caster, LivingEntity target, int spearCount, float spreadAngle) {
        createDualSpearAttack(caster.level(), caster, target, spearCount, spreadAngle);
    }

    public static void createStormSerpentSwarm(LivingEntity caster, LivingEntity target, int serpentCount, double spreadDistance) {
        createStormSerpentSwarm(caster.level(), caster, target, serpentCount, spreadDistance);
    }

    public static void createWaveAttack(LivingEntity caster, int waveCount, float angleStep, double distance) {
        createWaveAttack(caster.level(), caster, waveCount, angleStep, distance);
    }

    public static void createSparkExplosion(LivingEntity caster, double x, double y, double z, int sparkCount) {
        createSparkExplosion(caster.level(), caster, x, y, z, sparkCount);
    }

    public static void createChainLightningEffect(Level world, LivingEntity caster, double centerX, double centerZ, int branchCount, int particlesPerBranch,
                                                  double initialRadius, double radiusIncrement, double curveFactor, int delay) {
        if (world == null || world.isClientSide() || caster == null) return;

        float angleIncrement = (float) (2 * Math.PI / branchCount);
        for (int branch = 0; branch < branchCount; ++branch) {
            float baseAngle = angleIncrement * branch;

            for (int i = 0; i < particlesPerBranch; ++i) {
                double currentRadius = initialRadius + i * radiusIncrement;
                float currentAngle = (float) (baseAngle + i * angleIncrement / initialRadius + (float) (i * curveFactor));

                double xOffset = currentRadius * Math.cos(currentAngle);
                double zOffset = currentRadius * Math.sin(currentAngle);

                double spawnX = centerX + xOffset;
                double spawnZ = centerZ + zOffset;
                int actualDelay = delay * (i + 1);

                spawnLightningStorm(world, spawnX, spawnZ, caster.getY() - 5, caster.getY() + 3,
                        currentAngle, actualDelay,
                        (float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage,
                        caster, 1.0F);
            }
        }
    }

    public static void createRotatingLightningRing(Level world, LivingEntity caster, double radius, int lightningCount, float rotationSpeed, int duration) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 center = caster.position();

        for (int i = 0; i < lightningCount; i++) {
            float angle = (float) i * (float) Math.PI * 2.0F / lightningCount;
            Vec3 offset = new Vec3(Mth.cos(angle) * radius, 0, Mth.sin(angle) * radius);
            Vec3 spawnPos = center.add(offset);

            int delay = (int) (i * rotationSpeed);

            spawnLightningStorm(world, spawnPos.x, spawnPos.y, spawnPos.z, caster.getY() + 3, angle, delay,
                    (float) CMCommonConfig.Scylla.LightningStormDamage, (float) CMCommonConfig.Scylla.StormHpDamage,
                    caster, 1.5F);
        }
    }

    public static void createHomingLightningSpears(Level world, LivingEntity caster, LivingEntity target, int spearCount, float spreadAngle) {
        if (world == null || world.isClientSide() || caster == null || target == null) return;

        Vec3 casterPos = caster.position();
        Vec3 targetPos = target.position();
        Vec3 direction = targetPos.subtract(casterPos).normalize();

        for (int i = 0; i < spearCount; i++) {
            float angle = -spreadAngle / 2 + (spreadAngle * i / (spearCount - 1));
            double angleRad = angle * (Math.PI / 180.0);
            Vec3 spearDirection = direction.yRot((float) angleRad).normalize();

            double spawnX = casterPos.x + spearDirection.x * 2;
            double spawnY = casterPos.y + caster.getBbHeight() * 0.7;
            double spawnZ = casterPos.z + spearDirection.z * 2;

            Lightning_Spear_Entity spear = spawnLightningSpear(world, caster, spearDirection, spawnX, spawnY, spawnZ,
                    (float) CMCommonConfig.Scylla.SpearDamage,
                    (float) CMCommonConfig.Scylla.LightningAreaDamage,
                    (float) CMCommonConfig.Scylla.StormHpDamage,
                    2.0F);

            if (spear != null) {
                spear.accelerationPower = 0.15D;
            }
        }
    }

    public static void createCombinedElementalAttack(Level world, LivingEntity caster, LivingEntity target) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 casterPos = caster.position();

        createForwardLightningStorm(world, caster, 3.0);

        createWaveAttack(world, caster, 8, 45.0f, 2.5);

        if (target != null) {
            createSparkExplosion(world, caster, target.getX(), target.getY(), target.getZ(), 8);
        }

        ScreenShake_Entity.ScreenShake(world, casterPos, 30, 0.2f, 0, 20);
    }

    public static void createPhaseTransitionEffect(Level world, LivingEntity caster, int phaseLevel) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 center = caster.position();

        switch (phaseLevel) {
            case 1:
                createCircleLightningStorms(world, caster, 4.0, 6);
                createWaveAttack(world, caster, 4, 90.0f, 3.0);
                break;
            case 2:
                createRotatingLightningRing(world, caster, 5.0, 12, 10.0f, 100);
                createSparkExplosion(world, caster, center.x, center.y, center.z, 12);
                break;
            default:
                createForwardLightningStorm(world, caster, 2.0);
                break;
        }

        ScreenShake_Entity.ScreenShake(world, center, 25, 0.15f, 10, 15);
    }

    public static Vec3 calculateAimDirection(LivingEntity caster, LivingEntity target, double heightRatio) {
        if (caster == null || target == null) if (caster != null) {
            return caster.getLookAngle();
        }

        double targetX = target.getX();
        double targetY = target.getY() + target.getBbHeight() * heightRatio;
        double targetZ = target.getZ();

        double casterX = 0;
        if (caster != null) {
            casterX = caster.getX();
        }
        double casterY = 0;
        if (caster != null) {
            casterY = caster.getY() + caster.getBbHeight() * 0.7;
        }
        double casterZ = 0;
        if (caster != null) {
            casterZ = caster.getZ();
        }

        return new Vec3(targetX - casterX, targetY - casterY, targetZ - casterZ).normalize();
    }

    public static Vec3 getRandomCirclePosition(Vec3 center, double radius) {
        double angle = Math.random() * 2 * Math.PI;
        double x = center.x + Math.cos(angle) * radius * Math.random();
        double z = center.z + Math.sin(angle) * radius * Math.random();
        return new Vec3(x, center.y, z);
    }

    public static boolean isValidSpawnPosition(Level world, double x, double y, double z) {
        BlockPos pos = BlockPos.containing(x, y, z);
        return world.isEmptyBlock(pos) && world.isEmptyBlock(pos.above());
    }

    private static void createSixDirectionLightningStorms(ServerLevel level, LivingEntity caster, BlockPos targetPos, float damage) {
        Vec3 center = Vec3.atBottomCenterOf(targetPos);
        double distance = 5.0;

        for (int i = 0; i < 6; i++) {
            float angle = (float) i * 60.0F;
            double angleRad = Math.toRadians(angle);

            double stormX = center.x + Math.cos(angleRad) * distance;
            double stormZ = center.z + Math.sin(angleRad) * distance;

            ScyllaEffectInvoker.spawnLightningStorm(level,
                    stormX, center.y, stormZ,
                    center.y + 5,
                    angle,
                    0,
                    damage * 0.7f,
                    (float) CMCommonConfig.Scylla.StormHpDamage * 0.8f,
                    caster,
                    2.3F
            );

            ScyllaEffectInvoker.createSparkExplosion(level, caster,
                    stormX, center.y + 1, stormZ,
                    3,
                    damage * 0.3f,
                    (float) CMCommonConfig.Scylla.LightningAreaDamage * 0.6f,
                    (float) CMCommonConfig.Scylla.StormHpDamage * 0.4f,
                    1.5F
            );
        }

        ScyllaEffectInvoker.spawnLightningStorm(level,
                center.x, center.y, center.z,
                center.y + 6,
                caster.getYRot() * ((float) Math.PI / 180F),
                0,
                damage,
                (float) CMCommonConfig.Scylla.StormHpDamage,
                caster,
                2.2F
        );
    }

    private static void createMultipleLightningBolts(ServerLevel level, LivingEntity caster, Entity target, BlockPos targetPos,
                                                     LivingEntityPatch<?> entityPatch, AttackAnimation attackAnimation,
                                                     AttackAnimation.Phase phase, float damage) {
        Vec3 center = Vec3.atBottomCenterOf(targetPos);
        int lightningCount = 3;

        for (int i = 0; i < lightningCount; i++) {
            LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
            lightningbolt.setVisualOnly(true);

            double offsetX = (level.random.nextDouble() - 0.5) * 3.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 3.0;
            double offsetY = level.random.nextDouble() * 2.0;

            Vec3 lightningPos = center.add(offsetX, offsetY, offsetZ);
            lightningbolt.moveTo(lightningPos);
            lightningbolt.setDamage(0.0F);
            lightningbolt.setCause(entityPatch instanceof ServerPlayerPatch serverPlayerPatch ? serverPlayerPatch.getOriginal() : null);

            level.addFreshEntity(lightningbolt);

            // 在每个随机闪电位置也生成爆炸粒子
            level.sendParticles(ModParticle.LIGHTNING_EXPLODE.get(),
                    lightningPos.x, lightningPos.y, lightningPos.z,
                    1, 0, 0, 0, 0);

            createSplashDamageAtPosition(level, caster, lightningPos, damage * 0.4f, entityPatch, attackAnimation, phase);
        }
    }

    private static void createSplashDamageAtPosition(ServerLevel level, LivingEntity caster, Vec3 position, float splashDamage,
                                                     LivingEntityPatch<?> entityPatch, AttackAnimation attackAnimation,
                                                     AttackAnimation.Phase phase) {
        List<Entity> splashTargets = level.getEntities(caster, new AABB(position, position).inflate(2.0D), (e) -> {
            return !e.isAlliedTo(caster) && e instanceof LivingEntity;
        });

        for (Entity splashTarget : splashTargets) {
            DamageSource dmgSource = new DamageSource(splashTarget.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT), caster);
            EpicFightDamageSource damageSource = attackAnimation.getEpicFightDamageSource(dmgSource, entityPatch, splashTarget, phase)
                    .setUsedItem(caster.getItemInHand(InteractionHand.MAIN_HAND));

            if (splashTarget.hurt(damageSource, splashDamage)) {
                Vec3 knockback = splashTarget.position().subtract(position).normalize().scale(0.5);
                splashTarget.setDeltaMovement(splashTarget.getDeltaMovement().add(knockback.x, 0.2, knockback.z));
            }
        }
    }

    private static void createRandomSpearBarrage(ServerLevel level, LivingEntity caster, Entity target, int spearCount, float damage) {
        Vec3 casterPos = caster.position();
        Vec3 targetPos = target.position();

        for (int i = 0; i < spearCount; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = 2.0 + Math.random() * 2.0;
            double spawnX = casterPos.x + Math.cos(angle) * radius;
            double spawnY = casterPos.y + caster.getBbHeight() * 0.7 + (Math.random() - 0.5) * 1.5;
            double spawnZ = casterPos.z + Math.sin(angle) * radius;

            Vec3 direction = targetPos.subtract(spawnX, spawnY, spawnZ).normalize();

            float yRot = (float) (Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) + 90F;
            float xRot = (float) -(Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180F / Math.PI));

            if (i % 2 == 0) {
                Water_Spear_Entity waterSpear = ScyllaEffectInvoker.spawnWaterSpear(level, caster, direction, spawnX, spawnY, spawnZ, damage);
                if (waterSpear != null) {
                    waterSpear.setYRot(yRot);
                    waterSpear.setXRot(xRot);
                    waterSpear.accelerationPower = 0.15D;
                }
            } else {
                Lightning_Spear_Entity lightningSpear = ScyllaEffectInvoker.spawnLightningSpear(level, caster, direction, spawnX, spawnY, spawnZ, damage,
                        (float) CMCommonConfig.Scylla.LightningAreaDamage * 0.8f,
                        (float) CMCommonConfig.Scylla.StormHpDamage * 0.6F,
                        1.5F);
                if (lightningSpear != null) {
                    lightningSpear.setYRot(yRot);
                    lightningSpear.setXRot(xRot);
                    lightningSpear.accelerationPower = 0.15D;
                }
            }
        }
    }

    public static void createDirectionalLightningStormLine(ServerLevel level, LivingEntity caster,
                                                            double startX, double startZ,
                                                            double dirX, double dirZ,
                                                            double distance, int count, double stepDistance) {
        double length = Math.sqrt(dirX * dirX + dirZ * dirZ);
        double normX = dirX / length;
        double normZ = dirZ / length;

        float rotation = (float) Math.atan2(normZ, normX);

        for (int i = 0; i < count; i++) {
            double currentDistance = stepDistance * i;
            if (currentDistance > distance) break;

            double spawnX = startX + normX * currentDistance;
            double spawnZ = startZ + normZ * currentDistance;

            ScyllaEffectInvoker.spawnLightningStorm(level,
                    spawnX, caster.getY(), spawnZ,
                    caster.getY() + 3,
                    rotation,
                    i * 2,
                    (float) 4,
                    (float) 0.02,
                    caster,
                    2.0F
            );
        }
    }
}
