import javax.sound.midi.*;

public class GetMidi {
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

    public static void playNote(Receiver receiver, int channel, int note, int velocity, int ms)
            throws InvalidMidiDataException, InterruptedException {
        ShortMessage sm1 = new ShortMessage(ShortMessage.NOTE_ON, channel, note, velocity);
        receiver.send(sm1, -1);
        Thread.sleep(ms);
        ShortMessage sm2 = new ShortMessage(ShortMessage.NOTE_OFF, channel, note, 0);
        receiver.send(sm2, -1);
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
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        if (sm.getCommand() == ShortMessage.NOTE_ON) {
                            int key = sm.getData1(); // 鍵盤番号
                            int velocity = sm.getData2(); // ベロシティ（力の強さ）
                            if (velocity > 0) {
                                System.out.println("鍵盤が押されました: " + key+"鍵盤の強さは:" + velocity);
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
