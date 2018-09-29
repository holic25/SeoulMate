package kr.co.travelmaker.seoulmate.retrofilt;

import java.util.ArrayList;

import kr.co.travelmaker.seoulmate.model.ApplyMemberBoard;
import kr.co.travelmaker.seoulmate.model.Board;
import kr.co.travelmaker.seoulmate.model.GuideBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;
import kr.co.travelmaker.seoulmate.model.GuideMemberLicense;
import kr.co.travelmaker.seoulmate.model.IsCheck;
import kr.co.travelmaker.seoulmate.model.MatchMember;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.model.ReviewMember;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitRequest {

    @GET("SeoulMateDataBase.do")
    Call<ArrayList<Member>> member();

    @GET("getMemberData.do")
    Call<Member> getMemberData(@Query("member_id_inc") Long member_id_inc);

    @GET("getGuideMemberLicense.do")
    Call<GuideMemberLicense> getGuideMemberLicense(@Query("member_id_inc") Long member_id_inc);

    @Multipart
    @POST("SeoulMateMemberInsert.do")
    Call<Member> SeoulMateMemberInsert(@Part("member_id") RequestBody member_id,
                                       @Part("member_pw") RequestBody member_pw,
                                       @Part("member_email") RequestBody member_email,
                                       @Part("member_name") RequestBody member_name,
                                       @Part("member_nat") RequestBody member_nat,
                                       @Part("member_gender") Integer member_gender,
                                       @Part("member_kind") Integer member_kind,
                                       @Part("member_birth") RequestBody member_birth);

    @Multipart
    @POST("SeoulMateGuideMemberInsert.do")
    Call<Member> SeoulMateGuideMemberInsert(@Part("member_id") RequestBody member_id,
                                            @Part("member_pw") RequestBody member_pw,
                                            @Part("member_email") RequestBody member_email,
                                            @Part("member_name") RequestBody member_name,
                                            @Part("member_nat") RequestBody member_nat,
                                            @Part("member_gender") Integer member_gender,
                                            @Part("member_kind") Integer member_kind,
                                            @Part("member_birth") RequestBody member_birth,
                                            @Part MultipartBody.Part license_image,
                                            @Part("license_approval") Integer license_approval);

    @Multipart
    @POST("isCheckedId.do")
    Call<IsCheck> isCheckedId(@Part("member_id") RequestBody member_id);

    @Multipart
    @POST("loginCheck.do")
    Call<Member> loginCheck(@Part("member_id") RequestBody member_id,
                            @Part("member_pw") RequestBody member_pw);

    @Multipart
    @POST("BoardInsert.do")
    Call<Void> BoardTravelerInsert(
            @Part("board_title") RequestBody board_title,
            @Part("board_place") RequestBody board_place,
            @Part("board_paytype") RequestBody board_paytype,
            @Part("board_guidetype") RequestBody board_guidetype,
            @Part("board_content") RequestBody board_content,
            @Part("board_complete") Integer board_complete,
            @Part("board_kind") Integer board_kind,
            @Part("member_fk_inc") Long member_fk_inc,
            @Part("traveler_id_board_startdate") RequestBody traveler_id_board_startdate,
            @Part("traveler_id_board_enddate") RequestBody traveler_id_board_enddate);

    @Multipart
    @POST("BoardInsert.do")
    Call<Void> BoardGuideInsert(
            @Part("board_title") RequestBody board_title,
            @Part("board_place") RequestBody board_place,
            @Part("board_paytype") RequestBody board_paytype,
            @Part("board_guidetype") RequestBody board_guidetype,
            @Part("board_content") RequestBody board_content,
            @Part("board_complete") Integer board_complete,
            @Part("board_kind") Integer board_kind,
            @Part("member_fk_inc") Long member_fk_inc,
            @Part("guide_sun") Integer guide_sun,
            @Part("guide_mon") Integer guide_mon,
            @Part("guide_tue") Integer guide_tue,
            @Part("guide_wed") Integer guide_wed,
            @Part("guide_thu") Integer guide_thu,
            @Part("guide_fri") Integer guide_fri,
            @Part("guide_sat") Integer guide_sat,
            @Part("guide_startTime") RequestBody guide_startTime,
            @Part("guide_endTime") RequestBody guide_endTime);

    @GET("getAllTravelerBoardData.do")
    Call<ArrayList<TravelerBoard>> getAllTravelerBoard();

    @GET("getAllGuideBoardData.do")
    Call<ArrayList<GuideBoardMember>> getAllGuideBoard();

    @Multipart
    @POST("getSelectedTravelerBoardData.do")
    Call<ArrayList<TravelerBoard>> getSelectedTravelerBoard(
            @Part("board_paytype") RequestBody board_paytype,
            @Part("board_guidetype") RequestBody board_guidetype,
            @Part("traveler_id_board_startdate") RequestBody traveler_id_board_startdate,
            @Part("traveler_id_board_enddate") RequestBody traveler_id_board_enddate);

    @Multipart
    @POST("getSelectedGuideBoardData.do")
    Call<ArrayList<GuideBoardMember>> getSelectedGuideBoard(
            @Part("board_paytype") RequestBody board_paytype,
            @Part("board_guidetype") RequestBody board_guidetype,
            @Part("guide_sun") Integer guide_sun,
            @Part("guide_mon") Integer guide_mon,
            @Part("guide_tue") Integer guide_tue,
            @Part("guide_wed") Integer guide_wed,
            @Part("guide_thu") Integer guide_thu,
            @Part("guide_fri") Integer guide_fri,
            @Part("guide_sat") Integer guide_sat);

    @Multipart
    @POST("updateCompleteBoard.do")
    Call<Void> completeBoard(
            @Part("board_id_inc") Long board_id_inc
    );

    @Multipart
    @POST("deleteTravelerBoardData.do")
    Call<Void> deleteBoard(
            @Part("board_id_inc") Long board_id_inc
    );

    @Multipart
    @POST("updateBoardData.do")
    Call<Void> BoardTravelerUpdate(
            @Part("board_id_inc") Long board_id_inc,
            @Part("board_title") RequestBody board_title,
            @Part("board_place") RequestBody board_place,
            @Part("board_paytype") RequestBody board_paytype,
            @Part("board_guidetype") RequestBody board_guidetype,
            @Part("board_content") RequestBody board_content,
            @Part("board_kind") Integer board_kind,
            @Part("traveler_id_board_startdate") RequestBody traveler_id_board_startdate,
            @Part("traveler_id_board_enddate") RequestBody traveler_id_board_enddate);

    @Multipart
    @POST("updateBoardData.do")
    Call<Void> BoardGuideUpdate(
            @Part("board_id_inc") Long board_id_inc,
            @Part("board_title") RequestBody board_title,
            @Part("board_place") RequestBody board_place,
            @Part("board_paytype") RequestBody board_paytype,
            @Part("board_guidetype") RequestBody board_guidetype,
            @Part("board_content") RequestBody board_content,
            @Part("board_kind") Integer board_kind,
            @Part("guide_sun") Integer guide_sun,
            @Part("guide_mon") Integer guide_mon,
            @Part("guide_tue") Integer guide_tue,
            @Part("guide_wed") Integer guide_wed,
            @Part("guide_thu") Integer guide_thu,
            @Part("guide_fri") Integer guide_fri,
            @Part("guide_sat") Integer guide_sat,
            @Part("guide_startTime") RequestBody guide_startTime,
            @Part("guide_endTime") RequestBody guide_endTime);

    @GET("getGuideBoardData.do")
    Call<GuideBoardMember> getGuideBoard(@Query("board_id_inc") Long board_id_inc);

    @GET("getTravelerBoardData.do")
    Call<TravelerBoard> getTravelerBoard(@Query("board_id_inc") Long board_id_inc);

    @Multipart
    @POST("getSelectedApplyBoardData.do")
    Call<ArrayList<ApplyMemberBoard>> getSelectedApplyBoardData(@Part("member_id_inc") Long member_id_inc);

    @Multipart
    @POST("updateApplyApproval.do")
    Call<Void> updateApplyApproval(
            @Part("apply_id_inc") Long apply_id_inc,
            @Part("apply_approval") Integer apply_approval,
            @Part("traveler_id_inc") Long traveler_id_inc,
            @Part("guide_id_inc") Long guide_id_inc,
            @Part("match_date") RequestBody match_date);

    @GET("getApplyCount.do")
    Call<Integer> getApplyCount(@Query("member_id_inc") Long member_id_inc);

    @GET("getTravelerMatchData.do")
    Call<ArrayList<MatchMember>> getTravelerMatch(@Query("member_id_inc") Long member_id_inc);

    @GET("getGuideMatchData.do")
    Call<ArrayList<MatchMember>> getGuideMatch(@Query("member_id_inc") Long member_id_inc);

    @GET("getMyTravelerBoardData.do")
    Call<ArrayList<TravelerBoard>> getMyTravelerBoardData(@Query("member_id_inc") Long member_id_inc);

    @GET("getMyGuideBoardData.do")
    Call<ArrayList<GuideBoardMember>> getMyGuideBoardData(@Query("member_id_inc") Long member_id_inc);

    @Multipart
    @POST("updateMemberData.do")
    Call<Void> updateMemberData(
            @Part("member_id_inc") Long member_id_inc,
            @Part("member_kind") Integer member_kind,
            @Part("member_pw") RequestBody member_pw,
            @Part MultipartBody.Part license_image);

    @Multipart
    @POST("updateMemberData.do")
    Call<Void> updateMemberData(
            @Part("member_id_inc") Long member_id_inc,
            @Part("member_kind") Integer member_kind,
            @Part("member_pw") RequestBody member_pw);


    @FormUrlEncoded
    @POST("deleteTravelerMemberData.do")
    Call<Void> deleteTravelerMemberData(@Field("member_id_inc") Long member_id_inc);

    @FormUrlEncoded
    @POST("deleteGuideMemberData.do")
    Call<Void> deleteGuideMemberData(@Field("member_id_inc") Long member_id_inc);

    @GET("getReviewMatchMemberData.do")
    Call<MatchMember> getReviewMatchMember(@Query("match_id_inc") Long getMatch_id_inc, @Query("member_id_inc") Long getMember_id);

    @GET("updateReviewMatchMemberCompleteData.do")
    Call<Void> updateReviewMatchMemberComplete(@Query("match_id_inc") Long getMatch_id_inc);

    @POST("insertReviewData.do")
    Call<Void> insertReview(
            @Query("review_targeter_id") Long review_targeter_id,
            @Query("review_writer_id") Long review_writer_id,
            @Query("review_date") String review_date,
            @Query("review_content") String review_content,
            @Query("review_starpoint") float review_starpoint);

    @POST("applyInsert.do")
    Call<Void> applyInsert(@Query("member_id_inc") Long member_id_inc,
                           @Query("board_id_inc") Long board_id_inc);

    //로그인한 사람이 traveler일 때는 자기가 쓴 리뷰 list
    @GET("getTravelerReviewListData.do")
    Call<ArrayList<ReviewMember>> getTravelerReviewListData(@Query("review_writer_id") Long review_writer_id);

    //로그인한 사람이 guide일 때는 자기에게 달린 리뷰 list
    @GET("getGuideReviewListData.do")
    Call<ArrayList<ReviewMember>> getGuideReviewListData(@Query("review_targeter_id") Long review_target_id);
}