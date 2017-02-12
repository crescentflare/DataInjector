package com.crescentflare.datainjectorexample.helper;

import com.crescentflare.datainjector.dependency.InjectorDependency;
import com.crescentflare.datainjector.injector.DataInjector;
import com.crescentflare.datainjector.injector.ReplaceNullInjector;
import com.crescentflare.datainjector.injector.JoinStringInjector;
import com.crescentflare.datainjector.injector.SnakeToCamelCaseInjector;
import com.crescentflare.datainjectorexample.ExampleApplication;
import com.crescentflare.datainjectorexample.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The main activity shows a small layout example, explanation and buttons to show other layout examples
 */
public class MockDependency extends InjectorDependency
{
    // ---
    // Members
    // ---

    private List<DataInjector> injectors = Arrays.asList(
            new SnakeToCamelCaseInjector(),
            new ReplaceNullInjector()
    );
    private List<Object> storedJson = new ArrayList<>();
    private int rawResourceId;


    // ---
    // Initialization
    // ---

    public MockDependency(int rawResource)
    {
        rawResourceId = rawResource;
        if (rawResourceId == R.raw.customer_list)
        {
            List<DataInjector> newInjectors = new ArrayList<>();
            newInjectors.addAll(injectors);
            injectors = newInjectors;
            injectors.add(new JoinStringInjector("fullName", Arrays.asList("~firstName", "~middleName", "~lastName"), " ", true));
        }
    }


    // ---
    // Data access
    // ---

    @Override
    public Object obtainInjectableData()
    {
        return storedJson;
    }


    // ---
    // Resolving
    // ---

    @Override
    public void resolve(Map<String, String> input, CompleteListener completeListener)
    {
        InputStream stream = ExampleApplication.context.getResources().openRawResource(rawResourceId);
        if (stream != null)
        {
            String jsonString = readFromInputStream(stream);
            if (jsonString != null)
            {
                Type type = new TypeToken<List<Object>>(){}.getType();
                storedJson = new Gson().fromJson(jsonString, type);
                for (Object item : storedJson)
                {
                    for (DataInjector injector : injectors)
                    {
                        injector.apply(item, null, null);
                    }
                }
            }
            completeListener.onResolveResult(storedJson != null);
        }
        else
        {
            completeListener.onResolveResult(false);
        }
    }


    // ---
    // Helper
    // ---

    private static String readFromInputStream(InputStream stream)
    {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        String result = null;
        try
        {
            Reader in = new InputStreamReader(stream, "UTF-8");
            for ( ; ; )
            {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                {
                    break;
                }
                out.append(buffer, 0, rsz);
            }
            result = out.toString();
            stream.close();
        }
        catch (Exception ignored)
        {
        }
        return result;
    }
}
