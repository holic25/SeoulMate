package kr.co.travelmaker.seoulmate.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.GuideBoardDetailActivity;
import kr.co.travelmaker.seoulmate.GuideBoardWriteActivity;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.TravelerBoardDetailActivity;
import kr.co.travelmaker.seoulmate.TravelerBoardWriteActivity;
import kr.co.travelmaker.seoulmate.adapter.BoardGuideListAdapter;
import kr.co.travelmaker.seoulmate.adapter.BoardTravelerListAdapter;
import kr.co.travelmaker.seoulmate.adapter.BoardViewPagerAdapter;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.SearchGuideBoard;
import kr.co.travelmaker.seoulmate.event.SearchTravelerBoard;
import kr.co.travelmaker.seoulmate.event.GuideBoard;
import kr.co.travelmaker.seoulmate.event.TravelerBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardFragment extends Fragment {

    @BindView(R.id.btn_traveler) Button btnTraveler;
    @BindView(R.id.btn_guide) Button btnGuide;
    @BindView(R.id.layout_open_search) LinearLayout layoutOpenSearch;
    @BindView(R.id.btn_close) Button btnClose;
    @BindView(R.id.btn_open) Button btnOpen;
    @BindView(R.id.btn_add_board) Button btnAddBoard;
    @BindView(R.id.layout_search) LinearLayout layout_search;
    @BindView(R.id.vp_search_menu) ViewPager vpSearchMenu;
    BoardViewPagerAdapter boardViewPagerAdapter;
    @BindView(R.id.lv_list_board) ListView lvListBoard;
    BoardTravelerListAdapter boardTravelerListAdapter;
    ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard> travelerBoards = new ArrayList<>();
    BoardGuideListAdapter boardGuideListAdapter;
    ArrayList<GuideBoardMember> guideBoardMembers = new ArrayList<>();

    private Unbinder unbinder;

    Integer tabNum = 2;
    Button[] tabs = new Button[tabNum];

    static int TRAVELER = 0;
    static int GUIDE = 1;

    LoginService loginService = LoginService.getInstance();
    private Bus bus = BusProvider.getInstance().getBus();

    int currentIndex_intent = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        unbinder = ButterKnife.bind(this,view);
        bus.register(this);

        getAllTravelerBoard();

        tabs[0] = btnTraveler;
        tabs[1] = btnGuide;

        boardViewPagerAdapter = new BoardViewPagerAdapter(getChildFragmentManager());
        vpSearchMenu.setAdapter(boardViewPagerAdapter);

        vpSearchMenu.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int currentIndex) {
                for(int i=0;i<tabNum;i++) {
                    tabs[i].setBackgroundResource(R.drawable.shape_rectangle_dark_violet_line_30);
                    tabs[i].setTextColor(getColor(getContext(),R.color.dark_violet));
                }
                tabs[currentIndex].setBackgroundResource(R.drawable.shape_rectangle_dark_violet_full_30);
                tabs[currentIndex].setTextColor(getColor(getContext(),R.color.cf_white));

                if(currentIndex==0) {
                    getAllTravelerBoard();
                    currentIndex_intent = 0;
                }
                else if(currentIndex==1) {
                    getAllGuideBoard();
                    currentIndex_intent = 1;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return view;
    }

    @OnClick(R.id.btn_traveler)
    public void onClickTraveler() {
        vpSearchMenu.setCurrentItem(0);
    }

    @OnClick(R.id.btn_guide)
    public void onClickGuide() {
        vpSearchMenu.setCurrentItem(1);
    }

    @OnClick(R.id.btn_close)
    public void onClickClose() {
        layout_search.setVisibility(View.GONE);
        btnClose.setVisibility(View.GONE);
        layoutOpenSearch.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_open)
    public void onClickOpen() {
        layout_search.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);
        layoutOpenSearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_add_board)
    public void onClickAddBoard() {
        int loginMember_type = loginService.getLoginMember().getMember_kind();

        if(loginMember_type==TRAVELER) {
            Intent i = new Intent(getContext(), TravelerBoardWriteActivity.class);
            startActivity(i);
        }

        else if(loginMember_type==GUIDE) {
            Intent i = new Intent(getContext(), GuideBoardWriteActivity.class);
            startActivity(i);
        }
    }


    public void getAllTravelerBoard() {
        Call<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>> observ = RetrofitService.getInstance().getRetrofitRequest().getAllTravelerBoard();
        observ.enqueue(new Callback<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>>() {
            @Override
            public void onResponse(Call<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>> call, Response<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>> response) {
                if(response.isSuccessful()){
                    Log.d("datalog","success");

                    travelerBoards = response.body();

                    for(int i=0;i<travelerBoards.size();i++) {
                        Log.d("datalog",travelerBoards.get(i).toString());
                    }
                    boardTravelerListAdapter = new BoardTravelerListAdapter(travelerBoards);
                    lvListBoard.setAdapter(boardTravelerListAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>> call, Throwable t) {
                Log.d("datalog","fail");
            }
        });
    }

    public void getAllGuideBoard() {
        Call<ArrayList<GuideBoardMember>> observ = RetrofitService.getInstance().getRetrofitRequest().getAllGuideBoard();
        observ.enqueue(new Callback<ArrayList<GuideBoardMember>>() {
            @Override
            public void onResponse(Call<ArrayList<GuideBoardMember>> call, Response<ArrayList<GuideBoardMember>> response) {
                if(response.isSuccessful()){
                    Log.d("datalog","success");

                    guideBoardMembers = response.body();

                    for(int i=0;i<guideBoardMembers.size();i++) {
                        Log.d("datalog",guideBoardMembers.get(i).toString());
                    }
                    boardGuideListAdapter = new BoardGuideListAdapter(guideBoardMembers);
                    lvListBoard.setAdapter(boardGuideListAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GuideBoardMember>> call, Throwable t) {
                Log.d("datalog","fail");
            }
        });
    }

    @Subscribe
    public void searchTravelerBoard(SearchTravelerBoard event) {

        Log.d("datalog","searchTravelerBoard");
        Log.d("datalog",event.toString());

        RequestBody paymentType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(event.getPayment_type()));
        RequestBody guideType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(event.getGuide_type()));
        RequestBody firstDate = RequestBody.create(MediaType.parse("text/plain"), event.getFirst_date());
        RequestBody lastDate = RequestBody.create(MediaType.parse("text/plain"), event.getLast_date());

        Call<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>> observ = RetrofitService.getInstance().getRetrofitRequest().getSelectedTravelerBoard(paymentType,guideType,firstDate,lastDate);
        observ.enqueue(new Callback<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>>() {
            @Override
            public void onResponse(Call<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>> call, Response<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>> response) {
                if(response.isSuccessful()){
                    Log.d("datalog","success");

                    travelerBoards = response.body();

                    for(int i=0;i<travelerBoards.size();i++) {
                        Log.d("datalog",travelerBoards.get(i).toString());
                    }
                    boardTravelerListAdapter = new BoardTravelerListAdapter(travelerBoards);
                    lvListBoard.setAdapter(boardTravelerListAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<kr.co.travelmaker.seoulmate.model.TravelerBoard>> call, Throwable t) {
                Log.d("datalog","fail");
            }
        });
    }

    @Subscribe
    public void searchGuideBoard(SearchGuideBoard event) {

        Log.d("datalog","searchGuideBoard");
        Log.d("datalog",event.toString());

        RequestBody paymentType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(event.getPayment_type()));
        RequestBody guideType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(event.getGuide_type()));

        boolean[] isSelectDays = event.getIsSelectDays();
        Integer[] isSelected = new Integer[isSelectDays.length];
        for(int i=0;i<isSelected.length;i++) {
            if(isSelectDays[i]) {
                isSelected[i] = 1;
            }
            else {
                isSelected[i] = 0;
            }
        }


        Call<ArrayList<GuideBoardMember>> observ = RetrofitService.getInstance().getRetrofitRequest().getSelectedGuideBoard(paymentType,guideType,isSelected[0],isSelected[1],isSelected[2],isSelected[3],isSelected[4],isSelected[5],isSelected[6]);
        observ.enqueue(new Callback<ArrayList<GuideBoardMember>>() {
            @Override
            public void onResponse(Call<ArrayList<GuideBoardMember>> call, Response<ArrayList<GuideBoardMember>> response) {
                if(response.isSuccessful()){
                    Log.d("datalog","success");

                    guideBoardMembers = response.body();

                    for(int i=0;i<guideBoardMembers.size();i++) {
                        Log.d("datalog",guideBoardMembers.get(i).toString());
                    }
                    boardGuideListAdapter = new BoardGuideListAdapter(guideBoardMembers);
                    lvListBoard.setAdapter(boardGuideListAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GuideBoardMember>> call, Throwable t) {
                Log.d("datalog","fail");
            }
        });
    }

    @OnItemClick(R.id.lv_list_board)
    void onItemClick(int position){
        if(currentIndex_intent == 1){

            Log.d("dsj", guideBoardMembers.get(position).getGuideBoard().getBoard().getBoard_id_inc() + "");
            Intent intent = new Intent(getContext(),GuideBoardDetailActivity.class);
            intent.putExtra("board_id_inc", guideBoardMembers.get(position).getGuideBoard().getBoard().getBoard_id_inc());
            startActivity(intent);

        }else{

            Log.d("dsj", travelerBoards.get(position).getBoard().getBoard_id_inc() + "");
            Intent intent = new Intent(getContext(), TravelerBoardDetailActivity.class);

            intent.putExtra("board_id_inc", travelerBoards.get(position).getBoard().getBoard_id_inc());
            intent.putExtra("board_title", travelerBoards.get(position).getBoard().getBoard_title());
            intent.putExtra("board_place", travelerBoards.get(position).getBoard().getBoard_place());
            intent.putExtra("board_paytype", travelerBoards.get(position).getBoard().getBoard_paytype());
            intent.putExtra("board_guidetype", travelerBoards.get(position).getBoard().getBoard_guidetype());
            intent.putExtra("board_content", travelerBoards.get(position).getBoard().getBoard_content());
            intent.putExtra("board_complete", travelerBoards.get(position).getBoard().getBoard_complete());
            intent.putExtra("board_kind", travelerBoards.get(position).getBoard().getBoard_kind());
            intent.putExtra("member_fk_inc", travelerBoards.get(position).getBoard().getMember_fk_inc());
            intent.putExtra("traveler_id_board_inc", travelerBoards.get(position).getTraveler_add_board().getTraveler_id_board_inc());
            intent.putExtra("traveler_id_board_startdate", travelerBoards.get(position).getTraveler_add_board().getTraveler_id_board_startdate());
            intent.putExtra("traveler_id_board_enddate", travelerBoards.get(position).getTraveler_add_board().getTraveler_id_board_enddate());
            intent.putExtra("board_fk_inc", travelerBoards.get(position).getTraveler_add_board().getBoard_fk_inc());

            startActivity(intent);
        }
    }

    @Subscribe
    public void TravelerBoard(TravelerBoard event) {
        onClickTraveler();
        getAllTravelerBoard();
    }

    @Subscribe
    public void GuideBoard(GuideBoard event) {
        onClickGuide();
        getAllGuideBoard();
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
