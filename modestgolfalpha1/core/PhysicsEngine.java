import java.io.*;
import java.util.Stack;

public class PhysicsEngine
{
    double firstX, firstY, targetX, targetY, targetRadius;
    double sandX1, sandX2, sandY1, sandY2;
    double grassKinetic, grassStatic;
    double sandKinetic, sandStatic;
    String heightProfile;
    final double h = 0.1;
    final double g = 9.81;
    double[] stateVector = new double[4];
    static double[] newStateVector = new double[4];
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
                    firstX = Integer.parseInt(arr[arr.length - 1]);
                    arr = br.readLine().split(" ");
                    firstY = Integer.parseInt(arr[arr.length - 1]);
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
     * This method evaluates the expression for height for a given X and Y coordinate.
     * @param heightProfile is the height expression
     * @param x is the x coordinate
     * @param y is the y coordinate
     * @return the height for the coordinates
     */
    public double getHeight(String heightProfile, double x, double y)
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
            else if(curr.equals("sin")) ops.push(curr);
            else if(curr.equals("cos")) ops.push(curr);
            else if(curr.equals("sqrt")) ops.push(curr);
            else if(curr.equals("abs")) ops.push(curr);
            else if(curr.equals(")"))
            {
                String operator = ops.pop();
                //System.out.println(operator);
                double val = vals.pop();
                //System.out.println(val);
                if(operator.equals("+")) val = vals.pop() + val;
                else if(operator.equals("-")) val = vals.pop() - val;
                else if(operator.equals("*")) val = vals.pop() * val;
                else if(operator.equals("/")) val = vals.pop() / val;
                else if(operator.equals("sin")) val = Math.sin(val);
                else if(operator.equals("cos")) val = Math.cos(val);
                else if(operator.equals("sqrt")) val = Math.sqrt(val);
                else if(operator.equals("abs")) val = Math.abs(val);
                //System.out.println(val);
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
        else return Double.parseDouble(s);
    }

    /**
     * This method updates the state vector
     */
    public void updateVector(boolean atRest)
    {
        double x = stateVector[0];
        double y = stateVector[1];
        double speedX = stateVector[2];
        double speedY = stateVector[3];

        double limitZero = Double.MIN_VALUE;
        double newX = x + limitZero;
        double newY = y + limitZero;

        double kineticDenominator = Math.sqrt(speedX * speedX + speedY * speedY);
        double kineticCoeff = sandX1 < x && x < sandX2 && sandY1 < y && y < sandY2 ? sandKinetic : grassKinetic;
        double slopeX = (Math.abs(getHeight(heightProfile,x,y) - getHeight(heightProfile,newX,y))) / limitZero;
        double slopeY = (Math.abs(getHeight(heightProfile,x,y) - getHeight(heightProfile,x, newY))) / limitZero;
        //System.out.println(getHeight(heightProfile,x,y) + " " + getHeight(heightProfile,newX,y));
        double secondTermX = atRest ? kineticCoeff * g * (slopeX / Math.sqrt(slopeX * slopeX + slopeY * slopeY)) : kineticCoeff * g * (speedX / kineticDenominator);
        double secondTermY = atRest ? kineticCoeff * g * (slopeY / Math.sqrt(slopeX * slopeX + slopeY * slopeY)) : kineticCoeff * g * (speedY / kineticDenominator);
        double xAccel = -g * slopeX - secondTermX;
        double yAccel = -g * slopeY - secondTermY;
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

    public boolean atRest(double x, double y)
    {
        double limitZero = Double.MIN_VALUE;
        double derivativeX = (Math.abs(getHeight(heightProfile,x,y) - getHeight(heightProfile,x + limitZero,y))) / limitZero;
        double derivativeY = (Math.abs(getHeight(heightProfile,x,y) - getHeight(heightProfile,x,y + limitZero))) / limitZero;
        if(grassStatic > Math.sqrt(derivativeX * derivativeX + derivativeY * derivativeY)) return true;
        else return false;
    }

    public void runSimulation(double initX, double initY, double initSpeedX, double initSpeedY)
    {
        stateVector[0] = initX;
        stateVector[1] = initY;
        stateVector[2] = initSpeedX;
        stateVector[3] = initSpeedY;

        while(true)
        {
            if(Math.abs(stateVector[2]) < 0.1 && Math.abs(stateVector[3]) < 0.1)
            {
                while(!atRest(stateVector[0],stateVector[1]))
                {
                    updateVector(true);
                }
                break;
            }
            updateVector(false);
        }
    }

    public static void main(String[] args)
    {
        PhysicsEngine test = new PhysicsEngine("C:\\Users\\Matej Spisak\\IdeaProjects\\crazyPutting\\src\\example_inputfile.txt");
        test.runSimulation(test.firstX,test.firstY,1,0);
    }
}