package kr.co.travelmaker.seoulmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class GuideAddBoard {
	private Long guide_id_inc;
	private Integer guide_sun;
	private Integer guide_mon;
	private Integer guide_tue;
	private Integer guide_wed;
	private Integer guide_thu;
	private Integer guide_fri;
	private Integer guide_sat;
	private String guide_startTime;
	private String guide_endTime;
	private Long board_fk_inc;
}
