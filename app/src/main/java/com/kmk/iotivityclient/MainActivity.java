package com.kmk.iotivityclient;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import org.iotivity.base.ErrorCode;
import org.iotivity.base.ModeType;
import org.iotivity.base.ObserveType;
import org.iotivity.base.OcConnectivityType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;
import org.iotivity.base.OcResourceIdentifier;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final Activity mActivity = this;

    private HashMap<OcResourceIdentifier, OcResource> mFoundResources = new HashMap<>();
    private OcResource mFoundResource = null;
    private int mObserverCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        String uri = "coap://192.168.0.23/turnon";
        test(uri);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void test(final String uri) {
        OcPlatform.OnResourceFoundListener onResourceFoundListener = new OcPlatform.OnResourceFoundListener() {
            @Override
            public void onResourceFound(OcResource ocResource) {
                if (ocResource == null) {
                    Log.d(TAG, "Found resource is invalid");
                    return;
                }

                OcResource.OnGetListener onGetListener = new OcResource.OnGetListener() {
                    @Override
                    public void onGetCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation) {
                        Log.d(TAG, "GET request was successful");
                        Log.d(TAG, "Resource URI: " + ocRepresentation.getUri());
                        try {
                            String result = "" + ocRepresentation.getValue("result");
                            Log.d(TAG, "Result: " + result);
                        } catch (OcException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onGetFailed(Throwable throwable) {
                        if (throwable instanceof OcException) {
                            OcException ocEx = (OcException) throwable;
                            ErrorCode errCode = ocEx.getErrorCode();
                            Log.d(TAG, "Error code: " + errCode);
                        }
                        Log.d(TAG, "Failed to GET!");
                    }
                };

                String resourceName = "/" + uri.substring(7).split("/")[1];
                if (ocResource.getUri().equals(resourceName)) {
                    HashMap<String, String> queryParams = new HashMap<>();
                    try {
                        ocResource.get(queryParams, onGetListener);
                    } catch (OcException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "No resource: " + resourceName);
                }
            }

            @Override
            public void onFindResourceFailed(Throwable throwable, String s) {
                Log.d(TAG, "Failed to find resource: " + s);
            }
        };

        PlatformConfig platformConfig = new PlatformConfig(mActivity, ServiceType.IN_PROC, ModeType.CLIENT,
                "0.0.0.0", 0, QualityOfService.LOW
        );
        OcPlatform.Configure(platformConfig);
        String uriIp = uri.substring(7).split("/")[0];
        Log.d(TAG, "URI IP: " + uriIp);
        String requestUri = "coap://" + uriIp + OcPlatform.WELL_KNOWN_QUERY + "?rt=core.thing";
        try {
            OcPlatform.findResource("", requestUri, EnumSet.of(OcConnectivityType.CT_DEFAULT), onResourceFoundListener);
        } catch (OcException e) {
            e.printStackTrace();
        }
    }

    private void findResource(final String uri) {
        OcPlatform.OnResourceFoundListener onResourceFoundListener = new OcPlatform.OnResourceFoundListener() {
            @Override
            public void onResourceFound(OcResource ocResource) {
                if (ocResource == null) {
                    Log.d(TAG, "Found resource is invalid");
                    return;
                }

                if (mFoundResources.containsKey(ocResource.getUniqueIdentifier())) {
                    Log.d(TAG, "Found a previously seen resource again!");
                } else {
                    Log.d(TAG, "Found resource for the first time on server with ID: " + ocResource.getServerId());
                    mFoundResources.put(ocResource.getUniqueIdentifier(), ocResource);
                }
                Log.d(TAG, "URI of the resource: " + ocResource.getUri());
                Log.d(TAG, "Host address of the resource: " + ocResource.getHost());
                Log.d(TAG, "List of resource types: ");
                for (String resourceType : ocResource.getResourceTypes()) {
                    Log.d(TAG, "" + resourceType);
                }
                Log.d(TAG, "List of resource interfaces:");
                for (String resourceInterface : ocResource.getResourceInterfaces()) {
                    Log.d(TAG, "" + resourceInterface);
                }
                Log.d(TAG, "List of resource connectivity types:");
                for (OcConnectivityType connectivityType : ocResource.getConnectivityTypeSet()) {
                    Log.d(TAG, "" + connectivityType);
                }

                String resourceName = "/" + uri.substring(7).split("/")[1];
                if (ocResource.getUri().equals(resourceName)) {
                    mFoundResource = ocResource;
                } else {
                    Log.d(TAG, "No resource: " + resourceName);
                }
            }

            @Override
            public void onFindResourceFailed(Throwable throwable, String s) {
                Log.d(TAG, "Failed to find resource: " + s);
            }
        };

        PlatformConfig platformConfig = new PlatformConfig(mActivity, ServiceType.IN_PROC, ModeType.CLIENT,
                "0.0.0.0", 0, QualityOfService.LOW
        );
        OcPlatform.Configure(platformConfig);
        String uriIp = uri.substring(7).split("/")[0];
        Log.d(TAG, "URI IP: " + uriIp);
        String requestUri = "coap://" + uriIp + OcPlatform.WELL_KNOWN_QUERY + "?rt=core.thing";
        try {
            OcPlatform.findResource("", requestUri, EnumSet.of(OcConnectivityType.CT_DEFAULT), onResourceFoundListener);
        } catch (OcException e) {
            e.printStackTrace();
        }
    }

    private void getResourceRepresentation() {
        OcResource.OnGetListener onGetListener = new OcResource.OnGetListener() {
            @Override
            public void onGetCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation) {
                Log.d(TAG, "GET request was successful");
                Log.d(TAG, "Resource URI: " + ocRepresentation.getUri());
                try {
                    String result = "" + ocRepresentation.getValue("result");
                    Log.d(TAG, "Result: " + result);
                } catch (OcException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGetFailed(Throwable throwable) {
                if (throwable instanceof OcException) {
                    OcException ocEx = (OcException) throwable;
                    ErrorCode errCode = ocEx.getErrorCode();
                    Log.d(TAG, "Error code: " + errCode);
                }
                Log.d(TAG, "Failed to GET!");
            }
        };

        Log.d(TAG, "Getting Representation...");
        HashMap<String, String> queryParams = new HashMap<>();
        try {
            mFoundResource.get(queryParams, onGetListener);
        } catch (OcException e) {
            e.printStackTrace();
        }
    }

    private void postResourceRepresentation() {
        OcResource.OnPostListener onPostListener = new OcResource.OnPostListener() {
            @Override
            public void onPostCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation) {
                Log.d(TAG, "POST request was successful");
                Log.d(TAG, "Resource URI: " + ocRepresentation.getUri());
                try {
                    Log.d(TAG, "" + ocRepresentation.getValue("result"));
                } catch (OcException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostFailed(Throwable throwable) {
                if (throwable instanceof OcException) {
                    OcException ocEx = (OcException) throwable;
                    ErrorCode errCode = ocEx.getErrorCode();
                    Log.d(TAG, "Error code: " + errCode);
                }
                Log.d(TAG, "Failed to POST!");
            }
        };

        Log.d(TAG, "Post...");
        HashMap<String, String> queryParams = new HashMap<>();
        OcRepresentation ocRepresentation = new OcRepresentation();
        try {
            ocRepresentation.setValue("what", "hello");
            mFoundResource.post(ocRepresentation, queryParams, onPostListener);
        } catch (OcException e) {
            e.printStackTrace();
        }
    }


    private void putResourceRepresentation() {
        OcResource.OnPutListener onPutListener = new OcResource.OnPutListener() {
            @Override
            public void onPutCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation) {
                Log.d(TAG, "PUT request was successful");
                Log.d(TAG, "Resource URI: " + ocRepresentation.getUri());
                try {
                    Log.d(TAG, "" + ocRepresentation.getValue("result"));
                } catch (OcException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPutFailed(Throwable throwable) {
                if (throwable instanceof OcException) {
                    OcException ocEx = (OcException) throwable;
                    ErrorCode errCode = ocEx.getErrorCode();
                    Log.d(TAG, "Error code: " + errCode);
                }
                Log.d(TAG, "Failed to PUT!");
            }
        };

        Log.d(TAG, "Put...");
        HashMap<String, String> queryParams = new HashMap<>();
        OcRepresentation ocRepresentation = new OcRepresentation();
        try {
            ocRepresentation.setValue("what", "hello");
            mFoundResource.put(ocRepresentation, queryParams, onPutListener);
        } catch (OcException e) {
            e.printStackTrace();
        }
    }

    private void deleteResourceRepresentation() {
        OcResource.OnDeleteListener onDeleteListener = new OcResource.OnDeleteListener() {
            @Override
            public void onDeleteCompleted(List<OcHeaderOption> list) {
                Log.d(TAG, "DELETE request was successful");
                for (OcHeaderOption o : list) {
                    Log.d(TAG, "" + o.getOptionId() + ": " + o.getOptionData());
                }
            }

            @Override
            public void onDeleteFailed(Throwable throwable) {
                if (throwable instanceof OcException) {
                    OcException ocEx = (OcException) throwable;
                    ErrorCode errCode = ocEx.getErrorCode();
                    Log.d(TAG, "Error code: " + errCode);
                }
                Log.d(TAG, "Failed to DELETE!");
            }
        };

        Log.d(TAG, "Delete...");
        try {
            mFoundResource.deleteResource(onDeleteListener);
        } catch (OcException e) {
            e.printStackTrace();
        }
    }

    private void observeResourceRepresentation() {
        OcResource.OnObserveListener onObserveListener = new OcResource.OnObserveListener() {
            @Override
            public void onObserveCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation, int i) {
                Log.d(TAG, "OBSERVE request was successful");
                Log.d(TAG, "SequenceNumber:" + i);
                Log.d(TAG, "Resource URI: " + ocRepresentation.getUri());
                try {
                    Log.d(TAG, "" + ocRepresentation.getValue("result"));
                } catch (OcException e) {
                    e.printStackTrace();
                }
                if (mObserverCount++ == 10) {
                    try {
                        mFoundResource.cancelObserve();
                        mObserverCount = 0;
                    } catch (OcException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onObserveFailed(Throwable throwable) {
                if (throwable instanceof OcException) {
                    OcException ocEx = (OcException) throwable;
                    ErrorCode errCode = ocEx.getErrorCode();
                    Log.d(TAG, "Error code: " + errCode);
                }
                Log.d(TAG, "Failed to OBSERVE!");
            }
        };

        Log.d(TAG, "Observe...");
        HashMap<String, String> queryParams = new HashMap<>();
        try {
            mFoundResource.observe(ObserveType.OBSERVE, queryParams, onObserveListener);
        } catch (OcException e) {
            e.printStackTrace();
        }
    }
}
