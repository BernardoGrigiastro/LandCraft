package landmaster.landcraft.block;

import java.lang.reflect.*;
import java.util.*;

import javax.annotation.*;

import com.google.common.base.*;

import landmaster.landcraft.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import mcjty.lib.compat.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class BlockPlayerMime extends CompatBlock {
	static {
		MinecraftForge.EVENT_BUS.register(BlockPlayerMime.class);
	}
	
	public BlockPlayerMime() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName("player_mime").setRegistryName("player_mime");
        this.setCreativeTab(LandCraft.creativeTab);
	}
	@Override
	protected boolean clOnBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
            float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
            return true;
        }
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TEPlayerMime)) {
			return false;
		}
		player.openGui(LandCraft.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TEPlayerMime createTileEntity(World world, IBlockState state) {
		return new TEPlayerMime();
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void death(LivingDeathEvent event) {
		if (event.getEntity().getEntityWorld().isRemote) return;
		
		List<TEPlayerMime> tiles = Utils.getTileEntitiesWithinAABB(
				event.getEntity().getEntityWorld(), TEPlayerMime.class,
				new AxisAlignedBB(event.getEntity().getPositionVector().subtract(2, 2, 2),
						event.getEntity().getPositionVector().addVector(2, 2, 2)));
		if (recentlyHit(event.getEntityLiving()) < 22
				|| attackingPlayer(event.getEntityLiving()) == null) {
			for (TEPlayerMime te: tiles) {
				if (te.isEnabled(te)
						&& te.extractEnergy(null, 1000, true) >= 1000
						&& !event.getEntity().getEntityWorld().isBlockPowered(te.getPos())) {
					te.extractEnergy(null, 1000, false);
					recentlyHit(event.getEntityLiving(), 100);
					FakePlayer fake = FakePlayerFactory.getMinecraft((WorldServer)event.getEntity().getEntityWorld());
					attackingPlayer(event.getEntityLiving(), fake);
				}
			}
		}
	}
	
	private static final Field recentlyHitF;
	private static final Field attackingPlayerF;
	static {
		try {
			recentlyHitF = EntityLivingBase.class.getDeclaredField(
					"field_70718_bc"/*recentlyHit*/);
			recentlyHitF.setAccessible(true);
			
			attackingPlayerF = EntityLivingBase.class.getDeclaredField(
					"field_70717_bb"/*attackingPlayer*/);
			attackingPlayerF.setAccessible(true);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static void recentlyHit(EntityLivingBase elb, int val) {
		try {
			recentlyHitF.setInt(elb, val);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static int recentlyHit(EntityLivingBase elb) {
		try {
			return recentlyHitF.getInt(elb);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static EntityPlayer attackingPlayer(EntityLivingBase elb) {
		try {
			return (EntityPlayer)attackingPlayerF.get(elb);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static void attackingPlayer(EntityLivingBase elb, EntityPlayer ep) {
		try {
			attackingPlayerF.set(elb, ep);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
}
