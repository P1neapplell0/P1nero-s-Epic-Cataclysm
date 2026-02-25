package com.p1nero.p1nero_ec.client;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECPlayer;
import com.p1nero.p1nero_ec.skills.PECWeaponInnateSkillBase;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.SkillCastEvent;

@Mod.EventBusSubscriber(modid = PEpicCataclysmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class KeyMappings {

    public static final KeyMapping SKILL_1 = new KeyMapping(buildKey("skill_1"), GLFW.GLFW_KEY_1, "key.categories." + PEpicCataclysmMod.MOD_ID);
    public static final KeyMapping SKILL_2 = new KeyMapping(buildKey("skill_2"), GLFW.GLFW_KEY_2, "key.categories." + PEpicCataclysmMod.MOD_ID);
    public static final KeyMapping SKILL_3 = new KeyMapping(buildKey("skill_3"), GLFW.GLFW_KEY_3, "key.categories." + PEpicCataclysmMod.MOD_ID);

    public static String buildKey(String name) {
        return "key." + PEpicCataclysmMod.MOD_ID + "." + name;
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SKILL_1);
        event.register(SKILL_2);
        event.register(SKILL_3);
    }

    @Mod.EventBusSubscriber(modid = PEpicCataclysmMod.MOD_ID, value = Dist.CLIENT)
    public static class KeyPressHandler {

        public static void handleKeyPress() {
            if (ClientEngine.getInstance().getPlayerPatch() == null) {
                return;
            }
            LocalPlayerPatch localPlayerPatch = ClientEngine.getInstance().getPlayerPatch();
            if (localPlayerPatch != null && localPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof PECWeaponInnateSkillBase pecWeaponInnateSkillBase) {
                ItemStack itemStack = localPlayerPatch.getOriginal().getMainHandItem();
                CapabilityItem capabilityItem = EpicFightCapabilities.getItemCapability(localPlayerPatch.getOriginal().getMainHandItem()).orElse(CapabilityItem.EMPTY);
                if(!PECPlayer.isValidWeapon(itemStack) || capabilityItem.getInnateSkill(localPlayerPatch, itemStack) != pecWeaponInnateSkillBase) {
                    return;
                }
                testKey(SKILL_1, Minecraft.getInstance().options.keyHotbarSlots[0], pecWeaponInnateSkillBase, 1, localPlayerPatch);
                testKey(SKILL_2, Minecraft.getInstance().options.keyHotbarSlots[1], pecWeaponInnateSkillBase, 2, localPlayerPatch);
                testKey(SKILL_3, Minecraft.getInstance().options.keyHotbarSlots[2], pecWeaponInnateSkillBase, 3, localPlayerPatch);
            }
        }

        /**
         * 如果按键重复，则消耗数字键，防止按一次同时触发释放和切换物品
         */
        public static void testKey(KeyMapping custom, KeyMapping hotbar, PECWeaponInnateSkillBase pecWeaponInnateSkillBase, int id, LocalPlayerPatch localPlayerPatch) {
            KeyMapping keyMapping = custom.getKey().getValue() == hotbar.getKey().getValue() ? hotbar : custom;
            while (keyMapping.consumeClick()) {
                if (pecWeaponInnateSkillBase.hasSkillKeyIn(custom)) {
                    if (Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null && !Minecraft.getInstance().isPaused()) {
                        sendExecuteRequest(localPlayerPatch, id);
                    }
                    lockHotkeys();
                }
            }
        }

        public static void lockHotkeys() {
            for (int i = 0; i < 9; ++i) {
                while (Minecraft.getInstance().options.keyHotbarSlots[i].consumeClick());
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase.equals(TickEvent.Phase.END)) {

            }
        }

        public static SkillCastEvent sendExecuteRequest(LocalPlayerPatch executor, int skillId) {
            SkillContainer container = executor.getSkill(SkillSlots.WEAPON_INNATE);
            SkillCastEvent event = new SkillCastEvent(executor, container, null);
            if (container.canUse(executor, event)) {
                CPSkillRequest packet = new CPSkillRequest(container.getSlot());
                packet.getBuffer().writeInt(skillId);
                EpicFightNetworkManager.sendToServer(packet);
            }
            return event;
        }

    }

}
