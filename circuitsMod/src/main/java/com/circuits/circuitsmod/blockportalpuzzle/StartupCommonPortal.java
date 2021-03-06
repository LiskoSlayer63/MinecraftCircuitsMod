package com.circuits.circuitsmod.blockportalpuzzle;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * The Startup classes for this example are called during startup, in the following order:
 *  preInitCommon
 *  preInitClientOnly
 *  initCommon
 *  initClientOnly
 *  postInitCommon
 *  postInitClientOnly
 */
public class StartupCommonPortal
{
  public static BlockPortalPuzzle blockPortalPuzzle;  // this holds the unique instance of your block
  public static ItemBlock itemblockPortalPuzzle;  // this holds the unique instance of the ItemBlock corresponding to your block

  public static void preInitCommon()
  {
    blockPortalPuzzle = (BlockPortalPuzzle)(new BlockPortalPuzzle().setUnlocalizedName("blockPortalPuzzle"));
    blockPortalPuzzle.setRegistryName("blockPortalPuzzle");
    GameRegistry.register(blockPortalPuzzle);
    
    itemblockPortalPuzzle = new ItemBlock(blockPortalPuzzle);
    itemblockPortalPuzzle.setRegistryName(blockPortalPuzzle.getRegistryName());
    GameRegistry.register(itemblockPortalPuzzle);
  }

  public static void initCommon()
  {
  }

  public static void postInitCommon()
  {
  }

}
