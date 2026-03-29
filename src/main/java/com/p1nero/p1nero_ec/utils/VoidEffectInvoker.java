package com.p1nero.p1nero_ec.utils;

import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Void_Vortex_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Void_Howitzer_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Void_Rune_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Void_Shard_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class VoidEffectInvoker {

    public static boolean spawnVoidRune(Level world, double x, double y, double z, float rotation, int delay, float damage, @Nullable LivingEntity caster) {
        if (world == null) {
            return false;
        }

        world.addFreshEntity(new Void_Rune_Entity(world, x, y, z, rotation, delay, damage, caster));
        return true;
    }

    public static Void_Howitzer_Entity spawnVoidHowitzer(Level world, double x, double y, double z, @Nullable LivingEntity thrower) {
        if (world == null) {
            return null;
        }

        Void_Howitzer_Entity howitzer = null;
        if (thrower != null) {
            howitzer = new Void_Howitzer_Entity(ModEntities.VOID_HOWITZER.get(), world, thrower);
        } else {
            howitzer = new Void_Howitzer_Entity(ModEntities.VOID_HOWITZER.get(), world);
        }
        howitzer.setPos(x, y, z);
        world.addFreshEntity(howitzer);
        return howitzer;
    }

    public static Void_Shard_Entity spawnVoidShard(Level world, double x, double y, double z, @Nullable LivingEntity thrower) {
        if (world == null) {
            return null;
        }

        Void_Shard_Entity shard = null;
        if (thrower != null) {
            shard = new Void_Shard_Entity(ModEntities.VOID_SHARD.get(), world, thrower);
        } else {
            shard = new Void_Shard_Entity(ModEntities.VOID_SHARD.get(), world);
        }
        shard.setPos(x, y, z);
        world.addFreshEntity(shard);
        return shard;
    }

    public static Void_Shard_Entity spawnVoidShardWithMovement(Level world, double x, double y, double z, Vec3 movement, @Nullable LivingEntity thrower, @Nullable Entity ignoreEntity) {
        if (world == null) {
            return null;
        }

        Void_Shard_Entity shard = new Void_Shard_Entity(world, thrower, x, y, z, movement, ignoreEntity);
        world.addFreshEntity(shard);
        return shard;
    }

    public static boolean spawnVoidVortex(Level world, double x, double y, double z, float rotation, @Nullable LivingEntity caster, int lifespan) {
        if (world == null) {
            return false;
        }

        world.addFreshEntity(new Void_Vortex_Entity(world, x, y, z, rotation, caster, lifespan));
        return true;
    }

    public static boolean spawnVoidVortex(Level world, double x, double y, double z, float rotation, @Nullable LivingEntity caster) {
        return spawnVoidVortex(world, x, y, z, rotation, caster, 60); // 默认60 ticks
    }

    public static boolean spawnVoidVortexInFront(Level world, LivingEntity caster, double distance, int lifespan) {
        if (world == null || caster == null) {
            return false;
        }

        Vec3 lookVec = caster.getLookAngle();

        Vec3 baseSpawnPos = caster.getEyePosition()
                .add(lookVec.x * distance, -1.7, lookVec.z * distance);

        BlockPos spawnBlockPos = BlockPos.containing(baseSpawnPos);
        Vec3 finalSpawnPos = baseSpawnPos;
        BlockState blockState = world.getBlockState(spawnBlockPos.below());
        if (!blockState.isFaceSturdy(world, spawnBlockPos.below(), Direction.UP)) {
            for (int i = 1; i <= 5; i++) {
                BlockPos checkPos = spawnBlockPos.below(i);
                BlockState checkState = world.getBlockState(checkPos);
                if (checkState.isFaceSturdy(world, checkPos, Direction.UP)) {
                    finalSpawnPos = new Vec3(baseSpawnPos.x, checkPos.getY() + 1.0, baseSpawnPos.z);
                    break;
                }
            }
            if (finalSpawnPos.equals(baseSpawnPos)) {
                for (int i = 1; i <= 3; i++) {
                    BlockPos checkPos = spawnBlockPos.above(i);
                    BlockState checkState = world.getBlockState(checkPos.below());
                    if (checkState.isFaceSturdy(world, checkPos.below(), Direction.UP)) {
                        finalSpawnPos = new Vec3(baseSpawnPos.x, checkPos.getY(), baseSpawnPos.z);
                        break;
                    }
                }
            }
        }

        float rotation = caster.getYRot() * ((float) Math.PI / 180F);

        return spawnVoidVortex(world, finalSpawnPos.x, finalSpawnPos.y, finalSpawnPos.z, rotation, caster, lifespan);
    }


    public static void createVoidRuneRing(Level world, double centerX, double centerY, double centerZ, @Nullable LivingEntity caster) {
        if (world == null || world.isClientSide()) return;

        int standingOnY = Mth.floor(centerY) - 3;

        createVoidRuneCircle(world, centerX, centerZ, standingOnY, centerY + 1, 6, 2.5D, 0.4f, 0, caster);      // 内圈
        createVoidRuneCircle(world, centerX, centerZ, standingOnY, centerY + 1, 11, 3.5D, 0.36f, 2, caster);   // 第二圈
        createVoidRuneCircle(world, centerX, centerZ, standingOnY, centerY + 1, 14, 4.5D, 0.32f, 4, caster);   // 第三圈
        createVoidRuneCircle(world, centerX, centerZ, standingOnY, centerY + 1, 19, 5.5D, 0.28f, 6, caster);   // 第四圈
        createVoidRuneCircle(world, centerX, centerZ, standingOnY, centerY + 1, 26, 6.5D, 0.24f, 8, caster);   // 外圈

        ScreenShake_Entity.ScreenShake(world, new Vec3(centerX, centerY, centerZ), 30, 0.2f, 0, 20);
    }

    private static void createVoidRuneCircle(Level world, double centerX, double centerZ, double minY, double maxY,
                                             int count, double radius, float angleOffset, int baseDelay, @Nullable LivingEntity caster) {
        for (int i = 0; i < count; ++i) {
            float angle = (float) i * (float) Math.PI * 2.0F / count + angleOffset;
            double x = centerX + radius * Mth.cos(angle);
            double z = centerZ + radius * Mth.sin(angle);
            int delay = baseDelay + i % 3;

            spawnVoidRuneOnGround(world, x, z, minY, maxY, angle, delay, (float) CMCommonConfig.VoidCore.runeDamage, caster);
        }
    }

    public static void createVoidRuneInGaps(Level world, LivingEntity caster) {
        if (world == null || world.isClientSide() || caster == null) return;

        Vec3 center = caster.position().add(0, 0.1, 0);
        float yawRadians = caster.getYRot() * ((float) Math.PI / 180F);

        float width = 5f;
        float length = 5.0f;
        double startDistance = 1.0;

        Vec3[] corners = new Vec3[4];
        corners[0] = new Vec3(-width / 2, 0, startDistance);                    // 近端左侧
        corners[1] = new Vec3(width / 2, 0, startDistance);                     // 近端右侧
        corners[2] = new Vec3(-width / 2, 0, startDistance + length);           // 远端左侧
        corners[3] = new Vec3(width / 2, 0, startDistance + length);            // 远端右侧

        for (int i = 0; i < 4; i++) {
            double rotatedX = corners[i].x * Math.cos(yawRadians) - corners[i].z * Math.sin(yawRadians);
            double rotatedZ = corners[i].x * Math.sin(yawRadians) + corners[i].z * Math.cos(yawRadians);
            corners[i] = new Vec3(rotatedX, corners[i].y, rotatedZ).add(center);
        }

        int runeRows = 3;
        int runeCols = 6;

        for (int row = 0; row < runeRows; row++) {
            for (int col = 0; col < runeCols; col++) {
                float u = (row + 0.5f) / runeRows;
                float v = (col + 0.5f) / runeCols;

                Vec3 topPos = corners[0].add(corners[1].subtract(corners[0]).scale(u));
                Vec3 bottomPos = corners[2].add(corners[3].subtract(corners[2]).scale(u));
                Vec3 runePos = topPos.add(bottomPos.subtract(topPos).scale(v));

                double randomOffsetX = (world.random.nextDouble() - 0.5) * 0.8;
                double randomOffsetZ = (world.random.nextDouble() - 0.5) * 0.8;

                double finalX = runePos.x + randomOffsetX;
                double finalZ = runePos.z + randomOffsetZ;

                int delay = row * 3 + col * 2;

                spawnVoidRuneOnGround(
                        world,
                        finalX, finalZ,
                        caster.getY() - 1, caster.getY() + 2,
                        0,
                        delay,
                        (float) CMCommonConfig.VoidCore.runeDamage * 0.6f,
                        caster
                );
            }
        }
    }


    public static void spawnVoidRuneOnGround(Level world, double x, double z, double minY, double maxY,
                                             float rotation, int delay, float damage, @Nullable LivingEntity caster) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
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
        } while (blockpos.getY() >= Mth.floor(minY) - 1);
        if (foundGround) {
            spawnVoidRune(world, x, (double) blockpos.getY() + groundY, z, rotation, delay, damage, caster);
        }
    }

    public static void createSimpleVoidRuneRing(Level world, double centerX, double centerY, double centerZ,
                                                @Nullable LivingEntity caster, int rings, float maxRadius) {
        if (world == null || world.isClientSide()) return;
        for (int ring = 1; ring <= rings; ring++) {
            int runeCount = 6 + ring * 2;
            double radius = maxRadius * (ring / (float) rings);

            for (int i = 0; i < runeCount; i++) {
                float angle = (float) i * (float) Math.PI * 2.0F / runeCount;
                double x = centerX + radius * Mth.cos(angle);
                double z = centerZ + radius * Mth.sin(angle);
                int delay = ring * 2;

                spawnVoidRuneOnGround(world, x, z, centerY - 3, centerY + 1, angle, delay, (float) CMCommonConfig.VoidCore.runeDamage, caster);
            }
        }
    }

    public static void createForwardVoidRuneCluster(Level world, double startX, double startY, double startZ,
                                                    LivingEntity caster, float yaw) {
        if (world.isClientSide()) return;

        float damage = (float) CMCommonConfig.VoidCore.runeDamage;
        Vec3 lookVec = caster.getLookAngle();

        int clawCount = 3;
        double baseDistance = 1.4;
        double maxDistance = 6.0;
        double spreadAngle = 60.0;

        Vec3 upVec = new Vec3(0, 1, 0);
        Vec3 rightVec = lookVec.cross(upVec).normalize();

        for (int claw = 0; claw < clawCount; claw++) {
            double angle;
            angle = -spreadAngle / 2 + (spreadAngle * claw / (clawCount - 1));

            double angleRad = angle * (Math.PI / 180.0);

            Vec3 clawDirection = lookVec.yRot((float) angleRad).normalize();

            int runesPerClaw = claw == 1 ? 5 : 4;

            for (int i = 0; i < runesPerClaw; i++) {
                double progress = (double) i / (runesPerClaw - 1);
                double easedProgress = 1 - Math.pow(1 - progress, 1.5);
                double distance = baseDistance + (maxDistance - baseDistance) * easedProgress;

                double curveOffset = Math.sin(progress * Math.PI) * 0.5;
                Vec3 curveVector = rightVec.scale(curveOffset * Math.signum(angle));

                Vec3 basePos = new Vec3(
                        startX + clawDirection.x * distance,
                        startY,
                        startZ + clawDirection.z * distance
                );
                Vec3 finalPos = basePos.add(curveVector);

                double randomOffsetX = (world.random.nextDouble() - 0.5) * 0.4;
                double randomOffsetZ = (world.random.nextDouble() - 0.5) * 0.4;

                double runeX = finalPos.x + randomOffsetX;
                double runeZ = finalPos.z + randomOffsetZ;

                int delay = (int) (progress * 12) + world.random.nextInt(4);
                float runeDamage = damage * (1.0f - (float) progress * 0.4f);

                float rotation = (float) Math.atan2(runeZ - startZ, runeX - startX);

                spawnVoidRuneOnGround(world, runeX, runeZ, startY - 1, startY + 2,
                        rotation, delay, runeDamage, caster);
            }
        }
    }


}

