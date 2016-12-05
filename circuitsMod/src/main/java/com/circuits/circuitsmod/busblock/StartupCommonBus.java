package com.circuits.circuitsmod.busblock;

import com.circuits.circuitsmod.common.ItemBlockMeta;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class StartupCommonBus
{
  public static BusBlock busBlock; 
  public static ItemBlock itembusBlock;

  public static void preInitCommon()
  {
    busBlock = (BusBlock)(new BusBlock().setUnlocalizedName("busblock"));
    busBlock.setRegistryName("busblock");
    GameRegistry.register(busBlock);
    
    itembusBlock = new ItemBlockMeta(busBlock);
    itembusBlock.setRegistryName(busBlock.getRegistryName());
    GameRegistry.register(itembusBlock);
  }

  public static void initCommon()
  {
  }

  public static void postInitCommon()
  {
  }

}
