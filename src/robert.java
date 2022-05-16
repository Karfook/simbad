import simbad.gui.Simbad;
import simbad.sim.*;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.text.DecimalFormat;
import java.util.*;

public class robert
{
    static public class Robot extends KheperaRobot
    {
        /* Sensor */
        double sensorL, sensorUL, sensorFL, sensorFR, sensorUR, sensorR, sensorBL, sensorBR;

        /* Coordinate */
        Point3d[] coords = new Point3d[256];
        double xA, xB, yA, yB, zA, zB, y;
        Point3d pointA, pointB, pointZ;
        int i = 0, j = 0, k, l;

        public Robot(Vector3d position, String name)
        {
            super(position, name);
        }

        private void recordCoords(Point3d point, boolean collision)
        {
            boolean c = false;
            i = j;

            for(k = 0; coords[k] != null; k++) /* Check whether coordinate exist */
            {
                if(coords[k].getX() == point.getX()) /* Decide axis - Horizontal */
                {
                    if(Math.abs(coords[k].getZ() - point.getZ()) < 0.009)
                        c = true;
                }
                else if(coords[k].getZ() == point.getZ()) /* Decide axis - Vertical */
                {
                    if(Math.abs(coords[k].getX() - point.getX()) < 0.009)
                        c = true;
                }
            }

            if(!c) /* If coordinate exists */
            {
                if(j != 256) /* Condition to check if memory is full */
                    j++;
                else
                {
                    for(k = 0; k < 256; k++) /* Move coords 1 index forward, thus deleting the index 0 array everytime called */
                    {
                        coords[k] = coords[k + 1];
                    }
                    coords[j] = null; /* Remove array in index 256 to store latest coords */
                }

                if(!collision) /* Condition for collision nodes */
                    i = j;
                else
                    i = j - 1;

                coords[j] = point;
            }
        }

