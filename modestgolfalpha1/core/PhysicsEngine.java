import java.io.*;
import java.util.Stack;

public class PhysicsEngine
{
    double firstX, firstY, targetX, targetY, targetRadius;
    double grassKinetic, grassStatic;
    String heightProfile;
    final double h = 0.1;
    final double g = 9.81;
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
     * @param initX
     * @param initY
     * @param initSpeedX in m/s
     * @param initSpeedY in m/s
     * @param timeframe is how many seconds per frame
     */
    public void acceleration(double initX, double initY, double initSpeedX, double initSpeedY, double timeframe)
    {
        double kineticDenominator = Math.sqrt(initSpeedX * initSpeedX + initSpeedY * initSpeedY);
        double xAccel =  -g * ((Math.abs(getHeight(heightProfile,initX,initY) - getHeight(heightProfile,initX + timeframe * initSpeedX, initY + timeframe * initSpeedY)))/(timeframe * initSpeedX)) - grassKinetic * g * (initSpeedX / kineticDenominator);
        double yAccel = -g * ((Math.abs(getHeight(heightProfile,initX,initY) - getHeight(heightProfile,initX + timeframe * initSpeedX, initY + timeframe * initSpeedY)))/(timeframe * initSpeedY)) - grassKinetic * g * (initSpeedY / kineticDenominator);
        System.out.println(xAccel + " " + yAccel);
    }

    public static void main(String[] args)
    {
        PhysicsEngine test = new PhysicsEngine("C:\\Users\\mspisak\\IdeaProjects\\CrazyPutting\\src\\example_inputfile.txt");
        test.acceleration(test.firstX,test.firstY,2,1,0.1);
    }
}
