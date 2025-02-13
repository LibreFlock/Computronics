package org.libreflock.computronics.util;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.libreflock.computronics.reference.Config;

public class Camera {

	private World world;
	private float xDirection, yDirection, zDirection;
	private double oxPos, oyPos, ozPos, xPos, yPos, zPos;
	private Object hit;

	public Camera() {
		// not initialized
	}

	public boolean ray(World worldObj, int xCoord, int yCoord, int zCoord, Direction dir, float x, float y) {
		return ray(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, dir, x, y, true);
	}

	public boolean ray(World worldObj, double xCoord, double yCoord, double zCoord, Direction dir, float x, float y, boolean doBlockOffset) {
		hit = null;
		if(x < -1.0F || x > 1.0F || y < -1.0F || y > 1.0F) {
			return false;
		}
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
		BlockPos pos = new BlockPos((int) Math.floor(xPos), (int) Math.floor(yPos), (int) Math.floor(zPos));
		if(!world.isEmptyBlock(pos)) {
			hit = world.getBlockState(pos).getBlock();
			return true;
		}

		// shoot ray
		float steps = Config.COMMON.CAMERA_DISTANCE.get();
		Vector3d origin = new Vector3d(xPos, yPos, zPos);
		Vector3d target = new Vector3d(xPos + (xDirection * steps), yPos + (yDirection * steps), zPos + (zDirection * steps));

		RayTraceContext context = new RayTraceContext(origin, target, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, null);

		RayTraceResult mop = world.clip(context);
		if(mop != null) {
			xPos = mop.getLocation().x;
			yPos = mop.getLocation().y;
			zPos = mop.getLocation().z;
			oxPos += oxOffset;
			oyPos += oyOffset;
			ozPos += ozOffset;
			switch(mop.getType()) {
				case ENTITY: {
					hit = ((EntityRayTraceResult) mop).getEntity();
				}
				break;
				case BLOCK: {
					hit = world.getBlockState(((BlockRayTraceResult)mop).getBlockPos());
				}
				break;
				default:
					break;
			}
		}
		return true;
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
