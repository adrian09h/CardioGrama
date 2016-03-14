package com.gigster.cardiograma.Fragments.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gigster.cardiograma.Activities.MainActivity;
import com.gigster.cardiograma.Models.ChangedValueMsg;
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
import java.util.List;

import de.greenrobot.event.EventBus;

public class HistoryAdapter extends BaseAdapter {

    public static List<HistoryHeartData> arrayHistoryData;
    private LayoutInflater inflater;
    private MainActivity mActivity;
    private Context mContext;
    DateFormat dateFormatter;


    public HistoryAdapter(List<HistoryHeartData> historyData, MainActivity activity, Context context) {
        this.arrayHistoryData = historyData;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        this.mActivity = activity;
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
            holder.imgvEdit = (ImageView)convertView.findViewById(R.id.imgvEdit);
            holder.graph = (GraphView) convertView.findViewById(R.id.graph);
            holder.editMotionStateNote = (EditText) convertView.findViewById(R.id.editMotionStateNote);
            convertView.setTag(holder);
        } else {
            holder = (HistoryHolder) convertView.getTag();
        }

        final HistoryHeartData beatdata = arrayHistoryData.get(position);

        try {
//            holder.imgvState.setTag(String.valueOf(position));
//            holder.imgvState.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onClickEdit(v);
//                }
//            });
            if (beatdata.motion_state.equals(mActivity.getResources().getString(R.string.REST))) {
                Picasso.with(mActivity).load(R.drawable.rest_deselected).into(holder.imgvState);
            } else if (beatdata.motion_state.equals(mActivity.getResources().getString(R.string.WARM_UP))) {
                Picasso.with(mActivity).load(R.drawable.warmup_deselected).into(holder.imgvState);
            } else if (beatdata.motion_state.equals(mActivity.getResources().getString(R.string.CARDIO))) {
                Picasso.with(mActivity).load(R.drawable.cardiounselected).into(holder.imgvState);
            } else {
                Picasso.with(mActivity).load(R.drawable.extremeunselected).into(holder.imgvState);
            }
            holder.imgvEdit.setTag(String.valueOf(position));
            holder.imgvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickEdit(v);
                }
            });

            String strHeartBeat = String.valueOf(beatdata.heart_beat);
            holder.txtHeartBeat.setText(strHeartBeat);
            String strDate = dateFormatter.format(beatdata.saved_date);
            holder.txtDate.setText(strDate);
            holder.editMotionStateNote.clearFocus();
            holder.editMotionStateNote.setText(beatdata.motion_state_note);
            holder.editMotionStateNote.setTag(String.valueOf(position));
            holder.editMotionStateNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        EditText editText = (EditText)v;
                        String strPosition = (String) v.getTag();
                        int editingPosition = Integer.parseInt(strPosition);
                        HistoryHeartData data = arrayHistoryData.get(editingPosition);
                        Log.d("EditText", "Text: " + editText.getText().toString() + " position: " + (String) v.getTag());
                        if (!data.motion_state_note.equals(editText.getText().toString())){
                            data.motion_state_note = editText.getText().toString();// + " p: " + strPosition;
                            data.save();
                        }

                    }
                }
            });



            LineGraphSeries<DataPoint> mSeries2 = new LineGraphSeries<DataPoint>(getDataPoints(beatdata.data));
            mSeries2.setColor(Color.RED);
            holder.graph.removeAllSeries();
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


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error:", e.toString());
        }

        return convertView;
    }

    public void onClickEdit(View v){
        String strPosition = (String) v.getTag();
        int editingPosition = Integer.parseInt(strPosition);
        DialogFragment newFragment = MyAlertDialogFragment
                .newInstance(R.string.accept, editingPosition);
        newFragment.show(mActivity.getSupportFragmentManager(), "dialog");
    }

    public DataPoint[] getDataPoints(String strSerialized) {
        String[] strDatas = strSerialized.split(",");
        int count = strDatas.length;
        DataPoint[] values = new DataPoint[count];
        for (int i = 0; i < strDatas.length; i++) {
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
        ImageView imgvEdit;
        GraphView graph;
        EditText editMotionStateNote;
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title, int position) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            args.putInt("position", position);
            frag.setArguments(args);
            return frag;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_selectactivity_dialog, container, false);
            final int curPos = getArguments().getInt("position");
            ImageView imgvRest = (ImageView)rootView.findViewById(R.id.imgvRest);
            imgvRest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDataAndNotify(curPos, getResources().getString(R.string.REST));
                    dismiss();
                }
            });
            ImageView imgvWarmup = (ImageView)rootView.findViewById(R.id.imgvWarmup);
            imgvWarmup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDataAndNotify(curPos, getResources().getString(R.string.WARM_UP));
                    dismiss();
                }
            });
            ImageView imgvCardio = (ImageView)rootView.findViewById(R.id.imgvCardio);
            imgvCardio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDataAndNotify(curPos, getResources().getString(R.string.CARDIO));
                    dismiss();
                }
            });
            ImageView imgvExtreme = (ImageView)rootView.findViewById(R.id.imgvExtreme);
            imgvExtreme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDataAndNotify(curPos, getResources().getString(R.string.EXTREME));
                    dismiss();
                }
            });
            getDialog().setTitle("Choose the activity");
            return rootView;
        }

        public void setDataAndNotify(int position, String state){
            HistoryHeartData historyHeartData =  arrayHistoryData.get(position);
            historyHeartData.motion_state = state;
            historyHeartData.save();
            EventBus.getDefault().post(new ChangedValueMsg(true));
        }
    }

}
