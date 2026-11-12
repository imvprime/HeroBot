package hero.bane.herobot.mod.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartRenderer.class)
public abstract class AbstractMinecartRendererMixin {
    @Unique
    private static final Identifier herobot$MINECART_LOCATION =
            Identifier.withDefaultNamespace("textures/entity/minecart.png");

    @Unique
    private static final int herobot$GHOST_COLOR = 654311423;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Redirect(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/Identifier;III)V"
            )
    )
    private void herobot$ghostInvisibleCart(SubmitNodeCollector collector, Model model, Object state, PoseStack poseStack,
                                            Identifier texture, int light, int overlay, int outlineColor) {
        LocalPlayer viewer = Minecraft.getInstance().player;
        if (state instanceof MinecartRenderState cart && cart.isInvisible && viewer != null && viewer.isSpectator()) {
            collector.submitModel(model, state, poseStack,
                    RenderTypes.entityTranslucentCull(texture),
                    light, overlay, herobot$GHOST_COLOR, null, outlineColor);
        } else {
            collector.submitModel(model, state, poseStack, texture, light, overlay, outlineColor);
        }
    }
}
