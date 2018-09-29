package kr.co.travelmaker.seoulmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinSelActivity extends AppCompatActivity {

    @BindView(R.id.rel_traveler_sel) RelativeLayout rel_traveler_sel;
    @BindView(R.id.rel_guide_sel) RelativeLayout rel_guide_sel;

    int join_type;
    static int ORIGINAL = 0;
    static int GOOGLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_sel);
        ButterKnife.bind(this);

        Intent i = getIntent();
        join_type = i.getIntExtra("join_type",-1);
    }
    @OnClick(R.id.rel_traveler_sel)
    public void TravelerOnclick(){
        if(join_type==ORIGINAL) {
            Intent intent = new Intent(JoinSelActivity.this,TravelerJoinActivity.class);
            startActivity(intent);
            finish();
        }
        else if(join_type==GOOGLE) {
            Intent intent = new Intent(JoinSelActivity.this,ApiJoinTravelerActivity.class);
            intent.putExtra("join_type",GOOGLE);
            startActivity(intent);
            finish();
        }
    }
    @OnClick(R.id.rel_guide_sel)
    public void GuideOnclick(){
        if(join_type==ORIGINAL) {
            Intent intent = new Intent(JoinSelActivity.this,GuideJoinActivity.class);
            startActivity(intent);
            finish();
        }
        else if(join_type==GOOGLE) {
            Intent intent = new Intent(JoinSelActivity.this,ApiJoinGuideActivity.class);
            intent.putExtra("join_type",GOOGLE);
            startActivity(intent);
            finish();
        }
    }
}
