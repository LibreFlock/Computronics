package org.libreflock.computronics.util;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.libreflock.computronics.reference.Config;

public class Camera {

	private World world;
	private float xDirection, yDirection, zDirection;
	private double oxPos, oyPos, ozPos, xPos, yPos, zPos;
	private Object hit;

	public Camera() {
		// not initialized
	}

	public boolean ray(World worldObj, int xCoord, int yCoord, int zCoord, ForgeDirection dir, float x, float y) {
		return ray(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, dir, x, y, true);
	}

	public boolean ray(World worldObj, double xCoord, double yCoord, double zCoord, ForgeDirection dir, float x, float y, boolean doBlockOffset) {
		hit = null;
		if(x < -1.0F || x > 1.0F || y < -1.0F || y > 1.0F) {
			return false;
		}
		if(dir != null) {
			xDirection = 0.0F;
			yDirection = y;
			zDirection = 0.0F;

			double oxOffset = 0, oyOffset = 0, ozOffset = 0;

			switch(dir) {
				case EAST: {
					xDirection = 1.0F;
					zDirection = -x;
					if(doBlockOffset) {
						xCoord += 0.6;
						oxOffset = -0.1;
					}
				}
				break;
				case NORTH: {
					zDirection = -1.0F;
					xDirection = x;
					if(doBlockOffset) {
						zCoord -= 0.6;
						ozOffset = 0.1;
					}
				}
				break;
				case SOUTH: {
					zDirection = 1.0F;
					xDirection = -x;
					if(doBlockOffset) {
						zCoord += 0.6;
						ozOffset = -0.1;
					}
				}
				break;
				case WEST: {
					xDirection = -1.0F;
					zDirection = x;
					if(doBlockOffset) {
						xCoord -= 0.6;
						oxOffset = 0.1;
					}
				}
				break;
				case DOWN: {
					yDirection = -1.0F;
					xDirection = x;
					zDirection = y;
					if(doBlockOffset) {
						yCoord -= 0.6;
						oyOffset = 0.1;
					}
				}
				break;
				case UP: {
					yDirection = 1.0F;
					xDirection = x;
					zDirection = y;
					if(doBlockOffset) {
						yCoord += 0.6;
						oyOffset = -0.1;
					}
				}
				break;
				case UNKNOWN:
					return false;
				default:
					return false;
			}
			world = worldObj;
			xPos = xCoord;
			yPos = yCoord;
			zPos = zCoord;
			oxPos = xPos;
			oyPos = yPos;
			ozPos = zPos;
			// A little workaround for the way I do things (skipping the block right in front, that is)
			if(!world.isAirBlock((int) Math.floor(xPos), (int) Math.floor(yPos), (int) Math.floor(zPos))) {
				hit = world.getBlock((int) Math.floor(xPos), (int) Math.floor(yPos), (int) Math.floor(zPos));
				return true;
			}

			// shoot ray
			float steps = Config.CAMERA_DISTANCE;
			Vec3 origin = Vec3.createVectorHelper(xPos, yPos, zPos);
			Vec3 target = Vec3.createVectorHelper(xPos + (xDirection * steps), yPos + (yDirection * steps), zPos + (zDirection * steps));
			MovingObjectPosition mop = world.rayTraceBlocks(origin, target);
			if(mop != null) {
				xPos = mop.hitVec.xCoord;
				yPos = mop.hitVec.yCoord;
				zPos = mop.hitVec.zCoord;
				oxPos += oxOffset;
				oyPos += oyOffset;
				ozPos += ozOffset;
				switch(mop.typeOfHit) {
					case ENTITY: {
						hit = mop.entityHit;
					}
					break;
					case BLOCK: {
						hit = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
					}
					break;
					default:
						break;
				}
			}
			return true;
		}
		return false;
	}

	public double getDistance() {
		if(hit == null) {
			return -1.0;
		}
		double x = xPos - oxPos;
		double y = yPos - oyPos;
		double z = zPos - ozPos;
		return Math.sqrt(x * x + y * y + z * z);
	}
}
