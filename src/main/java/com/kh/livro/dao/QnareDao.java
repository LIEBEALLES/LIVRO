package com.kh.livro.dao;

import java.util.List;

import com.kh.livro.dto.QnareDto;

public interface QnareDao {
	
	public String NAMESPACE = "qnare.";
	
	//댓글목록
	public List<QnareDto> selectList(int qna_no);
	//댓글 등록
	public int insert(QnareDto dto);
	//댓글 삭제
	public int delete(int qnare_no);
	//댓글 수정
	public int update(QnareDto dto);
	
}
