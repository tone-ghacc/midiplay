import java.util.ArrayList;

public class Score {
    ArrayList<Note> notes;

    Score(Note note) {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(note);
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public ArrayList<Note> getNote() {
        return notes;
    }
}
