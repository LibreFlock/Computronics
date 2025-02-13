package org.libreflock.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tile.TileRadar;

public class BlockRadar extends BlockPeripheral {

	public BlockRadar() {
		super("radar", Rotation.NONE);
		this.setCreativeTab(Computronics.tab);
		this.setTranslationKey("computronics.radar");
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState metadata) {
		return new TileRadar();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileRadar.class;
	}
}
