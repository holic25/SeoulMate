package kr.co.travelmaker.seoulmate.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchGuideBoard {
    boolean[] isSelectDays;
    Integer payment_type;
    Integer guide_type;
    String start_time;
    String end_time;
}
