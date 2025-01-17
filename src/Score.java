import java.util.ArrayList;

public class Score {
    private ArrayList<Note> notes;
    private int judNum;

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

    public boolean judge(Note inputNote) {
        if(judNum < notes.size() && notes.get(judNum).key == inputNote.key) {
            judNum++;
            return true;
        }
        return false;
    }
}
