package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;

import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.RouteList;
import ru.qsolution.vodovoz.driver.R;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 07.06.16.
 * (c) Quality Solution Ltd.
 */

public class GetRouteListsTask extends AsyncTask<String, Void, AsyncTaskResult<ArrayList<RouteList>>> {
    private final List<IAsyncTaskListener<AsyncTaskResult<ArrayList<RouteList>>>> listeners = new ArrayList<>();
    private final LinearLayout linlaHeaderProgress;
    private final SwipeRefreshLayout swipeRefreshLayout;

    public GetRouteListsTask(Activity activity) {
        swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swiperefresh);
        linlaHeaderProgress = (LinearLayout) activity.findViewById(R.id.linlaHeaderProgress);
    }

    public void addListener(IAsyncTaskListener<AsyncTaskResult<ArrayList<RouteList>>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected void onPreExecute() {
        if (!swipeRefreshLayout.isRefreshing())
            linlaHeaderProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected AsyncTaskResult<ArrayList<RouteList>> doInBackground(String... args) {
        AsyncTaskResult<ArrayList<RouteList>> result;
        String METHOD_NAME = NetworkWorker.METHOD_GET_ROUTE_LISTS;

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ANDROID_SERVICE_URL);

        SoapObject request = new SoapObject(NetworkWorker.NAMESPACE, METHOD_NAME);
        request.addProperty(NetworkWorker.FIELD_AUTH_KEY, args[0]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        System.setProperty("http.keepAlive", "false");

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME, NetworkWorker.ACTION_INTERFACE_ANDROID), envelope, headerPropertyArrayList);
            Object routeListsObj = envelope.getResponse();
            ArrayList<RouteList> routeListsArray;

            SoapObject routeLists = (SoapObject) routeListsObj;

            routeListsArray = new ArrayList<>();
            if(routeLists != null) {
                for (int i = 0; i < routeLists.getPropertyCount(); i++) {
                    Object property = routeLists.getProperty(i);
                    if (property instanceof SoapObject) {
                        SoapObject soapObject = (SoapObject) property;
                        RouteList routeList = new RouteList(soapObject);
                        routeListsArray.add(routeList);
                    }
                }
            }
            result = new AsyncTaskResult<>(routeListsArray);
        } catch (IOException | XmlPullParserException e) {
            result = new AsyncTaskResult<>(e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<ArrayList<RouteList>> result) {
        for (IAsyncTaskListener<AsyncTaskResult<ArrayList<RouteList>>> listener : listeners) {
            listener.AsyncTaskCompleted(result);
        }
        if (!swipeRefreshLayout.isRefreshing())
            linlaHeaderProgress.setVisibility(View.GONE);
    }
}