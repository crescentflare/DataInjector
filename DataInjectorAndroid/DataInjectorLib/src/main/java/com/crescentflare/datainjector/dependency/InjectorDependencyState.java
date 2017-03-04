package com.crescentflare.datainjector.dependency;

/**
 * Data injector dependency: the state
 * Used to keep track of the dependency state
 */
public enum InjectorDependencyState
{
    Idle(0),
    Loading(1),
    Resolved(2),
    Refreshing(3),
    Error(4),
    RefreshError(6);

    private int intValue;

    InjectorDependencyState(int intValue)
    {
        this.intValue = intValue;
    }

    public boolean isTypeOfState(InjectorDependencyState state)
    {
        if (state == Idle && this == Idle)
        {
            return true;
        }
        return (intValue & state.intValue) == state.intValue;
    }
}
