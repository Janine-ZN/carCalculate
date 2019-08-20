package com.ustc.carcalculate.utils;

import java.util.Comparator;

import com.ustc.carcalculate.dto.PhoneInfo;
import com.ustc.carcalculate.dto.SortModel;

/**
 * 实现自定义排序,继承Comparator接口
 * 
 * @author zxl
 * 
 */
public class PinYinComparator implements Comparator<PhoneInfo> {

	/**
	 * 排序比较
	 */
	@Override
	public int compare(PhoneInfo lhs, PhoneInfo rhs) {
		return lhs.getSortLetter().compareTo(rhs.getSortLetter());
	}

}
