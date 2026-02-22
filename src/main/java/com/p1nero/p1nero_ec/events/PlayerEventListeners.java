package com.p1nero.p1nero_ec.events;


import com.p1nero.p1nero_ec.PECMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.capability.PECCapabilityProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PECMod.MOD_ID)
public class PlayerEventListeners {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (player instanceof ServerPlayer serverPlayer) {
            PECCapabilityProvider.syncPlayerDataToClient(serverPlayer);
            PECDataManager.skillPoint.put(serverPlayer, 0D);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            PECCapabilityProvider.syncPlayerDataToClient(serverPlayer);
            PECDataManager.skillPoint.put(serverPlayer, 0D);
        }
    }

}
