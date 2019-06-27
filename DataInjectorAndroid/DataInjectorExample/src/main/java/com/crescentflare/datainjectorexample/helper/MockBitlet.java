package com.crescentflare.datainjectorexample.helper;

import android.os.Handler;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;
import com.crescentflare.datainjector.injector.BaseInjector;
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

/**
 * Define a bitlet which can load an example mock JSON file and executes a number of data injectors
 */
public class MockBitlet implements BitletHandler<MockBitlet.ObjectArray>
{
    // ---
    // Members
    // ---

    private int rawResourceId;
    private String cacheKey;
    private List<BaseInjector> injectors = new ArrayList<>();


    // ---
    // Initialization
    // ---

    public MockBitlet(int rawResourceId, String cacheKey)
    {
        this.rawResourceId = rawResourceId;
        this.cacheKey = cacheKey;
        injectors.addAll(Arrays.asList(
                new SnakeToCamelCaseInjector(),
                new ReplaceNullInjector()
        ));
        if (rawResourceId == R.raw.customer_list)
        {
            injectors.add(new JoinStringInjector("fullName", Arrays.asList("~firstName", "~middleName", "~lastName"), " ", true));
        }
    }


    // ---
    // Get cache key
    // ---

    public String getCacheKey()
    {
        return cacheKey;
    }


    // ---
    // Resolving
    // ---

    public void load(final BitletObserver<ObjectArray> observer)
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
                String hash = "unknown";
                if (stream != null)
                {
                    String jsonString = readFromInputStream(stream);
                    if (jsonString != null)
                    {
                        Type type = new TypeToken<List<Object>>(){}.getType();
                        hash = HashUtil.generateMD5(jsonString);
                        processedJson = new Gson().fromJson(jsonString, type);
                        if (processedJson != null)
                        {
                            for (Object item : processedJson)
                            {
                                for (BaseInjector injector : injectors)
                                {
                                    injector.apply(item, null, null);
                                }
                            }
                        }
                    }
                }

                // Then apply the changes on the main thread, wait for half a second to simulate a delay in network traffic
                final List<Object> applyJson = processedJson;
                final String applyHash = hash;
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (applyJson != null)
                        {
                            ObjectArray objectArray = new ObjectArray();
                            objectArray.setItemList(applyJson);
                            observer.setBitlet(objectArray);
                        }
                        else
                        {
                            observer.setException(new Exception("bitlet invalid"));
                        }
                        observer.setBitletHash(applyHash);
                        observer.finish();
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


    // ---
    // Interface for wrapping an object array in a bitlet
    // ---

    public static class ObjectArray
    {
        private List<Object> itemList = new ArrayList<>();

        public List<Object> getItemList()
        {
            return itemList;
        }

        public void setItemList(List<Object> items)
        {
            itemList = items;
        }
    }
}
