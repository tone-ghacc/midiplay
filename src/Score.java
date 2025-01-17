import java.util.ArrayList;

public class Score {
    ArrayList<Note> notes;
    int judNum;

    Score() {
        notes = new ArrayList<>();
        judNum = 0;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public ArrayList<Note> getNote() {
        return notes;
    }

    public boolean Judge(Note note) {
        if (notes.get(judNum).getKey() == note.getKey()) {
            judNum++;
            return true;
        } else {
            return false;
        }
    }
}
