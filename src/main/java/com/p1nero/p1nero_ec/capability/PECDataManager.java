package com.p1nero.p1nero_ec.capability;

import com.p1nero.p1nero_ec.network.PECPacketHandler;
import com.p1nero.p1nero_ec.network.PECPacketRelay;
import com.p1nero.p1nero_ec.network.packet.clientbound.PersistentBoolDataSyncPacket;
import com.p1nero.p1nero_ec.network.packet.clientbound.PersistentDoubleDataSyncPacket;
import com.p1nero.p1nero_ec.network.packet.clientbound.PersistentStringDataSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public class PECDataManager {
    private final static Set<String> EXISTING_ID = new HashSet<>();
    public static DoubleData skillPoint = new DoubleData("skill_point", 0);
    public static BoolData isLockOn = new BoolData("is_lock_on", false);
    //能否使用技能
    public static BoolData TIDAL_CLAW_LOCK = new BoolData("tidal_claw_lock", false);
    public static BoolData CERAUNUS_LOCK = new BoolData("ceraunus_lock", false);
    public static BoolData INFERNAL_FORGE_LOCK = new BoolData("infernal_forge_lock", false);
    public static BoolData GAUNTLET_OF_GUARD_LOCK = new BoolData("gauntlet_of_guard_lock", false);
    public static BoolData DUAL_ANNIHILATOR_LOCK = new BoolData("dual_annihilator_lock", false);
    public static BoolData SOUL_RENDER_LOCK = new BoolData("soul_render_lock", false);
    public static BoolData ASTRAPE_LOCK = new BoolData("astrape_lock", false);
    public static BoolData THE_INCINERATOR_LOCK = new BoolData("the_incinerator_lock", false);
    public static BoolData WRATH_OF_THE_DESERT_LOCK = new BoolData("wrath_of_the_desert_lock", false);

    /**
     * 初始化玩家是否可以使用武器技能，用于逐步解锁
     */
    public static void resetAll(ServerPlayer player, boolean locked) {
        TIDAL_CLAW_LOCK.put(player, locked);
        CERAUNUS_LOCK.put(player, locked);
        INFERNAL_FORGE_LOCK.put(player, locked);
        GAUNTLET_OF_GUARD_LOCK.put(player, locked);
        DUAL_ANNIHILATOR_LOCK.put(player, locked);
        SOUL_RENDER_LOCK.put(player, locked);
        ASTRAPE_LOCK.put(player, locked);
        THE_INCINERATOR_LOCK.put(player, locked);
        WRATH_OF_THE_DESERT_LOCK.put(player, locked);
    }

    public static void putData(Player player, String key, double value) {
        getPECPlayer(player).putDouble(key, value);
    }

    public static void putData(Player player, String key, String value) {
        getPECPlayer(player).putString(key, value);
    }

    public static void putData(Player player, String key, boolean value) {
        getPECPlayer(player).putBoolean(key, value);
    }

    public static boolean getBool(Player player, String key) {
        return getPECPlayer(player).getBoolean(key);
    }

    public static double getDouble(Player player, String key) {
        return getPECPlayer(player).getDouble(key);
    }

    public static String getString(Player player, String key) {
        return getPECPlayer(player).getString(key);
    }

    public static PECPlayer getPECPlayer(Player player) {
        return player.getCapability(PECCapabilityProvider.PEC_PLAYER).orElse(new PECPlayer());
    }


    public abstract static class Data<T> {

        protected String key;
        protected boolean isLocked = false;//增加一个锁，用于初始化数据用
        protected int id;

        public Data(String key) {
            if (EXISTING_ID.contains(key)) {
                throw new IllegalArgumentException(key + " is already exist!");
            }
            this.key = key;
            EXISTING_ID.add(key);
        }

        public String getKey() {
            return key;
        }

        public void init(Player player) {
            isLocked = getPECPlayer(player).getBoolean(key + "isLocked");

        }

        public boolean isLocked(Player player) {
            return getPECPlayer(player).getBoolean(key + "isLocked");
        }

        public boolean isLocked(CompoundTag playerData) {
            return playerData.getBoolean(key + "isLocked");
        }

        public void lock(Player player) {
            putData(player, key + "isLocked", true);
            isLocked = true;
        }

        public void unLock(Player player) {
            putData(player, key + "isLocked", false);
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            isLocked = false;
        }

        public abstract T get(Player player);

        public abstract void put(Player player, T data);

    }

    public static class StringData extends Data<String> {

        protected boolean isLocked = false;//增加一个锁
        protected String defaultString = "";

        public StringData(String key, String defaultString) {
            super(key);
            this.defaultString = defaultString;
        }

        @Override
        public void init(Player player) {
            put(player, defaultString);
        }

        @Override
        public void put(Player player, String value) {
            if (!isLocked(player)) {
                putData(player, key, value);
                if (player instanceof ServerPlayer serverPlayer) {
                    PECPacketRelay.sendToPlayer(PECPacketHandler.INSTANCE, new PersistentStringDataSyncPacket(key, isLocked, value), serverPlayer);
                } else {
                    PECPacketRelay.sendToServer(PECPacketHandler.INSTANCE, new PersistentStringDataSyncPacket(key, isLocked, value));
                }
            }
        }

        @Override
        public String get(Player player) {
            return getPECPlayer(player).getString(key);
        }

        public String get(CompoundTag playerData) {
            return playerData.getString(key);
        }

    }

    public static class DoubleData extends Data<Double> {

        private double defaultValue = 0;

        public DoubleData(String key, double defaultValue) {
            super(key);
            this.defaultValue = defaultValue;
        }

        public void init(Player player) {
            isLocked = getPECPlayer(player).getBoolean(key + "isLocked");
            put(player, defaultValue);
        }

        @Override
        public void put(Player player, Double value) {
            if (!isLocked(player)) {
                putData(player, key, value);
                if (player instanceof ServerPlayer serverPlayer) {
                    PECPacketRelay.sendToPlayer(PECPacketHandler.INSTANCE, new PersistentDoubleDataSyncPacket(key, isLocked, value), serverPlayer);
                } else {
                    PECPacketRelay.sendToServer(PECPacketHandler.INSTANCE, new PersistentDoubleDataSyncPacket(key, isLocked, value));
                }
            }
        }

        @Override
        public Double get(Player player) {
            return getPECPlayer(player).getDouble(key);
        }

        public double get(CompoundTag playerData) {
            return playerData.getDouble(key);
        }

    }

    public static class BoolData extends Data<Boolean> {

        boolean defaultBool;

        public BoolData(String key, boolean defaultBool) {
            super(key);
            this.defaultBool = defaultBool;
        }

        public void init(Player player) {
            isLocked = getPECPlayer(player).getBoolean(key + "isLocked");
            put(player, defaultBool);
        }

        @Override
        public void put(Player player, Boolean value) {
            if (isLocked(player))
                return;

            putData(player, key, value);
            if (player instanceof ServerPlayer serverPlayer) {
                PECPacketRelay.sendToPlayer(PECPacketHandler.INSTANCE, new PersistentBoolDataSyncPacket(key, isLocked, value), serverPlayer);
            } else {
                PECPacketRelay.sendToServer(PECPacketHandler.INSTANCE, new PersistentBoolDataSyncPacket(key, isLocked, value));
            }
        }

        @Override
        public Boolean get(Player player) {
            return getPECPlayer(player).getBoolean(key);
        }

        public boolean get(CompoundTag playerData) {
            return playerData.getBoolean(key);
        }

    }

}
