package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.ratlantis.EntityVialOfSentience;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemVialOfSentience extends Item {

    public ItemVialOfSentience() {
        super(new Item.Properties().group(RatsMod.getRatlantisTab()));
        this.setRegistryName(RatsMod.MODID, "vial_of_sentience");
    }

    public boolean hasEffect(ItemStack stack) {
        return true;
    }


    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".desc").func_240699_a_(TextFormatting.GRAY));
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        ItemStack itemstack1 = playerIn.isCreative() ? itemstack.copy() : itemstack.split(1);
        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        EntityVialOfSentience entitypotion = new EntityVialOfSentience(worldIn, playerIn);
        Vector3d vector3d = playerIn.getLook(1.0F);
        Vector3f vector3f = new Vector3f(vector3d);
        entitypotion.shoot((double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), 1.0F, 0.5F);
        worldIn.addEntity(entitypotion);
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
    }
}
