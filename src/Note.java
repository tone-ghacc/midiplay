public class Note {
    int note;
    int velocity;
    int tick;

    public int getNote() {
        return this.note;
    }

    public int getVelocity() {
        return this.velocity;
    }

    public int getTick() {
        return this.tick;
    }

    Note(int note, int velocity, int tick) {
        this.note = note;
        this.velocity = velocity;
        this.tick = tick;
    }
}
