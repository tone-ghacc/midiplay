import javax.sound.midi.*;

public class MidiDeviceHandler {

    public static Transmitter getHardwareMidiDeviceTransmitter() throws MidiUnavailableException {
        // システムのすべてのMIDIデバイスを取得
        MidiDevice.Info[] midiDevices = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info info : midiDevices) {
            MidiDevice device = MidiSystem.getMidiDevice(info);

            // シーケンサーとシンセサイザーとWindowsを除外
            if(!(device instanceof Sequencer) && !(device instanceof Synthesizer) && !(info.getName().contains("Microsoft"))) {
                // ハードウェアMIDIデバイスをチェック
                if(device.getMaxTransmitters() != 0) { // 最大送信数を確認
                    device.open(); // デバイスをオープン
                    Transmitter transmitter = device.getTransmitter(); // Transmitterを取得
                    System.out.println("Found hardware MIDI device: " + info.getName());
                    return transmitter; // Transmitterを返す
                }
            }
        }

        throw new MidiUnavailableException("No hardware MIDI device with a Transmitter found.");
    }

    public static Receiver getHardwareMidiDeviceReceiver() throws MidiUnavailableException {
        // システムのすべてのMIDIデバイスを取得
        MidiDevice.Info[] midiDevices = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info info : midiDevices) {
            MidiDevice device = MidiSystem.getMidiDevice(info);

            // シーケンサーとシンセサイザーとWindowsを除外
            if(!(device instanceof Sequencer) && !(device instanceof Synthesizer) && !(info.getName().contains("Microsoft"))) {
                // ハードウェアMIDIデバイスをチェック
                if(device.getMaxReceivers() != 0) { // 最大受信数を確認
                    device.open(); // デバイスをオープン
                    Receiver receiver = device.getReceiver(); // Receiverを取得
                    System.out.println("Found hardware MIDI device: " + info.getName());
                    return receiver; // Receiverを返す
                }
            }
        }

        throw new MidiUnavailableException("No hardware MIDI devices found.");
    }

    public static void midiDeviceCloser() {
        // MIDIデバイスのリストを取得
        MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info info : devices) {
            try {
                // MIDIデバイスを取得
                MidiDevice device = MidiSystem.getMidiDevice(info);

                // デバイスがオープン状態であれば閉じる
                if(device.isOpen()) {
                    System.out.println("Closing open MIDI device: " + info.getName());
                    device.close();
                } else {
                    // System.out.println("MIDI device is not open: " + info.getName());
                }
            } catch (MidiUnavailableException e) {
                System.err.println("Could not access MIDI device: " + info.getName());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Receiver receiver = getHardwareMidiDeviceReceiver();
            System.out.println("Receiver obtained: " + receiver);
            // 必要に応じて、Receiverを使用してMIDIメッセージを送信可能
        } catch (MidiUnavailableException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}