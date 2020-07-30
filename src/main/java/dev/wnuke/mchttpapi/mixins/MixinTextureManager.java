package dev.wnuke.mchttpapi.mixins;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TextureManager.class)
public class MixinTextureManager {
    @Inject(method = "loadTextureAsync", at = @At("HEAD"), cancellable = true)
    private void loadTexturesAsync(Identifier id, Executor executor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.setReturnValue(CompletableFuture.completedFuture(null));
        cir.cancel();
    }

    @Inject(method = "bindTexture", at = @At("HEAD"), cancellable = true)
    private void bindTexture(Identifier id, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "registerDynamicTexture", at = @At("HEAD"), cancellable = true)
    private void registerDynamicTexture(String prefix, NativeImageBackedTexture texture, CallbackInfoReturnable<Identifier> cir) {
        cir.setReturnValue(new Identifier(""));
        cir.cancel();
    }

    @Inject(method = "registerTexture", at = @At("HEAD"), cancellable = true)
    private void registerTexture(Identifier identifier, AbstractTexture abstractTexture, CallbackInfo ci) {
        ci.cancel();
    }
}
