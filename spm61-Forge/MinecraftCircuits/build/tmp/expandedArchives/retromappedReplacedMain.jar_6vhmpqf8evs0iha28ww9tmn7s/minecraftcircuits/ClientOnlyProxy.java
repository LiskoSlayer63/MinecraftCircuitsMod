package minecraftcircuits;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * ClientProxy is used to set up the mod and start it running on normal minecraft.  It contains all the code that should run on the
 *   client side only.
 *   For more background information see here http://greyminecraftcoder.blogspot.com/2013/11/how-forge-starts-up-your-code.html
 */
public class ClientOnlyProxy extends CommonProxy
{

  /**
   * Run before anything else. Read your config, create blocks, items, etc, and register them with the GameRegistry
   */
  public void preInit()
  {
    super.preInit();
    minecraftcircuits.frameblock.StartupClientOnly.preInitClientOnly();
    minecraftcircuits.frameblock.StartupClientOnly.preInitClientOnly();
  }

  /**
   * Do your mod setup. Build whatever data structures you care about. Register recipes,
   * send FMLInterModComms messages to other mods.
   */
  public void init()
  {
    super.init();
   minecraftcircuits.frameblock.StartupClientOnly.initClientOnly();
   minecraftcircuits.controlblock.StartupClientOnly.initClientOnly();
  }

  /**
   * Handle interaction with other mods, complete your setup based on this.
   */
  public void postInit()
  {
    super.postInit();
    minecraftcircuits.frameblock.StartupClientOnly.postInitClientOnly();
    minecraftcircuits.controlblock.StartupClientOnly.postInitClientOnly();
  }

  @Override
  public boolean playerIsInCreativeMode(EntityPlayer player) {
    if (player instanceof EntityPlayerMP) {
      EntityPlayerMP entityPlayerMP = (EntityPlayerMP)player;
      return entityPlayerMP.field_71134_c.func_73083_d();
    } else if (player instanceof EntityPlayerSP) {
      return Minecraft.func_71410_x().field_71442_b.func_78758_h();
    }
    return false;
  }

  @Override
  public boolean isDedicatedServer() {return false;}

}