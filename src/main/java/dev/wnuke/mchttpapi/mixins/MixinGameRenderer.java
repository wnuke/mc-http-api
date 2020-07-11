package dev.wnuke.mchttpapi.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.renderer.GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "updateCameraAndRender", at = @At("HEAD"), cancellable = true)
    public void renderWorld(float partialTicks, long nanoTime, boolean renderWorldIn, CallbackInfo ci) {
        ci.cancel();
    }
}
