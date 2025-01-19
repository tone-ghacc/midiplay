import java.io.File;
import javax.sound.midi.*;

public class StudyPiano {
    public static void main(String[] args) {
        try {
            // MIDIデバイスを取得
            Transmitter transmitter = MidiDeviceHandler.getHardwareMidiDeviceTransmitter();
            Receiver receiver = MidiDeviceHandler.getHardwareMidiDeviceReceiver();

            // MIDIレコーダーを取得
            MidiRecorder recorder = MidiRecorder.getInstance();
            recorder.startRecording();

            // MIDIレコーダーを停止
            recorder.saveMidiFile("assets/output.mid");

            // MIDIファイルを再生
            File midiFile = new File("assets/output.mid");
            MidiHandler.playMidiFile(receiver, midiFile);

            // MIDIデバイスをクローズ
            MidiDeviceHandler.midiDeviceCloser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
