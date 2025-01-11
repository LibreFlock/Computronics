package org.libreflock.computronics.util.boom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.network.PacketType;
import org.libreflock.asielib.network.Packet;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author Vexatos
 */
public class SelfDestruct extends Explosion {


	protected World world;
	protected float explosionSize;
	private boolean destroyBlocks;

	public SelfDestruct(World world, @Nullable Entity exploder, double x, double y, double z, float size, boolean destroyBlocks) {
		super(world, exploder, x, y, z, size, false, (destroyBlocks ? Explosion.Mode.DESTROY : Explosion.Mode.NONE));
		this.world = world;
		this.destroyBlocks = destroyBlocks;
		this.explosionSize = size;
	}

	//Unfortunately I had to copy a lot of code for this one.
	@Override
	public void finalizeExplosion(boolean spawnParticles) {
		Vector3d position = getPosition();
		final double
			explosionX = position.x,
			explosionY = position.y,
			explosionZ = position.z;
		final BlockPos explosionPos = new BlockPos(explosionX, explosionY, explosionZ);

		this.world.playSound(null, explosionX, explosionY, explosionZ, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);

		this.world.addParticle(ParticleTypes.EXPLOSION, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D); // EXPLOSION_HUGE

		for(BlockPos blockpos : this.getToBlow()) {
			BlockState state = this.world.getBlockState(blockpos);
			Block block = state.getBlock();

			if(spawnParticles) {
				double d0 = (double) ((float) blockpos.getX() + this.world.random.nextFloat());
				double d1 = (double) ((float) blockpos.getY() + this.world.random.nextFloat());
				double d2 = (double) ((float) blockpos.getZ() + this.world.random.nextFloat());
				double d3 = d0 - explosionX;
				double d4 = d1 - explosionY;
				double d5 = d2 - explosionZ;
				double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
				d3 = d3 / d6;
				d4 = d4 / d6;
				d5 = d5 / d6;
				double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
				d7 = d7 * (double) (this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
				d3 = d3 * d7;
				d4 = d4 * d7;
				d5 = d5 * d7;
				this.world.addParticle(ParticleTypes.EXPLOSION, (d0 + explosionX * 1.0D) / 2.0D, (d1 + explosionY * 1.0D) / 2.0D, (d2 + explosionZ * 1.0D) / 2.0D, d3, d4, d5); // EXPLOSION_NORMAL
				this.world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, d3, d4, d5); // SMOKE_NORMAL
			}

			if(state.getMaterial() != Material.AIR) {
				if(this.world instanceof ClientWorld
					&& blockpos.equals(explosionPos)) {
					// This is the case.
					TileEntity tile = this.world.getBlockEntity(blockpos);
					if(tile != null && !tile.isRemoved() && tile instanceof IInventory) {
						IInventory inv = (IInventory) tile;
						for (int i = 0; i < inv.getContainerSize(); i++) { inv.setItem(i, ItemStack.EMPTY); }
					}
				}

				if(destroyBlocks) {
					if(!blockpos.equals(explosionPos)) {
						// This not is the case.
						if(block.canDropFromExplosion(state, this.world, blockpos, this)) {
							// block.dropBlockAsItemWithChance(this.world, blockpos, this.world.getBlockState(blockpos), 1.0F / this.explosionSize, 0);
							block.dropFromExplosion(this); // fixerino
						}
					}
					// block.onBlockExploded(this.world, blockpos, this);
					block.onBlockExploded(state, world, explosionPos, this);
				}
			}
		}
	}

	public static void goBoom(World world, BlockPos pos, boolean destroyBlocks) {
		goBoom(world, pos.getX(), pos.getY(), pos.getZ(), destroyBlocks);
	}

	public static void goBoom(World world, double xPos, double yPos, double zPos, boolean destroyBlocks) {
		SelfDestruct explosion = new SelfDestruct(world, null, xPos, yPos, zPos, 4.0F, destroyBlocks);
		explosion.explode();
		explosion.finalizeExplosion(false);

		int x = (int) xPos;
		int y = (int) yPos;
		int z = (int) zPos;

		AxisAlignedBB worldBounds = new AxisAlignedBB(
			Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
			Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
		);

		for(PlayerEntity playerEntity : world.getEntitiesOfClass(PlayerEntity.class, worldBounds)) {
			if(playerEntity instanceof ServerPlayerEntity) {
				ServerPlayerEntity PlayerEntity = (ServerPlayerEntity) playerEntity;

				if(PlayerEntity.position().distanceToSqr(xPos, yPos, zPos) < 4096.0D) {
					try {
						Packet p = Computronics.packet.create(PacketType.COMPUTER_BOOM.ordinal())
							.writeDouble(xPos)
							.writeDouble(yPos)
							.writeDouble(zPos)
							.writeFloat(4.0F)
							.writeByte((byte) (destroyBlocks ? 1 : 0));
						p.writeInt(explosion.getToBlow().size());

						{
							byte j, k, l;
							for(BlockPos pos : explosion.getToBlow()) {
								j = (byte) (pos.getX() - x);
								k = (byte) (pos.getY() - y);
								l = (byte) (pos.getZ() - z);
								p.writeByte(j);
								p.writeByte(k);
								p.writeByte(l);
							}
						}

						Vector3d motion = explosion.getPlayerKnockbackMap().get(PlayerEntity);
						float motionX = 0;
						float motionY = 0;
						float motionZ = 0;
						if(motion != null) {
							motionY = (float) motion.x;
							motionX = (float) motion.y;
							motionZ = (float) motion.z;
						}
						p.writeFloat(motionY);
						p.writeFloat(motionX);
						p.writeFloat(motionZ);

						Computronics.packet.sendTo(p, PlayerEntity);
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
