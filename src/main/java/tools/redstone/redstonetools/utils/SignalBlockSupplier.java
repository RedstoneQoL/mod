package tools.redstone.redstonetools.utils;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

@FunctionalInterface
public interface SignalBlockSupplier {

    NbtCompound createNbt(int signalStrength);

    default ItemStack getItemStack(Block block, int signalStrength) {
        ItemStack item = new ItemStack(block);
        setCompoundNbt(item, this.createNbt(signalStrength));
        setItemName(item, signalStrength);
        return item;
    }

    static SignalBlockSupplier container(int slots) {
        return signalStrength -> {
            if (isInvalidSignalStrength(signalStrength, 1779))
                throw new IllegalArgumentException("Container signal must be 0-1779");

            NbtCompound tags = new NbtCompound();
            NbtList itemsTag = new NbtList();

            Item item = getBestItem(signalStrength, slots);
            int stackSize = getStackSize(signalStrength, item);
            int itemsNeeded = Math.max(0, signalStrength == 1
                    ? 1
                    : (int) Math.ceil(slots * (signalStrength - 1) / 14D * item.getMaxCount()));
            String itemId = Registries.ITEM.getId(item).toString();

            // Check that the calculated number of items is correct.
            // This is to prevent problems with items that have a maximum stack size of 1
            // but stackSize > 1.
            // TODO: This can be improved by removing an item and adding stackable items up
            // to the desired signal strength.
            // Even with the improvement, this will still fail for inventories with no
            // available slots.
            if (calculateComparatorOutput(itemsNeeded, slots, item.getMaxCount()) != signalStrength)
                throw new IllegalStateException("This signal strength cannot be achieved with the selected container");

            for (int slot = 0, count = itemsNeeded; count > 0; slot++, count -= stackSize) {
                NbtCompound slotTag = new NbtCompound();
                slotTag.putByte("Slot", (byte) slot);
                slotTag.putString("id", itemId);
                slotTag.putByte("Count", (byte) Math.min(stackSize, count));
                itemsTag.add(slotTag);
            }

            NbtCompound tag = new NbtCompound();
            tag.put("Items", itemsTag);
            tags.put("BlockEntityTag", tag);

            return tags;
        };
    }

    static SignalBlockSupplier composter() {
        return signalStrength -> {
            if (signalStrength == 7 || isInvalidSignalStrength(signalStrength, 8))
                throw new IllegalArgumentException("Composter signal must be 0-6 or 8");

            NbtCompound tags = new NbtCompound();
            NbtCompound tag = new NbtCompound();
            tag.putInt("level", signalStrength);
            tags.put("BlockStateTag", tag);
            return tags;
        };
    }

    static SignalBlockSupplier commandBlock() {
        return signalStrength -> {
            if (isInvalidSignalStrength(signalStrength, Integer.MAX_VALUE))
                throw new IllegalArgumentException("Command block signal must be positive");

            NbtCompound tags = new NbtCompound();
            NbtCompound tag = new NbtCompound();
            tag.putInt("SuccessCount", signalStrength);
            tags.put("BlockEntityTag", tag);
            return tags;
        };
    }

    private static boolean isInvalidSignalStrength(int signalStrength, int maxSignalStrength) {
        return signalStrength < 0 || signalStrength > maxSignalStrength;
    }

    private static int calculateComparatorOutput(int items, int slots, int item$getMaxCount) {
        float f = (float) items / (float) item$getMaxCount;
        return MathHelper.floor((f /= (float) slots) * 14.0f) + (items > 0 ? 1 : 0);
    }

    private static Item getBestItem(int signalStrength, int slots) {
        if (signalStrength > 15)
            return Items.WHITE_SHULKER_BOX;
        else if (slots >= 15)
            return Items.WOODEN_SHOVEL;
        else
            return Items.STICK;
    }

    private static int getStackSize(int signalStrength, Item item) {
        if (signalStrength > 897)
            return 127;
        else if (signalStrength > 15)
            return 64;
        else
            return item.getMaxCount();
    }

    private static void setCompoundNbt(ItemStack item, NbtCompound nbt) {
        nbt.putBoolean("HideFlags", true);
        item.setNbt(nbt);
        item.addEnchantment(Enchantment.byRawId(0), 0);
    }

    private static void setItemName(ItemStack item, int signalStrength) {
        MutableText text = Text.literal(String.valueOf(signalStrength));
        text.setStyle(text.getStyle().withColor(Formatting.RED));
        item.setCustomName(text);
    }

}
