package kr.co.travelmaker.seoulmate.model;


import java.util.ArrayList;

import kr.co.travelmaker.seoulmate.data.ResultData;
import kr.co.travelmaker.seoulmate.data.RowData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchConcertDetail {
    private Integer list_total_count;
    private ResultData RESULT;
    private ArrayList<RowData> row;
}
