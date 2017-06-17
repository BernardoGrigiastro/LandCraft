package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemLandiaIngot extends Item {
	public ItemLandiaIngot() {
		setHasSubtypes(true);
		setUnlocalizedName("landia_ingot").setRegistryName("landia_ingot");
		setCreativeTab(LandCraftContent.creativeTab);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + LandiaOreType.values()[stack.getMetadata()];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (int i=0; i<LandiaOreType.values().length; ++i) {
			subItems.add(new ItemStack(this, 1, i));
		}
	}
}
