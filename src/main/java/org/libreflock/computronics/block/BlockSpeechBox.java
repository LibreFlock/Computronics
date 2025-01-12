package org.libreflock.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.libreflock.computronics.tile.TileSpeechBox;

/**
 * @author Vexatos
 */
public class BlockSpeechBox extends BlockPeripheral {

	public BlockSpeechBox() {
		super("speech_box", Rotation.FOUR);
		this.setTranslationKey("computronics.speechBox");
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileSpeechBox();
	}

	@Override
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileSpeechBox.class;
	}
}
