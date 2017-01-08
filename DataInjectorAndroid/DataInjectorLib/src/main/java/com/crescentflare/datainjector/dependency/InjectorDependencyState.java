package com.crescentflare.datainjector.dependency;

/**
 * Data injector dependency: the state
 * Used to keep track of the dependency state
 */
public enum InjectorDependencyState
{
    Pending,
    Obtaining,
    Refreshing,
    Resolved,
    ObtainError,
    RefreshError
}
