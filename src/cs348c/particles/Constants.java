package cs348c.particles;

/**
 * Default constants.
 * 
 * @author Doug James, February 2009
 */
public interface Constants
{
    /** Mass-proportional damping. */
    public static final double DAMPING_MASS      = .5; 

    /** Mass of a particle. */
    public static final double PARTICLE_MASS     = 1.0;

    /** Spring stretching stiffness. */
    public static final double STIFFNESS_STRETCH = 10100.0; 

    /** Spring bending stiffness. */
    public static final double STIFFNESS_BEND    = 5.; 
    
    public static final double PENALTY_STIFFNESS = 60;
    
    public static final double PENALTY_DAMPING = 12;

}
