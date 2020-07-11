package dev.wnuke.mchttpapi.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class MixinDeathScreen {
    @Inject(method = "render", at = @At("HEAD"))
    public void init(int p_render_1_, int p_render_2_, float p_render_3_, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.respawnPlayer();
        }
    }
}
