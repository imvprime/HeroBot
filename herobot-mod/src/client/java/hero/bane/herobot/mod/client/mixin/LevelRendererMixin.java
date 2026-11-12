package hero.bane.herobot.mod.client.mixin;

import hero.bane.herobot.common.rule.HeroBotSettings;
import hero.bane.herobot.mod.client.HeroBotClient;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 26.3: the spectator check for smart culling moved from LevelRenderer.renderLevel to Camera.extractRenderState.
@Mixin(Camera.class)
public class LevelRendererMixin
{
    @Redirect(method = "extractRenderState(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    private boolean canSeeWorld(LocalPlayer clientPlayerEntity)
    {
        return clientPlayerEntity.isSpectator() || (HeroBotSettings.creativeNoClip && clientPlayerEntity.isCreative() && HeroBotClient.isHeroBotLoaded());
    }
}
