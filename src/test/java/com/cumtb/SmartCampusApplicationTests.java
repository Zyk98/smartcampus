package com.cumtb;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.service.impl.QuestionServiceImpl;
import com.cumtb.mp.service.impl.QuestionTagServiceImpl;
import com.cumtb.mp.service.impl.TagServiceImpl;
import com.cumtb.mp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.net.Inet4Address;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
public class SmartCampusApplicationTests {

	@Autowired
	DataSource dataSource;

	@Autowired
	UserServiceImpl userService;

	@Autowired
	QuestionServiceImpl questionService;

	@Autowired
	QuestionTagServiceImpl questionTagService;

	@Autowired
	TagServiceImpl tagService;

	@Test
	void contextLoads() throws SQLException {
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.select("question_id");
		queryWrapper.eq("tag_id",4);

		ArrayList<Integer> list = (ArrayList<Integer>) questionTagService.list(queryWrapper);
		System.out.println(list);

		ReentrantLock lock = new ReentrantLock();
		lock.lock();

		HashMap<Integer,Integer> hashtale = new HashMap<>();
		hashtale.put(1,2);

	}

}
