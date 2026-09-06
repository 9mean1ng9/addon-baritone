package com.minedest.addon;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("minedestaddon")
public class MineDestAddon {

    private final Minecraft mc = Minecraft.getInstance();

    public MineDestAddon() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        ClientPlayerEntity player = mc.player;
        if (player == null || mc.level == null) return;

        try {
            IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
            if (baritone == null) return;

            // Логика авто-еды
            if (player.getFoodData().getFoodLevel() <= 14) {
                int foodSlot = findFoodInHotbar(player);
                if (foodSlot != -1) {
                    player.inventory.selected = foodSlot;
                    mc.options.keyUse.setDown(true);
                }
            } else {
                if (mc.options.keyUse.isDown()) {
                    mc.options.keyUse.setDown(false);
                }
            }
        } catch (Exception ignored) {}
    }

    private int findFoodInHotbar(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem().isEdible()) {
                return i;
            }
        }
        return -1;
    }
}
