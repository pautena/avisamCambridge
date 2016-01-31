package cambridge.hack.alarmbike.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import cambridge.hack.alarmbike.entities.Station;

/**
 * Created by joel on 1/30/16.
 */
public class WearMessageService extends IntentService {
    private static final String TAG = WearMessageService.class.getSimpleName();

    private static GoogleApiClient mGoogleApiClient;
    private String nodeId = "";
    private static LinkedTransferQueue<Pair<String, String>> messages;

    public WearMessageService() {
        super(WearMessageService.class.getSimpleName());
        messages = new LinkedTransferQueue<>();
    }

    private static String pickBestNodeId(List<Node> nodes) {
        System.out.println(nodes);
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private static void sendMessage(GoogleApiClient client, String nodeId, String message, String path) {
        Log.d(TAG, "Sending message");
        System.out.println(nodeId);
        System.out.println(client.isConnected());
        if (message == null)
            message = "";
        Wearable.MessageApi
                .sendMessage(client, nodeId, path, message.getBytes())
                .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d(TAG, "Message result: " + sendMessageResult.getStatus().toString());
                        Log.d(TAG, sendMessageResult.getStatus().getStatus().getStatusMessage());
                    }
                });
    }

    @Override
    public void onCreate() {
        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                Pair<String, String> s;
                                synchronized (WearMessageService.class) {
                                    nodeId = pickBestNodeId(getConnectedNodesResult.getNodes());
                                }
//                                while ((s = messages.poll()) != null)
//                                    sendMessage(s.first, s.second);
                                Log.d(TAG, "Connected node: " + nodeId);
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e(TAG, connectionResult.getErrorMessage());
                    }
                })
                .addApi(Wearable.API)
                .build();

        Log.d(TAG, "Created MessageService");
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String msg = intent.getStringExtra("message");
        final String path = intent.getStringExtra("path");
        final Context context = getApplicationContext();

        new Thread() {
            @Override
            public void run() {
                GoogleApiClient client = mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addApi(Wearable.API).build();
                client.blockingConnect();
                String nodeId = pickBestNodeId(Wearable.NodeApi.getConnectedNodes(client)
                        .await().getNodes());
                sendMessage(client, nodeId, msg, path);
            }
        }.start();

//        if (nodeId == null || !mGoogleApiClient.isConnected())
//            messages.put(new Pair<String, String>(msg, path));
//        else sendMessage(msg, path);
        //while (nodeId == null && !mGoogleApiClient.isConnected());
//        Log.d("node", nodeId);
//        sendMessage(msg, path);
    }

    @Override
    public void onDestroy() {
        //mGoogleApiClient.disconnect();
        super.onDestroy();
    }
}
