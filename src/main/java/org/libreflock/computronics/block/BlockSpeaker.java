package org.libreflock.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tile.TileSpeaker;

public class BlockSpeaker extends BlockPeripheral {

	public BlockSpeaker() {
		super("speaker", Rotation.SIX);
		this.setTranslationKey("computronics.speaker");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState stat) {
		return new TileSpeaker();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileSpeaker.class;
	}
}
