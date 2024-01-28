package tools.redstone.redstonetools.mixin.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tools.redstone.redstonetools.features.commands.ItemBindFeature;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.features.feedback.FeedbackSender;

import static tools.redstone.redstonetools.RedstoneToolsClient.INJECTOR;


public abstract class ItemBindMixin {

    @Mixin(ItemStack.class)
    private abstract static class ItemStackMixin {

        @Shadow
        public abstract @Nullable NbtCompound getNbt();

        @Inject(method = "use", at = @At("HEAD"), cancellable = true)
        public void checkCommandNBT(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
            if (tryToExecuteNBTCommand(hand, world)) {
                cir.setReturnValue(TypedActionResult.pass((ItemStack) ((Object) this)));
            }
        }

        @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
        public void checkCommandNBT(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
            if (tryToExecuteNBTCommand(context.getHand(), context.getWorld())) {
                cir.setReturnValue(ActionResult.PASS);
            }
        }

        private boolean tryToExecuteNBTCommand(Hand hand, World world) {
            if (hand == Hand.OFF_HAND || world.isClient) return false;
            NbtCompound nbt = getNbt();
            if (nbt == null || !nbt.contains("command")) return false;
            NbtString command = (NbtString) nbt.get("command");
            MinecraftClient.getInstance().getNetworkHandler().sendChatCommand(command.asString());

            return true;
        }
    }


    @Mixin(ClientPlayNetworkHandler.class)
    private abstract static class NetworkHandlerMixin {

        @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
        public void injectCommand(String message, CallbackInfo ci) {
            if (!ItemBindFeature.waitingForCommand) return;

            Feedback addCommandFeedback = ItemBindFeature.addCommand(message);
            if (addCommandFeedback != null) {
                INJECTOR.getInstance(FeedbackSender.class).sendFeedback(
                        MinecraftClient.getInstance().getServer().getCommandSource(), addCommandFeedback);
                ci.cancel();
            }
        }
    }


}
