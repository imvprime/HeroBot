package hero.bane.herobot.mod.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import hero.bane.herobot.mod.common.bot.BotPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin {

    // 26.3: `entity instanceof ServerPlayer` became `entity.canSimulateMovement()`.
    // Bots are simulated on the server, so pistons must push their velocity like any other entity.
    @ModifyExpressionValue(method = "moveCollidedEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;canSimulateMovement()Z"))
    private static boolean botPlayerCanSimulateMovement(boolean original, @com.llamalad7.mixinextras.sugar.Local Entity entity) {
        if (entity instanceof BotPlayer) return true;
        return original;
    }
}
