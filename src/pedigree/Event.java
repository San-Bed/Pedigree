package pedigree;

/**
 * Class to store an event. Used in the priority queue of events.
 *
 * @author Sandrine Bédard et Robin Legault
 */
public class Event implements Comparable<Event> {

    public enum Type { Birth, Reproduction, Death }

    private final Sim subject;
    private final Type type;
    private final double time;

    /**
     * Constructor
     */
    public Event(final Sim subject, final Type type, final double time) {
        this.subject = subject;
        this.type = type;
        this.time = time;
    }

    /**
     * Getters
     */
    public Sim getSubject() {
        return this.subject;
    }

    public Type getType() {
        return this.type;
    }

    public double getTime() {
        return this.time;
    }

    @Override
    public int compareTo(Event o) {
        return Double.compare(this.getTime(), o.getTime());
    }


    @Override
    public String toString() {
        return "{ "+ "subject: " + this.getSubject().toString() + ", " +
                "type: " + this.getType() + ", " +
                "time: " + (int) this.getTime() +
                " }";
    }
}
