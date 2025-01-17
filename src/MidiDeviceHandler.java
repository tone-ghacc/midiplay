import javax.sound.midi.*;

public class MidiDeviceHandler {

    public static Transmitter getHardwareMidiDeviceTransmitter() throws MidiUnavailableException {
        // MIDIデバイス情報をすべて取得
        MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info info : midiDeviceInfos) {
            MidiDevice device = MidiSystem.getMidiDevice(info);

            // ハードウェアMIDIデバイスかどうかを確認
            if(!(device instanceof Sequencer) && !(device instanceof Synthesizer)) {
                // デバイスがTransmitterをサポートしているか確認
                if(device.getMaxTransmitters() != 0) {
                    try {
                        // デバイスを開く
                        device.open();

                        // Transmitterを取得して返す
                        Transmitter transmitter = device.getTransmitter();
                        System.out.println("Found hardware MIDI device: " + info.getName());
                        return transmitter;
                    } catch (MidiUnavailableException e) {
                        System.err.println("Failed to access Transmitter for device: " + info.getName());
                    }
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

            // ハードウェアMIDIデバイスをチェック
            if(device.getMaxReceivers() != 0) { // 最大受信数を確認
                device.open(); // デバイスをオープン
                Receiver receiver = device.getReceiver(); // Receiverを取得
                System.out.println("Found MIDI device: " + info.getName());
                return receiver; // Receiverを返す
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
                    System.out.println("MIDI device is not open: " + info.getName());
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