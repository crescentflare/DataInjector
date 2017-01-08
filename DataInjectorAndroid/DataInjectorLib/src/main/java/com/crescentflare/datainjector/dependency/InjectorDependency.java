package com.crescentflare.datainjector.dependency;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorDataDetector;
import com.crescentflare.datainjector.utility.InjectorDataType;
import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data injector dependency: defines a dependency
 * Keeps track of a dependency item
 */
public class InjectorDependency
{
    // ---
    // Members
    // ---

    private int lastUpdated = 0;
    private InjectorDependencyState state = InjectorDependencyState.Pending;
    private int expiration = -1;
    private List<String> requiresInput = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();


    // ---
    // Initialization
    // ---

    public InjectorDependency()
    {
    }


    // ---
    // Data access
    // ---

    public Object obtainInjectableData()
    {
        return null;
    }


    // ---
    // State checking
    // ---

    public void resetExpiration()
    {
        lastUpdated = (int)(System.currentTimeMillis() / 1000);
    }

    public boolean isExpired()
    {
        if (expiration >= 0)
        {
            int currentTimeSeconds = (int)(System.currentTimeMillis() / 1000);
            return currentTimeSeconds - lastUpdated >= expiration;
        }
        return false;
    }

    public boolean isError()
    {
        return state == InjectorDependencyState.ObtainError || state == InjectorDependencyState.RefreshError;
    }


    // ---
    // Resolving
    // ---

    public void resolve(Map<String, String> input, CompleteListener completeListener)
    {
        completeListener.onResolveResult(false);
    }


    // ---
    // Change values
    // ---

    public InjectorDependencyState getState()
    {
        return state;
    }

    public void setState(InjectorDependencyState state)
    {
        this.state = state;
    }

    public void setExpiration(int expiration)
    {
        this.expiration = expiration;
    }

    public List<String> getRequiresInput()
    {
        return requiresInput;
    }

    public void setRequiresInput(List<String> requiresInput)
    {
        this.requiresInput = requiresInput;
    }

    public List<String> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies)
    {
        this.dependencies = dependencies;
    }


    // ---
    // Complete listener
    // ---

    public interface CompleteListener
    {
        void onResolveResult(boolean success);
    }
}
