package tools.redstone.redstonetools.utils;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class DirectionUtils {
    public static Direction firstOrdinal(Direction playerFacing) {
        return switch (playerFacing) {
            case EAST_NORTHEAST, NORTH_NORTHEAST -> Direction.NORTHEAST;
            case NORTH_NORTHWEST, WEST_NORTHWEST -> Direction.NORTHWEST;
            case EAST_SOUTHEAST, SOUTH_SOUTHEAST -> Direction.SOUTHEAST;
            case SOUTH_SOUTHWEST, WEST_SOUTHWEST -> Direction.SOUTHWEST;
            default -> playerFacing;
        };
    }

    public static DirectionArgument relativeToAbsolute(DirectionArgument direction, Direction playerFacing) {
        return switch (direction) {
            case ME, FORWARD -> switch (firstOrdinal(playerFacing)) {
                case UP -> DirectionArgument.UP;
                case DOWN -> DirectionArgument.DOWN;
                case NORTH -> DirectionArgument.NORTH;
                case EAST -> DirectionArgument.EAST;
                case SOUTH -> DirectionArgument.SOUTH;
                case WEST -> DirectionArgument.WEST;
                case NORTHEAST -> DirectionArgument.NORTHEAST;
                case NORTHWEST -> DirectionArgument.NORTHWEST;
                case SOUTHWEST -> DirectionArgument.SOUTHWEST;
                case SOUTHEAST -> DirectionArgument.SOUTHEAST;
                default -> null;
            };
            case LEFT -> switch (firstOrdinal(playerFacing)) {
                //case UP, DOWN -> throw new CommandException(Text.of("Can't determine direction"));
                case NORTH -> DirectionArgument.WEST;
                case EAST -> DirectionArgument.NORTH;
                case SOUTH -> DirectionArgument.EAST;
                case WEST -> DirectionArgument.SOUTH;
                case NORTHWEST -> DirectionArgument.SOUTHWEST;
                case NORTHEAST -> DirectionArgument.NORTHWEST;
                case SOUTHEAST -> DirectionArgument.NORTHEAST;
                case SOUTHWEST -> DirectionArgument.SOUTHEAST;
                default -> null;
            };
            case RIGHT -> switch (firstOrdinal(playerFacing)) {
                //case UP, DOWN -> throw new CommandException(Text.of("Can't determine direction"));
                case NORTH -> DirectionArgument.EAST;
                case EAST -> DirectionArgument.SOUTH;
                case SOUTH -> DirectionArgument.WEST;
                case WEST -> DirectionArgument.NORTH;
                case NORTHEAST -> DirectionArgument.SOUTHEAST;
                case NORTHWEST -> DirectionArgument.NORTHEAST;
                case SOUTHWEST -> DirectionArgument.NORTHWEST;
                case SOUTHEAST -> DirectionArgument.SOUTHWEST;
                default -> null;
            };
            case BACK -> switch (firstOrdinal(playerFacing)) {
                case UP -> DirectionArgument.DOWN;
                case DOWN -> DirectionArgument.UP;
                case NORTH -> DirectionArgument.SOUTH;
                case EAST -> DirectionArgument.WEST;
                case SOUTH -> DirectionArgument.NORTH;
                case WEST -> DirectionArgument.EAST;
                case NORTHWEST -> DirectionArgument.SOUTHEAST;
                case NORTHEAST -> DirectionArgument.SOUTHWEST;
                case SOUTHEAST -> DirectionArgument.NORTHWEST;
                case SOUTHWEST -> DirectionArgument.NORTHEAST;
                default -> null;
            };
            default -> direction;
        };
    }

    // big evil match direction function, there might be a better way to do this but
    // i don't know how
    @NotNull
    public static Direction matchDirection(DirectionArgument direction, Direction playerFacing) {
        var absoluteDirection = relativeToAbsolute(direction, playerFacing);
        return switch (absoluteDirection) {
            case NORTH -> Direction.NORTH;
            case EAST -> Direction.EAST;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case NORTHEAST -> Direction.NORTHEAST;
            case NORTHWEST -> Direction.NORTHWEST;
            case SOUTHEAST -> Direction.SOUTHEAST;
            case SOUTHWEST -> Direction.SOUTHWEST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            default -> {
                assert false;
                yield null;
            }
        };
    }

    // so many switch cases
    public static BlockVector3 directionToBlock(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockVector3.at(0, 0, -1);
            case EAST -> BlockVector3.at(1, 0, 0);
            case SOUTH -> BlockVector3.at(0, 0, 1);
            case WEST -> BlockVector3.at(-1, 0, 0);
            case UP -> BlockVector3.at(0, 1, 0);
            case DOWN -> BlockVector3.at(0, -1, 0);
            case NORTHEAST -> BlockVector3.at(1, 0, -1);
            case NORTHWEST -> BlockVector3.at(-1, 0, -1);
            case SOUTHEAST -> BlockVector3.at(1, 0, 1);
            case SOUTHWEST -> BlockVector3.at(-1, 0, 1);
            default -> null;
        };
    }
}
