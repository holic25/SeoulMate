package kr.co.travelmaker.seoulmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GuideMemberLicense {
    private Long license_id_inc;
    private String license_image;
    private String license_imagepath;
    private Integer license_approval;
    private Long member_fk_inc;
}