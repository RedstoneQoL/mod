package tools.redstone.redstonetools.features.commands;


import com.google.auto.service.AutoService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.ItemUtils;

import javax.xml.crypto.Data;

@AutoService(AbstractFeature.class)
@Feature(command = "itembind", description = "Allows you to bind command to a specific item", name = "Item Bind")
public class ItemBindFeature extends CommandFeature{
    public static boolean waitingForCommand = false;
    private static ServerPlayerEntity player;


    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {

        player = source.getPlayerOrThrow();
        waitingForCommand = true;

        return Feedback.success("Please run any command and hold the item you want the command be bound to");
    }

    public static Feedback addCommand(String command) {
        if (!waitingForCommand || MinecraftClient.getInstance().getServer() == null) return null;

        if (player == null || MinecraftClient.getInstance().getServer().getPlayerManager().getPlayer(player.getUuid()) != player) {
            waitingForCommand = false;
            return null;
        }

        ItemStack mainHandStack = player.getMainHandStack();
        if (mainHandStack == null || mainHandStack.getItem() == Items.AIR) {
            return Feedback.error("You need to be holding an item!");
        }

        mainHandStack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
            currentNbt.put("command", NbtString.of(command));
        }));
        ItemUtils.addExtraNBTText(mainHandStack,"Command");

        waitingForCommand = false;

        return Feedback.success("Successfully bound command: '{}' to this item ({})!", command, mainHandStack.getItem());
    }
}
