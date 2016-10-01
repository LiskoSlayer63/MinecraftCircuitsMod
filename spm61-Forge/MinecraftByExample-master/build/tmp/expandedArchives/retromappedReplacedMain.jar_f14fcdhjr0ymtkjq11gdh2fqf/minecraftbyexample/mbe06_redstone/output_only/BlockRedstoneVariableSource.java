package minecraftbyexample.mbe06_redstone.output_only;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * BlockRedstoneVariableSource()
 * This block is a provider of redstone power, similar to a torch, except that the amount of power can be adjusted.
 * Right-clicking on the block will cycle through the five power settings (0=off, 4, 8, 12, 15=full).
 *
 * The block uses a property to store the currently selected power level; for more information about creating blocks with
 *   properties, see MBE03_BLOCK_VARIANTS, including an example of how to make a block that can face in different
 *   directions depending on how you place it.
 */
public class BlockRedstoneVariableSource extends Block
{
  public BlockRedstoneVariableSource()
  {
    super(Material.field_151576_e);
    this.func_149647_a(CreativeTabs.field_78030_b);   // the block will appear on the Blocks tab in creative
  }

  //-------------------- methods related to redstone

  /**
   * This block can provide power
   * @return
   */
  @Override
  public boolean func_149744_f(IBlockState iBlockState)
  {
    return true;
  }

  /** How much weak power does this block provide to the adjacent block?
   * See http://greyminecraftcoder.blogspot.com.au/2015/11/redstone.html for more information
   * @param worldIn
   * @param pos the position of this block
   * @param state the blockstate of this block
   * @param side the side of the block - eg EAST means that this is to the EAST of the adjacent block.
   * @return The power provided [0 - 15]
   */
  @Override
  public int func_180656_a(IBlockState state, IBlockAccess worldIn, BlockPos pos,  EnumFacing side)
  {
    Integer powerIndex = (Integer)state.func_177229_b(POWER_INDEX);

    if (powerIndex < 0) {
      powerIndex = 0;
    } else if (powerIndex > MAXIMUM_POWER_INDEX) {
      powerIndex = MAXIMUM_POWER_INDEX;
    }
    return POWER_VALUES[powerIndex];
  }

  // The variable source block does not provide strong power.  See BlockButton for a example of a block which does.
  @Override
  public int func_176211_b(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
  {
    return 0;
  }

  //--------- methods associated with storing the currently-selected power

  // one property for this block - the power level it is providing, index = 0 - 4 which maps to [0, 4, 8, 12, 15]

  private static final int POWER_VALUES [] = {0, 4, 8, 12, 15};
  private static final int MAXIMUM_POWER_INDEX = POWER_VALUES.length - 1;
  public static final PropertyInteger POWER_INDEX = PropertyInteger.func_177719_a("power_index", 0, MAXIMUM_POWER_INDEX);

  // getStateFromMeta, getMetaFromState are used to interconvert between the block's property values and
  //   the stored metadata (which must be an integer in the range 0 - 15 inclusive)
  @Override
  public IBlockState func_176203_a(int meta)
  {
    return this.func_176223_P().func_177226_a(POWER_INDEX, Integer.valueOf(meta));
  }

  @Override
  public int func_176201_c(IBlockState state)
  {
    return (Integer)state.func_177229_b(POWER_INDEX);
  }

  // this method isn't required if your properties only depend on the stored metadata.
  // it is required if:
  // 1) you are making a multiblock which stores information in other blocks eg BlockBed, BlockDoor
  // 2) your block's state depends on other neighbours (eg BlockFence)
  @Override
  public IBlockState func_176221_a(IBlockState state, IBlockAccess worldIn, BlockPos pos)
  {
    return state;
  }

  // necessary to define which properties your blocks use
  // will also affect the variants listed in the blockstates model file
  @Override
  protected BlockStateContainer func_180661_e()
  {
    return new BlockStateContainer(this, new IProperty[] {POWER_INDEX});
  }

  // Every time the player right-clicks, cycle through to the next power setting.
  // Need to trigger an update and notify all neighbours to make sure the new power setting takes effect.
  @Override
  public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                                  @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (!playerIn.field_71075_bZ.field_75099_e) {
      return false;
    } else {

      final int MARK_BLOCKS_FOR_UPDATE_FLAG = 2;
      final int NOTIFY_NEIGHBOURS_FLAG = 1;
      worldIn.func_180501_a(pos, state.func_177231_a(POWER_INDEX),
                            MARK_BLOCKS_FOR_UPDATE_FLAG | NOTIFY_NEIGHBOURS_FLAG);
      return true;
    }
  }

  // When the block is broken, you may need to notify neighbours.
  @Override
  public void func_180663_b(World worldIn, BlockPos pos, IBlockState state)
  {
    worldIn.func_175685_c(pos, this);
    super.func_180663_b(worldIn, pos, state);
  }

  //----- methods related to the block's appearance (see MBE01_BLOCK_SIMPLE and MBE02_BLOCK_PARTIAL)

  // the block will render in the SOLID layer.  See http://greyminecraftcoder.blogspot.co.at/2014/12/block-rendering-18.html for more information.
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer func_180664_k()
  {
    return BlockRenderLayer.SOLID;
  }

  // used by the renderer to control lighting and visibility of other blocks.
  // set to false because this block doesn't occupy the entire 1x1x1 space
  @Override
  public boolean func_149662_c(IBlockState iBlockState) {
    return false;
  }

  // used by the renderer to control lighting and visibility of other blocks, also by
  // (eg) wall or fence to control whether the fence joins itself to this block
  // set to false because this block doesn't occupy the entire 1x1x1 space
  @Override
  public boolean func_149686_d(IBlockState iBlockState) {
    return false;
  }

  // render using a BakedModel
  // not strictly required because the default (super method) is MODEL.
  @Override
  public EnumBlockRenderType func_149645_b(IBlockState iBlockState) {
    return EnumBlockRenderType.MODEL;
  }

}