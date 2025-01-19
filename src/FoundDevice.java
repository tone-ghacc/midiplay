import javax.sound.midi.*;

public class FoundDevice {
    public static void main(String[] args) {
        try {
            // 利用可能なMIDIデバイスを列挙
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            for (MidiDevice.Info info : infos) {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                System.out.println("Device: " + info.getName() + ", Transmitter Supported: " + device.getMaxTransmitters() + ", Receiver Supported: " + device.getMaxReceivers());
            }
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }
}
