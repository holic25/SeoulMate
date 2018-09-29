package kr.co.travelmaker.seoulmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TravelerAddBoard {
	private Long traveler_id_board_inc;
	private String traveler_id_board_startdate;
	private String traveler_id_board_enddate;
	private Long board_fk_inc;
}
