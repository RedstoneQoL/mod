package tools.redstone.redstonetools.macros.actions;

import net.minecraft.client.MinecraftClient;
import net.royawesome.jlibnoise.module.combiner.Min;
import tools.redstone.redstonetools.utils.CommandSourceUtils;

public class CommandAction extends Action {
    public String command;

    public CommandAction(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        var player = MinecraftClient.getInstance().player;
        assert player != null;

        player.sendChatMessage(command.startsWith("/") ? command : "/" + command);
    }

    @Override
    public void runSilent(){
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isIntegratedServerRunning()){
            run();
            return;
        }
        var player = MinecraftClient.getInstance().player;
        assert player != null;

        if (command.startsWith("/")){
            command = command.replace("/", "");
        }
        player.sendChatMessage("/" + "silent " + command);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CommandAction commandAction) {
            return command.equals(commandAction.command);
        }

        return super.equals(obj);
    }

}
