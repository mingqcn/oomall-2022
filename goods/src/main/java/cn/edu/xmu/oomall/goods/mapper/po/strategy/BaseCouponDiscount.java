package cn.edu.xmu.oomall.goods.mapper.po.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseCouponDiscount implements Computable{

	public BaseCouponDiscount(BaseCouponLimitation limitation, long value) {
		this.couponLimitation = limitation;
		this.value = value;
		this.className = this.getClass().getName();
	}

	protected long value;

	protected String className;

	protected BaseCouponLimitation couponLimitation;

	@Override
	public List<Item> compute(List<Item> items) {
		if (!couponLimitation.pass(items)) {
			for (Item oi : items) {
				oi.setCouponActivityId(null);
			}
			return items;
		}

		calcAndSetDiscount(items);

		return items;
	}

	public abstract void calcAndSetDiscount(List<Item> items);


	public static BaseCouponDiscount getInstance(String jsonString) throws JsonProcessingException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(jsonString);
		String className = root.get("className").asText();
		BaseCouponDiscount bc = (BaseCouponDiscount) Class.forName(className).getConstructor().newInstance();

		String limitation = root.get("couponLimitation").toString();
		BaseCouponLimitation bl = BaseCouponLimitation.getInstance(limitation);

		bc.setCouponLimitation(bl);
		bc.setValue(root.get("value").asLong());
		bc.setClassName(className);

		return bc;
	}

}
