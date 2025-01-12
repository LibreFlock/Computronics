package org.libreflock.computronics.util.sound;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundNBT;
import org.libreflock.computronics.reference.Config;

import javax.annotation.Nullable;
import java.util.ArrayList;


// NOTE: i change Config.SOUND_SAMPLE_RATE to Config.COMMON.SOUND_SAMPLE_RATE, but that may not be correct
// Adjust if this is a mistake


/**
 * @author Vexatos
 */
public class AudioUtil {

	public enum Gate {
		Open {
			@Override
			public double getValue(AudioProcess process, State state, boolean isModulating) {
				if(!isModulating && (state.isAmpMod || state.isFreqMod)) {
					return 0;
				}
				double value = state.generator instanceof Noise ? ((Noise) state.generator).noiseOutput :
					state.generator instanceof Wave ? ((Wave) state.generator).type.generate(state.offset) : 0;
				if(state.freqMod != null && !state.isFreqMod && !state.isAmpMod) {
					value = state.freqMod.getModifiedValue(process, state, value);
				} else {
					state.offset += state.frequencyInHz / Config.COMMON.SOUND_SAMPLE_RATE.get(); // is this right?
				}
				if(state.offset > 1) {
					state.offset %= 1.0F;
					if(state.generator instanceof Noise) {
						((Noise) state.generator).updateModifier(state);
					}
				}
				if(state.ampMod != null && !state.isAmpMod && !state.isFreqMod) {
					value = state.ampMod.getModifiedValue(process, state, value);
				}
				if(state.envelope != null) {
					value = state.envelope.getModifiedValue(state, value);
				}
				return value * state.volume;
			}
		},
		Closed {
			@Override
			public double getValue(AudioProcess process, State state, boolean isModulating) {
				if(state.envelope != null && state.envelope.phase != null) {
					return Open.getValue(process, state, isModulating);
				}
				return 0;
			}
		};

		public abstract double getValue(AudioProcess process, State state, boolean isModulating);

		public double getValue(AudioProcess process, State state) {
			return getValue(process, state, false);
		}

		public static final Gate[] VALUES = values();

		public static Gate fromIndex(int index) {
			return index >= 0 && index < VALUES.length ? VALUES[index] : Closed;
		}
	}

	public static abstract class Modulation {

		public abstract double getModifiedValue(AudioProcess process, State state, double value);
	}

	public static class FrequencyModulation extends Modulation {

		public final int modulatorIndex;
		public final float index;

		public FrequencyModulation(int modulatorIndex, float index) {
			this.modulatorIndex = modulatorIndex;
			this.index = index;
		}

		@Override
		public double getModifiedValue(AudioProcess process, State state, double value) {
			State mstate = process.states.get(modulatorIndex);
			double deviation = mstate.gate.getValue(process, mstate, true) * index;
			state.offset += (state.frequencyInHz + deviation) / Config.COMMON.SOUND_SAMPLE_RATE.get();
			return value;
		}
	}

	public static class AmplitudeModulation extends Modulation {

		public final int modulatorIndex;

		public AmplitudeModulation(int modulatorIndex) {
			this.modulatorIndex = modulatorIndex;
		}

		@Override
		public double getModifiedValue(AudioProcess process, State state, double value) {
			State mstate = process.states.get(modulatorIndex);
			return value * (1 + mstate.gate.getValue(process, mstate, true));
		}
	}

	public static class ADSR {

		public int attackDuration;
		public int decayDuration;
		public float attenuation;
		public int releaseDuration;
		public Phase phase = Phase.Attack;
		public double progress;
		private final Phase initialPhase;
		private final double initialProgress;

		// Precalculated speeds
		private double attackSpeed;
		private double decaySpeed;
		private double releaseSpeed;

