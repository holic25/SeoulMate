package kr.co.travelmaker.seoulmate.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.squareup.otto.Bus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.SearchGuideBoard;

public class GuideFragment extends Fragment {

    private static GuideFragment curr = null;

    public static GuideFragment getInstance() {
        if(curr==null) {
            curr = new GuideFragment();
        }
        return curr;
    }

    @BindView(R.id.sp_payment_type) Spinner spPaymentType;
    @BindView(R.id.sp_guide_type) Spinner spGuideType;
    @BindView(R.id.btn_sun) Button btnSun;
    @BindView(R.id.btn_mon) Button btnMon;
    @BindView(R.id.btn_tue) Button btnTue;
    @BindView(R.id.btn_wed) Button btnWed;
    @BindView(R.id.btn_thu) Button btnThu;
    @BindView(R.id.btn_fri) Button btnFri;
    @BindView(R.id.btn_sat) Button btnSat;
    @BindView(R.id.btn_guide_search) Button btnGuideSearch;

    int[] days = {R.string.sun, R.string.mon, R.string.tue, R.string.wed, R.string.thu, R.string.fri, R.string.sat};
    Button[] btnDays = new Button[7];
    boolean[] isSelectDays = new boolean[7];

    private Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        unbinder = ButterKnife.bind(this,view);
        bus.register(this);

        btnDays[0] = btnSun;
        btnDays[1] = btnMon;
        btnDays[2] = btnTue;
        btnDays[3] = btnWed;
        btnDays[4] = btnThu;
        btnDays[5] = btnFri;
        btnDays[6] = btnSat;

        for(int i=0;i<btnDays.length;i++) {
            final int finalI = i;
            btnDays[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSelectDays[finalI] = !isSelectDays[finalI];
                    selectDays(finalI);
                }
            });
        }

        ArrayAdapter paymentAdapter = ArrayAdapter.createFromResource(container.getContext(),R.array.sp_payment_type, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentType.setAdapter(paymentAdapter);

        ArrayAdapter guideAdapter = ArrayAdapter.createFromResource(container.getContext(),R.array.sp_guide_type, android.R.layout.simple_spinner_item);
        guideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGuideType.setAdapter(guideAdapter);

        return view;
    }

    public void selectDays(int i) {
        if(isSelectDays[i]) {
            btnDays[i].setBackgroundResource(R.drawable.shape_rectangle_dark_violet_full_30);
            btnDays[i].setTextColor(getColor(getContext(),R.color.cf_white));
        }
        else {
            btnDays[i].setBackgroundResource(R.drawable.shape_rectangle_dark_violet_line_30);
            btnDays[i].setTextColor(getColor(getContext(),R.color.dark_violet));
        }
    }

    @OnClick(R.id.btn_guide_search)
    public void onClickGuideSearch() {
        SearchGuideBoard searchGuideBoard = new SearchGuideBoard();
        searchGuideBoard.setGuide_type(spGuideType.getSelectedItemPosition());
        searchGuideBoard.setPayment_type(spPaymentType.getSelectedItemPosition());
        searchGuideBoard.setIsSelectDays(isSelectDays);

        bus.post(searchGuideBoard);
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

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }
}
