package com.example.moupass10;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.services.safebrowsing.v4.Safebrowsing;
import com.google.api.services.safebrowsing.v4.model.FindThreatMatchesRequest;
import com.google.api.services.safebrowsing.v4.model.FindThreatMatchesResponse;
import com.google.api.services.safebrowsing.v4.model.GoogleSecuritySafebrowsingV4FindThreatMatchesRequest;
import com.google.api.services.safebrowsing.v4.model.GoogleSecuritySafebrowsingV4FindThreatMatchesResponse;
import com.google.api.services.safebrowsing.v4.model.GoogleSecuritySafebrowsingV4ThreatMatch;
import com.google.api.services.safebrowsing.v4.model.ThreatEntry;
import com.google.api.services.safebrowsing.v4.model.ThreatInfo;
import com.google.api.services.safebrowsing.v4.model.ThreatMatch;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class SafeBrowsingTask extends AsyncTask<String, Void, String> {

    private static final String API_KEY = "AIzaSyDmlOM-i457qZobyUJgsMQ7LJ2XIHszLSY";
    private static final String CLIENT_ID = "MouPassPwMgr";

    private static final String SAFE_STATUS = "safe";
    private static final String MALICIOUS_STATUS = "malicious";
    private static final String DANGER_STATUS = "danger";

    private SafeBrowsingCallback callback;

    public SafeBrowsingTask(SafeBrowsingCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            HttpRequestInitializer requestInitializer = request -> {
                ArrayMap<String, Object> clientInfo = new ArrayMap<>();
                clientInfo.put("clientId", CLIENT_ID);
                request.getHeaders().set("x-client-info", clientInfo);
            };

            Safebrowsing safeBrowsing = new Safebrowsing.Builder(httpTransport, jsonFactory, requestInitializer)
                    .setApplicationName("Safe Browsing App")
                    .setGoogleClientRequestInitializer(request -> request.setDisableGZipContent(true))
                    .build();

            FindThreatMatchesRequest threatMatchesRequest = new FindThreatMatchesRequest();
            ThreatInfo threatInfo = new ThreatInfo();
            threatInfo.setThreatTypes(Collections.singletonList("MALWARE"));
            threatInfo.setPlatformTypes(Collections.singletonList("ANY_PLATFORM"));
            threatInfo.setThreatEntryTypes(Collections.singletonList("URL"));
            List<ThreatEntry> threatEntries = Collections.singletonList(new ThreatEntry().set("url", urls[0]));
            threatInfo.setThreatEntries(threatEntries);
            threatMatchesRequest.setThreatInfo(threatInfo);

            GoogleSecuritySafebrowsingV4FindThreatMatchesRequest GoogleSecuritySafebrowsingV4FindThreatMatchesRequest = new GoogleSecuritySafebrowsingV4FindThreatMatchesRequest();
            Safebrowsing.ThreatMatches.Find findRequest = safeBrowsing.threatMatches().find(GoogleSecuritySafebrowsingV4FindThreatMatchesRequest);
            GoogleSecuritySafebrowsingV4FindThreatMatchesResponse findResponse = findRequest.setKey(API_KEY).execute();

            List<GoogleSecuritySafebrowsingV4ThreatMatch> threatMatches = findResponse.getMatches();

            if (threatMatches != null && !threatMatches.isEmpty()) {
                String threatType = threatMatches.get(0).getThreatType();
                if (threatType.equals("MALWARE")) {
                    return DANGER_STATUS;
                }
            }

            return SAFE_STATUS;
        } catch (IOException | GeneralSecurityException e) {
            Log.e("SafeBrowsingTask", "Error checking URL safety", e);
            return SAFE_STATUS; // Treat any error as safe
        }
    }

    @Override
    protected void onPostExecute(String status) {
        callback.onSafeBrowsingComplete(status);
    }

    public interface SafeBrowsingCallback {
        void onSafeBrowsingComplete(String status);
    }
}
