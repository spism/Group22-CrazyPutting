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
    double[] stateVector = {firstX, firstY, 0, 0};
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
                    sandX1 = Double.parseDouble(arr[3]);
                    sandX2 = Double.parseDouble(arr[5]);
                    arr = br.readLine().split(" ");
                    sandY1 = Double.parseDouble(arr[3]);
                    sandY2 = Double.parseDouble(arr[5]);
                }
                else if(lineIndex == 5)
                {
                    sandKinetic = Double.parseDouble(arr[3]);
                    arr = br.readLine().split(" ");
                    sandStatic = Double.parseDouble(arr[3]);
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
                double val = vals.pop();
                if(operator.equals("+")) val = vals.pop() + val;
                else if(operator.equals("-")) val = vals.pop() - val;
                else if(operator.equals("*")) val = vals.pop() * val;
                else if(operator.equals("/")) val = vals.pop() / val;
                else if(operator.equals("sin")) val = Math.sin(vals.pop());
                else if(operator.equals("cos")) val = Math.cos(vals.pop());
                else if(operator.equals("sqrt")) val = Math.sqrt(vals.pop());
                else if(operator.equals("abs")) val = Math.abs(vals.pop());
                vals.push(val);
            }
            else vals.push(checkXY(curr,x,y));
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
     *
     * @param initX in meters
     * @param initY in meters
     * @param initSpeedX in meters per second
     * @param initSpeedY in meters per second
     */
    public void updateVector(double initX, double initY, double initSpeedX, double initSpeedY)
    {
        double kineticDenominator = Math.sqrt(initSpeedX * initSpeedX + initSpeedY * initSpeedY);
        double newX = initX + h * initSpeedX;
        double newY = initY + h * initSpeedY;
        double kineticCoeff = sandX1 < initX && initX < sandX2 && sandY1 < initY && initY < sandY2 ? sandKinetic : grassKinetic;
        double xAccel = -g * ((Math.abs(getHeight(heightProfile,initX,initY) - getHeight(heightProfile,newX, newY))) / (h * initSpeedX)) - kineticCoeff * g * (initSpeedX / kineticDenominator);
        double yAccel = -g * ((Math.abs(getHeight(heightProfile,initX,initY) - getHeight(heightProfile,newX, newY))) / (h * initSpeedY)) - kineticCoeff * g * (initSpeedY / kineticDenominator);
        double[] newStateVector = {initSpeedX, initSpeedY, xAccel, yAccel};
        for(int i = 0; i < stateVector.length; i++)
        {
            stateVector[i] = stateVector[i] + h * newStateVector[i];
        }
    }

    public boolean atRest(double x, double y)
    {
        double derivative = (getHeight(heightProfile,x,y) - getHeight(heightProfile,x + 0.0000001, y + 0.0000001)) / 0.0000001;
        if(grassStatic > Math.sqrt(derivative * derivative + derivative * derivative)) return true;
        else return false;
    }

    public void runSimulation(double initX, double initY, double initSpeedX, double initSpeedY)
    {
        while(stateVector[0] != 0 && stateVector[1] != 0) updateVector(initX,initY,initSpeedX,initSpeedY);
        if(stateVector[0] == 0 && stateVector[1] == 0) while(stateVector[2] != 0 && stateVector[3] != 0) updateVector(stateVector[0],stateVector[1],stateVector[2],stateVector[3]);
    }

    public static void main(String[] args)
    {
        PhysicsEngine test = new PhysicsEngine("C:\\Users\\mspisak\\IdeaProjects\\CrazyPutting\\src\\example_inputfile.txt");
        test.updateVector(test.firstX,test.firstY,2,1);
    }
}