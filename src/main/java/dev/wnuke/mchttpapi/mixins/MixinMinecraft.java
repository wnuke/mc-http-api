package dev.wnuke.mchttpapi.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft {
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.setShowsDeathScreen(false);
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(boolean tick, CallbackInfo ci) {
        MinecraftClient.getInstance().skipGameRender = true;
        if (tick) {
            MinecraftClient.getInstance().tick();
        }
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
    public void isModded(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("minecraft");
    }
}
