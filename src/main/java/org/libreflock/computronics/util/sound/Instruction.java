package org.libreflock.computronics.util.sound;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import org.libreflock.computronics.util.sound.AudioUtil.AmplitudeModulation;
import org.libreflock.computronics.util.sound.AudioUtil.AudioProcess;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;
import static org.libreflock.computronics.util.sound.AudioUtil.ADSR;
import static org.libreflock.computronics.util.sound.AudioUtil.FrequencyModulation;
import static org.libreflock.computronics.util.sound.AudioUtil.Gate;
import static org.libreflock.computronics.util.sound.AudioUtil.State;

/**
 * @author Vexatos
 */
public abstract class Instruction {

	public static class Open extends ChannelSpecific {

		public Open(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.gate = Gate.Open;
			if(state.envelope != null) {
				state.envelope.reset();
			}
		}
	}

	public static class Close extends ChannelSpecific {

		public Close(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.gate = Gate.Closed;
		}
	}

	public static class SetWave extends ChannelSpecific {

		public final AudioType type;

		public SetWave(int channelIndex, AudioType type) {
			super(channelIndex);
			this.type = type;
		}

		public SetWave(CompoundNBT tag) {
			super(tag.getByte("c"));
			this.type = AudioType.fromIndex(tag.getByte("w"));
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.generator = new AudioUtil.Wave(type);
		}
	}

	public static class Delay extends Instruction implements Ticking {

		public final int delay;

		public Delay(int delay) {
			this.delay = delay;
		}

		public Delay(CompoundNBT tag) {
			this.delay = tag.getInt("d");
		}

		@Override
		public void encounter(AudioProcess process) {
			process.delay = this.delay;
		}
	}

	public static class SetFM extends ChannelSpecific {

		public final FrequencyModulation freqMod;

		public SetFM(int channelIndex, int modulatorIndex, float index) {
			super(channelIndex);
			this.freqMod = new FrequencyModulation(modulatorIndex, index);
		}

