package org.libreflock.asielib.api.tile;

/*import net.minecraftforge.fml.common.Optional;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mrtjp.projectred.api.IBundledTile;
import org.libreflock.asielib.reference.Mods;*/

import net.minecraft.util.Direction;

import javax.annotation.Nullable;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledEmitter", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IBundledUpdatable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mods.immibis.redlogic.api.wiring.IConnectable", modid = Mods.RedLogic),
	@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = Mods.ProjectRed)
})*/
public interface IBundledRedstoneProvider /*extends IBundledEmitter, IBundledUpdatable, IConnectable, IBundledTile*/ {

	public boolean canBundledConnectToInput(@Nullable Direction side);

	public boolean canBundledConnectToOutput(@Nullable Direction side);

	@Nullable
	public byte[] getBundledOutput(@Nullable Direction side);

	public void onBundledInputChange(@Nullable Direction side, @Nullable byte[] data);
}
