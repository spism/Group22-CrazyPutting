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
    private double currT;
    private String heightProfile;
    private final double h = 0.00001;
    private final double g = 9.81;
    public double[] stateVector = new double[4];
    private boolean initSpeedsDefined = false;
    public PhysicsEngine(String filename)
    {
        currT = 0;
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
        double xAccel = getAccel(true,false);
        double yAccel = getAccel(false,true);
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
    private double getAccel(boolean x, boolean y)
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
            if(Math.abs(pythagoreanSpeed) < h && (slopeX != 0 || slopeY != 0) && !atRest(xCoor,yCoor)) acceleration = -g * slopeX - kineticCoeff * g * (slopeX / denominator2);
            else if(Math.abs(pythagoreanSpeed) < h && (slopeX != 0 || slopeY != 0) && atRest(xCoor,yCoor))
            {
                stateVector[2] = 0;
                stateVector[3] = 0;
                acceleration = 0;
            }
            else acceleration = -g * slopeX - kineticCoeff * g * (xSpeed / denominator);
        }
        else if(y && !x)
        {
            if(Math.abs(pythagoreanSpeed) < h && (slopeX != 0 || slopeY != 0) && !atRest(xCoor,yCoor)) acceleration = -g * slopeY - kineticCoeff * g * (slopeY / denominator2);
            else if(Math.abs(pythagoreanSpeed) < h && (slopeX != 0 || slopeY != 0) && atRest(xCoor,yCoor))
            {
                stateVector[2] = 0;
                stateVector[3] = 0;
                acceleration = 0;
            }
            else acceleration = -g * slopeY - kineticCoeff * g * (ySpeed / denominator);
        }
        return acceleration;
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
        else if(solver == 1) stateVector = updateVectorRK2(stateVector);
        else if(solver == 2) stateVector = updateVectorRK4(stateVector);
        else throw new IllegalArgumentException("Nonexistent solver!");
    }

    /**
     * The Runge-Kutta 2nd order method.
     * @param w is the current iteration of the variable to be changed
     * @param a no idea what this is
     * @return the equation result except for the addition of w
     */
    private double RK2(double w, double a) // what is a?
    {
        return h * ((1 - (1 / (2 * a))) * function(currT,w)) + (1 / (2 * a)) * function(currT + a * h,w + a * h * function(currT,w));
    }

    /**
     * Updates the state vector once using RK2.
     * @param stateVector is the current iteration of the state vector
     * @return the next iteration of the state vector
     */
    private double[] updateVectorRK2(double[] stateVector)
    {
        for(int i = 0; i < stateVector.length; i++)
        {
            //System.out.println(stateVector[i] + " + " + RK2(stateVector[i],2/3));
            stateVector[i] += RK2(stateVector[i],2/3);
        }
        currT += h;
        return stateVector;
    }

    public double rungeKuttaSecondOrder (double x0, double y0, double x)
    {
        int n = (int)((x-x0)/h);
        double y = y0;
        double incrementBeginning, incrementMidpoint;
        for (int i = 1; i<= n; i++){

            incrementBeginning = h * function(x0, y);
            incrementMidpoint = h * function(x0 + h/2, y + incrementBeginning/2);

            y += (incrementBeginning + 2 * incrementMidpoint)/6;

            x0 += h;
        }
        return y;
    }

    public void newStateVectorUpdater_RK4(String fx) {
        if(!atRest(stateVector[0],stateVector[1])) {
            for(int stateVectorInt=0;stateVectorInt<stateVector.length;stateVectorInt++) {
                newW_RK4(stateVectorInt);
            }
        }
        currT += h;
    }

    public void newW_RK4(int stateVectorInt) {
        double[] new4k = new double[4];
        for(int k=1;k<5;k++) {
            new4k[k-1] = kEval(k,stateVector[stateVectorInt]);
        }
        stateVector[stateVectorInt] += (new4k[0] + 2*new4k[1] + 2*new4k[2] + new4k[3])/6;
    }

    public double kEval(int k,double w) {
        if(k==1) return h * function(currT, w);
        else if (k==2 || k==3) return h*function(currT + h/2, w + (kEval(k-1, w) / 2));
        else if (k == 4) return h*function(currT + h, w+kEval(k-1, w));
        else throw new IllegalArgumentException("Invalid input k");
    }

    /**
     * Increments the state vector once using RK4.
     * @param stateVector is the current state vector
     * @return the next iteration of the state vector
     */
    private double[] updateVectorRK4(double[] stateVector)
    {
        for(int i = 0; i < stateVector.length; i++)
        {
            stateVector[i] = RK4(stateVector[i]);
        }
        currT += h;
        return stateVector;
    }

    /**
     * This is the Runge-Kutta 4th order method.  It is used to handle the bulk of the physics for the game.
     * @param w is the current iteration of the variable
     * @return the result of the equation except for the addition of the w variable
     */
    private double RK4(double w)
    {
        double[] k = new double[4];
        k[0] = h * function(currT, w);
        k[1] = h * function(currT + h/2,w + k[0]);
        k[2] = h * function(currT + h/2,w + k[1]);
        k[3] = h * function(currT + h,w + k[2]);
        return (k[0] + 2 * k[1] + 2 * k[2] + k[3])/6;
    }

    public double[] ruleBasedBot(int solver)
    {
        for(double ySpeed = -targetY * grassKinetic; ySpeed < targetY; ySpeed = ySpeed + targetRadius)
        {
            System.out.println("Trying new Y speed!");
            for(double xSpeed = -targetX * grassKinetic; xSpeed < targetX; xSpeed = xSpeed + targetRadius)
            {
                System.out.println("Trying new X speed!");
                stateVector[0] = firstX;
                stateVector[1] = firstY;
                stateVector[2] = xSpeed;
                stateVector[3] = ySpeed;
                while(true)
                {
                    runSimulation(xSpeed,ySpeed,solver);
                    if(inHole(stateVector[0],stateVector[1]))
                    {
                        return new double[]{xSpeed,ySpeed};
                    }
                    if(inWater(stateVector[0],stateVector[1])) break;
                    if(stateVector[2] == 0 && stateVector[3] == 0) break;
                }
            }
        }
        return null;
    }

    public double[] hillClimbing(int solver)
    {
        boolean solutionFound = false;
        double[] currState = new double[4];
        Random random = new Random(System.currentTimeMillis());
        double prevX = 0;
        double prevY = 0;
        double XSpeed = (random.nextInt(2) - 1) * random.nextDouble(10);
        double YSpeed = (random.nextInt(2) - 1) * random.nextDouble(10);
        double currBest = 0;
        double goalState = targetX * targetX + targetY * targetY;
        if(inHole(currState[0],currState[1])) return currState;
        else
        {
            while(XSpeed - prevX < h && YSpeed - prevY < h)
            {
                currState[2] = XSpeed;
                currState[3] = YSpeed;
                stateVector[0] = 0;
                stateVector[1] = 0;
                stateVector[2] = currState[2];
                stateVector[3] = currState[3];
                while(stateVector[2] != 0 || stateVector[3] != 0)
                {
                    runSimulation(currState[2],currState[3],solver);
                    if(inWater(stateVector[0],stateVector[1])) break;
                }
                double euclideanDistance = stateVector[0] * stateVector[0] + stateVector[1] * stateVector[1];
                if(inHole(stateVector[0],stateVector[1]))
                {
                    return currState;
                }
                else if(goalState - euclideanDistance < goalState - currBest)
                {
                    currBest = euclideanDistance;
                }
            }
        }
        return currState;
    }

    private boolean inHole(double x, double y)
    {
        return targetX - targetRadius < x && x < targetX + targetRadius && targetY - targetRadius < y && y < targetY + targetRadius;
    }

    private boolean inWater(double x, double y)
    {
        return function(x,y) < 0;
    }

    public static void main(String[] args)
    {
        PhysicsEngine test = new PhysicsEngine("src\\example_inputfile.txt");
        double[] speeds = test.ruleBasedBot(0);
        if(speeds != null) System.out.println("Found speeds!");
        else System.out.println("No speeds found!");
        /*int count = 0;
        while(true)
        {
            if(count % 1000000 == 0)
            {
                System.out.println("One step: ");
                System.out.println(test.stateVector[0]);
                System.out.println(test.stateVector[1]);
                System.out.println(test.stateVector[2]);
                System.out.println(test.stateVector[1]);
            }
            if(test.targetX - test.targetRadius < test.stateVector[0] && test.stateVector[0] < test.targetX + test.targetRadius && test.targetY - test.targetRadius < test.stateVector[1] && test.stateVector[1] < test.targetY + test.targetRadius) break;
            test.runSimulation(3,0,0);
            if(test.stateVector[2] == 0 && test.stateVector[3] == 0)
            {
                System.out.println("Final step: ");
                System.out.println(test.stateVector[0]);
                System.out.println(test.stateVector[1]);
                System.out.println(test.stateVector[2]);
                System.out.println(test.stateVector[1]);
                break;
            }
            count++;
        }*/
    }
}