		public SetFM(CompoundNBT tag) {
			super(tag.getByte("c"));
			this.freqMod = new FrequencyModulation(
				tag.getInt("m"),
				tag.getFloat("i")
			);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.isAmpMod || state.isFreqMod) {
				return;
			}
			if(state.freqMod != null) {
				State mstate = process.states.get(state.freqMod.modulatorIndex);
				if(mstate != null) {
					mstate.isFreqMod = false;
				}
			}
			State mstate = process.states.get(this.freqMod.modulatorIndex);
			if(mstate != null) {
				mstate.isFreqMod = true;
				state.freqMod = this.freqMod;
			}
		}
	}

	public static class ResetFM extends ChannelSpecific {

		public ResetFM(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.freqMod == null) {
				return;
			}
			State mstate = process.states.get(state.freqMod.modulatorIndex);
			if(mstate != null) {
				mstate.isFreqMod = false;
			}
			state.freqMod = null;
		}
	}

	public static class SetAM extends ChannelSpecific {

		public final AmplitudeModulation ampMod;

		public SetAM(int channelIndex, int modulatorIndex) {
			super(channelIndex);
			this.ampMod = new AmplitudeModulation(modulatorIndex);
		}

		public SetAM(CompoundNBT tag) {
			super(tag.getByte("c"));
			this.ampMod = new AmplitudeModulation(tag.getInt("m"));
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.isAmpMod || state.isFreqMod) {
				return;
			}
			if(state.ampMod != null) {
				State mstate = process.states.get(state.ampMod.modulatorIndex);
				if(mstate != null) {
					mstate.isAmpMod = false;
				}
			}
			State mstate = process.states.get(this.ampMod.modulatorIndex);
			if(mstate != null) {
				mstate.isAmpMod = true;
				state.ampMod = this.ampMod;
			}
		}
	}

	public static class ResetAM extends ChannelSpecific {

		public ResetAM(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.ampMod == null) {
				return;
			}
			State mstate = process.states.get(state.ampMod.modulatorIndex);
			if(mstate != null) {
				mstate.isAmpMod = false;
			}
			state.ampMod = null;
		}
	}

	public static class SetADSR extends ChannelSpecific {

		public final ADSR envelope;

		public SetADSR(int channelIndex, int attackDuration, int decayDuration, float attenuation, int releaseDuration) {
			super(channelIndex);
			this.envelope = new ADSR(attackDuration, decayDuration, attenuation, releaseDuration);
		}

		public SetADSR(CompoundNBT tag) {
			super(tag.getByte("c"));
			this.envelope = new ADSR(
				tag.getInt("a"),
				tag.getInt("d"),
				tag.getFloat("s"),
				tag.getInt("r")
			);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			if(state.envelope != null) {
				this.envelope.progress = state.envelope.progress;
				this.envelope.phase = state.envelope.phase;
			}
			state.envelope = this.envelope;
		}
	}

	public static class ResetEnvelope extends ChannelSpecific {

		public ResetEnvelope(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.envelope = null;
		}
	}

	public static class SetVolume extends ChannelSpecific {

		public final float volume;

		public SetVolume(int channelIndex, float volume) {
			super(channelIndex);
			this.volume = Math.min(Math.max(volume, 0), 1);
		}

		public SetVolume(CompoundNBT tag) {
			super(tag.getByte("c"));
			this.volume = tag.getFloat("v");
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.volume = this.volume;
		}
	}

	public static class SetFrequency extends ChannelSpecific {

		public final float frequency;

		public SetFrequency(int channelIndex, float frequency) {
			super(channelIndex);
			this.frequency = frequency;
		}

		public SetFrequency(CompoundNBT tag) {
			super(tag.getByte("c"));
			this.frequency = tag.getFloat("f");
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.frequencyInHz = frequency;
		}
	}

	public static class SetWhiteNoise extends ChannelSpecific {

		public SetWhiteNoise(int channelIndex) {
			super(channelIndex);
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.generator = new AudioUtil.WhiteNoise();
		}
	}

	public static class SetLFSR extends ChannelSpecific {

		public final int initial, mask;

		public SetLFSR(int channelIndex, int initial, int mask) {
			super(channelIndex);
			this.initial = initial;
			this.mask = mask;
		}

		public SetLFSR(CompoundNBT tag) {
			this(tag.getByte("c"), tag.getInt("i"), tag.getInt("m"));
		}

		@Override
		public void encounter(AudioProcess process, State state) {
			state.generator = new AudioUtil.LFSR(initial, mask);
		}
	}

	public static abstract class ChannelSpecific extends Instruction {

		public final int channelIndex;

		public ChannelSpecific(int channelIndex) {
			this.channelIndex = channelIndex;
		}

		@Override
		public final void encounter(AudioProcess process) {
			this.encounter(process, process.states.get(this.channelIndex));
		}

		public abstract void encounter(AudioProcess process, State state);
	}

	public abstract void encounter(AudioProcess process);

	public interface Ticking {

	}

	@Nullable
	public static Instruction load(CompoundNBT tag) {
		int type = tag.getByte("t");
		switch(type) {
			case 0: {
				return new Open(tag.getByte("c"));
			}
			case 1: {
				return new Close(tag.getByte("c"));
			}
			case 2: {
				return new SetWave(tag);
			}
			case 3: {
				return new Delay(tag);
			}
			case 4: {
				return new SetFM(tag);
			}
			case 5: {
				return new ResetFM(tag.getByte("c"));
			}
			case 6: {
				return new SetAM(tag);
			}
			case 7: {
				return new ResetAM(tag.getByte("c"));
			}
			case 8: {
				return new SetADSR(tag);
			}
			case 9: {
				return new ResetEnvelope(tag.getByte("c"));
			}
			case 10: {
				return new SetVolume(tag);
			}
			case 11: {
				return new SetFrequency(tag);
			}
			case 12: {
				return new SetWhiteNoise(tag.getByte("c"));
			}
			case 13: {
				return new SetLFSR(tag);
			}
		}
		return null;
	}

	public static void save(CompoundNBT tag, Instruction inst) {
		if(inst instanceof ChannelSpecific) {
			tag.putByte("c", (byte) ((ChannelSpecific) inst).channelIndex);
		}
		if(inst instanceof Open) {
			tag.putByte("t", (byte) 0);
		} else if(inst instanceof Close) {
			tag.putByte("t", (byte) 1);
		} else if(inst instanceof SetWave) {
			tag.putByte("t", (byte) 2);
			tag.putInt("w", ((SetWave) inst).type.ordinal());
		} else if(inst instanceof Delay) {
			tag.putByte("t", (byte) 3);
			tag.putInt("d", ((Delay) inst).delay);
		} else if(inst instanceof SetFM) {
			tag.putByte("t", (byte) 4);
			tag.putInt("m", ((SetFM) inst).freqMod.modulatorIndex);
			tag.putFloat("i", ((SetFM) inst).freqMod.index);
		} else if(inst instanceof ResetFM) {
			tag.putByte("t", (byte) 5);
		} else if(inst instanceof SetAM) {
			tag.putByte("t", (byte) 6);
			tag.putInt("m", ((SetAM) inst).ampMod.modulatorIndex);
		} else if(inst instanceof ResetAM) {
			tag.putByte("t", (byte) 7);
		} else if(inst instanceof SetADSR) {
			tag.putByte("t", (byte) 8);
			ADSR envelope = ((SetADSR) inst).envelope;
			tag.putInt("a", envelope.attackDuration);
			tag.putInt("d", envelope.decayDuration);
			tag.putFloat("s", envelope.attenuation);
			tag.putInt("r", envelope.releaseDuration);
		} else if(inst instanceof ResetEnvelope) {
			tag.putByte("t", (byte) 9);
		} else if(inst instanceof SetVolume) {
			tag.putByte("t", (byte) 10);
			tag.putFloat("v", ((SetVolume) inst).volume);
		} else if(inst instanceof SetFrequency) {
			tag.putByte("t", (byte) 11);
			tag.putFloat("f", ((SetFrequency) inst).frequency);
		} else if(inst instanceof SetWhiteNoise) {
			tag.putByte("t", (byte) 12);
		} else if(inst instanceof SetLFSR) {
			tag.putByte("t", (byte) 13);
			tag.putInt("i", ((SetLFSR) inst).initial);
			tag.putInt("m", ((SetLFSR) inst).mask);
		}
	}

	public static Queue<Instruction> fromNBT(ListNBT l) {
		ArrayDeque<Instruction> instructions = new ArrayDeque<Instruction>();
		for(int i = 0; i < l.size(); i++) {
			CompoundNBT tag = l.getCompound(i);
			if(!tag.isEmpty()) {
				Instruction instr = load(tag);
				if(instr != null) {
					instructions.add(instr);
				}
			}
		}
		return instructions;
	}

	public static void toNBT(ListNBT l, Queue<Instruction> instructions) {
		for(Instruction instruction : instructions) {
			CompoundNBT tag = new CompoundNBT();
			save(tag, instruction);
			l.add(tag);
		}
	}
}