        public void initBehavior()
        {
            /* 0.014999999664723873 */
            y = (double) Math.round(0.014999999664723873 * 1000) / 1000;
            pointZ = new Point3d(0, y, 0);
            coords[0] = pointZ;
            System.out.println("init: " + coords[0]);
            this.setWheelsVelocity(0.01, 0.01);

        }
        public void performBehavior()
        {
            /* IR Sensor readings */
            sensorL = this.getIRSensors().getMeasurement(0);
            sensorUL = this.getIRSensors().getMeasurement(1);
            sensorFL = this.getIRSensors().getMeasurement(2);
            sensorFR = this.getIRSensors().getMeasurement(3);
            sensorUR = this.getIRSensors().getMeasurement(4);
            sensorR = this.getIRSensors().getMeasurement(5);
            sensorBL = this.getIRSensors().getMeasurement(7);
            sensorBR = this.getIRSensors().getMeasurement(6);

            /* Coordination
            * 1. Get coordinates every 60 frame - issues such as overlapping coords, uneven distribute of coords and so on might occur
            * 2. Or maybe, get starting and collided coordinates, then divide them into smaller pieces - issue: either need a lot of time to startup (circling), or
            * 3. Get coordinates after traveled certain range, comparing current coordinate to the last recorded node - but this method is resource exhausting since keep looping
            *
            * Compare current coordinate with previous one to determine the length moved by robot, whereas 1 step = 0.055 if 1 step = robot's body size
            * Then, we compare that axis to determine what axis its on and facing
            * Lets say, we record coordinates every 0.055 / 0.0275, as well as when collided with wall. This should resolve problem of over-walling
            *
            * How about array type? Array, 2-dimensional array, Tree?
            * 1. Use array, but 1 dimension and only point 3d can be recorded
            * 2. 2-dimensional array seems unsuitable
            * 3. List, but might not be what lecturer expected, also limiting size is quite ambiguous
            *
            * Backtracking
            * 1. Use of manhattan and euclidean distance to determine move
            *
            * Problem now 17/5
            * 1. how do we command to locate certain coords exactly / accurately
            *       solution: euclidean distance to calculate how far, then move toward certain axis. If it collides before reaching 1 axis, turn, then move toward on another axis
            *       but this lead to problem such as we have no idea how to command robot to face certain direction
            * */

            /*Go, Robert!*/
            if((getCounter() % 60 == 0))
            {
                /* Movement control rules */
                if(sensorFL <= 0.010 && sensorFR <= 0.010) /* smaller than 0.005 means its close to collide, or already collide; 0.010 means still far away before collision */
                {

                    if(sensorL > 0.010) /* Turn left */
                    {
                        this.rotateY(1.5708);
                    }
                    else if(sensorR > 0.010 && sensorL <= 0.010) /* Turn right */
                    {
                        this.rotateY(-1.5708);
                    }
                    else if(sensorL <= 0.010 && sensorR <= 0.010) /* Turn back? */
                    {
                        this.rotateY(3.14159);
                    }
                }

                /* Coordination rules */
                pointA = new Point3d(); /* Current point3d coords */
                getCoords(pointA);
                xA = (double) Math.round(pointA.getX() * 1000) / 1000;
                pointA.setX(xA);
                yA = (double) Math.round(pointA.getY() * 1000) / 1000;
                pointA.setY(yA);
                zA = (double) Math.round(pointA.getZ() * 1000) / 1000;
                pointA.setZ(zA);

                pointB = coords[i]; /* Array's last coords */
//                xB = (double) Math.round(pointB.getX() * 1000) / 1000;
//                yB = (double) Math.round(pointB.getY() * 1000) / 1000;
//                zB = (double) Math.round(pointB.getZ() * 1000) / 1000;
                xB = pointB.getX();
                yB = pointB.getY();
                zB = pointB.getZ();

//                pointX = new Point3d(xA, yA, zA);

//                System.out.println("xA: " + xA + " | zA: " + zA + " && xB: " + xB + " | zB: " + zB);
//                System.out.println(pointA + " || " + pointB);

                if(collisionDetected()) /* Record coords as well when collide*/
                {
                    recordCoords(pointA, true);
                    System.out.println(Arrays.toString(coords));
                }

                if(xA == xB) /* Decide axis - Horizontal */
                {
                    if(Math.abs(zA - zB) >= 0.055 && Math.abs(zA - zB) <= 0.065)
                    {
                        recordCoords(pointA, false);
                        System.out.println(Arrays.toString(coords));
                    }
                }
                else if(zA == zB) /* Decide axis - Vertical */
                {
                    if(Math.abs(xA - xB) >= 0.055  && Math.abs(xA - xB) <= 0.065)
                    {
                        recordCoords(pointA, false);
                        System.out.println(Arrays.toString(coords));
                    }
                }
            }

//            if(getCounter() % 3600 == 0) /* Print coords array every 3600 frames */
//                System.out.println(Arrays.toString(coords));
        }
    }

    public static class Environment extends EnvironmentDescription
    {
        public Environment()
        {
            this.setWorldSize(1.0F);
            light1IsOn = true;
            light2IsOn = false;

            Wall w1 = new Wall(new Vector3d(0.5, 0, 0), 0.05F,1.05F, 0.05F, this);
            add(w1);
            Wall w2 = new Wall(new Vector3d(-0.5, 0, 0), 0.05F, 1.05F, 0.05F, this);
            add(w2);

            Wall w3 = new Wall(new Vector3d(0, 0, -0.5), 0.05F, 1.05F, 0.05F, this);
            w3.rotate90(1);
            add(w3);
            Wall w4 = new Wall(new Vector3d(0, 0, 0.5), 0.05F, 1.05F, 0.05F, this);
            w4.rotate90(1);
            add(w4);

            this.add(new Box(new Vector3d(0.0, 0.0, -0.06), new Vector3f(0.1F, 0.03F, 0.055F), this));
            this.add(new Box(new Vector3d(0.2, 0.0, 0.0), new Vector3f(0.055F, 0.1F, 0.055F), this));
            this.add(new Box(new Vector3d(0.15, 0.0, -0.06), new Vector3f(0.055F, 0.1F, 0.055F), this));
            this.add(new Box(new Vector3d(0.15, 0.0, 0.06), new Vector3f(0.055F, 0.1F, 0.055F), this));

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