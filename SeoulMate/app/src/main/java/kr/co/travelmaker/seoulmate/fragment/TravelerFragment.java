package kr.co.travelmaker.seoulmate.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.SearchTravelerBoard;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TravelerFragment extends Fragment {

    private static TravelerFragment curr = null;

    public static TravelerFragment getInstance() {
        if(curr==null) {
            curr = new TravelerFragment();
        }
        return curr;
    }

    @BindView(R.id.sp_payment_type) Spinner spPaymentType;
    @BindView(R.id.sp_guide_type) Spinner spGuideType;
    @BindView(R.id.tv_travel_first_date) TextView tvTravelFirstDate;
    @BindView(R.id.tv_travel_last_date) TextView tvTravelLastDate;
    @BindView(R.id.layout_first_date) LinearLayout layoutFirstDate;
    @BindView(R.id.layout_last_date) LinearLayout layoutLastDate;

    Calendar cal = Calendar.getInstance();
    int currYear, currMonth, currDay;

    private Unbinder unbinder;
    private Bus bus = BusProvider.getInstance().getBus();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traveler, container, false);
        unbinder = ButterKnife.bind(this,view);
        bus.register(this);

        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH);
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        String currDate;
        currDate = currYear+"-"+(currMonth+1)+"-"+currDay;

        tvTravelFirstDate.setText(currDate);
        tvTravelLastDate.setText(currDate);

        ArrayAdapter paymentAdapter = ArrayAdapter.createFromResource(container.getContext(),R.array.sp_payment_type, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentType.setAdapter(paymentAdapter);

        ArrayAdapter guideAdapter = ArrayAdapter.createFromResource(container.getContext(),R.array.sp_guide_type, android.R.layout.simple_spinner_item);
        guideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGuideType.setAdapter(guideAdapter);

        return view;
    }

    @OnClick(R.id.layout_first_date)
    public void onClickFirstDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String pickDate;
                pickDate = year+"-"+(month+1)+"-"+day;

                tvTravelFirstDate.setText(pickDate);
            }
        };
        DatePickerDialog dateDialog = new DatePickerDialog(getContext(),dateSetListener,currYear,currMonth,currDay);
        dateDialog.show();
    }

    @OnClick(R.id.layout_last_date)
    public void onClickLastDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String pickDate;
                pickDate = year+"-"+(month+1)+"-"+day;

                tvTravelLastDate.setText(pickDate);
            }
        };
        DatePickerDialog dateDialog = new DatePickerDialog(getContext(),dateSetListener,currYear,currMonth,currDay);
        dateDialog.show();
    }

    @OnClick(R.id.btn_traveler_search)
    public void onClickTravelerSearch() {
        SearchTravelerBoard searchTravelerBoard = new SearchTravelerBoard();
        searchTravelerBoard.setFirst_date(tvTravelFirstDate.getText().toString());
        searchTravelerBoard.setLast_date(tvTravelLastDate.getText().toString());
        searchTravelerBoard.setGuide_type(spGuideType.getSelectedItemPosition());
        searchTravelerBoard.setPayment_type(spPaymentType.getSelectedItemPosition());

        bus.post(searchTravelerBoard);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
