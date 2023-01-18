package screret.oredrills.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import screret.oredrills.capability.vein.VeinCapability;

public class ProspectCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("prospect")
                .requires(user -> user.hasPermission(2))
                .then(Commands.argument("position", BlockPosArgument.blockPos())).executes(disp -> execute(disp, BlockPosArgument.getLoadedBlockPos(disp, "position")))
                .executes(ProspectCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player player){
            var veins = player.level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability is null.")).getOreVeins(new ChunkPos(player.blockPosition()));
            player.sendSystemMessage(Component.translatable("msg.prospect").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
            for (var vein : veins.keySet()){
                player.sendSystemMessage(Component.literal("  - " + vein.id).withStyle(ChatFormatting.YELLOW));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int execute(CommandContext<CommandSourceStack> command, BlockPos pos){
        if(command.getSource().getEntity() instanceof Player){
            return execute(command);
        } else {
            var level = command.getSource().getLevel();
            var veins = level.getCapability(VeinCapability.CAPABILITY).orElseThrow(() -> new IllegalStateException("VeinCapability is null.")).getOreVeins(new ChunkPos(pos));
            level.getServer().sendSystemMessage(Component.translatable("msg.prospect").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
            for (var vein : veins.keySet()){
                level.getServer().sendSystemMessage(Component.literal("  - " + vein.id).withStyle(ChatFormatting.YELLOW));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
