import javax.sound.midi.*;

public class MidiRecorder {

    public static void printMidiDevices() {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        System.out.println("MidiDevice.Info : " + infos.length + " item(s)");
        for (int i = 0; i < infos.length; i++) {
            System.out.println(" MidiDevice.Info[" + i + "]");
            System.out.println("  Name         : " + infos[i].getName());
            System.out.println("  Version      : " + infos[i].getVersion());
            System.out.println("  Vendor       : " + infos[i].getVendor());
            System.out.println("  Description  : " + infos[i].getDescription());
            MidiDevice device;
            try {
                device = MidiSystem.getMidiDevice(infos[i]);
            } catch (MidiUnavailableException ex) {
                throw new IllegalStateException(ex);
            }
            System.out.println("  Receivers    : " + device.getMaxReceivers());
            System.out.println("  Transmitters : " + device.getMaxTransmitters());
        }
    }

    public static void main(String[] args) {
        printMidiDevices();
        try {
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
                      
            // トランスミッターを設定
            Transmitter transmitter = device.getTransmitter();
            transmitter.setReceiver(new Receiver() {
                @Override
                public void send(MidiMessage message, long timeStamp) {
                    // シーケンスとトラックを作成
                    Sequence sequence = new Sequence(Sequence.PPQ, 24);
                    Track track = sequence.createTrack();

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

                @Override
                public void close() {
                }
            });

            System.out.println("MIDIリスナーが開始されました。");
            System.in.read(); // Enterキーが押されるまで実行



            // デバイスをクローズ
            device.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

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