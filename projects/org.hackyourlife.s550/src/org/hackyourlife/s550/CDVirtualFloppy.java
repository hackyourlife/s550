package org.hackyourlife.s550;

import java.io.IOException;
import java.util.Arrays;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class CDVirtualFloppy extends Struct {
	private final PatchParameter[] patchParameter = new PatchParameter[16];
	private final FunctionParameter functionParameter = new FunctionParameter();
	private final MidiParameter midiParameter = new MidiParameter();
	private final ToneParameter[] toneParameter = new ToneParameter[32];
	private final ToneList[] toneList = new ToneList[32];
	private final CDWaveData[] waveDataA = new CDWaveData[18];
	private final CDWaveData[] waveDataB = new CDWaveData[18];

	public CDVirtualFloppy() {
		for(int i = 0; i < patchParameter.length; i++) {
			patchParameter[i] = new PatchParameter();
		}
		for(int i = 0; i < toneParameter.length; i++) {
			toneParameter[i] = new ToneParameter();
		}
		for(int i = 0; i < toneList.length; i++) {
			toneList[i] = new ToneList();
		}
		for(int i = 0; i < waveDataA.length; i++) {
			waveDataA[i] = new CDWaveData();
		}
		for(int i = 0; i < waveDataB.length; i++) {
			waveDataB[i] = new CDWaveData();
		}
	}

	public PatchParameter[] getPatchParameters() {
		return Arrays.copyOf(patchParameter, patchParameter.length);
	}

	public PatchParameter getPatchParameter(int i) {
		return patchParameter[i];
	}

	public ToneParameter[] getToneParameters() {
		return Arrays.copyOf(toneParameter, toneParameter.length);
	}

	public ToneParameter getToneParameter(int i) {
		return toneParameter[i];
	}

	public ToneList[] getToneLists() {
		return Arrays.copyOf(toneList, toneList.length);
	}

	public ToneList getToneList(int i) {
		return toneList[i];
	}

	public CDWaveData getWaveDataA(int i) {
		return waveDataA[i];
	}

	public CDWaveData getWaveDataB(int i) {
		return waveDataB[i];
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		for(int i = 0; i < patchParameter.length; i++) {
			patchParameter[i].read(in);
		}
		functionParameter.read(in);
		midiParameter.read(in);
		for(int i = 0; i < toneParameter.length; i++) {
			toneParameter[i].read(in);
		}
		for(int i = 0; i < toneList.length; i++) {
			toneList[i].read(in);
		}
		for(int i = 0; i < waveDataA.length; i++) {
			waveDataA[i].read(in);
		}
		for(int i = 0; i < waveDataB.length; i++) {
			waveDataB[i].read(in);
		}
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		for(int i = 0; i < patchParameter.length; i++) {
			patchParameter[i].write(out);
		}
		functionParameter.write(out);
		midiParameter.write(out);
		for(int i = 0; i < toneParameter.length; i++) {
			toneParameter[i].write(out);
		}
		for(int i = 0; i < toneList.length; i++) {
			toneList[i].write(out);
		}
		for(int i = 0; i < waveDataA.length; i++) {
			waveDataA[i].write(out);
		}
		for(int i = 0; i < waveDataB.length; i++) {
			waveDataB[i].write(out);
		}
	}

	public void writeFloppy(WordOutputStream out, SystemProgram systemProgram) throws IOException {
		systemProgram.write(out);
		for(int i = 0; i < patchParameter.length; i++) {
			patchParameter[i].write(out);
		}
		functionParameter.write(out);
		midiParameter.write(out);
		for(int i = 0; i < toneParameter.length; i++) {
			toneParameter[i].write(out);
		}
		for(int i = 0; i < toneList.length; i++) {
			toneList[i].write(out);
		}
		WaveData wave = new WaveData();
		for(int i = 0; i < waveDataA.length; i++) {
			wave.setSamples(waveDataA[i].getSamples());
			wave.write(out);
		}
		for(int i = 0; i < waveDataB.length; i++) {
			wave.setSamples(waveDataB[i].getSamples());
			wave.write(out);
		}
	}
}
