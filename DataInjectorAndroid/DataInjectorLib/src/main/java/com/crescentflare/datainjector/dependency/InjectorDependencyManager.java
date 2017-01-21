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

    private Map<String, InjectorDependency> dependencies = new HashMap<>();
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

    public void addDependency(String name, InjectorDependency dependency)
    {
        dependencies.put(name, dependency);
    }

    public InjectorDependency getDependency(String name)
    {
        return dependencies.get(name);
    }

    public String dependencyNameFrom(String injectSource)
    {
        if (injectSource != null && !injectSource.startsWith("@.") && injectSource.startsWith("@"))
        {
            String sourcePath[] = injectSource.split("\\.");
            if (sourcePath.length > 0)
            {
                return sourcePath[0];
            }
        }
        return null;
    }


    // ---
    // Resolving dependencies
    // ---

    public List<String> getDependenciesInProgress(List<String> checkDependencies)
    {
        return getDependenciesInProgress(checkDependencies, true);
    }

    public List<String> getDependenciesInProgress(List<String> checkDependencies, boolean includeBase)
    {
        List<String> inProgress = new ArrayList<>();
        for (String checkDependency : checkDependencies)
        {
            InjectorDependency dependencyItem = dependencies.get(checkDependency);
            if (dependencyItem != null)
            {
                if (dependencyItem.getState() == InjectorDependencyState.Obtaining || dependencyItem.getState() == InjectorDependencyState.Refreshing)
                {
                    inProgress.add(checkDependency);
                }
                if (includeBase)
                {
                    inProgress.addAll(getDependenciesInProgress(dependencyItem.getDependencies(), true));
                }
            }
        }
        return new ArrayList<>(new HashSet<>(inProgress));
    }

    public List<String> getDependenciesWithError(List<String> checkDependencies)
    {
        return getDependenciesWithError(checkDependencies, true);
    }

    public List<String> getDependenciesWithError(List<String> checkDependencies, boolean includeBase)
    {
        List<String> withError = new ArrayList<>();
        for (String checkDependency : checkDependencies)
        {
            InjectorDependency dependencyItem = dependencies.get(checkDependency);
            if (dependencyItem != null)
            {
                if (dependencyItem.isError())
                {
                    withError.add(checkDependency);
                }
                if (includeBase)
                {
                    withError.addAll(getDependenciesWithError(dependencyItem.getDependencies(), true));
                }
            }
        }
        return new ArrayList<>(new HashSet<>(withError));
    }

    public List<String> getUnresolvedDependencies(List<String> checkDependencies)
    {
        return getUnresolvedDependencies(checkDependencies, true);
    }

    public List<String> getUnresolvedDependencies(List<String> checkDependencies, boolean includeBase)
    {
        List<String> unresolvedDependencies = new ArrayList<>();
        for (String checkDependency : checkDependencies)
        {
            InjectorDependency dependencyItem = dependencies.get(checkDependency);
            if (dependencyItem != null)
            {
                if (dependencyItem.getState() != InjectorDependencyState.Resolved && dependencyItem.getState() != InjectorDependencyState.Refreshing && dependencyItem.getState() != InjectorDependencyState.RefreshError)
                {
                    unresolvedDependencies.add(checkDependency);
                }
                if (includeBase)
                {
                    unresolvedDependencies.addAll(getUnresolvedDependencies(dependencyItem.getDependencies(), true));
                }
            }
        }
        return new ArrayList<>(new HashSet<>(unresolvedDependencies));
    }

    public List<String> getUnresolvedBaseDependencies(List<String> checkDependencies)
    {
        List<String> baseDependencies = new ArrayList<>();
        for (String checkDependency : checkDependencies)
        {
            InjectorDependency dependencyItem = dependencies.get(checkDependency);
            if (dependencyItem != null)
            {
                List<String> recursiveBaseDependencies = getUnresolvedBaseDependencies(dependencyItem.getDependencies());
                if (recursiveBaseDependencies.size() > 0)
                {
                    baseDependencies.addAll(recursiveBaseDependencies);
                }
                else
                {
                    baseDependencies.addAll(getUnresolvedDependencies(dependencyItem.getDependencies(), true));
                }
            }
        }
        return new ArrayList<>(new HashSet<>(baseDependencies));
    }

    public void resolveDependency(String dependency)
    {
        resolveDependency(dependency, false, null);
    }

    public void resolveDependency(String dependency, boolean forceRefresh)
    {
        resolveDependency(dependency, forceRefresh, null);
    }

    public void resolveDependency(String dependency, Map<String, String> input)
    {
        resolveDependency(dependency, false, input);
    }

    public void resolveDependency(final String dependency, boolean forceRefresh, Map<String, String> input)
    {
        final InjectorDependency dependencyItem = dependencies.get(dependency);
        if (dependencyItem != null)
        {
            // If the item is already busy resolving, bail out and wait for the existing resolution to be complete
            if (dependencyItem.getState() == InjectorDependencyState.Obtaining || dependencyItem.getState() == InjectorDependencyState.Refreshing)
            {
                return;
            }

            // Return if the dependency is already up to date
            if (dependencyItem.getState() == InjectorDependencyState.Resolved && !dependencyItem.isExpired() && !forceRefresh)
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
            for (String key : dependencyItem.getRequiresInput())
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
                dependencyItem.setState(dependencyItem.getState() == InjectorDependencyState.Resolved || dependencyItem.getState() == InjectorDependencyState.RefreshError ? InjectorDependencyState.Refreshing : InjectorDependencyState.Obtaining);
                dependencyItem.resolve(sendInput, new InjectorDependency.CompleteListener()
                {
                    @Override
                    public void onResolveResult(boolean success)
                    {
                        if (success)
                        {
                            dependencyItem.resetExpiration();
                            dependencyItem.setState(InjectorDependencyState.Resolved);
                            for (DependencyUpdateListener listener : updateListeners)
                            {
                                listener.onDependencyResolved(dependency);
                            }
                        }
                        else
                        {
                            dependencyItem.setState(dependencyItem.getState() == InjectorDependencyState.Refreshing ? InjectorDependencyState.RefreshError : InjectorDependencyState.ObtainError);
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
        else
        {
            for (DependencyUpdateListener listener : updateListeners)
            {
                listener.onDependencyFailed(dependency, "notExists");
            }
        }
    }


    // ---
    // Handling data
    // ---

    public Map<String, Object> generateInjectableData()
    {
        Map<String, Object> data = new HashMap<>();
        for (String name : dependencies.keySet())
        {
            InjectorDependency dependency = dependencies.get(name);
            Object injectionData = dependency.obtainInjectableData();
            if (injectionData != null)
            {
                data.put(name, injectionData);
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
        void onDependencyResolved(String dependency);
        void onDependencyFailed(String dependency, String reason);
    }
}
