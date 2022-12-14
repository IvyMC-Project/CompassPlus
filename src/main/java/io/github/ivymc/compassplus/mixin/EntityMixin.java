package io.github.ivymc.compassplus.mixin;

import io.github.ivymc.compassplus.Configs;
import io.github.ivymc.compassplus.player.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "updatePositionAndAngles", at = @At("HEAD"))
    private void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        if(!Configs.commonConfig.data.enabled) return;

        if(!((Entity) (Object) this instanceof ServerPlayerEntity player)) return;
        var server = player.getServer();

        if(Configs.commonConfig.data.reduceDebug && !server.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO)) {
            var rule = server.getGameRules().get(GameRules.REDUCED_DEBUG_INFO);
            rule.set(true, server);
        }
        if(!Configs.compassConfig.data.enabled) return;
        if(!(player.getMainHandStack().isOf(Items.COMPASS) || player.getOffHandStack().isOf(Items.COMPASS))) return;

        if(Configs.compassConfig.data.rightClick && !Helper.of(player).getData().rightClick) {
            return;
        }

        float degreee = MathHelper.wrapDegrees(player.getHeadYaw());
        String name = "";
        if (degreee > -22.5 && degreee <= 22.5)
            name = "SOUTH";
        else if (degreee > 22.5 && degreee <= 67.5)
            name = "SOUTH WEST";
        else if (degreee > 67.5 && degreee <= 112.5)
            name = "WEST";
        else if (degreee > 112.5 && degreee <= 157.5)
            name = "NORTH WEST";
        else if (degreee > 157.5 || degreee <= -157.5)
            name = "NORTH";
        else if (degreee > -157.5 && degreee <= -112.5)
            name = "NORTH EAST";
        else if (degreee > -112.5 && degreee <= -67.5)
            name = "EAST";
        else if (degreee > -67.5 && degreee <= -22.5)
            name = "SOUTH EAST";
        int[] pos = {player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ()};
        player.sendMessage(Text.of(String.format("X:%d Y:%d Z:%d %s", pos[0], pos[1], pos[2], name)), true);
    }

}
