package tools.redstone.redstonetools.mixin.macros;


import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public abstract class DisableCommandOutputMixin {

    @Shadow public abstract int execute(ServerCommandSource commandSource, String command);

    @Inject(method = "execute", at=@At("HEAD"), cancellable = true)
    public void execute(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> cir){
        if (command.contains("/silent ")) {
            command = command.replace("/silent ", "");
            System.out.println("silent ");
            System.out.println(command);
            command = command;
            commandSource = commandSource.withSilent();

            execute(commandSource, command);
            cir.cancel();
        }
    }


}
