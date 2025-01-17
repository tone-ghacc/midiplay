import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.midi.*;

public class MidiHandler {
    public static Score recordMidiScore(Transmitter transmitter, long startTime) {
        Score score = new Score();
        Sequence sequence;
        Track track;
        try {
            sequence = new Sequence(Sequence.PPQ, 24);
            track = sequence.createTrack();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            return null; // or handle the error appropriately
        }

        MetaMessage bpmchange = getTempoMessage(240);
        track.add(new MidiEvent(bpmchange, 0));

        // Create a Receiver to handle MIDI messages
        Receiver receiver = new Receiver() {
            @Override
            public void send(MidiMessage message, long timeStamp) {
                if(message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    int command = sm.getCommand();
                    int key = sm.getData1();
                    int velocity = sm.getData2();

                    // イベントのタイミングを計算
                    long tick = (System.currentTimeMillis() - startTime) / 10;

                    try {
                        if(command == ShortMessage.NOTE_ON && velocity > 0) {
                            System.out.println("鍵盤が押されました: " + key);
                            track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, key, velocity), tick));
                            score.addNote(new Note(key, velocity, tick));
                        } else if(command == ShortMessage.NOTE_OFF || (command == ShortMessage.NOTE_ON && velocity == 0)) {
                            System.out.println("鍵盤が離されました: " + key);
                            track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, key, velocity), tick));
                        }
                    } catch (InvalidMidiDataException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void close() {
            }
        };

        // Connect the Transmitter to the custom Receiver
        transmitter.setReceiver(receiver);

        // Start a thread to listen for the Enter key to terminate
        Thread inputThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Press Enter to stop recording...");
                scanner.nextLine();
                transmitter.close();
                System.out.println("Recording stopped.");
            }
        });

        inputThread.start();

        try {
            // Wait for the input thread to complete
            inputThread.join();
        } catch (InterruptedException e) {
            System.err.println("Input thread was interrupted: " + e.getMessage());
        }

        saveMidiFile(sequence, "assets/output.mid");

        return score;
    }

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

    public static boolean startJudgement(Transmitter transmitter, Score score, long startTime) {
        final boolean[] isJudge = { true };
        try {
            Receiver receiver = new Receiver() {
                @Override
                public void send(MidiMessage message, long timeStamp) {
                    if(message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        if(sm.getCommand() == ShortMessage.NOTE_ON) {
                            int key = sm.getData1();
                            int velocity = sm.getData2();
                            long tick = (System.currentTimeMillis() - startTime) / 10;

                            Note receivedNote = new Note(key, velocity, tick);

                            if(score.judge(receivedNote)) {
                                System.out.println("Correct note received. Waiting for the next note...");
                            } else {
                                System.out.println("Incorrect note! Ending program.");
                                isJudge[0] = false;
                                transmitter.close();
                            }
                        }
                    }
                }

                @Override
                public void close() {
                    System.out.println("Receiver closed.");
                }
            };

            transmitter.setReceiver(receiver);
            System.out.println("Listening for MIDI input...");
        } catch (Exception e) {
            System.err.println("Error initializing MIDI receiver: " + e.getMessage());
            e.printStackTrace();
        }
        return isJudge[0];
    }

    private static void saveMidiFile(Sequence sequence, String filename) {
        try {
            File midiFile = new File(filename);
            MidiSystem.write(sequence, 1, midiFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MetaMessage getTempoMessage(double bpm) {
        long mpq = Math.round(60000000d / bpm);
        byte[] data = new byte[3];
        data[0] = (byte) (mpq / 0x10000);
        data[1] = (byte) ((mpq / 0x100) % 0x100);
        data[2] = (byte) (mpq % 0x100);
        try {
            return new MetaMessage(0x51, data, data.length);
        } catch (InvalidMidiDataException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
