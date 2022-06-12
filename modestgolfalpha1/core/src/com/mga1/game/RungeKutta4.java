package com.mga1.game;

public class RungeKutta4 implements Solver
{
    private final PhysicsEngine physicsEngine;
    private final double h;
    public RungeKutta4()
    {
        physicsEngine = PhysicsEngine.getPhysicsEngine();
        h = physicsEngine.h;
    }
    public double[] nextStep(double[] stateVector)
    {
        double[][] k = new double[4][4];

        k[0][0] = h * stateVector[2];
        k[1][0] = h * stateVector[3];
        k[2][0] = h * physicsEngine.getAccel(true,false,stateVector);
        k[3][0] = h * physicsEngine.getAccel(false,true,stateVector);

        double[] newStateVector = new double[4];
        for(int i = 0; i < 4; i++)
        {
            newStateVector[i] = stateVector[i] + 0.5 * k[i][0];
        }

        k[0][1] = h * newStateVector[2];
        k[1][1] = h * newStateVector[3];
        k[2][1] = h * physicsEngine.getAccel(true,false,newStateVector);
        k[3][1] = h * physicsEngine.getAccel(false,true,newStateVector);

        for(int i = 0; i < 4; i++)
        {
            newStateVector[i] = stateVector[i] + 0.5 * k[i][1];
        }

        k[0][2] = h * newStateVector[2];
        k[1][2] = h * newStateVector[3];
        k[2][2] = h * physicsEngine.getAccel(true,false,newStateVector);
        k[3][2] = h * physicsEngine.getAccel(false,true,newStateVector);

        for(int i = 0; i < 4; i++)
        {
            newStateVector[i] = stateVector[i] + k[i][2];
        }

        k[0][3] = h * newStateVector[2];
        k[1][3] = h * newStateVector[3];
        k[2][3] = h * physicsEngine.getAccel(true,false,newStateVector);
        k[3][3] = h * physicsEngine.getAccel(false,true,newStateVector);

        for(int i = 0; i < 4; i++)
        {
            stateVector[i] += (k[i][0] + 2 * k[i][1] + 2 * k[i][2] + k[i][3]) / 6;
        }

        return stateVector;
    }
}
