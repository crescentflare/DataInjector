package com.crescentflare.datainjectorexample.helper;

import android.os.Handler;

import com.crescentflare.datainjector.dependency.InjectorDependency;
import com.crescentflare.datainjector.injector.DataInjector;
import com.crescentflare.datainjector.injector.JoinStringInjector;
import com.crescentflare.datainjector.injector.ReplaceNullInjector;
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
 * Define a dependency for the dependency manager which can resolve itself through the example mock JSON files
 */
public class MockDependency extends InjectorDependency
{
    // ---
    // Members
    // ---

    private List<DataInjector> injectors = new ArrayList<>();
    private List<Object> storedJson = null;
    private int rawResourceId;


    // ---
    // Initialization
    // ---

    public MockDependency(String name, int rawResource)
    {
        // Add basic injectors
        super(name);
        injectors.addAll(Arrays.asList(
                new SnakeToCamelCaseInjector(),
                new ReplaceNullInjector()
        ));

        // Store loading resource and add extra injector for the customer list
        rawResourceId = rawResource;
        if (rawResourceId == R.raw.customer_list)
        {
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
    public void resolve(Map<String, String> input, final CompleteListener completeListener)
    {
        final Handler handler = new Handler();
        final int rawResourceId = this.rawResourceId;
        Thread loadThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // First load and process the data
                List<Object> processedJson = null;
                InputStream stream = ExampleApplication.context.getResources().openRawResource(rawResourceId);
                if (stream != null)
                {
                    String jsonString = readFromInputStream(stream);
                    if (jsonString != null)
                    {
                        Type type = new TypeToken<List<Object>>(){}.getType();
                        processedJson = new Gson().fromJson(jsonString, type);
                        if (processedJson != null)
                        {
                            for (Object item : processedJson)
                            {
                                for (DataInjector injector : injectors)
                                {
                                    injector.apply(item, null, null);
                                }
                            }
                        }
                    }
                }

                // Then apply the changes on the main thread, wait for half a second to simulate a delay in network traffic
                final List<Object> applyJson = processedJson;
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (applyJson != null)
                        {
                            storedJson = applyJson;
                        }
                        completeListener.onResolveResult(applyJson != null);
                    }
                }, 500);
            }
        });
        loadThread.start();
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