		public ADSR(int attackDuration, int decayDuration, float attenuation, int releaseDuration) {
			this.attackDuration = Math.max(attackDuration, 0);
			this.decayDuration = Math.max(decayDuration, 0);
			this.attenuation = Math.min(Math.max(attenuation, 0), 1);
			this.releaseDuration = Math.max(releaseDuration, 0);

			this.attackSpeed = 1000D / (this.attackDuration * Config.COMMON.SOUND_SAMPLE_RATE.get());
			if(this.attackDuration == 0) {
				if(this.decayDuration == 0) {
					this.initialPhase = this.phase = Phase.Sustain;
					this.initialProgress = this.progress = attenuation;
				} else {
					this.initialPhase = this.phase = Phase.Decay;
					this.initialProgress = this.progress = 1;
				}
			} else {
				this.initialPhase = Phase.Attack;
				this.initialProgress = this.progress = 0;
			}
			this.decaySpeed = this.decayDuration == 0 ? Double.POSITIVE_INFINITY : ((this.attenuation - 1D) * 1000D) / (this.decayDuration * Config.COMMON.SOUND_SAMPLE_RATE.get());
			this.releaseSpeed = this.releaseDuration == 0 ? Double.NEGATIVE_INFINITY : (-this.attenuation * 1000D) / (this.releaseDuration * Config.COMMON.SOUND_SAMPLE_RATE.get());
		}

		public double getModifiedValue(State state, double value) {
			if(phase == null) {
				return 0;
			}
			if(state.gate == Gate.Closed && phase != Phase.Release) {
				phase = Phase.Release;
			}
			switch(phase) {
				case Attack: {
					// value = value * (progress / (double) attackSpeed);
					if((progress += attackSpeed) >= 1) {
						progress = 1;
						nextPhase();
					}
					break;
				}
				case Decay: {
					// value = value * (1 + ((attenuation - 1) * (progress / (double) decaySpeed)));
					if((progress += decaySpeed) <= attenuation) {
						progress = attenuation;
						nextPhase();
					}
					break;
				}
				case Release: {
					// value = value * (attenuation * (1 - (progress / (double) releaseSpeed)));
					if((progress += releaseSpeed) <= 0) {
						phase = null;
						progress = 0;
					}
					break;
				}
			}
			value *= progress;
			return value;
		}

		private void nextPhase() {
			phase = phase.next();
		}

		public void reset() {
			this.phase = this.initialPhase;
			this.progress = this.initialProgress;
		}

		private enum Phase {
			Attack,
			Decay,
			Sustain,
			Release;

			public static final Phase[] VALUES = values();

			@Nullable
			public Phase next() {
				int ordinal = ordinal();
				return ordinal < VALUES.length ? VALUES[ordinal + 1] : null;
			}

			public static Phase fromIndex(int index) {
				return index >= 0 && index < VALUES.length ? VALUES[index] : Attack;
			}
		}
	}

	public static abstract class Generator {

	}

	public static class Wave extends Generator {

		public AudioType type = AudioType.Square;

		public Wave() {

		}

		public Wave(AudioType type) {
			this.type = type;
		}
	}

	public static abstract class Noise extends Generator {

		public double noiseOutput;

		public void updateModifier(State state) {
			this.noiseOutput = generate(state);
		}

		protected abstract double generate(State state);

		public static Noise load(CompoundNBT nbt) {
			final Noise noise;
			switch(nbt.getByte("t")) {
				case 1:
					noise = new LFSR(nbt.getInt("v"), nbt.getInt("m"));
					break;
				default:
					noise = new WhiteNoise();
					break;
			}
			noise.noiseOutput = nbt.getDouble("o");
			return noise;
		}

		protected abstract void save(CompoundNBT nbt);

	}

	public static class WhiteNoise extends Noise {

		@Override
		protected double generate(State state) {
			return Math.random() * 2 - 1;
		}

		@Override
		protected void save(CompoundNBT nbt) {
			nbt.putByte("t", (byte) 0);
			nbt.putDouble("o", noiseOutput);
		}
	}

	public static class LFSR extends Noise {

		private int value;
		private final int mask;

		public LFSR(int value, int mask) {
			this.value = value;
			this.mask = mask;
		}

		@Override
		protected double generate(State state) {
			if((value & 1) != 0) {
				value = (value >>> 1) ^ mask;
				return 1;
			} else {
				value >>>= 1;
				return -1;
			}
		}

		@Override
		protected void save(CompoundNBT nbt) {
			nbt.putByte("t", (byte) 1);
			nbt.putDouble("o", noiseOutput);
			nbt.putInt("v", value);
			nbt.putInt("m", mask);
		}
	}

	public static class State {

		public Generator generator;
		public final int channelIndex;

