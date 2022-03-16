import java.io.*;
import java.util.Stack;

public class PhysicsEngine
{
    double firstX, firstY, targetX, targetY, targetRadius;
    double sandX1, sandX2, sandY1, sandY2;
    double grassKinetic, grassStatic;
    double sandKinetic, sandStatic;
    String heightProfile;
    final double h = 0.01;
    final double g = 9.81;
    double[] stateVector = new double[4];
    static double[] newStateVector = new double[4];
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
        if(s.equals("x")) return x;
        else if(s.equals("y")) return y;
        else if(s.equals("e")) return 2.71828;
        else if(s.equals("pi")) return 3.18;
        else if(s.equals("g")) return 9.81;
        else return Double.parseDouble(s);
    }

    /**
     * This method updates the state vector using Euler's method.  It increments the current y and x position based on the speed
     * and it increments the current x and y speed based on the acceleration.  The acceleration (in a certain direction) is calculated
     * using the equations x'' = -g * ( dh / dx ) - muk * g ( vx / sqrt (vx ^ 2 + vy ^ 2 ) ) and
     * y'' = -g * ( dh / dy ) - muk * g ( vy / sqrt (vx ^ 2 + vy ^ 2 ) )
     * @param atRest is the state of the ball when the velocities reach 0
     */
    public void updateVector(boolean atRest)
    {
        double x = stateVector[0];
        double y = stateVector[1];
        double speedX = stateVector[2];
        double speedY = stateVector[3];

        double limitZero = 0.000000000001;
        double newX = x + limitZero;
        double newY = y + limitZero;

        double kineticDenominator = Math.sqrt(speedX * speedX + speedY * speedY);
        double kineticCoeff = sandX1 < x && x < sandX2 && sandY1 < y && y < sandY2 ? sandKinetic : grassKinetic;
        double slopeX = (Math.abs(getHeight(x,y) - getHeight(newX,y))) / limitZero;
        double slopeY = (Math.abs(getHeight(x,y) - getHeight(x, newY))) / limitZero;
        //System.out.println(getHeight(x,y) + " " + getHeight(newX,y));
        //System.out.println("slope X: " + slopeX + " slope y: " + slopeY);
        double secondTermX = atRest ? kineticCoeff * g * (slopeX / Math.sqrt(slopeX * slopeX + slopeY * slopeY)) : kineticCoeff * g * (speedX / kineticDenominator);
        double secondTermY = atRest ? kineticCoeff * g * (slopeY / Math.sqrt(slopeX * slopeX + slopeY * slopeY)) : kineticCoeff * g * (speedY / kineticDenominator);
        double xAccel = -g * slopeX - secondTermX;
        double yAccel = -g * slopeY - secondTermY;
        //System.out.println(secondTermX + " " + secondTermY);
        //System.out.println("x acceleration: " + xAccel + " y acceleration: " + yAccel);

        newStateVector[0] = stateVector[2];
        newStateVector[1] = stateVector[3];
        newStateVector[2] = xAccel;
        newStateVector[3] = yAccel;
        for(int i = 0; i < stateVector.length; i++)
        {
            System.out.println(stateVector[i] + " + " + h + " * " + newStateVector[i]);
            stateVector[i] = stateVector[i] + h * newStateVector[i];
            System.out.println(stateVector[i]);
        }
        System.out.println();
    }

    /**
     * Checks if the ball will move after reaching 0 total velocity by calculating the slope and comparing it to the static friction coefficient
     * @param x is the current x coordinate
     * @param y is the current y coordinate
     * @return true if the ball will stay at rest, false if not
     */
    public boolean atRest(double x, double y)
    {
        double limitZero = 0.000000000001;
        double derivativeX = (Math.abs(getHeight(x,y) - getHeight(x + limitZero,y))) / limitZero;
        double derivativeY = (Math.abs(getHeight(x,y) - getHeight(x,y + limitZero))) / limitZero;
        System.out.println(derivativeX + " " + derivativeY);
        if(grassStatic > Math.sqrt(derivativeX * derivativeX + derivativeY * derivativeY)) return true;
        else return false;
    }

    /**
     * Updates the state vector based on some conditions given an initial speed in the X and Y direction
     * @param initSpeedX is the initial speed in the X direction
     * @param initSpeedY is the initial speed in the Y direction
     */
    public void runSimulation(double initSpeedX, double initSpeedY)
    {
        //if(!initSpeedsDefined)
        //{
            stateVector[0] = firstX;
            stateVector[1] = firstY;
            stateVector[2] = initSpeedX;
            stateVector[3] = initSpeedY;
            initSpeedsDefined = true;
        //}

        while(true)
        {
            if(Math.abs(stateVector[2]) < 0.2 && Math.abs(stateVector[3]) < 0.2)
            {
                break;
            }
            updateVector(false);
        }

        /*if (getHeight(stateVector[0], stateVector[1]) < 0)
        {
            System.out.println("Ball landed in water!");
        }
        updateVector(false);*/
    }

    public static void main(String[] args)
    {
        PhysicsEngine test = new PhysicsEngine("C:\\Users\\mspisak\\IdeaProjects\\CrazyPutting\\src\\example_inputfile.txt");
        //while(Math.abs(test.stateVector[2]) < 0.1 && Math.abs(test.stateVector[3]) < 0.1)
        //{
            test.runSimulation(2,0);
        //}
    }
}
