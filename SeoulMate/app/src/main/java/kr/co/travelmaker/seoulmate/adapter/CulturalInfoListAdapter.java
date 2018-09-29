package kr.co.travelmaker.seoulmate.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;

import kr.co.travelmaker.seoulmate.data.RowData;
import kr.co.travelmaker.seoulmate.R;

public class CulturalInfoListAdapter extends BaseAdapter {
    ArrayList<RowData> items;

    public CulturalInfoListAdapter(ArrayList<RowData> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_cultural_info, parent, false);
            holder.lv_mainIm = convertView.findViewById(R.id.lv_mainIm);
            holder.text_IngOrEd = convertView.findViewById(R.id.text_IngOrEd);
            holder.text_title = convertView.findViewById(R.id.text_title);
            holder.text_date = convertView.findViewById(R.id.text_date);
            holder.text_area = convertView.findViewById(R.id.text_area);

            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        RowData item = (RowData)getItem(position);

        if(!item.getMAIN_IMG().equals("")){
            String url = item.getMAIN_IMG();
            int find = url.lastIndexOf(".");
            String realUrl = url.substring(0, find).toLowerCase();
            String jpg = url.substring(find+1, url.length());
            Glide.with(parent.getContext()).load(realUrl+"."+jpg).into(holder.lv_mainIm);
        }else{
            holder.lv_mainIm.setBackgroundResource(R.drawable.no_image);
        }

        Calendar calendar = Calendar.getInstance();
        int Year = calendar.get(Calendar.YEAR);
        int Month = calendar.get(Calendar.MONTH);
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        Month++;


        if(item.getSTRTDATE() ==null || item.getSTRTDATE().equals("")){
            if(Month<10){
                item.setSTRTDATE(Year+"-0"+Month+"-"+Day);
                if(Day<10) {
                    item.setSTRTDATE(Year + "-0" + Month + "-0" + Day);
                }
            }else{
                item.setSTRTDATE(Year+"-"+Month+"-"+Day);
            }
        }

        if(item.getEND_DATE() ==null || item.getEND_DATE().equals("")){
            if(Month<10){
                item.setEND_DATE(Year+"-0"+Month+"-"+Day);
                if(Day<10){
                    item.setEND_DATE(Year+"-0"+Month+"-0"+Day);
                }
            }else{
                item.setEND_DATE(Year+"-"+Month+"-"+Day);
            }
        }

        String[] startDate = item.getSTRTDATE().split("-");
        String[] endDate = item.getEND_DATE().split("-");

        int searchStartYear = Integer.parseInt(startDate[0]);
        int searchStartMonth = Integer.parseInt(startDate[1]);
        int searchStartDay = Integer.parseInt(startDate[2]);

        int searchEndYear = Integer.parseInt(endDate[0]);
        int searchEndMonth = Integer.parseInt(endDate[1]);
        int searchEndDay = Integer.parseInt(endDate[2]);

        if ((searchEndYear < Year) || ((searchEndYear == Year) && (searchEndMonth < Month)) || ((searchEndYear == Year) && (searchEndMonth == Month) && (searchEndDay < Day))) {
            holder.text_IngOrEd.setText(R.string.ed);
        } else if ((searchStartYear > Year) || ((searchStartYear == Year) && (searchStartMonth > Month)) || ((searchStartYear == Year) && (searchStartMonth == Month) && (searchStartDay > Day))) {
            holder.text_IngOrEd.setText(R.string.coming);
        } else {
            holder.text_IngOrEd.setText(R.string.ing);
        }

        if(item.getTITLE() != null || !item.getTITLE().equals(null)){
            holder.text_title.setText(item.getTITLE());
        }else{
            holder.text_title.setText("");
        }

        if(item.getSTRTDATE() == null || item.getSTRTDATE().equals(null)){
            item.setSTRTDATE("");
        }else if(item.getEND_DATE() == null || item.getEND_DATE().equals(null)){
            item.setEND_DATE("");
        }

        holder.text_date.setText(item.getSTRTDATE() +"~"+ item.getEND_DATE());

        if(item.getGCODE() != null || !item.getGCODE().equals(null)){
            holder.text_area.setText(item.getGCODE());
        }else{
            holder.text_area.setText("");
        }

        Log.d("datalog", "cultural["+position+"] : "+item.getTITLE()+"/"+item.getSTRTDATE()+"/"+item.getEND_DATE());

        return convertView;
    }


    public class Holder{
        ImageView lv_mainIm;
        TextView text_IngOrEd;
        TextView text_title;
        TextView text_date;
        TextView text_area;
    }
}
