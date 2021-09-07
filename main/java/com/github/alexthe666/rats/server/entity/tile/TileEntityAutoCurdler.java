package com.github.alexthe666.rats.server.entity.tile;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.blocks.RatsBlockRegistry;
import com.github.alexthe666.rats.server.blocks.RatsFluidRegistry;
import com.github.alexthe666.rats.server.inventory.ContainerAutoCurdler;
import com.github.alexthe666.rats.server.message.MessageAutoCurdlerFluid;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;
import java.util.Optional;

public class TileEntityAutoCurdler extends LockableTileEntity implements ITickableTileEntity, ISidedInventory, INamedContainerProvider {
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{1};
    public int ticksExisted;
    public FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 5);
    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN);
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
    private NonNullList<ItemStack> curdlerStacks = NonNullList.withSize(2, ItemStack.EMPTY);
    public int cookTime;
    public int totalCookTime;
    private int prevFluid = 0;
    public final IIntArray furnaceData = new IIntArray() {
        public int get(int index) {
            switch(index) {
                case 2:
                    return TileEntityAutoCurdler.this.cookTime;
                case 3:
                    return TileEntityAutoCurdler.this.totalCookTime;
                default:
                    return 0;
            }
        }

        public void set(int index, int value) {
            switch(index) {
                case 2:
                    TileEntityAutoCurdler.this.cookTime = value;
                    break;
                case 3:
                    TileEntityAutoCurdler.this.totalCookTime = value;
            }

        }

        public int size() {
            return 4;
        }
    };
    public TileEntityAutoCurdler() {
        super(RatsTileEntityRegistry.AUTO_CURDLER);
    }

    public static boolean isMilk(ItemStack stack) {
        if (stack.getItem() == Items.MILK_BUCKET) {
            return true;
        }
        Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(stack);
        return fluidStack.orElse(null) != null && (fluidStack.orElse(null).getDisplayName().func_230531_f_().toString().contains("milk") || fluidStack.orElse(null).getTranslationKey().contains("Milk"));
    }

    public int getSizeInventory() {
        return this.curdlerStacks.size();
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.curdlerStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getStackInSlot(int index) {
        return this.curdlerStacks.get(index);
    }

    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.curdlerStacks, index, count);
    }

    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.curdlerStacks, index);
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = this.curdlerStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.curdlerStacks.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 0 && !flag) {
            this.markDirty();
        }
    }

    public void func_230337_a_(BlockState state,  CompoundNBT compound) {
        super.func_230337_a_(state, compound);
        tank.readFromNBT(compound);
        this.curdlerStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.curdlerStacks);
        this.cookTime = compound.getInt("CookTime");
        this.totalCookTime = compound.getInt("CookTimeTotal");
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        tank.writeToNBT(compound);
        compound.putInt("CookTime", (short) this.cookTime);
        compound.putInt("CookTimeTotal", (short) this.totalCookTime);
        ItemStackHelper.saveAllItems(compound, this.curdlerStacks);
        return compound;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    public boolean isBurning() {
        return this.tank.getFluidAmount() >= 1000 && isMilkFluid(tank.getFluid());
    }

    public void tick() {
        if (!world.isRemote) {
            if (prevFluid != tank.getFluidAmount()) {
                RatsMod.sendMSGToAll(new MessageAutoCurdlerFluid(this.getPos().toLong(), tank.getFluid()));
                prevFluid = tank.getFluidAmount();
            }

        }
        this.totalCookTime = RatConfig.milkCauldronTime;
        ticksExisted++;
        boolean flag = this.isBurning();
        boolean flag1 = false;

        if (this.isBurning()) {
            if (this.canSmelt()) {
                ++this.cookTime;

                if (this.cookTime == this.totalCookTime) {
                    this.cookTime = 0;
                    this.totalCookTime = RatConfig.milkCauldronTime;
                    this.smeltItem();
                    flag1 = true;
                }
            } else {
                this.cookTime = 0;
            }
        } else if (!this.isBurning() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
        }
        if (curdlerStacks.get(0).getItem() == Items.MILK_BUCKET) {
            FluidBucketWrapper milkWrapper = new FluidBucketWrapper(new ItemStack(Items.MILK_BUCKET));
            FluidStack milkFluid = new FluidStack(milkWrapper.getFluid(), 1000);
            if(milkFluid.isEmpty()){
                milkFluid = new FluidStack(RatsFluidRegistry.MILK_FLUID, 1000);
            }
            if (tank.fill(milkFluid.copy(), IFluidHandler.FluidAction.SIMULATE) != 0) {
                tank.fill(milkFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                curdlerStacks.set(0, new ItemStack(Items.BUCKET));
            }

        } else if (isMilk(curdlerStacks.get(0))) {
            Optional<FluidStack> fluidStackOptional = FluidUtil.getFluidContained(curdlerStacks.get(0));
            fluidStackOptional.ifPresent(fluidStack -> {
                LazyOptional<IFluidHandlerItem> fluidHandlerOptional = FluidUtil.getFluidHandler(curdlerStacks.get(0));
                fluidHandlerOptional.ifPresent(fluidHandler -> {
                    if (fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE).getAmount() > 0) {
                        if (tank.fill(fluidStack.copy(), IFluidHandler.FluidAction.SIMULATE) != 0) {
                            int amount = tank.fill(fluidStack.copy(), IFluidHandler.FluidAction.EXECUTE);
                            fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                            //support container changing tanks
                            ItemStack container = fluidHandler.getContainer();
                            if (curdlerStacks.get(0) != container) {
                                curdlerStacks.set(0, container);
                            }
                        }
                    }
                });
            });
        }
    }

    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack toAdd = new ItemStack(RatsBlockRegistry.BLOCK_OF_CHEESE);
            if (this.getStackInSlot(1).isItemEqual(toAdd) && this.getStackInSlot(1).getCount() < 64) {
                this.getStackInSlot(1).grow(1);
            } else if (this.getStackInSlot(1).isEmpty()) {
                this.setInventorySlotContents(1, toAdd.copy());
            }
            tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public boolean canSmelt() {
        if (tank.getFluidAmount() < 1000) {
            return false;
        } else {
            ItemStack itemstack = new ItemStack(RatsBlockRegistry.BLOCK_OF_CHEESE);

            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = this.curdlerStacks.get(1);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.isItemEqual(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize())  // Forge fix: make furnace respect stack sizes in furnace recipes
                {
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        }
    }

    public void openInventory(PlayerEntity player) {
        if (!world.isRemote) {
            RatsMod.sendMSGToAll(new MessageAutoCurdlerFluid(this.getPos().toLong(), tank.getFluid()));
        }
    }

    public void closeInventory(PlayerEntity player) {
        if (!world.isRemote) {
            RatsMod.sendMSGToAll(new MessageAutoCurdlerFluid(this.getPos().toLong(), tank.getFluid()));
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 1) {
            return false;
        } else {
            return isMilk(stack);
        }
    }

    private boolean isMilkFluid(FluidStack fluid) {
        return fluid.getTranslationKey().contains("milk") || fluid.getTranslationKey().contains("Milk");
    }

    public int getField(int id) {
        switch (id) {
            case 0:
                return this.tank.getFluidAmount();
            case 1:
                return this.cookTime;
            case 2:
                return this.totalCookTime;
            default:
                return 0;
        }
    }

    public void setField(int id, int value) {
        switch (id) {
            case 0:
                if (tank.getFluid() != null) {
                    this.tank.getFluid().setAmount(value);
                }
                break;
            case 1:
                this.cookTime = value;
                break;
            case 2:
                this.totalCookTime = value;
        }
    }

    public int getFieldCount() {
        return 3;
    }

    public void clear() {
        this.curdlerStacks.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        } else {
            return SLOTS_TOP;
        }
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        if (direction == Direction.DOWN && index == 1) {
            Item item = stack.getItem();
            return true;
        }
        return false;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.auto_curdler");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return null;
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
             if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[0].cast();
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(capability, facing);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerAutoCurdler(id, this, playerInventory, furnaceData);
    }
}
