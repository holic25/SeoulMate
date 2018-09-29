package kr.co.travelmaker.seoulmate.event;

import kr.co.travelmaker.seoulmate.data.RowData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RowDataEvent {
    RowData rowData;
}
