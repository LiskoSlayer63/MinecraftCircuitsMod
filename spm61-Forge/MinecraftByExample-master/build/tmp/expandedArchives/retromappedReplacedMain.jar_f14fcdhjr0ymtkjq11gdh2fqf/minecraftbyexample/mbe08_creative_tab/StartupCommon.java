package minecraftbyexample.mbe08_creative_tab;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHardenedClay;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * The Startup classes for this example are called during startup, in the following order:
 *  preInitCommon
 *  preInitClientOnly
 *  initCommon
 *  initClientOnly
 *  postInitCommon
 *  postInitClientOnly
 *  See MinecraftByExample class for more information
 */
public class StartupCommon
{
  public static CreativeTabs customTab;               // will hold our first custom creative tab
  public static AllMbeItemsTab allMbeItemsTab;        // will hold our second custom creative tab displaying all MBE items

  public static Block testBlock;
  public static ItemBlock testItemBlock;  // the itemBlock corresponding to testBlock
  public static Item testItem;

  public static void preInitCommon()
  {

//   Because the CreativeTabs class only has one abstract method, we can easily use an anonymous class.
//   For more information about anonymous classes, see this link:
//   http://docs.oracle.com/javase/tutorial/java/javaOO/anonymousclasses.html
//   To set the actual name of the tab, you have to go into your .lang file and add the line
//    itemGroup.mbe08_creative_tab=Gold Nugget Tab
//   where gold_nugget_tab is the unlocalized name of your creative tab, and Gold Nugget Tab
//   is the actual name of your tab
   /*
   * Note that there is no need to explicitly register the creative tabs themselves. When
   * you make a new CreativeTabs object, it gets added automatically in the constructor of
   * the CreativeTabs class.
   */
    customTab = new CreativeTabs("mbe08_creative_tab") {
      @Override
      @SideOnly(Side.CLIENT)
      public Item func_78016_d() {
        return Items.field_151074_bl;
      }
    };

//  The lines below create a test block and item instance that are going to be added to the creative tabs.
//  An item can be listed on multiple tabs by overriding Item.getCreativeTabs()
//  A block can only be listed on one tab, unless you give it a custom ItemBlock which overrides .getCreativeTabs()
    testBlock = new BlockHardenedClay().func_149663_c("mbe08_creative_tab_block_unlocalised_name").func_149647_a(customTab);
    testBlock.setRegistryName("mbe08_creative_tab_block_registry_name");
    GameRegistry.register(testBlock);
    // register the itemblock corresponding to the block
    testItemBlock = new ItemBlock(testBlock);
    testItemBlock.setRegistryName(testBlock.getRegistryName());
    GameRegistry.register(testItemBlock);

    // add an item (an item without a corresponding block)
    testItem = new ItemSword(Item.ToolMaterial.GOLD).func_77655_b("mbe08_creative_tab_item_unlocalised_name").func_77637_a(customTab);
    testItem.setRegistryName("mbe08_creative_tab_item_registry_name");
    GameRegistry.register(testItem);

    allMbeItemsTab = new AllMbeItemsTab("mbe08_creative_tab_all_MBE");
  }

  public static void initCommon()
  {
  }

  public static void postInitCommon()
  {
  }
}
