import java.io.File;

public class StudyPiano {
    private static boolean isJudge;

    public static void main(String[] args) {
        try {
            // scoreのインスタンスを生成
            Score score = MidiHandler.recordMidiScore(MidiDeviceHandler.getHardwareMidiDeviceTransmitter(), System.currentTimeMillis());
            score.notes.forEach(note -> System.out.println("Key: " + note.getKey() + ", Velocity: " + note.getVelocity() + ", Tick: " + note.getTick()));

            // MIDIファイルを再生し、判定を行う
            isJudge = true;
            while (isJudge) {
                MidiHandler.playMidiFile(MidiDeviceHandler.getHardwareMidiDeviceReceiver(), new File("assets/output.mid"));
                isJudge = MidiHandler.startJudgement(MidiDeviceHandler.getHardwareMidiDeviceTransmitter(), score, System.currentTimeMillis());
            }

            // デバイスをクローズ
            MidiDeviceHandler.midiDeviceCloser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
