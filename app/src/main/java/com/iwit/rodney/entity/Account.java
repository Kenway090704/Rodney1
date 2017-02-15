package com.iwit.rodney.entity;

public class Account {
	private Integer uid;
	private String name;
	private String email;
	private String phonenum;// 不用
	private String restypes;
	private String password;
	private Integer iscurrent;
	private String nickname;// 不用
	private Integer age;
	private Integer sex;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getRestypes() {
		return restypes;
	}

	public void setRestypes(String restypes) {
		this.restypes = restypes;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getIscurrent() {
		return iscurrent;
	}

	public void setIscurrent(Integer iscurrent) {
		this.iscurrent = iscurrent;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

}
