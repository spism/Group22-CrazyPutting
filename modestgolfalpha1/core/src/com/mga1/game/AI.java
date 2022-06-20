package com.mga1.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI
{
    private double[] stateVector;
    private final double[] targetParameters = new double[3];
    private final double firstX, firstY;
    PhysicsEngine physicsEngine;

    public AI()
    {
        physicsEngine = PhysicsEngine.getPhysicsEngine();
        firstX = physicsEngine.firstX;
        firstY = physicsEngine.firstY;
        targetParameters[0] = physicsEngine.targetX;
        targetParameters[1] = physicsEngine.targetY;
        targetParameters[2] = physicsEngine.targetRadius;
    }

    public double[] ruleBasedBot(int solver)
    {
        int iterationNumber = 0;
        for(double ySpeed = -5; ySpeed < 5; ySpeed = ySpeed + 0.5 * targetParameters[2])
        {
            System.out.println("Trying new Y speed!!!!!!!!!!!");
            for(double xSpeed = -5; xSpeed < 5; xSpeed = xSpeed + 0.5 * targetParameters[2])
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
                    physicsEngine.runSimulation(firstX,firstY,xSpeed,ySpeed,solver);
                    if(physicsEngine.inHole(stateVector[0],stateVector[1]))
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
        System.out.println("Initial speeds are: " + xSpeed + " " + ySpeed);
        double totalBest = Integer.MAX_VALUE;
        double localBest = Integer.MAX_VALUE;
        double[] bestPos = new double[2];
        double[] localPos = physicsEngine.takeShot(firstX,firstY,new double[]{xSpeed,ySpeed},solver);
        int prevIndex = -1;
        int iterationNumber = 0;


        //used to find the current speed using random values
        if(physicsEngine.inHole(bestPos[0],bestPos[1]))
        {
            return bestSpeeds;
        }
        while(!physicsEngine.inHole(bestPos[0], bestPos[1]))
        {
            iterationNumber++;
            assert localPos != null;
            double euclideanDistance = distanceFromGoal(localPos);

            double spread = 2;
            //left and right speeds (the current speed is change by a bit in both directions) are evaluated and the better one is chosen
            double[] rightSpeed = {xSpeed + sign(ySpeed) * spread * targetParameters[2] * euclideanDistance, ySpeed - sign(xSpeed) * spread * targetParameters[2] * euclideanDistance};
            double[] leftSpeed = {xSpeed - sign(ySpeed) * spread * targetParameters[2] * euclideanDistance, ySpeed + sign(xSpeed) * spread * targetParameters[2] * euclideanDistance};
            double[] strongerSpeed = {xSpeed + sign(xSpeed) * spread * targetParameters[2] * euclideanDistance, ySpeed + sign(ySpeed) * spread * targetParameters[2] * euclideanDistance};

            //order is very important, keep shots in order left -> center -> right with respective indices 0, 1, 2 or any multiple of those
            double[][] speeds = {leftSpeed,strongerSpeed,rightSpeed};

            double[] centerShot = physicsEngine.takeShot(firstX,firstY,strongerSpeed,solver);
            if(centerShot == null)
            {
                System.out.println("Finding the speeds took " + iterationNumber + " iterations");
                return strongerSpeed;
            }

            double[] rightShot = physicsEngine.takeShot(firstX,firstY,rightSpeed,solver);
            if(rightShot == null)
            {
                System.out.println("Finding the speeds took " + iterationNumber + " iterations");
                return rightSpeed;
            }

            double[] leftShot = physicsEngine.takeShot(firstX,firstY,leftSpeed,solver);
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
            else if(localBest < totalBest)
            {
                //if local peak has been reached, check if the local peak is indeed better than the current located global peak
                System.out.println("Improvement!  New best position!");
                System.out.println(localBest);
                totalBest = localBest;
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

                xSpeed = (random.nextInt(2) - 1) * random.nextDouble() * 5;
                ySpeed = (random.nextInt(2) - 1) * random.nextDouble() * 5;

                localPos = physicsEngine.takeShot(firstX,firstY,new double[]{xSpeed,ySpeed},solver);
                if(localPos == null)
                {
                    System.out.println("Finding the speeds took " + iterationNumber + " iterations");
                    return new double[]{xSpeed,ySpeed};
                }
            }

        }
        System.out.println("Finding the speeds took " + iterationNumber + " iterations");
        return bestSpeeds;
    }

    private static int sign(double number)
    {
        if(number > 0) return 1;
        else if(number == 0) return 0;
        else return -1;
    }

    public List<double[]> mazeAI(int solver)
    {
        Random rand = new Random(System.currentTimeMillis());
        ArrayList<double[]> shotsTaken = new ArrayList<>();
        double currX = firstX;
        double currY = firstY;

        while(!physicsEngine.inHole(currX,currY))
        {
            //System.out.println("Current ball position: " + currX + " " + currY);
            //System.out.println("taking another shot...");
            double vx = (rand.nextInt(2) - 1) * rand.nextDouble() * 5; // make the random shots lean towards the goal, that should speed up by a lot
            double vy = (rand.nextInt(2) - 1) * rand.nextDouble() * 5;
            String[] shotInfo = localize(new double[]{currX,currY},new double[]{vx,vy},solver).split(" ");
            double[] localSpeeds = {Double.parseDouble(shotInfo[1]),Double.parseDouble(shotInfo[2])};
            //System.out.println(localSpeeds[0] + " " + localSpeeds[1]);
            shotsTaken.add(localSpeeds);
            if(shotInfo[0].equals("yes"))
            {
                break;
            }
            double[] shotTaken = physicsEngine.takeShot(currX,currY,localSpeeds,solver);
            currX = shotTaken[0];
            currY = shotTaken[1];
        }

        System.out.println(currX + " " + currY);
        return shotsTaken;
    }

    public String localize(double[] initPos, double[] initSpeeds, int solver)
    {
        double xSpeed = initSpeeds[0];
        double ySpeed = initSpeeds[1];
        double currBest = shotScore(physicsEngine.takeShot(initPos[0],initPos[1],initSpeeds,solver),solver);
        double distanceFromGoal = distanceFromGoal(initPos);

        while(true)
        {
            //System.out.println("localizing...");

            double spread = distanceFromGoal / (4 * badShots(initPos,solver));

            //left and right speeds (the current speed is changed by a bit in both directions) are evaluated and the better one is chosen
            double[] rightSpeed = {xSpeed + (sign(ySpeed) * spread * targetParameters[2]), ySpeed - (sign(xSpeed) * spread * targetParameters[2])};
            double[] leftSpeed = {xSpeed - (sign(ySpeed) * spread * targetParameters[2]), ySpeed + (sign(xSpeed) * spread * targetParameters[2])};
            double[] strongerSpeed = {xSpeed + (sign(xSpeed) * spread * targetParameters[2]), ySpeed + (sign(ySpeed) * spread * targetParameters[2])};

            //order is very important, keep shots in order left -> center -> right with respective indices 0, 1, 2 or any multiple of those
            double[][] speeds = {leftSpeed,strongerSpeed,rightSpeed};

            double[] strongerShot = physicsEngine.takeShot(initPos[0],initPos[1],strongerSpeed,solver);
            if(strongerShot == null)
            {
                return "yes " + strongerSpeed[0] + " " + strongerSpeed[1];
            }

            double[] rightShot = physicsEngine.takeShot(initPos[0],initPos[1],rightSpeed,solver);
            if(rightShot == null)
            {
                return "yes " + rightSpeed[0] + " " + rightSpeed[1];
            }

            double[] leftShot = physicsEngine.takeShot(initPos[0],initPos[1],leftSpeed,solver);
            if(leftShot == null)
            {
                return "yes " + leftSpeed[0] + " " + leftSpeed[1];
            }

            double min = Integer.MAX_VALUE;
            int index = -1;
            double[] shotScores = {shotScore(leftShot,solver),shotScore(strongerShot,solver),shotScore(rightShot,solver)};
            for(int i = 0; i < 3; i++)
            {
                //System.out.println(shotScores[i]);
                if(shotScores[i] < min)
                {
                    min = shotScores[i];
                    index = i;
                }
            }

            if(min < currBest)
            {
                currBest = min;
                xSpeed = speeds[index][0];
                ySpeed = speeds[index][1];
                //System.out.println("Speeds found: " + xSpeed + " " + ySpeed);
            }
            else
            {
                //System.out.println("no improvement found");
                break;
            }
        }
        return "no " + xSpeed + " " + ySpeed;
    }

    private double shotScore(double[] endPos, int solver)
    {
        if(physicsEngine.inWater(endPos[0],endPos[1])) return Integer.MAX_VALUE;
        double badShots = badShots(endPos,solver);
        System.out.println("Number of bad shots: " + badShots);
        return 0.05 * badShots + distanceFromGoal(endPos);
    }

    private double badShots(double[] pos, int solver)
    {
        double badShots = 0;
        double vx = 5;
        double vy = 5;
        while(vx != 5 - targetParameters[2] || vy != 5 + targetParameters[2])
        {
            //System.out.println(vx + " " + vy);
            //System.out.println("Comparing " + pos[0] + " " + pos[1]);
            double[] shot = physicsEngine.takeShot(pos[0],pos[1],new double[]{vx,vy},solver);
            //System.out.println("to" + shot[0] + " " + shot[1]);
            if(physicsEngine.inWater(shot[0],shot[1])) badShots++;
            if(physicsEngine.inSand(shot[0],shot[1])) badShots += 0.5; // 0.5 is an arbitrary number, only to indicate that sand shots are not as good as normal ones
            vx += sign(vy) * targetParameters[2];
            vy += sign(vx) * targetParameters[2];
            //System.out.println("changed" + vx + " " + vy);
        }
        return badShots;
    }

    private double distanceFromGoal(double[] vector)
    {
        return calculateEuclidean(targetParameters[0] - vector[0],targetParameters[1] - vector[1]);
    }

    private double calculateEuclidean(double x, double y)
    {
        return Math.sqrt(x * x + y * y);
    }
}
