package kr.co.travelmaker.seoulmate.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class row {
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
    private String USE_TRGT;
    private String USE_FEE;
    private String INQUIRY;
}
