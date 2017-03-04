package com.crescentflare.datainjector.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Data injector dependency: manage dependencies
 * Provide a central place to keep track of all dependencies within the application
 */
public class InjectorDependencyManager
{
    // ---
    // Singleton instance
    // ---

    public static final InjectorDependencyManager instance = new InjectorDependencyManager();


    // ---
    // Members
    // ---

    private List<InjectorDependency> dependencies = new ArrayList<>();
    private List<DependencyUpdateListener> updateListeners = new ArrayList<>();


    // ---
    // Initialization
    // ---

    private InjectorDependencyManager()
    {
    }


    // ---
    // Dependency management
    // ---

    public void addDependency(InjectorDependency dependency)
    {
        dependencies.add(dependency);
    }

    public InjectorDependency getDependency(String name)
    {
        for (InjectorDependency dependency : dependencies)
        {
            if (dependency.getName().equals(name))
            {
                return dependency;
            }
        }
        return null;
    }

    public List<InjectorDependency> getDependencies(List<String> names)
    {
        List<InjectorDependency> dependencyList = new ArrayList<>();
        for (InjectorDependency dependency : dependencies)
        {
            if (names.contains(dependency.getName()))
            {
                dependencyList.add(dependency);
            }
        }
        return dependencyList;
    }

    public String dependencyNameFrom(String injectSource)
    {
        if (injectSource != null && !injectSource.startsWith("@.") && injectSource.startsWith("@"))
        {
            String sourcePath[] = injectSource.split("\\.");
            if (sourcePath.length > 0)
            {
                return sourcePath[0].substring(1);
            }
        }
        return null;
    }


    // ---
    // Filtering dependencies
    // ---

    public List<InjectorDependency> filterBaseDependencies(List<InjectorDependency> dependencies)
    {
        List<InjectorDependency> baseDependencies = new ArrayList<>();
        for (InjectorDependency dependency : dependencies)
        {
            boolean foundBase = false;
            for (InjectorDependency checkDependency : dependency.getDependencies())
            {
                if (dependencies.contains(checkDependency))
                {
                    foundBase = true;
                    break;
                }
            }
            if (!foundBase)
            {
                baseDependencies.add(dependency);
            }
        }
        return baseDependencies;
    }

    public List<InjectorDependency> filterDependenciesForState(List<InjectorDependency> dependencies, InjectorDependencyState state)
    {
        return filterDependenciesForState(dependencies, state, true);
    }

    public List<InjectorDependency> filterDependenciesForState(List<InjectorDependency> dependencies, InjectorDependencyState state, boolean includeBase)
    {
        List<InjectorDependency> filteredList = new ArrayList<>();
        for (InjectorDependency dependency : dependencies)
        {
            if (dependency.getState().isTypeOfState(state))
            {
                filteredList.add(dependency);
            }
            if (includeBase)
            {
                filteredList.addAll(filterDependenciesForState(dependency.getDependencies(), state));
            }
        }
        return new ArrayList<>(new HashSet<>(filteredList));
    }

    public List<InjectorDependency> filterDependenciesExcludingState(List<InjectorDependency> dependencies, InjectorDependencyState state)
    {
        return filterDependenciesExcludingState(dependencies, state, true);
    }

    public List<InjectorDependency> filterDependenciesExcludingState(List<InjectorDependency> dependencies, InjectorDependencyState state, boolean includeBase)
    {
        List<InjectorDependency> filteredList = new ArrayList<>();
        for (InjectorDependency dependency : dependencies)
        {
            if (!dependency.getState().isTypeOfState(state))
            {
                filteredList.add(dependency);
            }
            if (includeBase)
            {
                filteredList.addAll(filterDependenciesExcludingState(dependency.getDependencies(), state));
            }
        }
        return new ArrayList<>(new HashSet<>(filteredList));
    }


    // ---
    // Resolving dependencies
    // ---

    public void resolveDependency(InjectorDependency dependency)
    {
        resolveDependency(dependency, false, null);
    }

    public void resolveDependency(InjectorDependency dependency, boolean forceRefresh)
    {
        resolveDependency(dependency, forceRefresh, null);
    }

    public void resolveDependency(InjectorDependency dependency, Map<String, String> input)
    {
        resolveDependency(dependency, false, input);
    }

    public void resolveDependency(final InjectorDependency dependency, boolean forceRefresh, Map<String, String> input)
    {
        // If the item is already busy resolving, bail out and wait for the existing resolution to be complete
        if (dependency.getState().isTypeOfState(InjectorDependencyState.Loading))
        {
            return;
        }

        // Return if the dependency is already up to date
        if (dependency.getState().isTypeOfState(InjectorDependencyState.Resolved) && !dependency.isExpired() && !forceRefresh)
        {
            for (DependencyUpdateListener listener : updateListeners)
            {
                listener.onDependencyResolved(dependency);
            }
            return;
        }

        // Check for input needed to resolve dependency
        boolean hasRequiredInput = true;
        Map<String, String> sendInput = new HashMap<>();
        for (String key : dependency.getRequiresInput())
        {
            if (input != null && input.containsKey(key))
            {
                sendInput.put(key, input.get(key));
            }
            else
            {
                hasRequiredInput = false;
                break;
            }
        }

        // Try to resolve the dependency or fail without enough input
        if (hasRequiredInput)
        {
            dependency.setState(dependency.getState().isTypeOfState(InjectorDependencyState.Resolved) ? InjectorDependencyState.Refreshing : InjectorDependencyState.Loading);
            dependency.resolve(sendInput, new InjectorDependency.CompleteListener()
            {
                @Override
                public void onResolveResult(boolean success)
                {
                    if (success)
                    {
                        dependency.resetExpiration();
                        dependency.setState(InjectorDependencyState.Resolved);
                        for (DependencyUpdateListener listener : updateListeners)
                        {
                            listener.onDependencyResolved(dependency);
                        }
                    }
                    else
                    {
                        dependency.setState(dependency.getState().isTypeOfState(InjectorDependencyState.Resolved) ? InjectorDependencyState.RefreshError : InjectorDependencyState.Error);
                        for (DependencyUpdateListener listener : updateListeners)
                        {
                            listener.onDependencyFailed(dependency, "resolveError");
                        }
                    }
                }
            });
        }
        else
        {
            for (DependencyUpdateListener listener : updateListeners)
            {
                listener.onDependencyFailed(dependency, "missingInput");
            }
        }
    }


    // ---
    // Handling data
    // ---

    public Map<String, Object> generateInjectableData()
    {
        Map<String, Object> data = new HashMap<>();
        for (InjectorDependency dependency : dependencies)
        {
            Object injectionData = dependency.obtainInjectableData();
            if (injectionData != null)
            {
                data.put(dependency.getName(), injectionData);
            }
        }
        return data;
    }


    // ---
    // Update listener
    // ---

    public void addUpdateListener(DependencyUpdateListener listener)
    {
        if (!updateListeners.contains(listener))
        {
            updateListeners.add(listener);
        }
    }

    public void removeUpdateListener(DependencyUpdateListener listener)
    {
        if (updateListeners.contains(listener))
        {
            updateListeners.remove(listener);
        }
    }

    public interface DependencyUpdateListener
    {
        void onDependencyResolved(InjectorDependency dependency);
        void onDependencyFailed(InjectorDependency dependency, String reason);
    }
}
