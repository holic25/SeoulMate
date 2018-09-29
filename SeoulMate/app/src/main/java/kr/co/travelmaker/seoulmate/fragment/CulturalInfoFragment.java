package kr.co.travelmaker.seoulmate.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.data.RowData;
import kr.co.travelmaker.seoulmate.data.TotalData;
import kr.co.travelmaker.seoulmate.event.CulturalInfoMoveViewPager;
import kr.co.travelmaker.seoulmate.event.RowDataEvent;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.util.Utils;
import kr.co.travelmaker.seoulmate.adapter.CulturalInfoListAdapter;
import kr.co.travelmaker.seoulmate.bus.BusProvider;

public class CulturalInfoFragment extends Fragment {
    private static CulturalInfoFragment curr = null;
    public static CulturalInfoFragment getInstance() {
        if (curr == null) {
            curr = new CulturalInfoFragment();
        }

        return curr;
    }

    @BindView(R.id.spinner_search) Spinner spinner_search;
    @BindView(R.id.cultureListView) ListView cultureListView;
    @BindView(R.id.Et_searchCulture) EditText Et_searchCulture;
    @BindView(R.id.imBtn_searchCulture) ImageView imBtn_searchCulture;
    @BindView(R.id.text_category) TextView text_category;
    @BindView(R.id.im_UpDown) ImageView im_UpDown;
    @BindView(R.id.layout_category) LinearLayout layout_category;

    TotalData totalData;
    ArrayList<RowData> items;
    ArrayList<RowData> copyItems;
    CulturalInfoListAdapter informationList_adapter;
    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;

    Integer mYear;
    Integer mMonth;
    Integer mDay;

