import java.util.ArrayList;

public class Score {
    ArrayList<Note> notes;
    int length;

    Score(Note note) {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(note);
        length = 0;
    }

    public void addNote(Note note) {
        notes.add(note);
        length++;
    }

    public ArrayList<Note> getNote() {
        return notes;
    }

    public boolean Judge(Note note) {
        if (notes.get(length).getNote() == note.getNote()) {
            return true;
        } else {
            return false;
        }
    }
}
