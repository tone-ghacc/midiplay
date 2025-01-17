import java.io.File;
import javax.sound.midi.Transmitter;
import java.util.ArrayList;

public class StudyPiano {
    private static boolean isJudge;

    public static void main(String[] args) {
        try {
            // scoreのインスタンスを生成
            Score score = MidiHandler.recordMidiScore(MidiDeviceHandler.getHardwareMidiDeviceTransmitter(), System.currentTimeMillis());
            score.getNote().forEach(note -> System.out.println("Key: " + note.getKey() + ", Velocity: " + note.getVelocity() + ", Tick: " + note.getTick()));

            // デバイスをクローズ
            MidiDeviceHandler.midiDeviceCloser();

            // MIDIファイルを再生し、判定を行う
            MidiHandler.playMidiFile(MidiDeviceHandler.getHardwareMidiDeviceReceiver(), new File("assets/output.mid"));
            // デバイスをクローズ
            MidiDeviceHandler.midiDeviceCloser();

            // isJudge = true;
            // while (MidiHandler.startJudgement(MidiDeviceHandler.getHardwareMidiDeviceTransmitter(), score, System.currentTimeMillis())) {
            // if(!isJudge) {
            // break;
            // }
            // }

            ArrayList<Note> notes = MidiHandler.recordNotes(MidiDeviceHandler.getHardwareMidiDeviceTransmitter(), System.currentTimeMillis());
            // デバイスをクローズ
            MidiDeviceHandler.midiDeviceCloser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
