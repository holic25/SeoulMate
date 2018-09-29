package kr.co.travelmaker.seoulmate.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Review {
	private Integer review_id_inc;
	private Integer review_targeter_id;
	private Integer review_writer_id;
	private String review_date;
	private String review_content;
	private float review_starpoint;
}
