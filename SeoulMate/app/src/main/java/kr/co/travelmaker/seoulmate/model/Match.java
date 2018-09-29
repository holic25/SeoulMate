package kr.co.travelmaker.seoulmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Match {
	private Long match_id_inc;
	private Long traveler_id_inc;
	private Long guide_id_inc;
	private String match_date;
	private Integer match_complete;
}
