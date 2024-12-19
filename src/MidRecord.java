import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidRecord {
    private static Sequence sequence;
    private static Track track;
    private static long startTime;
    private static MidiMessage bpmchange;

    public static void main(String[] args) {
        try {
            // MIDIシーケンスとトラックの作成
            sequence = new Sequence(Sequence.PPQ, 24);
            track = sequence.createTrack();
            startTime = System.currentTimeMillis();

            bpmchange = getTempoMessage(60);
            track.add(new MidiEvent(bpmchange, 0));

            // MIDIデバイスの取得
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            MidiDevice device = null;

            for (MidiDevice.Info info : infos) {
                device = MidiSystem.getMidiDevice(info);
                if (device.getMaxTransmitters() != 0) {
                    System.out.println("MIDIデバイスが見つかりました: " + info.getName());
                    break;
                }
            }

            if (device == null) {
                System.out.println("MIDIデバイスが見つかりません。");
                return;
            }

            // デバイスをオープン
            device.open();

            // トランスミッターの設定
            Transmitter transmitter = device.getTransmitter();
            transmitter.setReceiver(new Receiver() {
                @Override
                public void send(MidiMessage message, long timeStamp) {
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        int command = sm.getCommand();
                        int key = sm.getData1();
                        int velocity = sm.getData2();

                        // イベントのタイミングを計算
                        long tick = (System.currentTimeMillis() - startTime) / 10;

                        try {
                            if (command == ShortMessage.NOTE_ON && velocity > 0) {
                                System.out.println("鍵盤が押されました: " + key);
                                track.add(
                                        new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, key, velocity), tick));
                            } else if (command == ShortMessage.NOTE_OFF
                                    || (command == ShortMessage.NOTE_ON && velocity == 0)) {
                                System.out.println("鍵盤が離されました: " + key);
                                track.add(
                                        new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, key, velocity), tick));
                            }
                        } catch (InvalidMidiDataException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void close() {
                }
            });

            System.out.println("録音を開始しました。Enterキーを押すと停止します。");
            System.in.read(); // Enterキーが押されるまで実行

            // MIDIファイルの保存
            saveMidiFile(sequence, "output.mid");
            System.out.println("録音が完了しました。output.midに保存しました。");

            // デバイスをクローズ
            device.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MetaMessage getTempoMessage(double bpm) {
        long mpq = Math.round(60000000d / bpm);
        byte[] data = new byte[3];
        data[0] = (byte) (mpq / 0x10000);
        data[1] = (byte) ((mpq / 0x100) % 0x100);
        data[2] = (byte) (mpq % 0x100);
        try {
            return new MetaMessage(0x51, data, data.length);
        } catch (InvalidMidiDataException ex) {
            throw new IllegalStateException(ex);
        }
    }

    // MIDIファイルを保存するメソッド
    private static void saveMidiFile(Sequence sequence, String filename) {
        try {
            File midiFile = new File(filename);
            MidiSystem.write(sequence, 1, midiFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}