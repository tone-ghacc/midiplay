import java.io.File;
import javax.sound.midi.*;

public class StudyPiano {
    public static void main(String[] args) {
        try {
            // MIDIデバイスを取得
            Transmitter transmitter = MidiDeviceHandler.getHardwareMidiDeviceTransmitter();
            Receiver receiver = MidiDeviceHandler.getHardwareMidiDeviceReceiver();

            // 先生が演奏したMIDIを録音
            MidiRecorder recorder = MidiRecorder.getInstance();
            recorder.startTeacherRecording();
            transmitter.setReceiver(recorder);

            // Enterキーを押すまで待機
            System.out.println("Press Enter to stop recording.");
            System.in.read();

            // MIDIレコーダーを停止
            recorder.saveMidiFile("assets/output.mid");

            while (!recorder.getJudgement()) {
                // MIDIファイルを再生
                File midiFile = new File("assets/output.mid");
                MidiHandler.playMidiFile(receiver, midiFile);

                // 生徒が演奏したMIDIを録音
                recorder.startStudentRecording();

                waitMidiKey();

                // MIDIレコーダーを停止
                recorder.stopRecording();
            }

            // MIDIデバイスをクローズ
            MidiDeviceHandler.midiDeviceCloser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized static void waitMidiKey() {
        try {
            StudyPiano.class.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
