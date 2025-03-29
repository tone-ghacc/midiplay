import java.io.File;
import java.io.IOException;
import javax.sound.midi.*;

public class MidiRecorder implements Receiver {
    private static MidiRecorder instance = new MidiRecorder();
    private static long startTime = 0;
    private static boolean isRecording = false;
    private static boolean isTeacher = false;
    private static boolean isClear = false;
    private Score score = new Score();
    private Sequence sequence;
    private Track track;

    private MidiRecorder() {
        System.out.println("MidiRecorderインスタンスを作成しました。");
    }

    public static MidiRecorder getInstance() {
        startTime = System.currentTimeMillis();
        return instance;
    }

    public void startTeacherRecording() {
        try {
            sequence = new Sequence(Sequence.PPQ, 24);
            track = sequence.createTrack();
            MetaMessage bpmchange = getTempoMessage(240);
            track.add(new MidiEvent(bpmchange, 0));
            isRecording = true;
            isTeacher = true;
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void startStudentRecording() {
        isRecording = true;
        isTeacher = false;
    }

    public void stopRecording() {
        isRecording = false;
    }

    public void saveMidiFile(String filename) {
        isRecording = false;
        isTeacher = false;
        try {
            File midiFile = new File(filename);
            MidiSystem.write(sequence, 1, midiFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("MIDIファイルを保存しました。");
    }

    public boolean getJudgement() {
        return isClear;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if((message instanceof ShortMessage) && isRecording) {
            ShortMessage sm = (ShortMessage) message;
            int command = sm.getCommand();
            int key = sm.getData1();
            int velocity = sm.getData2();

            // イベントのタイミングを計算
            long tick = (System.currentTimeMillis() - startTime) / 10;
            if(isTeacher) {
                try {
                    if(command == ShortMessage.NOTE_ON && velocity > 0) {
                        System.out.println("キー: " + key + ", ベロシティ: " + velocity);
                        track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, key, velocity), tick));
                        score.addNote(new Note(key, velocity, tick));
                    } else if(command == ShortMessage.NOTE_OFF || (command == ShortMessage.NOTE_ON && velocity == 0)) {
                        track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, key, velocity), tick));
                    }
                } catch (InvalidMidiDataException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if(command == ShortMessage.NOTE_ON && velocity > 0) {
                        System.out.println("キー: " + key + ", ベロシティ: " + velocity);
                        if(score.judge(new Note(key, velocity, tick))) {
                            System.out.println("正解！");
                            if(score.getJudNum() == score.getNote().size()) {
                                System.out.println("全問正解！");
                                isClear = true;
                                notifyMain();
                            }
                        } else {

                            System.out.println("不正解...\nもう一度MIDIを再生するね！");
                            isClear = false;
                            notifyMain();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    private void notifyMain() {
        synchronized (StudyPiano.class) {
            StudyPiano.class.notify();
        }
    }

    private MetaMessage getTempoMessage(double bpm) {
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
