package kr.co.travelmaker.seoulmate.data;

import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RowData {
    private String CULTCODE;
    private String SUBJCODE;
    private String CODENAME;
    private String TITLE;
    private String STRTDATE;
    private String END_DATE;
    private String TIME;
    private String PLACE;
    private String ORG_LINK;
    private String MAIN_IMG;
    private String HOMEPAGE;
    private String USE_TRGT;
    private String USE_FEE;
    private String INQUIRY;
    private String SPONSOR;
    private String ETC_DESC;
    private String AGELIMIT;
    private String IS_FREE;
    private String TICKET;
    private String PROGRAM;
    private String PLAYER;
    private String CONTENTS;
    private String GCODE;


    public static String toJson(Object jsonObject) {
        return new Gson().toJson(jsonObject);
    }
}
