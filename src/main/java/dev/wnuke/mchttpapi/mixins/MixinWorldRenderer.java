package dev.wnuke.mchttpapi.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        ci.cancel();
    }
    @Inject(method = "processGlobalEvent", at = @At("HEAD"), cancellable = true)
    public void globalEvent(int eventId, BlockPos pos, int i, CallbackInfo ci) {
        ci.cancel();
    }
    @Inject(method = "processWorldEvent", at = @At("HEAD"), cancellable = true)
    public void worldEvent(PlayerEntity source, int eventId, BlockPos pos, int data, CallbackInfo ci) {
        ci.cancel();
    }
}
