package cs348c.particles;

import java.util.*;
import javax.vecmath.*;
import com.jogamp.opengl.GL2;


/**
 * A class to put your particle-edge collision detection and resolution implementation.
 * <pre>
 * SKELETON CODE IS INCLUDED, BUT CAN BE REMOVED/IGNORED IF DESIRED.
 * </pre>
 * @author Doug James, February 2009
 */
public class CollisionProcessor 
{
    /** 
     * Representation of a point-edge collision event requiring impulse resolution.
     */
    static class PointEdgeCollision
    {

         private PointEdgeCollision(Particle P, 
                                    Particle Q, Particle R, 
                                    double t, // <<< collision time, t* on (0,dt]
                                    double dt,
                                    double alpha)
        {
             this.P = P;
             this.Q = Q;
             this.R = R;
             this.t = t;              
             if(t <= 0) throw new IllegalArgumentException("Can't have t="+t);
             this.dt = dt;
             this.alpha = alpha;
        }
         
        public Particle P;
        public Particle Q;
        public Particle R;
        public double t;
        public double dt;
        public double alpha;
        
        /** Applies momentum-conserving impulses in an attempt to resolve the collision. */
        void resolveCollision()
        {
        	System.out.println("Entering resolveCollision");
        	Vector2d c_p = new Vector2d((1 - alpha) * Q.x.x + alpha * R.x.x, (1 - alpha) * Q.x.y + alpha * R.x.y);
        	Vector2d c_v = new Vector2d((1 - alpha) * Q.v.x + alpha * R.v.x, (1 - alpha) * Q.v.y + alpha * R.v.y);
        	Vector2d rq = new Vector2d(R.x.x - Q.x.x, R.x.y - Q.x.y);
        	Vector2d n = new Vector2d(rq.y /rq.length(), -rq.x / rq.length());
        	
        	double m = (1/P.m) + (Math.pow(1 - alpha, 2) / Q.m) + (Math.pow(alpha, 2) / R.m);
        	double epsilon = .1;
        	double n_v = new Vector2d(P.v.x, P.v.y).dot(n);
        	
        	
        	Vector2d n_v_old = scalMult(n, -P.v.dot(n));
        	Vector2d gamma = scalMult(n_v_old, (1 + epsilon) * m);

        	System.out.println("P old v : (" + P.v.x + ", " + P.v.y + ",");
        	System.out.println("Q old v : (" + Q.v.x + ", " + Q.v.y + ",");
        	System.out.println("R old v : (" + R.v.x + ", " + R.v.y + ",");
        	P.v = new Vector2d(P.v.x +               (gamma.x * -n.x / P.m), P.v.y +               (gamma.y * -n.y / P.m));
        	Q.v = new Vector2d(Q.v.x + ((1 - alpha) * gamma.x * n.x / Q.m), Q.v.y + ((1 - alpha) * gamma.y * n.y / Q.m));
        	R.v = new Vector2d(R.v.x +      (alpha  * gamma.x * n.x / R.m), R.v.y +      (alpha  * gamma.y * n.y / R.m));
        	System.out.println("P new v : (" + P.v.x + ", " + P.v.y + ",");
        	System.out.println("Q new v : (" + Q.v.x + ", " + Q.v.y + ",");
        	System.out.println("R new v : (" + R.v.x + ", " + R.v.y + ",");


        	System.out.println("Leaving resolveCollision");
        }

        /** 
         * Re-tests point-edge collision, and returns result (useful for verifying impulse "worked").
         */
        public PointEdgeCollision retest() 
        {
            return null;// testPointEdgeCollision(P, Q, R, dt, false);
        }
    }
    
    private static Vector2d subVec(Point2d a, Point2d b) {
    	return new Vector2d(a.x - b.x, a.y - b.y);
    }
    
    private static Vector2d scalMult(Vector2d v, double n) {
    	return new Vector2d(v.x * n, v.y * n);
    }
    
    private static Vector2d subVec(Vector2d a, Vector2d b) {
    	return new Vector2d(a.x - b.x, a.y - b.y);
    }
    
    private static double cp(Vector2d v1, Vector2d v2) 
    {
        double cp = (v1.x * v2.y) - (v1.y * v2.x);
        return (cp == -0.0)? 0.0 : cp;
    }
    
    
    public static double getAlpha(Vector2d pq, Vector2d rq) {
    	return (rq.x * pq.x + rq.y * pq.y)/rq.lengthSquared();
    }
    