		public float frequencyInHz;
		public float offset;
		public Gate gate = Gate.Closed;
		public FrequencyModulation freqMod;
		public AmplitudeModulation ampMod;
		public ADSR envelope;
		public float volume = 1;

		public boolean isFreqMod, isAmpMod;

		public State(int channelIndex) {
			this.generator = new Wave();
			this.channelIndex = channelIndex;
		}

		public void load(CompoundNBT nbt) {
			if(nbt.contains("wavehz")) {
				frequencyInHz = nbt.getFloat("wavehz");
			}
			if(nbt.contains("offset")) {
				offset = nbt.getFloat("offset");
			}
			if(nbt.contains("type")) {
				this.generator = new Wave(AudioType.fromIndex(nbt.getInt("type")));
			}
			if(nbt.contains("noise")) {
				this.generator = Noise.load(nbt.getCompound("noise"));
			}
			if(nbt.contains("gate")) {
				gate = Gate.fromIndex(nbt.getInt("gate"));
			}
			if(nbt.contains("fmodi") && nbt.contains("findex")) {
				freqMod = new FrequencyModulation(nbt.getInt("fmodi"), nbt.getFloat("findex"));
			}
			if(nbt.contains("amodi")) {
				ampMod = new AmplitudeModulation(nbt.getInt("amodi"));
			}
			if(nbt.contains("a")) {
				envelope = new ADSR(nbt.getInt("a"), nbt.getInt("d"), nbt.getFloat("s"), nbt.getInt("r"));
				envelope.phase = ADSR.Phase.fromIndex(nbt.getInt("p"));
				envelope.progress = nbt.getDouble("o");
			}
			if(nbt.contains("vol")) {
				volume = nbt.getFloat("vol");
			}

			if(nbt.contains("isfmod")) {
				isFreqMod = nbt.getBoolean("isfmod");
			}
			if(nbt.contains("isamod")) {
				isAmpMod = nbt.getBoolean("isamod");
			}
		}

		public void save(CompoundNBT nbt) {
			nbt.putFloat("wavehz", frequencyInHz);
			nbt.putFloat("offset", offset);
			if(generator instanceof Wave && ((Wave) generator).type != null) {
				nbt.putInt("type", ((Wave) generator).type.ordinal());
			}
			if(generator instanceof Noise) {
				CompoundNBT data = new CompoundNBT();
				((Noise) generator).save(data);
				nbt.put("noise", data);
			}
			nbt.putInt("gate", gate.ordinal());
			if(freqMod != null) {
				nbt.putInt("fmodi", freqMod.modulatorIndex);
				nbt.putFloat("findex", freqMod.index);
			}
			if(ampMod != null) {
				nbt.putInt("amodi", ampMod.modulatorIndex);
			}
			if(envelope != null) {
				nbt.putInt("a", envelope.attackDuration);
				nbt.putInt("d", envelope.decayDuration);
				nbt.putFloat("s", envelope.attenuation);
				nbt.putInt("r", envelope.releaseDuration);
				nbt.putInt("p", envelope.phase.ordinal());
				nbt.putDouble("o", envelope.progress);
			}
			nbt.putFloat("vol", volume);
			nbt.putBoolean("isfmod", isFreqMod);
			nbt.putBoolean("isamod", isAmpMod);
		}
	}

	public static class AudioProcess {

		public final ImmutableList<State> states;
		public int delay = 0;
		public double error = 0;

		public AudioProcess(int channelCount) {
			ArrayList<State> states = new ArrayList<State>(channelCount);
			for(int i = 0; i < channelCount; i++) {
				states.add(new AudioUtil.State(i));
			}
			this.states = ImmutableList.copyOf(states);
		}

		public void load(CompoundNBT nbt) {
			for(int i = 0; i < states.size(); i++) {
				if(nbt.contains("ch" + i)) {
					states.get(i).load(nbt.getCompound("ch" + i));
				}
			}
			if(nbt.contains("delay")) {
				delay = nbt.getInt("delay");
			}
		}

		public void save(CompoundNBT nbt) {
			for(int i = 0; i < states.size(); i++) {
				CompoundNBT stateNBT = new CompoundNBT();
				nbt.put("ch" + i, stateNBT);
				states.get(i).save(stateNBT);
			}
			nbt.putInt("delay", delay);
		}
	}
}
