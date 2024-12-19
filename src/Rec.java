import javax.sound.midi.*;
public class Rec implements Receiver {

    Sequence sequence;
    Track track;

    public void send(MidiMessage message, long timeStamp) {
        // シーケンスとトラックを作成
        //Sequence sequence = new Sequence(Sequence.PPQ, 24);
        //Track track = sequence.createTrack();

        if (message instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage) message;
            if (sm.getCommand() == ShortMessage.NOTE_ON) {
                int key = sm.getData1(); // 鍵盤番号
                int velocity = sm.getData2(); // ベロシティ（力の強さ）
                 if (velocity > 0) {
                    System.out.println("鍵盤が押されました: " + key+"鍵盤の強さは:" + velocity);
                    MidiEvent noteOnEvent = new MidiEvent(sm, 0);  // 0ティック目にノートオン
                    track.add(noteOnEvent);
                }
            } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                int key = sm.getData1();
                System.out.println("鍵盤が離されました: " + key);
                MidiEvent noteOffEvent = new MidiEvent(sm, 500);  // 500ティック後にノートオフ
                track.add(noteOffEvent);
            }
        }
    }

    public void close() {
    }

    Rec(){
        sequence = new Sequence(Sequence.PPQ, 24);
        track = sequence.createTrack();
    }

    
}