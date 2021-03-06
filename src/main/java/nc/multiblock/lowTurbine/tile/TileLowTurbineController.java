package nc.multiblock.lowTurbine.tile;

import nc.Global;
import nc.config.NCConfig;
import nc.multiblock.cuboidal.CuboidalPartPositionType;
import nc.multiblock.lowTurbine.LowTurbine;
import nc.multiblock.lowTurbine.block.BlockLowTurbineController;
import nc.util.RegistryHelper;
import net.minecraft.block.Block;

public class TileLowTurbineController extends TileLowTurbinePartBase {
	
	public TileLowTurbineController() {
		super(CuboidalPartPositionType.WALL);
	}
	
	@Override
	public void onMachineAssembled(LowTurbine controller) {
		doStandardNullControllerResponse(controller);
		super.onMachineAssembled(controller);
		if (getWorld().isRemote) return;
	}
	
	@Override
	public void onMachineBroken() {
		super.onMachineBroken();
		if (getWorld().isRemote) return;
		//getWorld().setBlockState(getPos(), getWorld().getBlockState(getPos()), 2);
	}
	
	@Override
	public void update() {
		super.update();
		tickTile();
		if (shouldTileCheck()) if (getBlock(pos) instanceof BlockLowTurbineController) {
			((BlockLowTurbineController) getBlock(pos)).setActiveState(getBlockState(pos), world, pos, world.isBlockPowered(pos) && isMultiblockAssembled());
		}
	}
	
	@Override
	public void tickTile() {
		tickCount++; tickCount %= NCConfig.machine_update_rate / 4;
	}
	
	public void doMeltdown() {
		Block corium = RegistryHelper.getBlock(Global.MOD_ID, "fluid_corium");
		world.removeTileEntity(pos);
		world.setBlockState(pos, corium.getDefaultState());
	}
	
	public boolean isPowered() {
		return world.isBlockPowered(pos);
	}
}
