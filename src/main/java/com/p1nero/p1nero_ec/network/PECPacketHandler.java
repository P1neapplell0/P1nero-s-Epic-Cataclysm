package com.p1nero.p1nero_ec.network;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.network.packet.clientbound.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public class PECPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(PEpicCataclysmMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    private static int index;

    public static synchronized void register() {
        register(PersistentBoolDataSyncPacket.class, PersistentBoolDataSyncPacket::decode);
        register(PersistentDoubleDataSyncPacket.class, PersistentDoubleDataSyncPacket::decode);
        register(PersistentStringDataSyncPacket.class, PersistentStringDataSyncPacket::decode);

        register(SyncPECPlayerPacket.class, SyncPECPlayerPacket::decode);
        register(AddAvlEntityAfterImageParticle.class, AddAvlEntityAfterImageParticle::decode);
    }

    private static <MSG extends BasePacket> void register(final Class<MSG> packet, Function<FriendlyByteBuf, MSG> decoder) {
        INSTANCE.messageBuilder(packet, index++).encoder(BasePacket::encode).decoder(decoder).consumerMainThread(BasePacket::handle).add();
    }
}
