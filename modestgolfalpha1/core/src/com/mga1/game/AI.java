package com.mga1.game;

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
                    physicsEngine.runSimulation(xSpeed,ySpeed,solver);
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
        double currBest = Integer.MAX_VALUE;
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
            double[] rightSpeed = {xSpeed - spread * targetParameters[2] * euclideanDistance, ySpeed + spread * targetParameters[2] * euclideanDistance};
            double[] leftSpeed = {xSpeed + spread * targetParameters[2] * euclideanDistance, ySpeed - spread * targetParameters[2] * euclideanDistance};
            double[] strongerSpeed = {xSpeed + spread * targetParameters[2] * euclideanDistance, ySpeed + spread * targetParameters[2] * euclideanDistance};

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

    private double distanceFromGoal(double[] vector)
    {
        return calculateEuclidean(targetParameters[0] - vector[0],targetParameters[1] - vector[1]);
    }

    private double calculateEuclidean(double x, double y)
    {
        return Math.sqrt(x * x + y * y);
    }
}
