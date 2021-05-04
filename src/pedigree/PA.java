package pedigree;

/**
 * Class representing an ancestor. Used in coalescence to order the ancestors by birth date.
 *
 * @author Sandrine Bédard et Robin Legault
 */
public class PA extends Sim {

    /**
     * Constructor
     * @param sim Person that will be converted to an ancestor
     */
    public PA(Sim sim) {
        super(sim.getMother(), sim.getFather(), sim.getBirthTime(), sim.getDeathTime(), sim.getSex(), sim.getIndent());
    }
}

