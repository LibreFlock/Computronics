package org.libreflock.computronics.block;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tile.TileRadar;

public class BlockRadar extends BlockMachineSidedIcon {
	public BlockRadar() {
		super("radar");
		this.setCreativeTab(Computronics.tab);
		this.setIconName("computronics:radar");
		this.setBlockName("computronics.radar");
		this.setRotation(Rotation.NONE);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileRadar();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mSide = r.registerIcon("computronics:radar_side");
	}

	@Override
	@Optional.Method(modid= Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileRadar.class;
	}
}
