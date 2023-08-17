package cn.edu.xmu.oomall.goods.mapper.po.strategy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", visible = true)
public abstract class BaseCouponLimitation {

	public BaseCouponLimitation(long value) {
		this.value = value;
		this.className = this.getClass().getName();
	}

	protected long value;

	protected String className;

	public abstract boolean pass(List<Item> items);

	public static BaseCouponLimitation getInstance(String jsonString) throws JsonProcessingException, ClassNotFoundException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(jsonString);
		String className = root.get("className").asText();
		return (BaseCouponLimitation) mapper.readValue(jsonString, Class.forName(className));
	}
}
