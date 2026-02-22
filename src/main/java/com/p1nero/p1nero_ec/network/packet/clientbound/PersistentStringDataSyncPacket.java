package com.p1nero.p1nero_ec.network.packet.clientbound;

import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.network.BasePacket;
import com.p1nero.p1nero_ec.network.packet.clientbound.helper.DistHelper;
import com.p1nero.p1nero_ec.network.packet.clientbound.helper.PECClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public record PersistentStringDataSyncPacket(String key, boolean isLocked, String value) implements BasePacket {
    public static PersistentStringDataSyncPacket decode(FriendlyByteBuf buf) {
        String key = buf.readComponent().getString();
        boolean isLocked = buf.readBoolean();
        String value = buf.readComponent().getString();
        return new PersistentStringDataSyncPacket(key, isLocked, value);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(Component.literal(key));
        buf.writeBoolean(isLocked);
        buf.writeComponent(Component.literal(value));
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
            PECClientHandler.syncStringData(key, isLocked, value);
        });
    }
}