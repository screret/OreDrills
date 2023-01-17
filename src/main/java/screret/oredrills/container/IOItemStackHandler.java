package screret.oredrills.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class IOItemStackHandler extends ItemStackHandler {

    private final boolean isInput;

    public IOItemStackHandler(int size, boolean isInput) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.isInput = isInput;
    }


    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if(isInput) return super.insertItem(slot, stack, simulate);
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if(isInput) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }
}
