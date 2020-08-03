package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.HeadlessAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft {
    @Shadow
    private ClientConnection connection;
    private final RenderTickCounter renderTickCounter = new RenderTickCounter(20.0F, 0L);
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(boolean tick, CallbackInfo ci) {
        MinecraftClient.getInstance().skipGameRender = true;
        if (null != HeadlessAPI.compatLayer) {
            connection = HeadlessAPI.compatLayer.connection;
        }
        if (null != MinecraftClient.getInstance().player) {
            MinecraftClient.getInstance().player.setShowsDeathScreen(false);
        }
        if (tick) {
            int k = renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs());
            for(int j = 0; j < Math.min(10, k); ++j) {
                MinecraftClient.getInstance().tick();
            }
        }
        ci.cancel();
    }

    @Inject(method = "setOverlay", at = @At("HEAD"), cancellable = true)
    private void setOverlay(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "updateWindowTitle", at = @At("HEAD"), cancellable = true)
    private void updateWindowTitle(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "isModded", at = @At("HEAD"), cancellable = true)
    public void setNotModded(CallbackInfoReturnable<? super Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<? super String> cir) {
        cir.setReturnValue("minecraft");
    }
}
