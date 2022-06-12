package com.mga1.game;

public class RungeKutta2 implements Solver
{
    private final PhysicsEngine physicsEngine;
    private final double h;
    public RungeKutta2()
    {
        physicsEngine = PhysicsEngine.getPhysicsEngine();
        h = physicsEngine.h;
    }
    public double[] nextStep(double[] stateVector)
    {
        double fx = physicsEngine.getAccel(true,false,stateVector);
        double fy = physicsEngine.getAccel(false,true,stateVector);

        double[] newStateVector = new double[4];

        newStateVector[0] = stateVector[0] + (2 * h) / 3 * stateVector[2];
        newStateVector[1] = stateVector[1] + (2 * h) / 3 * stateVector[3];
        newStateVector[2] = stateVector[2] + (2 * h) / 3 * fx;
        newStateVector[3] = stateVector[3] + (2 * h) / 3 * fy;

        double newfx = physicsEngine.getAccel(true, false, newStateVector);
        double newfy = physicsEngine.getAccel(false, true, newStateVector);

        stateVector[0] += 0.25 * h * (stateVector[2] + 3 * newStateVector[2]);
        stateVector[1] += 0.25 * h * (stateVector[3] + 3 * newStateVector[3]);
        stateVector[2] += 0.25 * h * (fx + 3 * newfx);
        stateVector[3] += 0.25 * h * (fy + 3 * newfy);

        return stateVector;
    }
}
