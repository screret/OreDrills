package screret.oredrills.block.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;
import screret.oredrills.block.ModBlockEntities;
import screret.oredrills.block.entity.BlockEntityOre;
import screret.oredrills.resources.OreVeinManager;

import java.util.Collections;
import java.util.List;

public class BlockOre extends BaseEntityBlock {

    public BlockOre() {
        super(Properties.of(Material.STONE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        BlockEntityOre ore = ModBlockEntities.ORE.get().create(pPos, pState);
        return ore;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        pLevel.getBlockEntity(pPos, ModBlockEntities.ORE.get()).ifPresent(var -> {
            var.setOreType(OreVeinManager.INSTANCE.getAllVeins().get(new ResourceLocation(pStack.getTag().getCompound("BlockEntityTag").getString("type"))));
        });
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof BlockEntityOre ore) {
            if (!pLevel.isClientSide) {
                ore.onDestroyOre();
            }
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
        var bp = pBuilder.getParameter(LootContextParams.ORIGIN);
        ResourceLocation resourcelocation = this.getNewLootTable(pBuilder.getLevel(), new BlockPos(bp));
        if (resourcelocation == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootContext lootcontext = pBuilder.withParameter(LootContextParams.BLOCK_STATE, pState).create(LootContextParamSets.BLOCK);
            ServerLevel serverlevel = lootcontext.getLevel();
            LootTable loottable = serverlevel.getServer().getLootTables().get(resourcelocation);
            return loottable.getRandomItems(lootcontext);
        }
    }

    public final ResourceLocation getNewLootTable(Level level, BlockPos pos) {
        var be = level.getBlockEntity(pos, ModBlockEntities.ORE.get());
        if(be.isPresent()){
            return be.get().getOreType().miningResultLootTable;
        }
        return new ResourceLocation("block/stone");
    }
}