    String title;
    String genre;
    String term;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cultural_info, container, false);
        unbinder = ButterKnife.bind(this,view);
        bus.register(this);

        title = getResources().getString(R.string.title);
        genre = getResources().getString(R.string.genre);
        term = getResources().getString(R.string.term);

        text_category.setText(title);

        Calendar calendar = Calendar.getInstance();

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        TranslationWordTask translationWordTask = new TranslationWordTask();
        translationWordTask.execute("http://openapi.seoul.go.kr:8088/74556251686f726e37306444466b54/json/SearchConcertDetailService/1/50");

        return view;
    }

    public class TranslationWordTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String json = Utils.getStringFromServer(strings[0]);
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();

            totalData = gson.fromJson(s,TotalData.class);

            items = new ArrayList<>();
            items = totalData.getSearchConcertDetailService().getRow();
            copyItems = items;

            informationList_adapter = new CulturalInfoListAdapter(items);
            cultureListView.setAdapter(informationList_adapter);
        }
    }

    @OnItemClick(R.id.cultureListView)
    public void onCultureItemClick(AdapterView<?> parent, int position){
        RowData item = items.get(position);

        RowDataEvent rowDataEvent = new RowDataEvent(item);
        bus.post(rowDataEvent);

        CulturalInfoMoveViewPager culturalInfoMoveViewPager = new CulturalInfoMoveViewPager(1);
        bus.post(culturalInfoMoveViewPager);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.layout_category)
    public void im_UpDown(){
        im_UpDown.setBackgroundResource(R.drawable.ic_up);

        View custom_view = this.getLayoutInflater().inflate(R.layout.custom_category, null);
        final ArrayList<String> ListItems = new ArrayList<>();

        ListItems.add(title);
        ListItems.add(genre);
        ListItems.add(term);
        final String[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext()).setCancelable(false);
        dlg.setCustomTitle(custom_view);

        dlg.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();

                if(selectedText.equals(title)) {

                    refreshListName("");
                    Et_searchCulture.setVisibility(View.VISIBLE);
                    imBtn_searchCulture.setVisibility(View.VISIBLE);
                    text_category.setText(title);
                    im_UpDown.setBackgroundResource(R.drawable.ic_down);
                }
                else if(selectedText.equals(genre)){
                    refreshListName("");
                    Et_searchCulture.setVisibility(View.VISIBLE);
                    imBtn_searchCulture.setVisibility(View.VISIBLE);
                    text_category.setText(genre);
                    im_UpDown.setBackgroundResource(R.drawable.ic_down);
                }
                else if(selectedText.equals(term)){
                    text_category.setText(title);
                    Et_searchCulture.setVisibility(View.GONE);
                    imBtn_searchCulture.setVisibility(View.GONE);

                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            mYear = year;
                            mMonth = month+1;
                            mDay = day;

                            refreshListName(mYear, mMonth, mDay);
                            im_UpDown.setBackgroundResource(R.drawable.ic_down);
                            mMonth--;
                        }
                    };
                    DatePickerDialog dateDialog = new DatePickerDialog(getContext(),dateSetListener,mYear,mMonth,mDay);
                    dateDialog.show();
                }

            }
        });
        dlg.show();
    }

    @OnItemSelected(R.id.spinner_search)
    public void spinner_search(AdapterView<?> parent, int position){
        if(spinner_search.getSelectedItem().toString().equals("기간")) {
            Et_searchCulture.setVisibility(View.GONE);
            imBtn_searchCulture.setVisibility(View.GONE);

            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mYear = year;
                    mMonth = month+1;
                    mDay = day;

                    refreshListName(mYear, mMonth, mDay);
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(parent.getContext(),dateSetListener,mYear,mMonth,mDay);
            dialog.show();

        }else if(spinner_search.getSelectedItem().toString().equals(title) || spinner_search.getSelectedItem().toString().equals(genre)){
            refreshListName("");
            Et_searchCulture.setVisibility(View.VISIBLE);
            imBtn_searchCulture.setVisibility(View.VISIBLE);
        }
    }

    public void refreshListName(Integer mYear, Integer mMonth, Integer mDay){
        items = searchDateItem(mYear, mMonth, mDay);
        informationList_adapter = new CulturalInfoListAdapter(items);
        cultureListView.setAdapter(informationList_adapter);
    }

    public ArrayList<RowData> searchDateItem(Integer mYear, Integer mMonth, Integer mDay){
        Integer searchStartYear, searchStartMonth, searchStartDay, searchEndYear, searchEndMonth, searchEndDay;
        ArrayList<RowData> searchList = new ArrayList<>();


        for (int i = 0; i < copyItems.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            int Year = calendar.get(Calendar.YEAR);
            int Month = calendar.get(Calendar.MONTH);
            int Day = calendar.get(Calendar.DAY_OF_MONTH);
            Month++;

            if(copyItems.get(i).getSTRTDATE() ==null || copyItems.get(i).getSTRTDATE().equals("")){
                if(Month<10){
                    copyItems.get(i).setSTRTDATE(Year+"-0"+Month+"-"+Day);
                    if(Day<10) {
                        copyItems.get(i).setSTRTDATE(Year + "-0" + Month + "-0" + Day);
                    }
                }else{
                    copyItems.get(i).setSTRTDATE(Year+"-"+Month+"-"+Day);
                }
            }
            if(copyItems.get(i).getEND_DATE() ==null || copyItems.get(i).getEND_DATE().equals("")){
                if(Month<10){
                    copyItems.get(i).setEND_DATE(Year+"-0"+Month+"-"+Day);
                    if(Day<10) {
                        copyItems.get(i).setEND_DATE(Year + "-0" + Month + "-0" + Day);
                    }
                }else{
                    copyItems.get(i).setEND_DATE(Year+"-"+Month+"-"+Day);
                }
            }
            searchStartYear = Integer.parseInt(copyItems.get(i).getSTRTDATE().substring(0,4));
            searchStartMonth = Integer.parseInt(copyItems.get(i).getSTRTDATE().substring(5,7));
            searchStartDay = Integer.parseInt(copyItems.get(i).getSTRTDATE().substring(8,10));

            searchEndYear = Integer.parseInt(copyItems.get(i).getEND_DATE().substring(0,4));
            searchEndMonth = Integer.parseInt(copyItems.get(i).getEND_DATE().substring(5,7));


            if (mYear >= searchStartYear && mYear <= searchEndYear ) {
                if (mMonth >= searchStartMonth && mMonth <= searchEndMonth) {
                    if(searchStartMonth == mMonth && mDay >= searchStartDay) {
                        searchList.add(copyItems.get(i));
                    }
                }
            }
        }
        return searchList;
    }

    @OnClick(R.id.imBtn_searchCulture)
    public void imBtn_searchCulture(){
        String editText = Et_searchCulture.getText().toString();
        if(text_category.getText().toString().equals(title)){
            refreshListName(editText);
        }else if(text_category.getText().toString().equals(genre)){
            refreshListSubCode(editText);
        }
    }

    public void refreshListName(String editText){
        items = searchNameItem(editText);
        informationList_adapter = new CulturalInfoListAdapter(items);
        cultureListView.setAdapter(informationList_adapter);
    }

    public ArrayList<RowData> searchNameItem(String editText){
        ArrayList<RowData> searchList = new ArrayList<>();
        if(!editText.equals("") || editText.length() != 0) {
            for (int i = 0; i < copyItems.size(); i++) {
                if (copyItems.get(i).getTITLE().contains(editText)) {
                    searchList.add(copyItems.get(i));
                }
            }
        }else{
            TranslationWordTask translationWordTask = new TranslationWordTask();
            translationWordTask.execute("http://openapi.seoul.go.kr:8088/74556251686f726e37306444466b54/json/SearchConcertDetailService/1/50");
            return copyItems;
        }
        return searchList;
    }

    public void refreshListSubCode(String editText){
        items = searchSubCodeItem(editText);
        informationList_adapter = new CulturalInfoListAdapter(items);
        cultureListView.setAdapter(informationList_adapter);
    }

    public ArrayList<RowData> searchSubCodeItem(String editText){
        ArrayList<RowData> searchList = new ArrayList<>();
        if(!editText.equals("") || editText.length() != 0) {
            for (int i = 0; i < copyItems.size(); i++) {
                if (copyItems.get(i).getCODENAME().contains(editText)) {
                    searchList.add(copyItems.get(i));
                }
            }
        }else{
            TranslationWordTask translationWordTask = new TranslationWordTask();
            translationWordTask.execute("http://openapi.seoul.go.kr:8088/74556251686f726e37306444466b54/json/SearchConcertDetailService/1/50");
            return copyItems;
        }
        return searchList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
