package kr.co.travelmaker.seoulmate.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.RequestDataChanged;
import kr.co.travelmaker.seoulmate.model.ApplyMember;
import kr.co.travelmaker.seoulmate.model.ApplyMemberBoard;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdapter extends BaseAdapter {
    ArrayList<ApplyMemberBoard> items;
    LoginService loginService = LoginService.getInstance();
    Member member = loginService.getLoginMember();
    Bus bus;

    public RequestAdapter(ArrayList<ApplyMemberBoard> items, Bus bus) {
        this.items = items;
        this.bus = bus;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_request, parent, false);
            holder = new RequestAdapter.Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        final ApplyMemberBoard item = (ApplyMemberBoard)getItem(position);

        Integer kind = member.getMember_kind();
        String member_id = member.getMember_id().toString();
        String board_title = item.getBoard().getBoard_title().toString();
        String board_post = item.getBoard().getBoard_content().toString();

        holder.txt_member_id.setText(member_id);
        holder.txt_board_title.setText(board_title);
        holder.txt_board_post.setText(board_post);

        if(kind == 0) {
            //traveler
            holder.img_kind.setBackgroundResource(R.drawable.ic_backpack);
        } else {
            holder.img_kind.setBackgroundResource(R.drawable.ic_compass);
        }

        holder.layoutApplicant.removeAllViews();
        for(int i = 0; i<items.get(position).getApplyMembers().size(); i++) {
            View child = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_apply, parent, false);

            ImageView img_applicant_for_travel = (ImageView)child.findViewById(R.id.img_applicant_for_travel);
            TextView txt_applicant_id_for_travel = (TextView)child.findViewById(R.id.txt_applicant_id_for_travel);
            Button btn_accept = (Button)child.findViewById(R.id.btn_accept);
            Button btn_refuse = (Button)child.findViewById(R.id.btn_refuse);

            final ApplyMember applyMember = items.get(position).getApplyMembers().get(i);

            if(applyMember.getMember().getMember_kind()==0) {
                img_applicant_for_travel.setBackgroundResource(R.drawable.ic_backpack);
            }
            else {
                img_applicant_for_travel.setBackgroundResource(R.drawable.ic_compass);
            }

            txt_applicant_id_for_travel.setText(applyMember.getMember().getMember_id());

            final int finalI = i;
            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent.getContext());
                    alertDialogBuilder.setTitle("수락하시겠습니까?");
                    alertDialogBuilder
                            .setMessage("수락 시, 취소는 불가능합니다.")
                            .setCancelable(false)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Long t_id;
                                            Long g_id;

                                            if(applyMember.getMember().getMember_kind()==0) {
                                                t_id = applyMember.getMember().getMember_id_inc();
                                                g_id = loginService.getLoginMember().getMember_id_inc();
                                            }
                                            else {
                                                g_id = applyMember.getMember().getMember_id_inc();
                                                t_id = loginService.getLoginMember().getMember_id_inc();
                                            }

                                            String date = Calendar.getInstance().get(Calendar.YEAR)+"/"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"/"+Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                                            RequestBody match_date = RequestBody.create(MediaType.parse("text/plain"), date);

                                            Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest()
                                                    .updateApplyApproval(applyMember.getApplyManagement().getApply_id_inc(),1,t_id,g_id,match_date);
                                            observ.enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if(response.isSuccessful()) {
                                                        Log.d("datalog","update success");
                                                        items.get(position).getApplyMembers().remove(finalI);
                                                        if(items.get(position).getApplyMembers().size()==0) {
                                                            items.remove(position);
                                                        }
                                                        notifyDataSetChanged();
                                                        RequestDataChanged requestDataChanged = new RequestDataChanged();
                                                        bus.post(requestDataChanged);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Log.d("datalog","fail");
                                                }
                                            });
                                        }
                                    })
                            .setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            btn_refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(parent.getContext());
                    alert_confirm.setMessage("거절하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest()
                                                    .updateApplyApproval(applyMember.getApplyManagement().getApply_id_inc(),2,null,null,null);
                                            observ.enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if(response.isSuccessful()) {
                                                        Log.d("datalog","delete success");
                                                        items.get(position).getApplyMembers().remove(finalI);
                                                        if(items.get(position).getApplyMembers().size()==0) {
                                                            items.remove(position);
                                                        }
                                                        notifyDataSetChanged();
                                                        RequestDataChanged requestDataChanged = new RequestDataChanged();
                                                        bus.post(requestDataChanged);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Log.d("datalog","fail");
                                                }
                                            });
                                        }
                                    })
                            .setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }
            });

            holder.layoutApplicant.addView(child);
        }

        return convertView;
    }

    public class Holder {
        @BindView(R.id.img_kind) ImageView img_kind;
        @BindView(R.id.txt_member_id) TextView txt_member_id;
        @BindView(R.id.txt_board_title) TextView txt_board_title;
        @BindView(R.id.txt_board_post) TextView txt_board_post;
        @BindView(R.id.layout_applicant) LinearLayout layoutApplicant;

        public Holder(View view) {
            ButterKnife.bind(this,view);
        }

    }
}