package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHat extends ArmorItem {
    private int modelIndex;
    private String name;

    public ItemHat(String name, int modelIndex) {
        super(RatsItemRegistry.HAT_ARMOR_MATERIAL, EquipmentSlotType.HEAD, new Item.Properties().group(RatsMod.TAB));
        this.setRegistryName(RatsMod.MODID, name);
        this.modelIndex = modelIndex;
        this.name = name;
    }

    public ItemHat(String name, int modelIndex, ItemGroup group) {
        super(RatsItemRegistry.HAT_ARMOR_MATERIAL, EquipmentSlotType.HEAD, new Item.Properties().group(group));
        this.setRegistryName(RatsMod.MODID, name);
        this.modelIndex = modelIndex;
        this.name = name;
    }

    public ItemHat(IArmorMaterial mat, String name, int modelIndex) {
        super(mat, EquipmentSlotType.HEAD, new Item.Properties().group(RatsMod.TAB));
        this.setRegistryName(RatsMod.MODID, name);
        this.modelIndex = modelIndex;
        this.name = name;
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getItem() == RatsItemRegistry.ARCHEOLOGIST_HAT) {
            tooltip.add(new TranslationTextComponent("item.rats.archeologist_hat.desc").func_240699_a_(TextFormatting.GRAY));
        }
        if (stack.getItem() == RatsItemRegistry.PLAGUE_DOCTOR_MASK) {
            tooltip.add(new TranslationTextComponent("item.rats.plague_doctor_mask.desc").func_240699_a_(TextFormatting.GRAY));
        }
        if (stack.getItem() == RatsItemRegistry.BLACK_DEATH_MASK) {
            tooltip.add(new TranslationTextComponent("item.rats.plague_doctor_mask.desc").func_240699_a_(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("item.rats.black_death_mask.desc").func_240699_a_(TextFormatting.GRAY));
        }
        if (stack.getItem() == RatsItemRegistry.RAT_FEZ) {
            tooltip.add(new TranslationTextComponent("item.rats.rat_fez.desc0").func_240699_a_(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("item.rats.rat_fez.desc1").func_240699_a_(TextFormatting.GRAY));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        return (A) RatsMod.PROXY.getArmorModel(modelIndex);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return "rats:textures/model/" + name + ".png";
    }
}
