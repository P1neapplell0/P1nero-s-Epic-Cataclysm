package com.p1nero.p1nero_ec.utils;

import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.entity.projectile.Axe_Blade_Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AxeBladeInvoker {

    private static float calculateRotationFromMovement(double xPower, double zPower) {
        return (float) Math.toDegrees(Math.atan2(zPower, xPower));
    }

    private static double findGroundY(Level world, double x, double z, double searchMinY, double searchMaxY) {
        BlockPos blockpos = BlockPos.containing(x, searchMaxY, z);
        boolean foundGround = false;
        double groundY = 0.0D;

        do {
            BlockPos belowPos = blockpos.below();
            BlockState blockstate = world.getBlockState(belowPos);
            if (blockstate.isFaceSturdy(world, belowPos, Direction.UP)) {
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
        } while (blockpos.getY() >= Mth.floor(searchMinY) - 1);

        return foundGround ? (double) blockpos.getY() + groundY : searchMaxY;
    }

    private static void spawnAxeBladeOnGround(Level world, LivingEntity caster, double x, double z,
                                              double searchMinY, double searchMaxY,
                                              double xPower, double zPower, float damage, float rotation) {
        double groundY = findGroundY(world, x, z, searchMinY, searchMaxY);

        Axe_Blade_Entity axeBlade = new Axe_Blade_Entity(
                caster,
                xPower, 0, zPower,
                world,
                damage,
                rotation - 90
        );
        axeBlade.setPos(x, groundY, z);
        world.addFreshEntity(axeBlade);
    }

    public static void createForwardAxeBlade(Level world, LivingEntity caster, double distance, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 lookVec = caster.getLookAngle();
        Vec3 spawnPos = caster.position().add(lookVec.x * distance, 0, lookVec.z * distance);

        float rotation = calculateRotationFromMovement(lookVec.x, lookVec.z);

        spawnAxeBladeOnGround(
                world, caster,
                spawnPos.x, spawnPos.z,
                caster.getY() - 2, caster.getY() + 2,
                lookVec.x, lookVec.z,
                damage, rotation
        );
    }

    public static void createClawAxeBlades(Level world, LivingEntity caster, double distance, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 lookVec = caster.getLookAngle();
        int clawCount = 3;
        double spreadAngle = 45.0;

        for (int claw = 0; claw < clawCount; claw++) {
            double angle;
            angle = -spreadAngle / 2 + (spreadAngle * claw / (clawCount - 1));

            double angleRad = angle * (Math.PI / 180.0);
            Vec3 clawDirection = lookVec.yRot((float) angleRad).normalize();

            Vec3 spawnPos = caster.position().add(clawDirection.x * distance, 0, clawDirection.z * distance);

            float rotation = calculateRotationFromMovement(clawDirection.x, clawDirection.z);

            spawnAxeBladeOnGround(
                    world, caster,
                    spawnPos.x, spawnPos.z,
                    caster.getY() - 2, caster.getY() + 2,
                    clawDirection.x, clawDirection.z,
                    damage, rotation
            );
        }
    }

    public static void createRadialAxeBlades(Level world, LivingEntity caster, double startDistance, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;

        int directions = 8;
        Vec3 center = caster.position();

        for (int i = 0; i < directions; i++) {
            float angle = (float) i * (float) Math.PI * 2.0F / directions;
            Vec3 direction = new Vec3(Mth.cos(angle), 0, Mth.sin(angle)).normalize();

            Vec3 spawnPos = center.add(direction.x * startDistance, 0, direction.z * startDistance);

            float rotation = calculateRotationFromMovement(direction.x, direction.z);

            spawnAxeBladeOnGround(
                    world, caster,
                    spawnPos.x, spawnPos.z,
                    caster.getY() - 2, caster.getY() + 2,
                    direction.x, direction.z,
                    damage, rotation
            );
        }
    }

    public static void createFiveClawAxeBlades(Level world, LivingEntity caster, double baseDistance, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 lookVec = caster.getLookAngle();
        int clawCount = 5;
        double spreadAngle = 100.0;

        for (int claw = 0; claw < clawCount; claw++) {
            double angle;
            angle = -spreadAngle / 2 + (spreadAngle * claw / (clawCount - 1));

            double angleRad = angle * (Math.PI / 180.0);
            Vec3 clawDirection = lookVec.yRot((float) angleRad).normalize();

            int bladesPerClaw = 3;
            for (int i = 0; i < bladesPerClaw; i++) {
                double progress = (double) (i + 1) / (bladesPerClaw + 1);
                double distance = baseDistance + progress * 3.0;

                Vec3 spawnPos = caster.position().add(clawDirection.x * distance, 0, clawDirection.z * distance);

                float rotation = calculateRotationFromMovement(clawDirection.x, clawDirection.z);

                spawnAxeBladeOnGround(
                        world, caster,
                        spawnPos.x, spawnPos.z,
                        caster.getY() - 2, caster.getY() + 2,
                        clawDirection.x, clawDirection.z,
                        damage * (1.0f - (float) progress * 0.2f),
                        rotation
                );
            }
        }
    }

    public static void createForwardAxeBladeOnSlope(Level world, LivingEntity caster, double distance, float damage) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 lookVec = caster.getLookAngle();

        for (double d = 1.0; d <= distance; d += 1.0) {
            Vec3 checkPos = caster.position().add(lookVec.x * d, 0, lookVec.z * d);
            double groundY = findGroundY(world, checkPos.x, checkPos.z, caster.getY() - 3, caster.getY() + 2);

            if (groundY > caster.getY() - 2.5 && d >= 1.0) {
                float rotation = calculateRotationFromMovement(lookVec.x, lookVec.z);

                Axe_Blade_Entity axeBlade = new Axe_Blade_Entity(
                        caster,
                        lookVec.x, 0, lookVec.z,
                        world,
                        damage,
                        rotation - 90
                );
                axeBlade.setPos(checkPos.x, groundY, checkPos.z);
                world.addFreshEntity(axeBlade);
                break;
            }
        }
    }

    public static void createForwardAxeBlade(Level world, LivingEntity caster, double distance) {
        createForwardAxeBlade(world, caster, distance, (float) CMCommonConfig.Aptrgangr.AxeBladeDamage);
    }

    public static void createClawAxeBlades(Level world, LivingEntity caster, double distance) {
        createClawAxeBlades(world, caster, distance, (float) CMCommonConfig.Aptrgangr.AxeBladeDamage);
    }

    public static void createRadialAxeBlades(Level world, LivingEntity caster, double startDistance) {
        createRadialAxeBlades(world, caster, startDistance, (float) CMCommonConfig.Aptrgangr.AxeBladeDamage);
    }

    public static void createFiveClawAxeBlades(Level world, LivingEntity caster, double baseDistance) {
        createFiveClawAxeBlades(world, caster, baseDistance, (float) CMCommonConfig.Aptrgangr.AxeBladeDamage);
    }

    public static void createForwardAxeBlade(LivingEntity caster) {
        createForwardAxeBlade(caster.level(), caster, 1.5);
    }

    public static void createClawAxeBlades(LivingEntity caster) {
        createClawAxeBlades(caster.level(), caster, 1.5);
    }

    public static void createRadialAxeBlades(LivingEntity caster) {
        createRadialAxeBlades(caster.level(), caster, 1.5);
    }

    public static void createFiveClawAxeBlades(LivingEntity caster) {
        createFiveClawAxeBlades(caster.level(), caster, 1.5);
    }
}
