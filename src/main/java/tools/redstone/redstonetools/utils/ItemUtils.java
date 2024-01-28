package tools.redstone.redstonetools.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.RaycastContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public class ItemUtils {
  
    private ItemUtils(){
    }

    public static void addExtraNBTText(ItemStack itemStack, String text) {
        NbtCompound displayNbt = itemStack.getSubNbt("display");
        NbtList loreList = new NbtList();

        if (displayNbt == null) {
            displayNbt = new NbtCompound();
        } else {
            loreList = (NbtList) displayNbt.get("Lore");
        }
        String lore = "\"(+"+text +")\"";

        if (loreList != null && !loreList.contains(NbtString.of(lore))) loreList.add(NbtString.of(lore));
        displayNbt.put("Lore", loreList);
        itemStack.setSubNbt("display", displayNbt);
    }

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getItem() == Items.AIR || itemStack.getCount() == 0;
    }

    public static ItemStack getMainItem(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        return isEmpty(stack) ? player.getOffHandStack() : stack;
    }

    public static BlockState getPlacementState(ClientPlayerEntity player, ItemStack stack, float reach) {
        Item type = stack.getItem();
        if (!(type instanceof BlockItem blockItem))
            return null;
        return blockItem.getBlock().getPlacementState(new ItemPlacementContext(
                player,
                player.getActiveHand(),
                stack,
                RaycastUtils.rayCastFromEye(player, reach)
        ));
    }

    public static BlockState getUseState(ClientPlayerEntity player, ItemStack stack, float reach) {
        if (stack == null || stack.getItem() == Items.AIR)
            return null;
        BlockState state = getPlacementState(player, stack, reach);
        if (state == null)
            state = Blocks.BEDROCK.getDefaultState();
        return state;
    }
  
}
