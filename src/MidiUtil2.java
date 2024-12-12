/*
 *  mocha-java.com
 */

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
/**
 *
 * @author minaberger
 */
public class MidiUtil2 {
  public static void playNote(Receiver receiver, int channel, int note, int velocity, int ms) throws InvalidMidiDataException, InterruptedException {
    ShortMessage sm1 = new ShortMessage(ShortMessage.NOTE_ON, channel, note, velocity);
    receiver.send(sm1, -1);
    Thread.sleep(ms);
    ShortMessage sm2 = new ShortMessage(ShortMessage.NOTE_OFF, channel, note, 0);
    receiver.send(sm2, -1);
  }
  public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
    Synthesizer synthesizer = MidiSystem.getSynthesizer();
    synthesizer.open();
    playNote(synthesizer.getReceiver(), 0, 60, 127, 1000);
    synthesizer.close();
  }
}