    /**
     * Checks for collision between Particle p, and the edge (q,r) on
     * the interval (0,dt] given their current position and velocity.
     * 
     * @return PointEdgeCollision for first collision on the interval
     * (0, dt], or null if no collision occurs on that interval.
     */
    public static PointEdgeCollision testPointEdgeCollision(Particle p, 
                                                            Particle q, Particle r, 
                                                            double   dt, boolean debug)
    {
        if(p == q || p==r) 
            return null;
        else {
        	Vector2d pq   = subVec(p.x, q.x);
        	Vector2d pq_v = subVec(p.v, q.v);
        	Vector2d rq   = subVec(r.x, q.x);
        	Vector2d rq_v = subVec(r.v, q.v);
        	
 
       	
        	double a = cp(rq, pq);
        	double b = cp(rq_v, pq) + cp(rq, pq_v);
        	double c = cp(rq_v, pq_v);
        	
        	if ((a == 0 && b == 0) || ((b * b) - (4 * a * c) < 0)) {
        		return null;
        	} else if (b * b - 4 * a * c >= 0 && a != 0 && b != 0) {
        		double z = -.5 * (b + Math.signum(b) * Math.sqrt(b * b - 4 * a * c));
        		double t1 = Math.min(z/a, c/z);
        		double t2 = Math.max(z/a, c/z);
        		t1 = (t1 == -0.0)? 0.0 : t1;

            	if (debug)
            		System.out.println("t1 = " + t1 + ", t2 = " + t2);

        		if (t1 > 0 && t1 <= dt) {
        			if (debug)
        				System.out.println("***********T1 possible " + t1);

        			Vector2d c_p = new Vector2d(p.x.getX() + p.v.x * t1, p.x.getY() + p.v.y * t1);
        			Vector2d c_pq = new Vector2d(c_p.x - q.x.x, c_p.y - q.x.y);
        			
        			double alpha = getAlpha(c_pq, rq);
                	if (debug)
                		System.out.println("alpha = " + alpha);
        			
        			if (alpha <= 1 && alpha > 0) {
        		    	if (debug)
        		    		System.out.println("***********Collision at " + t1);
        				return new PointEdgeCollision(p, q, r, t1, dt, alpha);
        			}
        		} else if (t2 > 0 && t2 <= dt) {
		    		System.out.println("***********T2 possible " + t2);

        			Vector2d c_p = new Vector2d(p.x.getX() + p.v.x * t2, p.x.getY() + p.v.y * t2);
        			Vector2d c_pq = new Vector2d(c_p.x - q.x.x, c_p.y - q.x.y);
        			
        			double alpha = getAlpha(c_pq, rq);
                	if (debug)
                		System.out.println("alpha = " + alpha);
        			
        			if (alpha <= 1 && alpha > 0) {
        		    	if (debug)
        		    		System.out.println("*************Collision at " + t2);
        				return new PointEdgeCollision(p, q, r, t2, dt, alpha);
        			}
        		}
        	} else if (a == 0) { 	
        		double t = -c/b;
        		if (t > 0 && t <= dt) {

        			Vector2d c_p = new Vector2d(p.x.getX() + p.v.x * t, p.x.getY() + p.v.y * t);
        			Vector2d c_pq = new Vector2d(c_p.x - q.x.x, c_p.y - q.x.y);
        			
        			double alpha = getAlpha(c_pq, rq);
                	if (debug)
                		System.out.println("alpha = " + alpha);
        			
        			if (alpha <= 1 && alpha >= 0) {
        		    	if (debug)
        		    		System.out.println("***********Collision at " + t);
        				return new PointEdgeCollision(p, q, r, t, dt, alpha);
        			}
        		} 
        	} else if (b == 0) {
        		double t = c;
        		if (t > 0 && t <= dt) {

        			Vector2d c_p = new Vector2d(p.x.getX() + p.v.x * t, p.x.getY() + p.v.y * t);
        			Vector2d c_pq = new Vector2d(c_p.x - q.x.x, c_p.y - q.x.y);
        			
        			double alpha = getAlpha(c_pq, rq);
                	if (debug)
                		System.out.println("alpha = " + alpha);
        			
        			if (alpha <= 1 && alpha >= 0) {
        				if (debug)
        					System.out.println("***********Collision at " + t);
        				return new PointEdgeCollision(p, q, r, t, dt, alpha);
        			}
        		} 
        	}
            return null; 
        }
    }

}