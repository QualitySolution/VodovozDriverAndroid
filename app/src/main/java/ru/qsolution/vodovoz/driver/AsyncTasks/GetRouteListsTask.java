package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
    private List<IAsyncTaskListener<AsyncTaskResult<ArrayList<RouteList>>>> listeners = new ArrayList<>();
    private LinearLayout linlaHeaderProgress;
    private WeakReference<Activity> weakActivity;

    public GetRouteListsTask(Activity activity) {
        weakActivity = new WeakReference<>(activity);
        linlaHeaderProgress = (LinearLayout) activity.findViewById(R.id.linlaHeaderProgress);
    }

    public void addListener(IAsyncTaskListener<AsyncTaskResult<ArrayList<RouteList>>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected void onPreExecute() {
        linlaHeaderProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected AsyncTaskResult<ArrayList<RouteList>> doInBackground(String... args) {
        AsyncTaskResult<ArrayList<RouteList>> result;
        String METHOD_NAME = "GetRouteLists";

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

        SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
        request.addProperty("authKey", args[0]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
            Object routeListsObj = envelope.getResponse();
            ArrayList<RouteList> routeListsArray;

            SoapObject routeLists = (SoapObject) routeListsObj;

            routeListsArray = new ArrayList<>();
            for (int i = 0; i < routeLists.getPropertyCount(); i++) {
                Object property = routeLists.getProperty(i);
                if (property instanceof SoapObject) {
                    SoapObject soapObject = (SoapObject) property;
                    RouteList routeList = new RouteList(soapObject);
                    routeListsArray.add(routeList);
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
        linlaHeaderProgress.setVisibility(View.GONE);
    }
}