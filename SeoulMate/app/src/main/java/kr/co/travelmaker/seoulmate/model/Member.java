package kr.co.travelmaker.seoulmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Member {
	private Long member_id_inc;
	private String member_id;
	private String member_pw;
	private String member_email;
	private String member_name;
	private String member_nat;
	private Integer member_gender;
	private Integer member_kind;
	private String member_birth;
}
