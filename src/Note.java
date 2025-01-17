public class Note {
    int key;
    int velocity;
    long tick;

    public int getKey() {
        return this.key;
    }

    public int getVelocity() {
        return this.velocity;
    }

    public long getTick() {
        return this.tick;
    }

    Note(int key, int velocity, long tick) {
        this.key = key;
        this.velocity = velocity;
        this.tick = tick;
    }
}
