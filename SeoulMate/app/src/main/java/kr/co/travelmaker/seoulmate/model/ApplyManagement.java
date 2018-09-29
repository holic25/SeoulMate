package kr.co.travelmaker.seoulmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApplyManagement {
    private Long apply_id_inc;
    private Long board_fk_inc;
    private Long applicant_fk_inc;
    private Integer apply_approval;
}