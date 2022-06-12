package com.mga1.game;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class PhysicsEngine
{
    private boolean hasSand = false;
    public double firstX, firstY, targetX, targetY, targetRadius, x1Wall, x2Wall, y1Wall, y2Wall, sandX1, sandX2, sandY1, sandY2, grassKinetic, grassStatic, sandKinetic, sandStatic;
    private Function heightProfile;
    public final double h = 0.01;
    public double[] stateVector = new double[4];
    private boolean initSpeedsDefined = false;
    private static PhysicsEngine physicsEngine;
    private static final ArrayList<Solver> solvers = new ArrayList<>();
    public Function getHeightProfile() {
        return heightProfile;
    }
    private PhysicsEngine(String filename)
    {
        
        try
        {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String[] arr;
            int lineIndex = 0;
            while((line = br.readLine()) != null)
            {
                arr = line.split(" ");
                if(lineIndex == 0)
                {
                    firstX = Double.parseDouble(arr[arr.length - 1]);
                    arr = br.readLine().split(" ");
                    firstY = Double.parseDouble(arr[arr.length - 1]);
                }
                else if(lineIndex == 1)
                {
                    targetX = Double.parseDouble(arr[arr.length - 1]);
                    arr = br.readLine().split(" ");
                    targetY = Double.parseDouble(arr[arr.length - 1]);
                    arr = br.readLine().split(" ");
                    targetRadius = Double.parseDouble(arr[arr.length - 1]);
                }
                else if(lineIndex == 2)
                {
                    grassKinetic = Double.parseDouble(arr[arr.length - 1]);
                    arr = br.readLine().split(" ");
                    grassStatic = Double.parseDouble(arr[arr.length - 1]);
                }
                else if(lineIndex == 3)
                {
                    heightProfile = new Function(line);
                    //getHeight(heightProfile,firstX,firstY);
                }
                else if(lineIndex == 4)
                {
                    hasSand = true;
                    sandX1 = Double.parseDouble(arr[2]);
                    sandX2 = Double.parseDouble(arr[6]);
                    arr = br.readLine().split(" ");
                    sandY1 = Double.parseDouble(arr[2]);
                    sandY2 = Double.parseDouble(arr[6]);
                }
                else if(lineIndex == 5)
                {
                    sandKinetic = Double.parseDouble(arr[2]);
                    arr = br.readLine().split(" ");
                    sandStatic = Double.parseDouble(arr[2]);
                }
                else if(lineIndex == 6)
                {
                    x1Wall = Double.parseDouble(arr[2]);
                    x2Wall = Double.parseDouble(arr[6]);
                    arr = br.readLine().split(" ");
                    y1Wall = Double.parseDouble(arr[2]);
                    y2Wall = Double.parseDouble(arr[6]);
                }
                lineIndex++;
            }
        }
        catch(IOException e)
        {
            System.out.println("I/O exception");
        }
        
    }
    private PhysicsEngine(String x0, String y0, String xT, String yT, String radius, String muk, String mus, String heightProfile)
    {
        firstX = Double.parseDouble(x0);
        firstY = Double.parseDouble(y0);
        targetX = Double.parseDouble(xT);
        targetY = Double.parseDouble(yT);
        targetRadius = Double.parseDouble(radius);
        grassKinetic = Double.parseDouble(muk);
        grassStatic = Double.parseDouble(mus);
        this.heightProfile = new Function(heightProfile);
        
    }

    private static void initSolvers()
    {
        solvers.add(new EulersMethod());
        solvers.add(new RungeKutta2());
        solvers.add(new RungeKutta4());
    }

    public static PhysicsEngine getPhysicsEngine(String x0, String y0, String xT, String yT, String radius, String muk, String mus, String heightProfile)
    {
        if(physicsEngine == null)
        {
            physicsEngine = new PhysicsEngine(x0, y0, xT, yT, radius, muk, mus, heightProfile);
            initSolvers();
        }

        return physicsEngine;
    }
    public static PhysicsEngine getPhysicsEngine()
    {
        return physicsEngine;
    }

    /**
     * Used to get the acceleration of the ball at the current point, so that it can be used to increment the state vector.  It uses the current state vector.
     * This method should only be used for internal acceleration requirements, and as such it is private.
     * @param x should be the only true variable if the x acceleration is required
     * @param y should be the only true variable if the y acceleration is required
     * @return the x or y acceleration based on the parameters
     */
    public double getAccel(boolean x, boolean y, double[] stateVector)
    {
        double xCoor = stateVector[0];
        double yCoor = stateVector[1];
        double xSpeed = stateVector[2];
        double ySpeed = stateVector[3];

        double limitZero = 0.000000000001; // lowest possible number before resulting in larger errors in computations
        double newX = xCoor + limitZero;
        double newY = yCoor + limitZero;

        double slopeX = (heightProfile.evaluate(newX,yCoor) - heightProfile.evaluate(xCoor,yCoor)) / limitZero;
        double slopeY = (heightProfile.evaluate(xCoor,newY) - heightProfile.evaluate(xCoor, yCoor)) / limitZero;

        double mainSlope;
        double mainSpeed;

        double kineticCoeff = hasSand && sandX1 < xCoor && xCoor < sandX2 && sandY1 < yCoor && yCoor < sandY2 ? sandKinetic : grassKinetic;
        double acceleration = 0;
        double pythagoreanSpeed = xSpeed * xSpeed + ySpeed * ySpeed;
        double pythagoreanSlope = slopeX * slopeX + slopeY * slopeY;
        double denominator = Math.sqrt(pythagoreanSpeed);
        double denominator2 = Math.sqrt(pythagoreanSlope);

        if(x && !y)
        {
            mainSlope = slopeX;
            mainSpeed = xSpeed;
        }
        else
        {
            mainSlope = slopeY;
            mainSpeed = ySpeed;
        }

        if(minimumSpeed(pythagoreanSpeed) && (slopeX != 0 || slopeY != 0) && !atRest(xCoor,yCoor))
        {
            acceleration = calcAccel(mainSlope,kineticCoeff,mainSlope,denominator2);
        }
        else if(inPit(pythagoreanSpeed,slopeX,slopeY))
        {
            stateVector[2] = 0;
            stateVector[3] = 0;
            acceleration = 0;
        }
        else if(minimumSpeed(pythagoreanSpeed) && (slopeX != 0 || slopeY != 0) && atRest(xCoor,yCoor))
        {
            stateVector[2] = 0;
            stateVector[3] = 0;
            acceleration = 0;
        }
        else if(inWater(stateVector[0],stateVector[1]))
        {
            stateVector[2] = 0;
            stateVector[3] = 0;
            acceleration = 0;
        }
        else if(touchesWall(stateVector[0],stateVector[1]))
        {
            if(touchesWallDown(stateVector[0], stateVector[1]) || touchesWallUp(stateVector[0], stateVector[1]))
            {
                stateVector[3] = -ySpeed;
            }
            else if(touchesWallLeft(stateVector[0], stateVector[1]) || touchesWallRight(stateVector[0], stateVector[1]))
            {
                stateVector[2] = -xSpeed;
            }
        }
        else
        {
            acceleration = calcAccel(mainSlope,kineticCoeff,mainSpeed,denominator);
        }

        return acceleration;
    }

    /**
     * Checks if the current speed and slopes mean that the ball will stay in place
     * @param speed the current speed
     * @param slopeX the given x slope at a point
     * @param slopeY the given y slope at a point
     * @return true if the ball will stay in place
     */
    private boolean inPit(double speed, double slopeX, double slopeY)
    {
        return Math.abs(speed) < 0.01 && slopeX == 0 & slopeY == 0;
    }

    /**
     * Checks if the ball will move after reaching 0 total velocity by calculating the slope and comparing it to the static friction coefficient
     * @param x is the current x coordinate
     * @param y is the current y coordinate
     * @return true if the ball will stay at rest, false if not
     */
    public boolean atRest(double x, double y)
    {
        double staticCoeff = hasSand && sandX1 < x && x < sandX2 && sandY1 < y && y < sandY2 ? sandStatic : grassStatic;
        double limitZero = 0.000000000001;
        double derivativeX = (Math.abs(heightProfile.evaluate(x,y) - heightProfile.evaluate(x + limitZero, y))) / limitZero;
        double derivativeY = (Math.abs(heightProfile.evaluate(x, y) - heightProfile.evaluate(x, y + limitZero))) / limitZero;
        //System.out.println(derivativeX + " " + derivativeY);
        return staticCoeff > Math.sqrt(derivativeX * derivativeX + derivativeY * derivativeY);
    }

    /**
     * The acceleration equation
     * @param slope is the slope
     * @param muk is the kinetic coefficient
     * @param numerator is velocity or the slope (depends on if speed is 0)
     * @param denominator is the euclidean distance of the slopes or speeds
     * @return the acceleration
     */
    public double calcAccel(double slope, double muk, double numerator, double denominator)
    {
        double g = 9.81;
        return -g * slope - muk * g * (numerator / denominator);
    }

    /**
     * This method defines the minimum cutoff speed
     * @param speed is speed that needs to be checked
     * @return true if the speed is less than the threshold
     */
    public boolean minimumSpeed(double speed)
    {
        return Math.abs(speed) < 0.01;
    }

    /**
     * Updates the state vector based on some conditions given an initial speed in the X and Y direction
     * @param initSpeedX is the initial speed in the X direction
     * @param initSpeedY is the initial speed in the Y direction
     * @param solver is the selected solver.  0 means Euler's method, 1 means Runge-Kutta 2nd order, 2 means Runge-Kutta 4th order
     */
    public void runSimulation(double initSpeedX, double initSpeedY, int solver)
    {
        if(!initSpeedsDefined)
        {
            stateVector[0] = firstX;
            stateVector[1] = firstY;
            stateVector[2] = initSpeedX;
            stateVector[3] = initSpeedY;
            initSpeedsDefined = true;
        }

        if(solver < solvers.size() && solver >= 0) solvers.get(solver).nextStep(stateVector);
        else throw new IllegalArgumentException("Nonexistent solver!");
    }


    /**
     * Uses the equation of a circle to check whether the ball is inside the hole
     * @param x is the current x position
     * @param y is the current y position
     * @return true if the ball is in the hole
     */
    public boolean inHole(double x, double y)
    {
        return (Math.pow(x - targetX,2) + Math.pow(y - targetY,2)) <= Math.pow(targetRadius,2);
    }

    /**
     * Simulates taking a shot.  Returns null if the ball hits the hole during the shot
     * @param speed defines the initial speeds
     * @param solver is the desired solver.  0 is Euler's method, 1 is RK2, 2 is RK4
     * @return
     */
    public double[] takeShot(double firstX, double firstY, double[] speed, int solver)
    {
        stateVector[0] = firstX;
        stateVector[1] = firstY;
        stateVector[2] = speed[0];
        stateVector[3] = speed[1];
        while(stateVector[2] != 0 || stateVector[3] != 0)
        {
            if(inHole(stateVector[0],stateVector[1]))
            {
                return null;
            }
            runSimulation(speed[0],speed[1],solver);
        }
        return new double[]{stateVector[0],stateVector[1]};
    }

    private boolean touchesWallDown(double x, double y)
    {
        boolean isInsideX = x > x1Wall && x < x2Wall;
        boolean isInsideY = y > y1Wall && y < y2Wall/2;
        return isInsideX && isInsideY;
    }

    private boolean touchesWallUp(double x, double y)
    {
        boolean isInsideX = x > x1Wall && x < x2Wall;
        boolean isInsideY = y > y1Wall/2 && y < y2Wall;
        return isInsideX && isInsideY;
    }

    private boolean touchesWallLeft(double x, double y)
    {
        boolean isInsideX = x > x1Wall/2 && x < x2Wall;
        boolean isInsideY = y > y1Wall && y < y2Wall;
        return isInsideX && isInsideY;
    }

    private boolean touchesWallRight(double x, double y)
    {
        boolean isInsideX = x > x1Wall && x < x2Wall/2;
        boolean isInsideY = y > y1Wall && y < y2Wall;
        return isInsideX && isInsideY;
    }

    private boolean touchesWall(double x, double y)
    {
        boolean isInsideX = x > x1Wall && x < x2Wall;
        boolean isInsideY = y > y1Wall && y < y2Wall;
        return isInsideX && isInsideY;
    }

    /**
     * A simple abstraction for whether the ball is in the water.
     * @param x is the current x position
     * @param y is the current y position
     * @return true if the ball is in water
     */
    private boolean inWater(double x, double y)
    {
        return heightProfile.evaluate(x,y) < 0;
    }

    public static void main(String[] args)
    {
        PhysicsEngine test = getPhysicsEngine("-1", "-0.5", "4", "1", "0.1", "0.1", "0.2", "( e ^ ( ( -1 * ( ( x ^ 2 ) + ( y ^ 2 ) ) ) / 40 ) )");

        /*double[] speeds = test.ruleBasedBot(2);
        if(speeds != null)
        {
            System.out.println("Found speeds!");
            System.out.println(speeds[0]);
            System.out.println(speeds[1]);
        }
        else System.out.println("No speeds found!");*/
        /*double[] speedsHillClimbing = test.hillClimbing(2);
        System.out.println(speedsHillClimbing[0]);
        System.out.println(speedsHillClimbing[1]);*/

        AI bots = new AI();
        double[] speedsHillClimbing = bots.hillClimbing(2);
        System.out.println(speedsHillClimbing[0]);
        System.out.println(speedsHillClimbing[1]);

        int count = 0;
        while(true)
        {
            if(count % 1000000 == 0)
            {
                System.out.println("One step: ");
                System.out.println(test.stateVector[0]);
                System.out.println(test.stateVector[1]);
                System.out.println(test.stateVector[2]);
                System.out.println(test.stateVector[3]);
            }
            test.runSimulation(3,0,2);
            if(test.stateVector[2] == 0 && test.stateVector[3] == 0)
            {
                System.out.println("Required " + count + " calculations");
                System.out.println("Final step: ");
                System.out.println(test.stateVector[0]);
                System.out.println(test.stateVector[1]);
                System.out.println(test.stateVector[2]);
                System.out.println(test.stateVector[3]);
                break;
            }
            count++;
        }
    }
}
