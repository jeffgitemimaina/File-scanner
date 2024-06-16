package com.velocistech.filescanner01;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class Filescanner01ApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void queryResponse(){

		Map<String,Object> dataBody = new HashMap<String,Object>();

		dataBody.put("query", "get_results");
		dataBody.put("malpedia-token", "");
		dataBody.put("task_id", "5aab36a2-f859-11ec-921d-42010aa4000b");


	}


}
