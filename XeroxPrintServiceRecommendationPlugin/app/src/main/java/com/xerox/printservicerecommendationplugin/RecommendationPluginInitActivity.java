/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xerox.printservicerecommendationplugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RecommendationPluginInitActivity extends Activity implements  PrintServicePlugin.PrinterDiscoveryCallback{

    XeroxPrintServiceRecommendationPlugin mXeroxRecommendation;
    String TAG = RecommendationPluginInitActivity.class.getSimpleName();
    TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xerox_discovery);
        tvCount = (TextView) findViewById(R.id.printer_count);
        mXeroxRecommendation = new XeroxPrintServiceRecommendationPlugin(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            mXeroxRecommendation.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            mXeroxRecommendation.start(RecommendationPluginInitActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChanged(final int numDiscoveredPrinters) {
        Log.d(TAG,"xerox printer count:"+numDiscoveredPrinters);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCount.setText(String.valueOf(numDiscoveredPrinters));
            }
        });

    }
}
