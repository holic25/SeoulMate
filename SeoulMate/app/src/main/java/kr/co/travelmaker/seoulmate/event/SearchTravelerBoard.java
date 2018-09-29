package kr.co.travelmaker.seoulmate.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchTravelerBoard {
    String first_date;
    String last_date;
    Integer payment_type;
    Integer guide_type;
}
