package kr.co.travelmaker.seoulmate.model;

import kr.co.travelmaker.seoulmate.data.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReviewMember {
    Review review;
    Member member;
}