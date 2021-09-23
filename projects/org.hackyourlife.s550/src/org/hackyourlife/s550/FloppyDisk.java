package org.hackyourlife.s550;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.hackyourlife.s550.io.BEInputStream;
import org.hackyourlife.s550.io.BEOutputStream;
import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class FloppyDisk extends Struct {
	public static final int PATCH_COUNT = 16;
	public static final int TONE_COUNT = 32;
	public static final int SEGMENT_COUNT = 18;

	private final SystemProgram systemProgram = new SystemProgram();
	private final PatchParameter[] patchParameter = new PatchParameter[16];
	private final FunctionParameter functionParameter = new FunctionParameter();
	private final MidiParameter midiParameter = new MidiParameter();
	private final ToneParameter[] toneParameter = new ToneParameter[32];
	private final ToneList[] toneList = new ToneList[32];
	private final WaveData[] waveDataA = new WaveData[18];
	private final WaveData[] waveDataB = new WaveData[18];

	public FloppyDisk() {
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
			waveDataA[i] = new WaveData();
		}
		for(int i = 0; i < waveDataB.length; i++) {
			waveDataB[i] = new WaveData();
		}
	}

	public SystemProgram getSystemProgram() {
		return systemProgram;
	}

	public PatchParameter getPatchParameter(int i) {
		return patchParameter[i];
	}

	public FunctionParameter getFunctionParameter() {
		return functionParameter;
	}

	public MidiParameter getMidiParameter() {
		return midiParameter;
	}

	public ToneParameter getToneParameter(int i) {
		return toneParameter[i];
	}

	public WaveData getWaveDataA(int i) {
		return waveDataA[i];
	}

	public WaveData getWaveDataB(int i) {
		return waveDataB[i];
	}

	public void copyFrom(FloppyDisk disk) {
		byte[] data;
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			disk.write(new BEOutputStream(out));
			out.flush();
			data = out.toByteArray();
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
		try(WordInputStream in = new BEInputStream(new ByteArrayInputStream(data))) {
			read(in);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		systemProgram.read(in);
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
		for(int i = 0; i < waveDataA.length; i++) {
			waveDataA[i].write(out);
		}
		for(int i = 0; i < waveDataB.length; i++) {
			waveDataB[i].write(out);
		}
	}
}
