package tools.redstone.redstonetools.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import java.util.Objects;

/**
 * Utilities for block state (de)serialization into NBT tags.
 */
public final class BlockStateNbtUtil {

    private BlockStateNbtUtil() {
    }

    /**
     * Serializes the given block state into an NBT compound.
     *
     * @param state The state.
     * @return The NBT tag or null if the block state is null.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static NbtCompound toNBT(BlockState state) {
        if (state == null) {
            return null;
        }

        NbtCompound root = new NbtCompound();
        root.putString("Id", Registries.BLOCK.getId(state.getBlock()).toString());

        // serialize properties
        if (!state.getProperties().isEmpty()) {
            NbtList properties = new NbtList();
            for (Property property : state.getProperties()) {
                // todo: maybe compress this? to optimize for memory
                NbtCompound propertyTag = new NbtCompound();
                propertyTag.putString("K", property.getName());
                propertyTag.putString("V", property.name(state.get(property)));
                properties.add(propertyTag);
            }

            root.put("Properties", properties);
        }

        return root;
    }

    /**
     * Attempts to deserialize a block state from the given NBT tag.
     *
     * @param compound The NBT tag.
     * @return The block state or null if the tag is null/empty.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static BlockState fromNBT(NbtCompound compound) {
        if (compound == null || compound.isEmpty()) {
            return null;
        }

        // find the block
        // we use new Identifier(...) here to allow it to throw exceptions
        // instead of getting a cryptic NPE
        Identifier identifier = new Identifier(compound.getString("Id"));
        Block block = Registries.BLOCK.get(identifier);

        // deserialize properties
        BlockState state = block.getDefaultState();
        NbtList propertiesTag = compound.getList("Properties", NbtElement.COMPOUND_TYPE);
        if (propertiesTag != null) {
            for (NbtElement element : propertiesTag) {
                // this cast is checked, as we require the COMPOUND type
                // when getting the list
                NbtCompound propertyTag = (NbtCompound) element;

                Property property = block.getStateManager().getProperty(propertyTag.getString("K"));
                if (property == null)
                    continue;

                state = state.with(property, (Comparable) property.parse(propertyTag.getString("V")).get());
            }
        }

        return state;
    }

    /**
     * Attempts to deserialize a block state from the given NBT tag,
     * or returns the given default if the tag is null/empty.
     *
     * @param compound The NBT tag.
     * @param def      The default.
     * @return The block state or {@code def} if the tag is null/empty.
     */
    public static BlockState fromNBT(NbtCompound compound, BlockState def) {
        BlockState state = fromNBT(compound);
        return state == null ? def : state;
    }

    /**
     * The key for the exact placement state in an items NBT.
     */
    public static final String EXACT_STATE_KEY = "ExactBlockState";

    /**
     * Modifies the given item stack to add the exact block state
     * placement data. The item stack should place the exact given
     * block state when attempted.
     *
     * @param stack The input stack.
     * @param state The block state to assign.
     */
    public static void putPlacement(ItemStack stack, BlockState state) {
        Objects.requireNonNull(stack);
        stack.getOrCreateNbt().put(EXACT_STATE_KEY, toNBT(state));
    }

    /**
     * Creates an item stack which should place the exact given
     * block state when attempted.
     *
     * The created item stack has a count of 1 by default.
     *
     * @param state The placement state.
     * @return The item stack.
     */
    public static ItemStack createPlacementStack(BlockState state) {
        ItemStack stack = new ItemStack(state.getBlock());
        putPlacement(stack, state);
        return stack;
    }

    /**
     * Tries to get the exact placement state for the given
     * item stack. If it is unable to determine the exact
     * block state it will return null.
     *
     * @param stack The item stack.
     * @return The block state or null.
     */
    public static BlockState getPlacementStateOrNull(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(EXACT_STATE_KEY)) {
            return null;
        }

        return fromNBT(nbt.getCompound(EXACT_STATE_KEY));
    }

    /**
     * Tries to get the exact placement state for the given
     * item stack. If it is unable to determine the exact
     * block state it will return the blocks default state
     * if the item is a {@link BlockItem}, or null otherwise.
     *
     * @param stack The item stack.
     * @return The block state or null.
     */
    public static BlockState getPlacementState(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(EXACT_STATE_KEY)) {
            if (stack.getItem() instanceof BlockItem blockItem)
                return blockItem.getBlock().getDefaultState();
            return null;
        }

        BlockState def = stack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock().getDefaultState() : null;
        return fromNBT(nbt.getCompound(EXACT_STATE_KEY), def);
    }

}
