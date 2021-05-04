package pedigree;

import java.util.Random;

/**
 * Class representing a person.
 *
 * @author Miklos Csuros (professor)
 *
 * Modified by Sandrine Bédard et Robin Legault
 */
public class Sim implements Comparable<Sim> {

    private final static Random RDM = new Random();

    private static int NEXT_SIM_IDX = 0;
    public static double MIN_MATING_AGE_F = 16.0;
    public static double MIN_MATING_AGE_M = 16.0;
    public static double MAX_MATING_AGE_F = 50.0; // Janet Jackson
    public static double MAX_MATING_AGE_M = 73.0; // Charlie Chaplin

    /**
     * Ordering by death date.
     *
     * @param o other pedigree.Sim
     * @return substraction of death times
     */
    @Override
    public int compareTo(Sim o) {
        if (this instanceof PA) {
            return Double.compare(this.getBirthTime(), o.getBirthTime());
        } else { return Double.compare(this.getDeathTime(), o.getDeathTime()); }
    }

    public enum Sex {F, M};

    private final int sim_ident;
    private double birthtime;
    private double deathtime;
    private Sim mother;
    private Sim father;
    private Sim mate;

    private Sex sex;

    /**
     * Constructor for founding pedigree.Sim
     */
    public Sim(Sex sex) {
        this(null, null, 0.0, sex);
    }

    /**
     * Regular constructors
     */
    public Sim(Sim mother, Sim father, double birth, Sex sex) {
        this.mother = mother;
        this.father = father;
        this.birthtime = birth;
        this.deathtime = Double.POSITIVE_INFINITY;
        this.sex = sex;

        this.sim_ident = NEXT_SIM_IDX++;
    }

    public Sim(Sim mother, Sim father, double birth, double death, Sex sex, int idx) {
        this.mother = mother;
        this.father = father;
        this.birthtime = birth;
        this.deathtime = death;
        this.sex = sex;

        this.sim_ident = idx;
    }

    /**
     * Getters
     */
    public Sex getSex() { return sex; }

    public int getIndent() { return this.sim_ident; }

    public double getBirthTime() { return birthtime; }

    public double getDeathTime() { return deathtime; }

    public Sim getMother() { return mother; }

    public Sim getFather() { return father; }

    public Sim getMate() { return mate; }

    private static String getIdentString(Sim sim) {
        return sim==null?"":"sim."+sim.sim_ident+"/"+sim.sex;
    }

    /**
     * Setters
     */
    public void setMate(Sim mate){ this.mate = mate; }

    public void setDeath(double death) { this.deathtime = death; }

    /**
     * Test methods
     */
    public boolean isFounder() { return (mother == null && father == null); }

    public boolean isAlive(double time) { return time < this.getDeathTime(); }

    public boolean isMale() { return sex.equals(Sex.M); }

    public boolean isFemale() { return sex.equals(Sex.F); }

    /**
     * If this Sim is of mating age at the given time
     *
     * @param time
     * @return true if alive, sexually mature and not too old
     */
    public boolean isMatingAge(double time) {
        if (time < getDeathTime()) {
            double age = time - getBirthTime();
            return
                    Sex.F.equals(getSex())
                            ? age>=MIN_MATING_AGE_F && age <= MAX_MATING_AGE_F
                            : age>=MIN_MATING_AGE_M && age <= MAX_MATING_AGE_M;
        } else
            return false; // No mating with dead people
    }

    public boolean isInARelationship(double time) {
        return mate != null && mate.getDeathTime() > time
                && mate.getMate()== this;
    }

    /**
     * To get a random sex
     * @return random sex
     */
    public static Sex randomSex() {

        int random = RDM.nextInt(2);
        if (random == 0) {
            return Sex.F;
        } else {
            return Sex.M;
        }
    }

    @Override
    public String toString() {
        return getIdentString(this)+" [" + (int) birthtime + ", " + (int) deathtime +
                ", mate " + getIdentString(mate) + "\tmom "+getIdentString(getMother())+"\tdad "+getIdentString(getFather()) + "]";
    }
}