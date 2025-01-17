import javax.sound.midi.*;

public class FoundDevice {
    public static void main(String[] args) {
        try {
            // 利用可能なMIDIデバイスを列挙
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            for (MidiDevice.Info info : infos) {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                System.out.println("Device: " + info.getName() + ", Transmitter Supported: " + device.getMaxTransmitters());
            }

            // トランスミッターが利用可能なデバイスを探す
            for (MidiDevice.Info info : infos) {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                if(!device.getTransmitters().isEmpty()) {
                    device.open();
                    // Transmitter transmitter = device.getTransmitter();
                    System.out.println("Found transmitter on: " + info.getName());
                    // 必要な処理を実行
                    device.close();
                    break;
                }
            }
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }
}
