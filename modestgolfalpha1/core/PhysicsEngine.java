package com.mga1.game;
import java.io.*;
import java.util.Stack;

public class PhysicsEngine
{
    boolean hasSand = false;
    double firstX, firstY, targetX, targetY, targetRadius;
    double sandX1, sandX2, sandY1, sandY2;
    double grassKinetic, grassStatic;
    double sandKinetic, sandStatic;
    String heightProfile;
    final double h = 0.000001;
    final double g = 9.81;
    public double[] stateVector = new double[4];
    boolean initSpeedsDefined = false;
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
    public double getHeight(double x, double y)
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
    public void updateVector()
    {
        double[] stepVector = new double[4];
        /*double x = stateVector[0];
        double y = stateVector[1];
        double speedX = stateVector[2];
        double speedY = stateVector[3];
        double limitZero = 0.000000000001;
        double newX = x + limitZero;
        double newY = y + limitZero;
        double kineticDenominator = Math.sqrt(speedX * speedX + speedY * speedY);
        double kineticCoeff = sandX1 < x && x < sandX2 && sandY1 < y && y < sandY2 ? sandKinetic : grassKinetic;
        double slopeX = (getHeight(newX,y) - getHeight(x,y)) / limitZero;
        double slopeY = (getHeight(x,newY) - getHeight(x, y)) / limitZero;
        System.out.println("height x: " + getHeight(x,y) + " " + getHeight(newX,y));
        System.out.println("height y: " + getHeight(x,y) + " " + getHeight(x,newY));
        System.out.println("slope x: " + slopeX + " slope y: " + slopeY);
        double secondTermX = atRest ? kineticCoeff * g * (slopeX / Math.sqrt(slopeX * slopeX + slopeY * slopeY)) : kineticCoeff * g * (speedX / kineticDenominator);
        double secondTermY = atRest ? kineticCoeff * g * (slopeY / Math.sqrt(slopeX * slopeX + slopeY * slopeY)) : kineticCoeff * g * (speedY / kineticDenominator);*/
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

        double limitZero = 0.000000000001;
        double newX = xCoor + limitZero;
        double newY = yCoor + limitZero;

        double slopeX = (getHeight(newX,yCoor) - getHeight(xCoor,yCoor)) / limitZero;
        double slopeY = (getHeight(xCoor,newY) - getHeight(xCoor, yCoor)) / limitZero;

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
        else if(y && !x) {
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
        double derivativeX = (Math.abs(getHeight(x,y) - getHeight(x + limitZero,y))) / limitZero;
        double derivativeY = (Math.abs(getHeight(x,y) - getHeight(x,y + limitZero))) / limitZero;
        //System.out.println(derivativeX + " " + derivativeY);
        return staticCoeff > Math.sqrt(derivativeX * derivativeX + derivativeY * derivativeY);
    }

    /**
     * Updates the state vector based on some conditions given an initial speed in the X and Y direction
     * @param initSpeedX is the initial speed in the X direction
     * @param initSpeedY is the initial speed in the Y direction
     */
    public void runSimulation(double initSpeedX, double initSpeedY, boolean atRest)
    {
        if(!initSpeedsDefined)
        {
            stateVector[0] = firstX;
            stateVector[1] = firstY;
            stateVector[2] = initSpeedX;
            stateVector[3] = initSpeedY;
            initSpeedsDefined = true;
        }

        updateVector();
    }

    

    
    
    public void newStateVectorUpdater_RK4(boolean atRest, String fx) {
    		if(!atRest(stateVector[0],stateVector[1])) {
    			for(int stateVectorInt=0;stateVectorInt<stateVector.length;stateVectorInt++) {
    				newW_RK4(fx, stateVectorInt);
    			}
    		}
    }
    
    
    
    public void newW_RK4(String fx, int stateVectorInt) {
    	double[] new4k = new double[4];
    		for(int k=1;k<5;k++) {
    			new4k[k-1] = kEval(k, fx, stateVector[stateVectorInt]);
    		}
    		stateVector[stateVectorInt] += (new4k[0] + 2*new4k[1] + 2*new4k[2] + new4k[3])/6;
    		}
    	
    
    
    public double kEval(int k, String fx, double w) {
    	if(k==1) {
    		return h*checkXY(fx, 0, 0);
    	} else if (k==2 || k==3) {
    		return h*checkXY(fx, h/2, w+kEval(k-1, fx, w));
    	} else if (k == 4) {
    		return h*checkXY(fx, h, w+kEval(k-1, fx, w));
    	} else throw new IllegalArgumentException("eskere, smt wrong, check it");
    }
    
    public static void main(String[] args)
    {
        PhysicsEngine test = new PhysicsEngine("src\\example_inputfile.txt");
        int count = 0;
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
            test.runSimulation(2,0,false);
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
        }
    }
}
    
    
    
  