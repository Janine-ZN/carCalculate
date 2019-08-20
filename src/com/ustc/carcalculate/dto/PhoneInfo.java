package com.ustc.carcalculate.dto;

/**
 * Phone实体集
 * 
 * @author Administrator
 * 
 */
public class PhoneInfo {
	public String name;
	public String number;
	public String sortLetter;

	public PhoneInfo(String name, String number) {
		setNumber(number);
		setName(name);
	}

	public PhoneInfo() {
		setNumber(number);
		setName(name);
	}

	public PhoneInfo(String name, String number, String sortLetter) {
		setNumber(number);
		setName(name);
		setSortLetter(sortLetter);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSortLetter() {
		return sortLetter;
	}

	public void setSortLetter(String sortLetter) {
		this.sortLetter = sortLetter;
	}
}
