import javax.sound.midi.*;

public class MidifPlay {
    public static void main(String[] args) {
        try {
            // 1. MIDIファイルを読み込む
            Sequence sequence = MidiSystem.getSequence(MidifPlay.class.getResource("sample.mid"));

            // 2. シーケンサーを取得
            Sequencer sequencer = MidiSystem.getSequencer(false); // false: デフォルトシーケンサーを使用しない
            sequencer.open();

            // 3. MIDIデバイス（電子ピアノなど）を取得
            MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
            for (MidiDevice.Info info : devices) {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                if (device.getMaxReceivers() != 0) {
                    System.out.println("Using device: " + info.getName());
                    device.open();
                    Transmitter transmitter = sequencer.getTransmitter();
                    Receiver receiver = device.getReceiver();
                    transmitter.setReceiver(receiver);
                    break;
                }
            }

            // 4. シーケンスをシーケンサーにセットして再生
            sequencer.setSequence(sequence);
            sequencer.start();

            System.out.println("Playback started...");

            // 再生が終わるまで待つ
            while (sequencer.isRunning()) {
                Thread.sleep(1000);
            }

            // 終了処理
            sequencer.stop();
            sequencer.close();
            System.out.println("Playback completed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
