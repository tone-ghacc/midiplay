import java.io.File;
import javax.sound.midi.*;

public class MidiHandler {
    public static void playMidiFile(Receiver receiver, File midiFile) {
        try {
            // Obtain a Sequencer instance
            Sequencer sequencer = MidiSystem.getSequencer(false); // Do not use the default device
            sequencer.open();

            // Set the custom Receiver
            Transmitter transmitter = sequencer.getTransmitter();
            transmitter.setReceiver(receiver);

            // Load the MIDI file into the sequencer
            Sequence sequence = MidiSystem.getSequence(midiFile);
            sequencer.setSequence(sequence);

            // Start playback
            sequencer.start();

            // Wait for the playback to finish
            while (sequencer.isRunning()) {
                Thread.sleep(100); // Check status periodically
            }

            // Close resources
            sequencer.stop();
            sequencer.close();
        } catch (Exception e) {
            System.err.println("Error while playing MIDI file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
