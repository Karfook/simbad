import simbad.gui.Simbad;
import simbad.sim.*;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class robert
{
    static public class Robot extends KheperaRobot
    {
        public Robot(Vector3d position, String name)
        {
            super(position, name);
        }
        public void initBehavior()
        {
        }
        public void performBehavior()
        {
            /*Go, Robert!*/
            if (collisionDetected())
            {
                this.setWheelsVelocity(-0.01, 0.01);
            }
            else
            {
                this.setWheelsVelocity(0.5, 0.5);
//                this.getCoords(this.);

            }
        }
    }

    public static class Environment extends EnvironmentDescription
    {
        public Environment()
        {
            this.setWorldSize(1.0F);
            light1IsOn = true;
            light2IsOn = false;

            Wall w1 = new Wall(new Vector3d(0.5, 0, 0), 0.05F,1F, 0.05F, this);
            add(w1);
            Wall w2 = new Wall(new Vector3d(-0.5, 0, 0), 0.05F, 1F, 0.05F, this);
            add(w2);

            Wall w3 = new Wall(new Vector3d(0, 0, -0.5), 0.05F, 1F, 0.05F, this);
            w3.rotate90(1);
            add(w3);
            Wall w4 = new Wall(new Vector3d(0, 0, 0.5), 0.05F, 1F, 0.05F, this);
            w4.rotate90(1);
            add(w4);

            this.add(new Box(new Vector3d(0.0, 0.0, -0.06), new Vector3f(0.1F, 0.03F, 0.055F), this));
            this.add(new Box(new Vector3d(0.2, 0.0, 0.0), new Vector3f(0.055F, 0.1F, 0.055F), this));

            /*Go, Robert!*/
            add(new Robot(new Vector3d(0, 0, 0), "Robert"));

        }
    }

    public static void main(String[] args) {
        // request antialising
        System.setProperty("j3d.implicitAntialiasing", "true");
        // create Simbad instance with given environment
        Simbad frame = new Simbad(new Environment(), false);
    }
}