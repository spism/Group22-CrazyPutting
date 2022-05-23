package com.mga1.game;
import java.io.*;
import java.util.Random;
import java.util.Stack;

public class PhysicsEngine
{
    private boolean hasSand = false;
    public double firstX, firstY, targetX, targetY, targetRadius;
    public double sandX1, sandX2, sandY1, sandY2;
    private double grassKinetic, grassStatic;
    private double sandKinetic, sandStatic;
    private String heightProfile;
    private final double h = 0.01;
    private final double g = 9.81;
    public double[] stateVector = new double[4];
    private boolean initSpeedsDefined = false;
    public PhysicsEngine(String filename)
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
                    heightProfile = line;
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
                lineIndex++;
            }
        }
        catch(IOException e)
        {
            System.out.println("I/O exception");
        }
    }

    /**
     * This method evaluates the expression for height for a given X and Y coordinate.  It works with the functions sin, cos, tan,
     * log with base 10, natural logarithm, and can perform addition, subtraction, multiplication and division granted there is a pair
     * of brackets for each performed operation.  It can work with up to two unknowns and can use the value for pi and Euler's
     * number.
     * @param x is the x coordinate
     * @param y is the y coordinate
     * @return the height for the coordinates
     */
    public double function(double x, double y)
    {
        Stack<String> ops = new Stack<>();
        Stack<Double> vals = new Stack<>();
        String[] arr = heightProfile.split(" ");
        for(int i = 2; i < arr.length; i++)
        {
            String curr = arr[i];
            if(curr.equals("("));
            else if(curr.equals("+")) ops.push(curr);
            else if(curr.equals("-")) ops.push(curr);
            else if(curr.equals("*")) ops.push(curr);
            else if(curr.equals("/")) ops.push(curr);
            else if(curr.equals("^")) ops.push(curr);
            else if(curr.equals("sin")) ops.push(curr);
            else if(curr.equals("cos")) ops.push(curr);
            else if(curr.equals("sqrt")) ops.push(curr);
            else if(curr.equals("abs")) ops.push(curr);
            else if(curr.equals("logb10")) ops.push(curr);
            else if(curr.equals("logbe")) ops.push(curr);
            else if(curr.equals(")"))
            {
                String operator = ops.pop();
                double val = vals.pop();
                //System.out.println("something " + operator + " " + val + " = ");
                if(operator.equals("+")) val = vals.pop() + val;
                else if(operator.equals("-")) val = vals.pop() - val;
                else if(operator.equals("*")) val = vals.pop() * val;
                else if(operator.equals("/")) val = vals.pop() / val;
                else if(operator.equals("^")) val = Math.pow(vals.pop(),val);
                else if(operator.equals("sin")) val = Math.sin(val);
                else if(operator.equals("cos")) val = Math.cos(val);
                else if(operator.equals("sqrt")) val = Math.sqrt(val);
                else if(operator.equals("abs")) val = Math.abs(val);
                else if(operator.equals("logb10")) val = Math.log10(val);
                else if(operator.equals("logbe")) val = Math.log(val);
                //System.out.print(val);
                //System.out.println();
                vals.push(val);
            }
            else
            {
                //System.out.println(checkXY(curr,x,y));
                vals.push(checkXY(curr,x,y));
                //System.out.println(checkXY(curr,x,y));
            }
        }
        double result = vals.pop();
        //System.out.println(result);
        return result;
    }

    /**
     * Replaces x/y variables with their respective values (to remove unknowns)
     * @param s is the value found inside the height profile function
     * @param x is the x coordinate
     * @param y is the y coordinate
     * @return the value that the input string has (can be the value for x, y or the value itself if e.g. the string is 0.5)
     */
    public double checkXY(String s, double x, double y)
    {
        //System.out.println(x + " " + y);
        if(s.equals("x")) return x;
        else if(s.equals("y")) return y;
        else if(s.equals("e")) return 2.71828;
        else if(s.equals("pi")) return 3.14;
        else if(s.equals("g")) return 9.81;
        else return Double.parseDouble(s);
    }

    /**
     * This method updates the state vector using Euler's method.  It is used to approximate the motion of a ball on a slope given by the input file
     * height profile line.
     */
    public double[] updateVectorEuler(double[] stateVector)
    {
        double[] stepVector = new double[4];
        double xAccel = getAccel(true,false,stateVector);
        double yAccel = getAccel(false,true,stateVector);
        //System.out.println(secondTermX + " " + secondTermY);
        //System.out.println("x acceleration: " + xAccel + " y acceleration: " + yAccel);

        stepVector[0] = stateVector[2];
        stepVector[1] = stateVector[3];
        stepVector[2] = xAccel;
        stepVector[3] = yAccel;
        for(int i = 0; i < stateVector.length; i++)
        {
            //System.out.println(stateVector[i] + " + " + h + " * " + stepVector[i]);
            stateVector[i] = stateVector[i] + h * stepVector[i];
            //System.out.println(stateVector[i]);
        }
        //System.out.println();
        return stateVector;
    }

    /**
     * Used to get the acceleration of the ball at the current point, so that it can be used to increment the state vector.  It uses the current state vector.
     * This method should only be used for internal acceleration requirements, and as such it is private.
     * @param x should be the only true variable if the x acceleration is required
     * @param y should be the only true variable if the y acceleration is required
     * @return the x or y acceleration based on the parameters
     */
    private double getAccel(boolean x, boolean y, double[] stateVector)
    {
        double xCoor = stateVector[0];
        double yCoor = stateVector[1];
        double xSpeed = stateVector[2];
        double ySpeed = stateVector[3];

        double limitZero = 0.000000000001; // lowest possible number before resulting in larger errors in computations
        double newX = xCoor + limitZero;
        double newY = yCoor + limitZero;

        double slopeX = (function(newX,yCoor) - function(xCoor,yCoor)) / limitZero;
        double slopeY = (function(xCoor,newY) - function(xCoor, yCoor)) / limitZero;

        double kineticCoeff = hasSand && sandX1 < xCoor && xCoor < sandX2 && sandY1 < yCoor && yCoor < sandY2 ? sandKinetic : grassKinetic;
        double acceleration = 0;
        double pythagoreanSpeed = xSpeed * xSpeed + ySpeed * ySpeed;
        double pythagoreanSlope = slopeX * slopeX + slopeY * slopeY;
        double denominator = Math.sqrt(pythagoreanSpeed);
        double denominator2 = Math.sqrt(pythagoreanSlope);

        if(x && !y)
        {
            if(minimumSpeed(pythagoreanSpeed) && (slopeX != 0 || slopeY != 0) && !atRest(xCoor,yCoor))
            {
                acceleration = calcAccel(slopeX,kineticCoeff,slopeX,denominator2);
            }
            else if(inPit(pythagoreanSpeed,slopeX,slopeY))
            {
                stateVector[2] = 0;
                stateVector[3] = 0;
                acceleration = 0;
            }
            else if(Math.abs(pythagoreanSpeed) < 0.01 && (slopeX != 0 || slopeY != 0) && atRest(xCoor,yCoor))
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
            else
            {
                acceleration = calcAccel(slopeX,kineticCoeff,xSpeed,denominator);
            }
        }
        else if(y && !x)
        {
            if(minimumSpeed(pythagoreanSpeed) && (slopeX != 0 || slopeY != 0) && !atRest(xCoor,yCoor))
            {
                acceleration = calcAccel(slopeY,kineticCoeff,slopeY,denominator2);
            }
            else if(inPit(pythagoreanSpeed,slopeX,slopeY))
            {
                stateVector[2] = 0;
                stateVector[3] = 0;
                acceleration = 0;
            }
            else if(Math.abs(pythagoreanSpeed) < 0.01 && (slopeX != 0 || slopeY != 0) && atRest(xCoor,yCoor))
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
            else acceleration = calcAccel(slopeY,kineticCoeff,ySpeed,denominator);
        }
        return acceleration;
    }

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
        double derivativeX = (Math.abs(function(x,y) - function(x + limitZero,y))) / limitZero;
        double derivativeY = (Math.abs(function(x,y) - function(x,y + limitZero))) / limitZero;
        //System.out.println(derivativeX + " " + derivativeY);
        return staticCoeff > Math.sqrt(derivativeX * derivativeX + derivativeY * derivativeY);
    }

    public double calcAccel(double slope, double muk, double numerator, double denominator)
    {
        return -g * slope - muk * g * (numerator / denominator);
    }

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

        if(solver == 0) stateVector = updateVectorEuler(stateVector);
        else if(solver == 1) RK2();
        else if(solver == 2) RK4();
        else throw new IllegalArgumentException("Nonexistent solver!");
    }

    /**
     * The Runge-Kutta 2nd order method.
     */
    private void RK2()
    {
        double fx = getAccel(true,false,stateVector);
        double fy = getAccel(false,true,stateVector);

        double[] newStateVector = new double[4];

        newStateVector[0] = stateVector[0] + (2 * h) / 3 * stateVector[2];
        newStateVector[1] = stateVector[1] + (2 * h) / 3 * stateVector[3];
        newStateVector[2] = stateVector[2] + (2 * h) / 3 * fx;
        newStateVector[3] = stateVector[3] + (2 * h) / 3 * fy;

        double newfx = getAccel(true, false, newStateVector);
        double newfy = getAccel(false, true, newStateVector);

        stateVector[0] += 0.25 * h * (stateVector[2] + 3 * newStateVector[2]);
        stateVector[1] += 0.25 * h * (stateVector[3] + 3 * newStateVector[3]);
        stateVector[2] += 0.25 * h * (fx + 3 * newfx);
        stateVector[3] += 0.25 * h * (fy + 3 * newfy);
    }

    /**
     * This is the Runge-Kutta 4th order method.  It is used to handle the bulk of the physics for the game.
     */
    private void RK4()
    {
        double[][] k = new double[4][4];

        k[0][0] = h * stateVector[2];
        k[1][0] = h * stateVector[3];
        k[2][0] = h * getAccel(true,false,stateVector);
        k[3][0] = h * getAccel(false,true,stateVector);

        double[] newStateVector = new double[4];
        for(int i = 0; i < 4; i++)
        {
            newStateVector[i] = stateVector[i] + 0.5 * k[i][0];
        }

        k[0][1] = h * newStateVector[2];
        k[1][1] = h * newStateVector[3];
        k[2][1] = h * getAccel(true,false,newStateVector);
        k[3][1] = h * getAccel(false,true,newStateVector);

        for(int i = 0; i < 4; i++)
        {
            newStateVector[i] = stateVector[i] + 0.5 * k[i][1]; // maybe newStateVector[i] + 0.5 * k[i][1]?
        }

        k[0][2] = h * newStateVector[2];
        k[1][2] = h * newStateVector[3];
        k[2][2] = h * getAccel(true,false,newStateVector);
        k[3][2] = h * getAccel(false,true,newStateVector);

        for(int i = 0; i < 4; i++)
        {
            newStateVector[i] = stateVector[i] + k[i][2];
        }

        k[0][3] = h * newStateVector[2];
        k[1][3] = h * newStateVector[3];
        k[2][3] = h * getAccel(true,false,newStateVector);
        k[3][3] = h * getAccel(false,true,newStateVector);

        for(int i = 0; i < 4; i++)
        {
            //System.out.println(stateVector[i] + " + " + (k[i][0] + 2 * k[i][1] + 2 * k[i][2] + k[i][3]) / 6);
            /*for(int j = 0; j < 4; j++)
            {
                System.out.println(j + " " + k[i][j]);
            }*/
            stateVector[i] += (k[i][0] + 2 * k[i][1] + 2 * k[i][2] + k[i][3]) / 6;
        }
    }

    public double[] ruleBasedBot(int solver)
    {
        int iterationNumber = 0;
        for(double ySpeed = -5; ySpeed < 5; ySpeed = ySpeed + 0.5 * targetRadius)
        {
            System.out.println("Trying new Y speed!!!!!!!!!!!");
            for(double xSpeed = -5; xSpeed < 5; xSpeed = xSpeed + 0.5 * targetRadius)
            {
                iterationNumber++;
                System.out.println("Trying new X speed!");
                stateVector[0] = firstX;
                stateVector[1] = firstY;
                stateVector[2] = xSpeed;
                stateVector[3] = ySpeed;
                System.out.println("X speed: " + xSpeed);
                System.out.println("Y speed: " + ySpeed);
                while(stateVector[2] != 0 || stateVector[3] != 0)
                {
                    runSimulation(xSpeed,ySpeed,solver);
                    //System.out.println(stateVector[2] + " " + stateVector[3]);
                    if(inHole(stateVector[0],stateVector[1]))
                    {
                        System.out.println("Finding the speed took " + iterationNumber + " iterations");
                        return new double[]{xSpeed,ySpeed};
                    }
                }
            }
        }
        return null;
    }

    public double[] hillClimbing(int solver)
    {
        double[] bestSpeeds = new double[2];
        Random random = new Random(System.currentTimeMillis());
        double xSpeed = (random.nextInt(2) - 1) * random.nextDouble() * 5;
        double ySpeed = (random.nextInt(2) - 1) * random.nextDouble() * 5;
        double currBest = Integer.MAX_VALUE;
        double localBest = Integer.MAX_VALUE;
        double[] bestPos = new double[2];
        double[] localPos = takeShot(new double[]{xSpeed,ySpeed},solver);
        int prevIndex = -1;
        int iterationNumber = 0;


        //used to find the current speed using random values
        if(inHole(bestPos[0],bestPos[1]))
        {
            return bestSpeeds;
        }
        while(!inHole(bestPos[0],bestPos[1]))
        {
            iterationNumber++;
            double euclideanDistance = distanceFromGoal(localPos);

            double spread = 2;
            //left and right speeds (the current speed is change by a bit in both directions) are evaluated and the better one is chosen
            double[] rightSpeed = {xSpeed - spread * targetRadius * euclideanDistance, ySpeed + spread * targetRadius * euclideanDistance};
            double[] leftSpeed = {xSpeed + spread * targetRadius * euclideanDistance, ySpeed - spread * targetRadius * euclideanDistance};
            double[] strongerSpeed = {xSpeed + spread * targetRadius * euclideanDistance, ySpeed + spread * targetRadius * euclideanDistance};

            //order is very important, keep shots in order left -> center -> right with respective indices 0, 1, 2 or any multiple of those
            double[][] speeds = {leftSpeed,strongerSpeed,rightSpeed};

            double[] centerShot = takeShot(strongerSpeed, solver);
            if(centerShot == null)
            {
                System.out.println("Finding the speeds took " + iterationNumber + " iterations");
                return strongerSpeed;
            }

            double[] rightShot = takeShot(rightSpeed, solver);
            if(rightShot == null)
            {
                System.out.println("Finding the speeds took " + iterationNumber + " iterations");
                return rightSpeed;
            }

            double[] leftShot = takeShot(leftSpeed, solver);
            if(leftShot == null)
            {
                System.out.println("Finding the speeds took " + iterationNumber + " iterations");
                return leftSpeed;
            }

            double[][] shotsTaken = {leftShot,centerShot,rightShot};

            double min = Integer.MAX_VALUE;
            int index = -1;
            double[] distancesFromGoal = {distanceFromGoal(leftShot),distanceFromGoal(centerShot),distanceFromGoal(rightShot)};
            for(int i = 0; i < 3; i++)
            {
                if(distancesFromGoal[i] < min)
                {
                    min = distancesFromGoal[i];
                    index = i;
                }
            }

            if(min < localBest)
            {
                //while climbing the local slope, set local best to new best, change speeds to new best and position to new best
                System.out.println("local hill climbing");
                prevIndex = index;
                localBest = min;
                xSpeed = speeds[index][0];
                ySpeed = speeds[index][1];
                localPos = shotsTaken[index];
            }
            else if(localBest < currBest)
            {
                //if local peak has been reached, check if the local peak is indeed better than the current located global peak
                System.out.println("Improvement!  New best position!");
                System.out.println(localBest);
                currBest = localBest;
                bestSpeeds[0] = xSpeed;
                bestSpeeds[1] = ySpeed;
                System.out.println("Best speeds are " + bestSpeeds[0] + " " + bestSpeeds[1]);
                bestPos = shotsTaken[prevIndex];
                System.out.println("euclidean is " + distanceFromGoal(bestPos));
                System.out.println("Best positions are " + bestPos[0] + " " + bestPos[1]);
            }
            else
            {
                //otherwise, attempt to shotgun out of local zone
                System.out.println("Shotgun!");
                localBest = Integer.MAX_VALUE;
                xSpeed = xSpeed + (random.nextInt(2) - 1) * random.nextDouble() * 5;
                ySpeed = ySpeed + (random.nextInt(2) - 1) * random.nextDouble() * 5;
                localPos = takeShot(new double[]{xSpeed,ySpeed},solver);
            }

        }
        System.out.println("Finding the speeds took " + iterationNumber + " iterations");
        return bestSpeeds;
    }

    private boolean speedWithinBounds(double speed, double oldSpeed, double euclidean)
    {
        return oldSpeed - targetRadius * euclidean < speed && speed < oldSpeed + targetRadius * euclidean;
    }

    private double distanceFromGoal(double[] vector)
    {
        return calculateEuclidean(targetX - vector[0],targetY - vector[1]);
    }

    private double[] takeShot(double[] speed, int solver)
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

    private double calculateEuclidean(double x, double y)
    {
        return Math.sqrt(x * x + y * y);
    }

    private boolean inHole(double x, double y)
    {
        return (Math.pow(x - targetX,2) + Math.pow(y - targetY,2)) <= Math.pow(targetRadius,2);
    }

    private boolean inWater(double x, double y)
    {
        return function(x,y) < 0;
    }

    public static void main(String[] args)
    {
        PhysicsEngine test = new PhysicsEngine("src\\example_inputfile.txt");
        /*double[] speeds = test.ruleBasedBot(2);
        if(speeds != null)
        {
            System.out.println("Found speeds!");
            System.out.println(speeds[0]);
            System.out.println(speeds[1]);
        }
        else System.out.println("No speeds found!");*/
        double[] speedsHillClimbing = test.hillClimbing(2);
        System.out.println(speedsHillClimbing[0]);
        System.out.println(speedsHillClimbing[1]);
        /*int count = 0;
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
            if(test.targetX - test.targetRadius < test.stateVector[0] && test.stateVector[0] < test.targetX + test.targetRadius && test.targetY - test.targetRadius < test.stateVector[1] && test.stateVector[1] < test.targetY + test.targetRadius) break;
            test.runSimulation(3,0,0);
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
        }*/
    }
}
