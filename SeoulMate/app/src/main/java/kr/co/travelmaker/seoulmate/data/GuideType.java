package kr.co.travelmaker.seoulmate.data;

import kr.co.travelmaker.seoulmate.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class GuideType {
    private int[] onlyGuideOrDrivingAndGuide = {R.string.guide_type, R.string.drive_guide, R.string.guide_only};
}