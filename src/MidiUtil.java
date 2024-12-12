import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class MidiUtil {
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
  }
}