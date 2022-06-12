package com.mga1.game;


public class EulersMethod implements Solver
{
    private final PhysicsEngine physicsEngine;
    private final double h;
    public EulersMethod()
    {
        physicsEngine = PhysicsEngine.getPhysicsEngine();
        h = physicsEngine.h;
    }
    public double[] nextStep(double[] stateVector)
    {
        double[] stepVector = new double[4];
        double xAccel = physicsEngine.getAccel(true,false,stateVector);
        double yAccel = physicsEngine.getAccel(false,true,stateVector);

        stepVector[0] = stateVector[2];
        stepVector[1] = stateVector[3];
        stepVector[2] = xAccel;
        stepVector[3] = yAccel;
        for(int i = 0; i < stateVector.length; i++)
        {
            stateVector[i] = stateVector[i] + h * stepVector[i];
        }
        return stateVector;
    }
}
