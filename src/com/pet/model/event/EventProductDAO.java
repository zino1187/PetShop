package com.pet.model.event;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pet.domain.Event;
import com.pet.domain.EventProduct;
import com.pet.exception.DMLException;

@Repository
public class EventProductDAO {
	@Autowired
	private SqlSessionTemplate sessionTemplate;
	
	public List selectAll() {
		return sessionTemplate.selectList("EventProduct.selectAll");
	}
	
	//이벤트에 소속된 상품 가져오기!
	public List selectJoinByEventId(int event_id) {
		return sessionTemplate.selectList("EventProduct.selectJoinByEventId", event_id);
	}
	
	public void insert(EventProduct eventProduct) throws DMLException{
		int result = sessionTemplate.insert("EventProduct.insert", eventProduct);
		if(result ==0) {
			throw new DMLException("이벤트 상품 등록에 실패하였습니다");
		}
	}
}




