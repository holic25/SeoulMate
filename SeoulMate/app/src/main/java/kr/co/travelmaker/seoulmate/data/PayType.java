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
public class PayType {
    private int[] cashOrCard = {R.string.payment_type, R.string.cash, R.string.goods};
}