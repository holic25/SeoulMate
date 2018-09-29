package kr.co.travelmaker.seoulmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Board {
	private Long board_id_inc;
	private String board_title;
	private String board_place;
	private Integer board_paytype;
	private Integer board_guidetype;
	private String board_content;
	private Integer board_complete;
	private Integer board_kind;
	private Long member_fk_inc;
}
