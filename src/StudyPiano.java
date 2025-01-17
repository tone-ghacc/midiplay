import java.io.File;
import javax.sound.midi.*;

public class StudyPiano {
    private static boolean isJudge;

    public static void main(String[] args) {
        try {
            // scoreのインスタンスを生成
            Transmitter transmitter = MidiDeviceHandler.getHardwareMidiDeviceTransmitter();
            Score score = MidiHandler.recordMidiScore(transmitter, System.currentTimeMillis());

            isJudge = true;
            while (isJudge) {
                MidiHandler.playMidiFile(transmitter, new File("assets/output.mid"));
                isJudge = MidiHandler.startJudgement(transmitter, score, System.currentTimeMillis());
            }

            // デバイスをクローズ
            MidiDeviceHandler.midiDeviceCloser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
