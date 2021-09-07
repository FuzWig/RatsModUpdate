package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.RatConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRatUpgradeEnergy extends ItemRatUpgrade {

    public ItemRatUpgradeEnergy(String name, int rarity, int textLength) {
        super(name, rarity, textLength);
    }

    public static int getRFTransferRate(Item item) {
        if (item == RatsItemRegistry.RAT_UPGRADE_BASIC_ENERGY) {
            return RatConfig.ratRFTransferBasic;
        }
        if (item == RatsItemRegistry.RAT_UPGRADE_ADVANCED_ENERGY) {
            return RatConfig.ratRFTransferAdvanced;
        }
        if (item == RatsItemRegistry.RAT_UPGRADE_ELITE_ENERGY) {
            return RatConfig.ratRFTransferElite;
        }
        if (item == RatsItemRegistry.RAT_UPGRADE_EXTREME_ENERGY) {
            return RatConfig.ratRFTransferExtreme;
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        int transferRate = getRFTransferRate(stack.getItem());
        tooltip.add(new TranslationTextComponent("item.rats.rat_upgrade_energy.desc0").func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.rats.rat_upgrade_energy.desc1").func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.rats.rat_upgrade_energy.transfer_rate", transferRate).func_240699_a_(TextFormatting.GRAY));
    }
}
