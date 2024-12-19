import javax.sound.midi.*;

public class MidiRecorder {

    public static void main(String[] args) {
        try {
            // シーケンスとトラックを作成
            Sequence sequence = new Sequence(Sequence.PPQ, 24);
            Track track = sequence.createTrack();
            
            // MIDIメッセージ: ノートオン（例: C4）
            ShortMessage noteOn = new ShortMessage();
            noteOn.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);  // 60はC4のノート番号、93は音量
            MidiEvent noteOnEvent = new MidiEvent(noteOn, 0);  // 0ティック目にノートオン
            track.add(noteOnEvent);
            
            // ノートオフ（例: 500ティック後）
            ShortMessage noteOff = new ShortMessage();
            noteOff.setMessage(ShortMessage.NOTE_OFF, 0, 60, 0);  // 60はC4、0は音量
            MidiEvent noteOffEvent = new MidiEvent(noteOff, 500);  // 500ティック後にノートオフ
            track.add(noteOffEvent);
            
            // MIDIファイルを保存
            MidiSystem.write(sequence, 1, new java.io.File("output.mid"));
            
            System.out.println("MIDIファイルを保存しました。");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}