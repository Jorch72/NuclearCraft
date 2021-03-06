package nc.tile.dummy;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import nc.ModCheck;
import nc.config.NCConfig;
import nc.init.NCBlocks;
import nc.recipe.NCRecipes;
import nc.tile.energyFluid.IBufferable;
import nc.tile.generator.TileFusionCore;
import nc.util.BlockFinder;
import nc.util.BlockPosHelper;
import nc.util.RecipeHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers")
public abstract class TileFusionDummy extends TileDummy<TileFusionCore> implements IBufferable, Environment {
	
	// Type can't be specified here as OC may not be loaded
	Object node;
	
	public static class Side extends TileFusionDummy {
		public Side() {
			super("fusion_dummy_side");
		}
		
		@Override
		public void findMaster() {
			BlockPosHelper helper = new BlockPosHelper(pos);
			for (BlockPos pos : helper.cuboid(-1, -1, -1, 1, 0, 1)) if (findCore(pos)) {
				masterPosition = pos;
				return;
			}
			masterPosition = null;
		}
	}
	
	public static class Top extends TileFusionDummy {
		public Top() {
			super("fusion_dummy_top");
		}
		
		@Override
		public void findMaster() {
			BlockPosHelper helper = new BlockPosHelper(pos);
			for (BlockPos pos : helper.cuboid(-1, -2, -1, 1, -2, 1)) if (findCore(pos)) {
				masterPosition = pos;
				return;
			}
			masterPosition = null;
		}
	}
	
	private BlockFinder finder;
	
	public TileFusionDummy(String name) {
		super(TileFusionCore.class, name, NCConfig.machine_update_rate, RecipeHelper.validFluids(NCRecipes.Type.FUSION).get(0));
	}
	
	@Override
	public void onAdded() {
		finder = new BlockFinder(pos, world);
		super.onAdded();
	}
	
	@Override
	public void update() {
		super.update();
		if (ModCheck.openComputersLoaded()) refreshNode();
		if(!world.isRemote) {
			pushEnergy();
			pushFluid();
		}
		if (findAdjacentComparator() && shouldTileCheck()) markDirty();
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (ModCheck.openComputersLoaded()) removeNode();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		if (ModCheck.openComputersLoaded()) removeNode();
	}
	
	// Redstone Flux

	@Override
	public boolean canReceiveEnergy(EnumFacing side) {
		if (hasMaster()) return !getMaster().isHotEnough();
		return false;
	}
	
	@Override
	public boolean canExtractEnergy(EnumFacing side) {
		if (hasMaster()) return getMaster().isHotEnough();
		return false;
	}
	
	// Finding Blocks
	
	protected boolean findCore(BlockPos pos) {
		return finder.find(pos, NCBlocks.fusion_core);
	}
	
	public boolean findAdjacentComparator() {
		return finder.horizontalYCount(pos, 1, Blocks.UNPOWERED_COMPARATOR, Blocks.POWERED_COMPARATOR) > 0;
	}
	
	// OpenComputers
	
	@Optional.Method(modid = "opencomputers")
	public void removeNode() {
		if (node instanceof Node) ((Node)node).remove();
	}
	
	@Optional.Method(modid = "opencomputers")
	public void refreshNode() {
		if (node() == null) node = Network.newNode(this, Visibility.None).create();
		if (node() != null && node().network() == null) Network.joinOrCreateNetwork(this);
	}
	
	/*@Override
	@Optional.Method(modid = "opencomputers")
	public String getComponentName() {
		return Global.MOD_SHORT_ID + "_fusion_reactor_dummy";
	}*/
	
	@Override
	@Optional.Method(modid = "opencomputers")
	public Node node() {
		return (Node)node;
	}

	@Override
	@Optional.Method(modid = "opencomputers")
	public void onConnect(Node node) {
		
	}

	@Override
	@Optional.Method(modid = "opencomputers")
	public void onDisconnect(Node node) {
		
	}

	@Override
	@Optional.Method(modid = "opencomputers")
	public void onMessage(Message message) {
		
	}
}
