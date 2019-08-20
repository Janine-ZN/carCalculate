package com.ustc.carcalculate.carroute.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 白天起步价Sp1
 夜间起步价Sp2
 白天公里租价Rp
 公里基程Ss
 空贴百分比1 Bp1
 空贴百分比2 Bp2
 空贴距离点1 B1
 空贴距离点2 B2
 免费等待时间 Ft
 高峰期等待费用 Hp
 低峰期等待费用 Lp
 夜间等待费用 Np
 夜间加收百分比 ps
 燃油费  op
 夜间起始时间(小时)SB_Hour
 夜间结束时间(分钟)SB_Minute
 夜间结束时间(小时)EB_Hour
 夜间结束时间(分钟)EB_Minute
 */

public class delay_time_jifei {

	// private final static int Sp1=0;
	// private final static int Sp2=0;
	// private final static int Rp=0;
	// private final static int Ss=0;
	// private final static int Bp1=0;
	// private final static int Bp2=0;
	// private final static int B1=0;
	// private final static int B2=0;
	private final static int Ft = 5;
	private final static double Hp = 0.6;
	private final static double Lp = 0.4;
	private final static double Np = 0.4;
	// private final static double ps=0.2;
	private final static int SB_Hour = 23;
	private final static int SB_Minute = 0;
	private final static int EB_Hour = 5;
	private final static int EB_Minute = 0;
	private final static int EhS_Hour = 7;
	private final static int EhS_Minute = 30;
	private final static int EhE_Hour = 9;
	private final static int EhE_Miunte = 30;
	private final static int NhS_Hour = 17;
	private final static int NhS_Minute = 0;
	private final static int NhE_Hour = 19;
	private final static int NhE_Miunte = 0;

	private double delay_time;
	private double total_delay_time;

	public delay_time_jifei(double delay_time, double total_delay_time) {
		this.delay_time = delay_time;
		this.total_delay_time = total_delay_time;
	}

	public double jifei() {

		if (total_delay_time < Ft * 60) {
			return 0;
		} else {
			if (isBusyTime()) {
				return (delay_time / 60 * Hp);
			}
			if (!isDay()) {
				return (delay_time / 60 * Np);
			} else
				return (delay_time / 60 * Lp);

		}
	}

	// 判断是否为早高峰
	public boolean isBusyTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(new Date());

		Calendar min1 = Calendar.getInstance();
		min1.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
		min1.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
		min1.set(Calendar.HOUR_OF_DAY, EhS_Hour);
		min1.set(Calendar.MINUTE, EhS_Minute);
		min1.set(Calendar.SECOND, 0);
		min1.set(Calendar.MILLISECOND, 0);

		Calendar max1 = Calendar.getInstance();
		max1.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
		max1.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
		max1.set(Calendar.HOUR_OF_DAY, EhE_Hour);
		max1.set(Calendar.MINUTE, EhE_Miunte);
		max1.set(Calendar.SECOND, 0);
		max1.set(Calendar.MILLISECOND, 0);

		Calendar min2 = Calendar.getInstance();
		min2.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
		min2.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
		min2.set(Calendar.HOUR_OF_DAY, NhS_Hour);
		min2.set(Calendar.MINUTE, NhS_Minute);
		min2.set(Calendar.SECOND, 0);
		min2.set(Calendar.MILLISECOND, 0);

		Calendar max2 = Calendar.getInstance();
		max2.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
		max2.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
		max2.set(Calendar.HOUR_OF_DAY, NhE_Hour);
		max2.set(Calendar.MINUTE, NhE_Miunte);
		max2.set(Calendar.SECOND, 0);
		max2.set(Calendar.MILLISECOND, 0);
		if (currentDate.getTimeInMillis() >= min1.getTimeInMillis()
				&& currentDate.getTimeInMillis() <= max1.getTimeInMillis()) {
			return true;
		}
		if (currentDate.getTimeInMillis() >= min2.getTimeInMillis()
				&& currentDate.getTimeInMillis() <= max2.getTimeInMillis()) {
			return true;
		}
		return false;
	}

	public boolean isDay() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(new Date());

		Calendar min = Calendar.getInstance();
		min.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
		min.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
		min.set(Calendar.HOUR_OF_DAY, EB_Hour);
		min.set(Calendar.MINUTE, EB_Minute);
		min.set(Calendar.SECOND, 0);
		min.set(Calendar.MILLISECOND, 0);

		Calendar max = Calendar.getInstance();
		max.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
		max.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
		max.set(Calendar.HOUR_OF_DAY, SB_Hour);
		max.set(Calendar.MINUTE, SB_Minute);
		max.set(Calendar.SECOND, 0);
		max.set(Calendar.MILLISECOND, 0);
		if (currentDate.getTimeInMillis() >= min.getTimeInMillis()
				&& currentDate.getTimeInMillis() <= max.getTimeInMillis()) {
			return true;
		}
		return false;
	}
}

// 等车要判断时速是否在12公里以内
