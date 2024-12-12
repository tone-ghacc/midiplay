import javax.sound.midi.*;

public class GetMidi {
    public static void main(String[] args) {
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
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        if (sm.getCommand() == ShortMessage.NOTE_ON) {
                            int key = sm.getData1(); // 鍵盤番号
                            int velocity = sm.getData2(); // ベロシティ（力の強さ）
                            if (velocity > 0) {
                                System.out.println("鍵盤が押されました: " + key);
                            }
                        } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                            int key = sm.getData1();
                            System.out.println("鍵盤が離されました: " + key);
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
    }
}
