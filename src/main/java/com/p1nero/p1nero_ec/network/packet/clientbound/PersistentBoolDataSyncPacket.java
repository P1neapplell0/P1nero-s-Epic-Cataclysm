package com.p1nero.p1nero_ec.network.packet.clientbound;

import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.network.BasePacket;
import com.p1nero.p1nero_ec.network.packet.clientbound.helper.DistHelper;
import com.p1nero.p1nero_ec.network.packet.clientbound.helper.PECClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public record PersistentBoolDataSyncPacket(String key, boolean isLocked, boolean value) implements BasePacket {
    public static PersistentBoolDataSyncPacket decode(FriendlyByteBuf buf) {
        String key = buf.readComponent().getString();
        boolean isLocked = buf.readBoolean();
        boolean value = buf.readBoolean();
        return new PersistentBoolDataSyncPacket(key, isLocked, value);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(Component.literal(key));
        buf.writeBoolean(isLocked);
        buf.writeBoolean(value);
    }

    @Override
    public void execute(Player player) {
        if (player != null) {
            if (isLocked) {
                PECDataManager.putData(player, key + "isLocked", true);
                return;
            }
            PECDataManager.putData(player, key, value);
            PECDataManager.putData(player, key + "isLocked", false);
        }
        DistHelper.runClient(() -> () -> {
            PECClientHandler.syncBoolData(key, isLocked, value);
        });
    }
}