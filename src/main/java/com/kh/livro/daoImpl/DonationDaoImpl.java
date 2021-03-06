package com.kh.livro.daoImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.livro.dao.DonationDao;
import com.kh.livro.dto.DonationDto;

@Repository
public class DonationDaoImpl implements DonationDao {

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	private Logger logger = LoggerFactory.getLogger(DonationDaoImpl.class);
	
	@Override
	public List<DonationDto> selectList() {
		List<DonationDto> list = new ArrayList<>();
		
		try {
			list = sqlSession.selectList(NAMESPACE + "selectList");
		} catch (Exception e) {
			logger.info("[ERROR] Donation selectList");
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public List<DonationDto> selectUser(String member_id) {
		List<DonationDto> list = new ArrayList<>();
		
		try {
			list = sqlSession.selectList(NAMESPACE + "selectUser", member_id);
		} catch (Exception e) {
			logger.info("[ERROR] Donation selectUser");
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<HashMap<String, Object>> selectAmountById(String member_id) {
		List<HashMap<String, Object>> list = new ArrayList<>();
		
		try {
			list = sqlSession.selectList(NAMESPACE + "selectAmountById", member_id);
		} catch (Exception e) {
			logger.info("[ERROR] Donation selectAmountById");
			e.printStackTrace();
		}

		return list;
	}
	
	@Override
	public List<HashMap<String, Object>> selectDaily(DonationDto dto) {
		List<HashMap<String, Object>> list = new ArrayList<>();
		
		try {
			list = sqlSession.selectList(NAMESPACE + "selectDaily", dto);
		} catch (Exception e) {
			logger.info("[ERROR] Donation selectDaily");
			e.printStackTrace();
		}
		
		return list;
	}
	
	@Override
	public int insert(DonationDto dto) {
		int res = 0;
		
		try {
			res = sqlSession.insert(NAMESPACE + "insert", dto);
		} catch (Exception e) {
			logger.info("[ERROR] Donation insert");
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int delete(int dona_no) {
		int res = 0;
		
		try {
			res = sqlSession.delete(NAMESPACE + "delete", dona_no);
		} catch (Exception e) {
			logger.info("[ERROR] Donation delete");
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public DonationDto selectAmountByUser(String dona_nickname) {
		DonationDto dto = new DonationDto();
		
		try {
			dto = sqlSession.selectOne(NAMESPACE + "selectAmountByUser", dona_nickname);
		} catch (Exception e) {
			logger.info("[ERROR] Donation selectAmountByUser");
			e.printStackTrace();
		}
		
		
		return dto;
	}
}
