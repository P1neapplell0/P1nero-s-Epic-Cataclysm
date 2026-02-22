package com.p1nero.p1nero_ec.capability;

import com.github.L_Ender.cataclysm.init.ModItems;
import com.p1nero.p1nero_ec.client.PECSounds;
import com.p1nero.p1nero_ec.client.gui.CustomGuiRenderer;
import com.p1nero.p1nero_ec.network.PECPacketHandler;
import com.p1nero.p1nero_ec.network.PECPacketRelay;
import com.p1nero.p1nero_ec.network.packet.clientbound.SyncPECPlayerPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

/**
 * 记录飞行和技能使用的状态，被坑了，这玩意儿也分服务端和客户端...
 * 懒得换成DataKey了将就一下吧
 */
public class PECPlayer {
    public static final int MAX_SKILL_POINTS = 5;
    private CompoundTag data = new CompoundTag();
    private int lastSkillPoints;
    private boolean isClientLockOn;

    public static boolean isValidWeapon(ItemStack itemStack) {
        return itemStack.is(ModItems.CERAUNUS.get())
                || itemStack.is(ModItems.INFERNAL_FORGE.get())
                || itemStack.is(ModItems.GAUNTLET_OF_GUARD.get())
                || itemStack.is(ModItems.THE_ANNIHILATOR.get())
                || itemStack.is(ModItems.THE_INCINERATOR.get())
                || itemStack.is(ModItems.SOUL_RENDER.get())
                || itemStack.is(ModItems.ASTRAPE.get())
                || itemStack.is(ModItems.WRATH_OF_THE_DESERT.get())
                || itemStack.is(ModItems.TIDAL_CLAWS.get());
    }

    public static void addSkillPoint(ServerPlayer serverPlayer) {
        serverPlayer.heal(2);
        double current = PECDataManager.skillPoint.get(serverPlayer);
        if (current < PECPlayer.MAX_SKILL_POINTS) {
            PECDataManager.skillPoint.put(serverPlayer, current + 1);
            serverPlayer.connection.send(new ClientboundSoundPacket(PECSounds.GAIN_ABILITY_POINTS.getHolder().orElseThrow(), SoundSource.PLAYERS, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), (float) (1.0F + 0.1F * current), (float) (0.7F + 0.1F * current), serverPlayer.getRandom().nextInt()));
        }
    }

    public static void setSkillPoint(ServerPlayer serverPlayer, int point) {
        if (point < PECPlayer.MAX_SKILL_POINTS && point >= 0) {
            int current = PECDataManager.skillPoint.get(serverPlayer).intValue();
            PECDataManager.skillPoint.put(serverPlayer, (double) point);
            //增加就播音效
            if (point > current) {
                serverPlayer.connection.send(new ClientboundSoundPacket(PECSounds.GAIN_ABILITY_POINTS.getHolder().orElseThrow(), SoundSource.PLAYERS, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 1.0F + 0.1F * point, 0.7F + 0.1F * point, serverPlayer.getRandom().nextInt()));
            }
        }
    }

    public static boolean hasSkillPoint(Player player, int count) {
        return PECDataManager.skillPoint.get(player) > count;
    }

    public static boolean consumeSkillPoint(ServerPlayer serverPlayer, int consumeValue) {
        if (serverPlayer.isCreative()) {
            return true;
        }
        int current = getSkillPoint(serverPlayer);
        if (current >= consumeValue) {
            setSkillPoint(serverPlayer, current - consumeValue);
            return true;
        }
        return false;
    }

    public static int getSkillPoint(Player player) {
        return PECDataManager.skillPoint.get(player).intValue();
    }

    public int getLastSkillPoints() {
        return lastSkillPoints;
    }

    public void setLastSkillPoints(int lastSkillPoints) {
        this.lastSkillPoints = lastSkillPoints;
    }

    public boolean isClientLockOn() {
        return isClientLockOn;
    }

    public void setClientLockOn(boolean clientLockOn) {
        isClientLockOn = clientLockOn;
    }

    public boolean getBoolean(String key) {
        return data.getBoolean(key);
    }

    public double getDouble(String key) {
        return data.getDouble(key);
    }

    public String getString(String key) {
        return data.getString(key);
    }

    public void putBoolean(String key, boolean value) {
        data.putBoolean(key, value);
    }

    public void putDouble(String key, double v) {
        data.putDouble(key, v);
    }

    public void putString(String k, String v) {
        data.putString(k, v);
    }

    public CompoundTag getData() {
        return data;
    }

    public void setData(Consumer<CompoundTag> consumer) {
        consumer.accept(data);
    }

    public CompoundTag saveNBTData(CompoundTag tag) {
        tag.put("customDataManager", data);
        return tag;
    }

    public void loadNBTData(CompoundTag tag) {
        lastSkillPoints = 0;
        if (FMLEnvironment.dist == Dist.CLIENT) {
            CustomGuiRenderer.reset();
        }
        data = tag.getCompound("customDataManager");
    }

    public void copyFrom(PECPlayer old) {
        this.data = old.data;

    }

    public void syncToClient(ServerPlayer serverPlayer) {
        PECPacketRelay.sendToPlayer(PECPacketHandler.INSTANCE, new SyncPECPlayerPacket(saveNBTData(new CompoundTag())), serverPlayer);
    }

    public void tick(Player player) {
        if (player.isLocalPlayer()) {
            //move to ClientForgeEvents
        }
    }

}
