/*
 * (c) Copyright 2016 Mopria Alliance, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.discovery.recommendation;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.discovery.recommendation.PrintServicePlugin;
import com.android.discovery.recommendation.ServiceRecommendationPlugin;
import com.android.discovery.recommendation.ServiceResolveQueue;
import com.hp.discovery.recommendation.HPRecommendationPlugin;
import org.mopria.discovery.recommendation.MopriaRecommendationPlugin;

import java.util.Locale;

public class DeviceListFragment extends ListFragment implements PrintServicePlugin.PrinterDiscoveryCallback {

    private ArrayAdapter<ServiceRecommendationPlugin> mArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceResolveQueue.createInstance((NsdManager)getActivity().getSystemService(Context.NSD_SERVICE));
        mArrayAdapter = new ArrayAdapter<ServiceRecommendationPlugin>(getActivity(), 0) {

            class ViewHolder {
                final TextView mText1;
                final TextView mText2;
                public ViewHolder(TextView text1, TextView text2) {
                    mText1 = text1;
                    mText2 = text2;
                }
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                ViewHolder holder;
                if (convertView == null) {
                    view = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
                    view.setTag(new ViewHolder((TextView)view.findViewById(android.R.id.text1),
                            (TextView)view.findViewById(android.R.id.text2)));
                } else {
                    view = convertView;
                }
                holder = (ViewHolder)view.getTag();
                holder.mText1.setText(getItem(position).getName());
                holder.mText2.setText(String.format(Locale.getDefault(), "%d",getItem(position).getCount()));
                return view;
            }
        };
        mArrayAdapter.add(new HPRecommendationPlugin(getActivity()));
        mArrayAdapter.add(new MopriaRecommendationPlugin(getActivity()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceResolveQueue.destroyInstance();
    }


    @Override
    public void onResume() {
        super.onResume();
        for(int i = 0; i < mArrayAdapter.getCount(); i++) {
            try {
                mArrayAdapter.getItem(i).start(this);
            } catch(Exception ignored) {}
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for(int i = 0; i < mArrayAdapter.getCount(); i++) {
            try {
                mArrayAdapter.getItem(i).stop();
            } catch(Exception ignored) {}
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(mArrayAdapter);
    }

    @Override
    public void onChanged(@IntRange(from = 0) int numDiscoveredPrinters) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mArrayAdapter.notifyDataSetChanged();
            }
        });
    }
}
