package kr.co.travelmaker.seoulmate.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApplyMemberBoard {
    private Board board;
    private ArrayList<ApplyMember> applyMembers;
}
