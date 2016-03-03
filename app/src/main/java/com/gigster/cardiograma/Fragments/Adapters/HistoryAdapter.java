package com.gigster.cardiograma.Fragments.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gigster.cardiograma.Models.GConstants;
import com.gigster.cardiograma.Models.HistoryHeartData;
import com.gigster.cardiograma.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends BaseAdapter {

	private List<HistoryHeartData> arrayHistoryData;
	private LayoutInflater inflater;
	private Context mContext;
	DateFormat dateFormatter;

	public HistoryAdapter(List<HistoryHeartData> historyData, Context context) {
		this.arrayHistoryData = historyData;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		this.dateFormatter = new SimpleDateFormat(GConstants.DateFormatString);
		dateFormatter.setLenient(false);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayHistoryData.size();
	}

	@Override
	public HistoryHeartData getItem(int position) {
		// TODO Auto-generated method stub
		return arrayHistoryData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		HistoryHolder holder = null;
		if (convertView == null) {
			holder = new HistoryHolder();
			convertView = inflater.inflate(R.layout.listitem_history, parent, false);
			holder.txtHeartBeat = (TextView) convertView
					.findViewById(R.id.txtHeartBeat);
			holder.txtDate = (TextView) convertView
					.findViewById(R.id.txtDate);
			holder.imgvState = (ImageView) convertView.findViewById(R.id.imgvMotionState);
			holder.graph = (GraphView)convertView.findViewById(R.id.graph);
			convertView.setTag(holder);
		} else {
			holder = (HistoryHolder) convertView.getTag();
		}

		final HistoryHeartData beatdata = arrayHistoryData.get(position);
		try {
			String strHeartBeat = String.valueOf(beatdata.heart_beat);
			holder.txtHeartBeat.setText(strHeartBeat);//beatdata.heart_beat);
			String strDate = dateFormatter.format(beatdata.saved_date);
			holder.txtDate.setText(strDate);
			if (beatdata.motion_state.equals(mContext.getResources().getString(R.string.REST))){
				Picasso.with(mContext).load(R.drawable.rest_deselected).into(holder.imgvState);
			} else if (beatdata.motion_state.equals(mContext.getResources().getString(R.string.WARM_UP))){
				Picasso.with(mContext).load(R.drawable.warmup_deselected).into(holder.imgvState);
			} else if (beatdata.motion_state.equals(mContext.getResources().getString(R.string.CARDIO))){
				Picasso.with(mContext).load(R.drawable.cardiounselected).into(holder.imgvState);
			} else{
				Picasso.with(mContext).load(R.drawable.extremeunselected).into(holder.imgvState);
			}

			LineGraphSeries<DataPoint> mSeries2 = new LineGraphSeries<DataPoint>(getDataPoints(beatdata.data));
			mSeries2.setColor(Color.RED);
			holder.graph.addSeries(mSeries2);
			holder.graph.getViewport().setXAxisBoundsManual(true);
			holder.graph.getViewport().setMinX(0);
			holder.graph.getViewport().setMaxX(40);
			holder.graph.getViewport().setYAxisBoundsManual(true);
			holder.graph.getViewport().setMaxY(3);
			holder.graph.getViewport().setMinY(-3);
			holder.graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE); // get rid of grid
			holder.graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);// hide x axis
			holder.graph.getGridLabelRenderer().setVerticalLabelsVisible(false);// hide y axis

		}catch (Exception e){
			e.printStackTrace();
			Log.e("error:", e.toString());
		}

		return convertView;
	}

	public DataPoint[] getDataPoints(String strSerialized){
		String[] strDatas = strSerialized.split(",");
		int count = strDatas.length;
		DataPoint[] values = new DataPoint[count];
		for (int i = 0; i<strDatas.length;i++){
			double data = Double.parseDouble(strDatas[i]);
			DataPoint dataPoint = new DataPoint(i, data);
			values[i] = dataPoint;
		}
		return values;
	}

	private class HistoryHolder {
		TextView txtHeartBeat;
		TextView txtDate;
		ImageView imgvState;
		GraphView graph;
	}

}
