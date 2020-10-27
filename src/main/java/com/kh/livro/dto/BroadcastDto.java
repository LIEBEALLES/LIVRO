package com.kh.livro.dto;

import java.sql.Date;

public class BroadcastDto {

	private int broadcast_no;
	private String member_id;
	private String broadcast_title;
	private String broadcast_content;
	private Date broadcast_regdate;
	private String broadcast_category;
	private String member_profile;
	
	public BroadcastDto() {
	}

	public BroadcastDto(int broadcast_no, String member_id, String broadcast_title, String broadcast_content,
			Date broadcast_regdate, String broadcast_category, String member_profile) {
		this.broadcast_no = broadcast_no;
		this.member_id = member_id;
		this.broadcast_title = broadcast_title;
		this.broadcast_content = broadcast_content;
		this.broadcast_regdate = broadcast_regdate;
		this.broadcast_category = broadcast_category;
		this.member_profile = member_profile;
	}
	
	public BroadcastDto(String member_id, String broadcast_title, String broadcast_content, String broadcast_category) {
		this.member_id = member_id;
		this.broadcast_title = broadcast_title;
		this.broadcast_content = broadcast_content;
		this.broadcast_category = broadcast_category;
	}

	public int getBroadcast_no() {
		return broadcast_no;
	}

	public void setBroadcast_no(int broadcast_no) {
		this.broadcast_no = broadcast_no;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getBroadcast_title() {
		return broadcast_title;
	}

	public void setBroadcast_title(String broadcast_title) {
		this.broadcast_title = broadcast_title;
	}

	public String getBroadcast_content() {
		return broadcast_content;
	}

	public void setBroadcast_content(String broadcast_content) {
		this.broadcast_content = broadcast_content;
	}

	public Date getBroadcast_regdate() {
		return broadcast_regdate;
	}

	public void setBroadcast_regdate(Date broadcast_regdate) {
		this.broadcast_regdate = broadcast_regdate;
	}

	public String getBroadcast_category() {
		return broadcast_category;
	}

	public void setBroadcast_category(String broadcast_category) {
		this.broadcast_category = broadcast_category;
	}

	public String getMember_profile() {
		return member_profile;
	}

	public void setMember_profile(String member_profile) {
		this.member_profile = member_profile;
	}
	
	
}
