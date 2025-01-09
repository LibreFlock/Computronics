package org.libreflock.computronics;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.libreflock.computronics.api.audio.AudioPacketDFPWM;
import org.libreflock.computronics.api.audio.AudioPacketRegistry;
import org.libreflock.computronics.audio.AudioPacketClientHandlerDFPWM;
import org.libreflock.computronics.audio.SoundCardPacket;
import org.libreflock.computronics.audio.SoundCardPacketClientHandler;
import org.libreflock.computronics.client.AudioCableRender;
import org.libreflock.computronics.client.LampRender;
import org.libreflock.computronics.oc.IntegrationOpenComputers;
import org.libreflock.computronics.oc.client.RackMountableRenderer;
import org.libreflock.computronics.oc.client.UpgradeRenderer;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tape.TapeScrollEventHandler;
import org.libreflock.computronics.util.boom.SelfDestruct;
import org.libreflock.computronics.util.sound.Audio;
import org.libreflock.lib.network.Packet;

import java.io.IOException;
import java.util.ArrayList;

public class ClientProxy extends CommonProxy {

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void registerAudioHandlers() {
        super.registerAudioHandlers();
        AudioPacketRegistry.INSTANCE.registerClientHandler(
                AudioPacketDFPWM.class, new AudioPacketClientHandlerDFPWM()
        );
    }

    @Override
    public void registerEntities() {
        super.registerEntities();
    }

    @Override
    public void init() {
        Audio.init();
        registerRenderers();
        MinecraftForge.EVENT_BUS.register(new TapeScrollEventHandler());
    }

    public void registerRenderers() {
        if(Computronics.colorfulLamp != null) {
            RenderingRegistry.registerBlockHandler(new LampRender());
        }
        if(Computronics.audioCable != null) {
            RenderingRegistry.registerBlockHandler(new AudioCableRender());
        }
        if(Computronics.railcraft != null) {
            Computronics.railcraft.registerRenderers();
        }
        if(Mods.isLoaded(Mods.OpenComputers)) {
            registerOpenComputersRenderers();
            if(Computronics.forestry != null) {
                Computronics.forestry.registerOCRenderers();
            }
        }
    }

    @Override
    public void onServerStop() {
        Computronics.instance.audio.removeAll();
    }

    @Override
    public void goBoom(Packet p) throws IOException {
        double
                x = p.readDouble(),
                y = p.readDouble(),
                z = p.readDouble();
        float force = p.readFloat();
        boolean destroyBlocks = p.readByte() != 0;
        Minecraft minecraft = Minecraft.getMinecraft();
        SelfDestruct explosion = new SelfDestruct(minecraft.theWorld,
                null, x, y, z, force, destroyBlocks);
        int size = p.readInt();
        ArrayList<ChunkPosition> list = new ArrayList<ChunkPosition>(size);
        int i = (int) x;
        int j = (int) y;
        int k = (int) z;
        {
            int j1, k1, l1;
            for(int i1 = 0; i1 < size; ++i1) {
                j1 = p.readByte() + i;
                k1 = p.readByte() + j;
                l1 = p.readByte() + k;
                list.add(new ChunkPosition(j1, k1, l1));
            }
        }
        explosion.affectedBlockPositions = list;
        explosion.doExplosionB(true);
        minecraft.thePlayer.motionX += (double) p.readFloat();
        minecraft.thePlayer.motionY += (double) p.readFloat();
        minecraft.thePlayer.motionZ += (double) p.readFloat();
    }

    @Override
    @Optional.Method(modid = Mods.Forestry)
    public void spawnSwarmParticle(World worldObj, double xPos, double yPos, double zPos, int color) {
        Computronics.forestry.spawnSwarmParticle(worldObj, xPos, yPos, zPos, color);
    }

    @Optional.Method(modid = Mods.OpenComputers)
    private void registerOpenComputersRenderers() {
        if(IntegrationOpenComputers.upgradeRenderer == null) {
            IntegrationOpenComputers.upgradeRenderer = new UpgradeRenderer();
        }
        MinecraftForge.EVENT_BUS.register(IntegrationOpenComputers.upgradeRenderer);

        if(IntegrationOpenComputers.mountableRenderer == null) {
            IntegrationOpenComputers.mountableRenderer = new RackMountableRenderer();
        }
        MinecraftForge.EVENT_BUS.register(IntegrationOpenComputers.mountableRenderer);
    }

    @Override
    @Optional.Method(modid = Mods.OpenComputers)
    protected void registerOpenComputersAudioHandlers() {
        super.registerOpenComputersAudioHandlers();
        AudioPacketRegistry.INSTANCE.registerClientHandler(
                SoundCardPacket.class, new SoundCardPacketClientHandler()
        );
    }
}