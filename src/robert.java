import simbad.gui.Simbad;
import simbad.sim.*;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.text.DecimalFormat;

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
                /* Basic turn when collides */
                this.setWheelsVelocity(-1, 1);

            }
            else
            {
                /* Usual behaviour */
                this.setWheelsVelocity(0.01, 0.01);
            }

            if((getCounter() % 100 == 0))
            {
                /* Coordinates */
                Point3d point = new Point3d(this.instantTranslation);
                getCoords(point);
                double x = (double) Math.round(point.getX() * 1000) / 1000;
                double y = (double) Math.round(point.getY() * 1000) / 1000;
                double z = (double) Math.round(point.getZ() * 1000) / 1000;

                System.out.println("x: " + x + "\ny: " + y + "\nz: " + z);
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