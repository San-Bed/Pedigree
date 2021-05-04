package pedigree;

import java.util.ArrayList;
import java.util.Random;

import pedigree.Event.Type;

/**
 * Main class of the program. Manages the population and events occurring.
 *
 * @author Sandrine Bédard et Robin Legault
 */
public class Simulation {

    private final AgeModel ageModel; // includes 3 parameters: accident_rate, death_rate and age_factor
    private final PQ<Event> eventQ;
    private final PQ<Sim> womenQ;
    private final PQ<Sim> menQ;
    private final Random RND;
    private final double r; // 4th parameter: rate of reproduction
    private final double fidelity; // 5th parameter: fidelity
    private double currentTime;

    // For the empirical study
    private int nextCentury;
    private ArrayList<Integer> populationHistory;
    private ArrayList<Integer> timeHistory;

    // Default parameters
    private static final double DEFAULT_FIDELITY = 0.9;
    private static final double REPRODUCTION = 2.0;

    /**
     * Basic constructor
     */
    public Simulation() {
        this.ageModel = new AgeModel();
        this.eventQ = new PQ<>(4, PQ.Type.MIN); // min heap, ordering by currentTime of event
        this.womenQ = new PQ<>(4, PQ.Type.MIN);
        this.menQ = new PQ<>(4, PQ.Type.MIN);
        this.RND = new Random();
        this.fidelity = DEFAULT_FIDELITY;
        this.r = REPRODUCTION / ageModel.expectedParenthoodSpan(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        this.populationHistory = new ArrayList();
        this.timeHistory = new ArrayList();
        this.nextCentury = 0;
    }

    /**
     * Regular constructor (with all 5 modifiable parameters)
     */
    public Simulation(double accident_rate, double death_rate, double age_scale, double reproduction, double fidelity) {
        this.ageModel = new AgeModel(accident_rate, death_rate, age_scale);
        this.eventQ = new PQ<>(4, PQ.Type.MIN); // min heap, ordering by currentTime of event
        this.womenQ = new PQ<>(4, PQ.Type.MIN);
        this.menQ = new PQ<>(4, PQ.Type.MIN);
        this.RND = new Random();
        this.fidelity = fidelity;
        this.r = reproduction / ageModel.expectedParenthoodSpan(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        this.populationHistory = new ArrayList();
        this.timeHistory = new ArrayList();
        this.nextCentury = 0;
    }

    /**
     * Getters
     */
    public PA[] getMenArray() {
        int nMen = this.menQ.size();
        PA[] menArray = new PA[nMen];
        for (int i = 0; i < nMen; i++) {
            menArray[i] = new PA(menQ.getElement(i));
        }
        return menArray;
    }

    public PA[] getWomenArray() {
        int nWomen = this.womenQ.size();
        PA[] womenArray = new PA[nWomen];
        for (int i = 0; i < nWomen; i++) {
            womenArray[i] = new PA(womenQ.getElement(i));
        }
        return womenArray;
    }

    public ArrayList getPopulationHistory() {
        return this.populationHistory;
    }

    public ArrayList getTimeHistory() {
        return this.timeHistory;
    }

    /**
     * Sets the time of the next reproduction
     *
     * @param x female Sim
     */
    private void nextReproduction(Sim x) {
        if (x.isMale()) {
            return;
        }
        double waitingTime = AgeModel.randomWaitingTime(RND, r);
        Event reproduction = new Event(x, Type.Reproduction, currentTime + waitingTime);
        eventQ.insert(reproduction);
    }

    /**
     * Handles the birth of a sim
     *
     * @param E birth of a Sim
     */
    private void birth(Event E) {
        //[n1]
        Sim x = E.getSubject();
        double birthTime = x.getBirthTime();

        double lifespan = ageModel.randomAge(RND);
        double deathTime = birthTime + lifespan;
        Event death = new Event(x, Type.Death, deathTime);
        x.setDeath(deathTime);
        eventQ.insert(death);
        //[n2]
        if (x.isFemale()) {
            nextReproduction(x);
        }
        //[n3]
        if (x.isFemale()) {
            womenQ.insert(x);
        } else {
            menQ.insert(x);
        }
    }

    /**
     * Removes all dead Sims from the population
     */
    private void death() {
        while (!menQ.isEmpty() && menQ.peek().getDeathTime() <= currentTime) {
            menQ.delete();
        }
        while (!womenQ.isEmpty() && womenQ.peek().getDeathTime() <= currentTime) {
            womenQ.delete();
        }
    }

    /**
     * Selects an appropriate father for a sim
     *
     * @param x female wanting to reproduce
     * @return male sim chosen to be the father
     */
    private Sim selectFather(Sim x) {
        // [p1]
        if (menQ.isEmpty()) {
            return null;
        }
        if (x.isInARelationship(currentTime)) {
            Sim z = x.getMate();

            // [p1.1]
            if (RND.nextDouble() < fidelity) {
                return z;
            } // [p1.2]
            else {
                //make sure a new potential mate exists (different from z and in mating age)
                boolean potentialMateExists = false;
                for (int i = 0; i < menQ.size(); i++) {
                    Sim potentialMate = menQ.getElement(i);
                    if (potentialMate.isMatingAge(currentTime) && potentialMate.getIndent() != z.getIndent()) {
                        potentialMateExists = true;
                        break;
                    }
                }
                if (!potentialMateExists) {
                    return null;
                }

                while (true) { //select a new mate (different from z)
                    Sim y = menQ.getElement(RND.nextInt(menQ.size()));
                    if (!y.equals(x.getMate()) && y.isMatingAge(currentTime)) {
                        return y;
                    }
                }
            }
        }
        // [p2]
        while (true) { //select a mate
            Sim y = menQ.getElement(RND.nextInt(menQ.size()));
            if (!y.isInARelationship(currentTime)) {
                return y;
            } else if (RND.nextDouble() > fidelity) {
                return y;
            }
        }
    }

    /**
     * Handles the reproduction between a female sim and her partner
     *
     * @param x female sim
     */
    private void reproduction(Sim x) {
        //[r1]
        if (!x.isAlive(currentTime)) {
            return;
        }
        //[r2]
        if (x.isMatingAge(currentTime)) {
            Sim y = selectFather(x);
            if (y != null) {
                y.setMate(x);
                x.setMate(y);
                Sim child = new Sim(x, y, currentTime, Sim.randomSex());
                Event birth = new Event(child, Type.Birth, currentTime);
                eventQ.insert(birth);
            }
        }
        //[r3]
        nextReproduction(x);
    }

    /**
     * Treats the event according to its type (birth, reproduction and death)
     *
     * @param E event to handled
     */
    public void treatEvent(Event E) {
        if (E.getTime() > this.nextCentury) {
            // Save the population size at a certain time (once per century)
            // Used in the empirical study
            saveSample();
        }
        Type eventType = E.getType();
        if (eventType.equals(Type.Reproduction)) {
            reproduction(E.getSubject());
        } else if (eventType.equals(Type.Death)) {
            death();
        } else if (eventType.equals(Type.Birth)) {
            birth(E);
        }
    }

    /**
     * Generates a population of founder Sim at time 0
     *
     * @param n population size
     */
    public void generateFounders(int n) {
        for (int i = 0; i < n; i++) {
            Sim founder = new Sim(Sim.randomSex());
            Event birth = new Event(founder, Type.Birth, 0);
            eventQ.insert(birth);
        }
    }

    /**
     * Records the population size at a certain time as a sample for the
     * empirical study
     */
    public void saveSample() {
        this.timeHistory.add((int)currentTime);
        this.populationHistory.add(this.menQ.size() + this.womenQ.size());
        nextCentury += 100;
    }

    /**
     * Main method of the program. Treats events by order of priority for the
     * duration of the simulation
     *
     * @param n number of founding sims
     * @param Tmax duration of the simulation
     */
    void simulate(int n, double Tmax) {
        // Creates the founding sims
        generateFounders(n);

        while (!eventQ.isEmpty()) {
            Event E = eventQ.delete(); // next event
            currentTime = E.getTime();

            if (currentTime > Tmax) {
                saveSample();
                break; // stop at Tmax
            }
            treatEvent(E);
        }
    }
}
