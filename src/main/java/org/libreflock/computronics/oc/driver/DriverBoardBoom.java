package org.libreflock.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.component.RackBusConnectable;
import li.cil.oc.api.component.RackMountable;
import li.cil.oc.api.internal.Rack;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
// import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.libreflock.computronics.oc.IntegrationOpenComputers;
import org.libreflock.computronics.reference.Config;
// import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.OCUtils;
import org.libreflock.computronics.util.boom.SelfDestruct;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

/**
 * @author Vexatos
 */
public class DriverBoardBoom extends DriverCardBoom implements RackMountable {

	protected final Rack container;
	protected boolean needsUpdate;
	protected boolean isActive;

	public DriverBoardBoom(Rack container) {
		super(container);
		this.container = container;
	}

	@Override
	protected void createNode() {
		this.setNode(Network.newNode(this, Visibility.Network).
			withComponent("server_destruct", Visibility.Network).
			withConnector().
			create());
	}

	@Override
	public void update() {
		setActive(node.tryChangeBuffer(-Config.COMMON.BOOM_BOARD_MAINTENANCE_COST.get()));
		if(needsUpdate) {
			container.markChanged(container.indexOfMountable(this));
			needsUpdate = false;
		}
		if(!isActive) {
			setTime(-1);
			return;
		}
		super.update();
	}

	@Override
	protected void setTime(int time) {
		if(time != this.time) {
			super.setTime(time);
			needsUpdate = true;
		}
	}

	public void setActive(boolean active) {
		if(active != this.isActive) {
			this.isActive = active;
			needsUpdate = true;
		}
	}

	@Override
	protected void goBoom() {
		final Set<Rack> racks = new LinkedHashSet<Rack>();
		final Queue<Rack> toSearch = new ArrayDeque<Rack>();
		toSearch.add(container);
		racks.add(container);
		final Vector3d origin = new Vector3d(container.xPosition(), container.yPosition(), container.zPosition());
		Rack cur;
		while((cur = toSearch.poll()) != null) {
			final World world = cur.world();
			final BlockPos currentPos = new BlockPos(cur.xPosition(), cur.yPosition(), cur.zPosition());
			for(Direction dir : Direction.values()) {
				final BlockPos pos = currentPos.relative(dir);

				if(origin.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 256 &&
					world.isLoaded(pos)) {
					TileEntity tile = world.getBlockEntity(pos);
					if(tile instanceof Rack && racks.add((Rack) tile)) {
						toSearch.add((Rack) tile);
					}
				}
			}
		}
		final Queue<Set<Rack>> rackList = new ArrayDeque<Set<Rack>>(((racks.size() + 5) / 6));
		Iterator<Rack> itr = racks.iterator();
		while(itr.hasNext()) {
			Set<Rack> sub = new HashSet<Rack>(6);
			for(int i = 0; i < 6; i++) {
				if(!itr.hasNext()) {
					break;
				}
				Rack rack = itr.next();
				sub.add(rack);
			}
			rackList.add(sub);
		}
		IntegrationOpenComputers.boomBoardHandler.queue(container, rackList);
	}

	@Override
	public CompoundNBT getData() {
		CompoundNBT tag = new CompoundNBT();
		tag.putBoolean("t", this.time >= 0);
		tag.putBoolean("r", this.isActive);
		return tag;
	}

	@Override
	public void onDisconnect(final Node node) {
	}

	@Override
	public void onMessage(final Message message) {
		// NO-OP
	}

	@Override
	public void loadData(CompoundNBT nbt) {
		super.loadData(nbt);
		if(nbt.getBoolean("active")) {
			setActive(nbt.getBoolean("active"));
		}
	}

	@Override
	public void saveData(CompoundNBT nbt) {
		super.saveData(nbt);
		nbt.putBoolean("active", this.isActive);
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Generic,
			"Server-cleaning service",
			OCUtils.Vendors.HuggingCreeper,
			"SSD-Struct M4"
		);
	}

	@Override
	public int getConnectableCount() {
		return 0;
	}

	@Override
	public RackBusConnectable getConnectableAt(int index) {
		return null;
	}

	@Override
	public boolean onActivate(PlayerEntity player, Hand hand, ItemStack heldItem, float hitX, float hitY) {
		return false;
	}

	@Override
	public EnumSet<State> getCurrentState() {
		return EnumSet.noneOf(State.class);
	}

	public static class BoomHandler {

		private final Set<Pair<Rack, Queue<Set<Rack>>>> boomQueue = new HashSet<Pair<Rack, Queue<Set<Rack>>>>();

		@SubscribeEvent
		// @Optional.Method(modid = Mods.OpenComputers)
		public void onServerTick(ServerTickEvent e) {
			if(e.phase != TickEvent.Phase.START || boomQueue.isEmpty()) {
				return;
			}
			Set<Pair<Rack, Queue<Set<Rack>>>> toRemove = new HashSet<Pair<Rack, Queue<Set<Rack>>>>();
			for(Pair<Rack, Queue<Set<Rack>>> lists : boomQueue) {
				if((lists.getKey().world().getGameTime() + lists.hashCode()) % 5 != 0) {
					continue; // Only explode every five ticks.
				}
				Set<Rack> current = lists.getValue().poll();
				for(Rack rack : current) {
					SelfDestruct.goBoom(rack.world(), rack.xPosition(), rack.yPosition(), rack.zPosition(), false);
				}
				if(lists.getValue().isEmpty()) {
					toRemove.add(lists);
				}
			}
			boomQueue.removeAll(toRemove);
		}

		// @Optional.Method(modid = Mods.OpenComputers)
		public void queue(Rack owner, Queue<Set<Rack>> rackList) {
			boomQueue.add(Pair.of(owner, rackList));
		}
	}
}
