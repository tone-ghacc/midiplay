import javax.sound.midi.*;
import java.io.File;

public class MidiPlayer {
    private Receiver receiver;

    // Constructor
    public MidiPlayer(Receiver receiver) {
        this.receiver = receiver;
    }

    // Method to play the MIDI file
    public void playMidiFile(File midiFile) {
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

    // Main method for testing
    public static void main(String[] args) {
        try {
            // Create an instance of MidiPlayer
            MidiPlayer player = new MidiPlayer(MidiDeviceHandler.getHardwareMidiDeviceReceiver());

            // Provide a .mid file to play
            File midiFile = new File("assets/output.mid");
            player.playMidiFile(midiFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
