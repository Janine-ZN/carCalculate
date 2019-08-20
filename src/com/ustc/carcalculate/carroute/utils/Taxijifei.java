package com.ustc.carcalculate.carroute.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

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

public class Taxijifei {
	/*
	 * private static String url; private final static double Sp1=8; private
	 * final static double Sp2=8; private final static double Rp=1.4; private
	 * final static double Ss=2500; private final static double Bp1=0.5; private
	 * final static double Bp2=0.75; private final static double B1=15000;
	 * private final static double B2=25000; //private final static double Ft=0;
	 * //private final static double Hp=0; //private final static double Np=0;
	 * private final static double ps=0.2; private final static double op=0;
	 * private final static int SB_Hour=23; private final static int
	 * SB_Minute=0; private final static int EB_Hour=5; private final static int
	 * EB_Minute=0; //private final static int EhS_Hour=0; //private final
	 * static int EhS_Minute=0; //private final static int EhE_Hour=0; //private
	 * final static int EhE_Miunte=0; //private final static int NhS_Hour=0;
	 * //private final static int NhS_Minute=0; //private final static int
	 * NhE_Hour=0; //private final static int NhE_Miunte=0;
	 */
	private String url;
	private double Sp1;
	private double Sp2;
	private double Rp;
	private double Ss;
	private double Bp1;
	private double Bp2;
	private double B1;
	private double B2;
	private double Ft;
	private double Hp;
	private double Lp;
	private double Np;
	private double ps;
	private double op;
	private int SB_Hour;
	private int SB_Minute;
	private int EB_Hour;
	private int EB_Minute;
	private int EhS_Hour;
	private int EhS_Minute;
	private int EhE_Hour;
	private int EhE_Miunte;
	private int NhS_Hour;
	private int NhS_Minute;
	private int NhE_Hour;
	private int NhE_Miunte;

	private double delay_time;
	private double total_delay_time;
	private double all_distance;
	private double interval_distance;
	LineNumberReader reader;
	int i = 0;

	public Taxijifei(Context context, String url, double all_distance,
			double interval_distance) {
		try {
			// 获取文件
			InputStream in = context.getResources().getAssets().open(url);
			// 将文件的inputstream转换为reader
			InputStreamReader isr = new InputStreamReader(in);
			reader = new LineNumberReader(isr);
			String tempString;
			StringBuffer sb = new StringBuffer("");

			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString + ',');
			}
			String[] s = sb.toString().split(",");
			this.Sp1 = Double.parseDouble(s[0]);
			System.out.println(this.Sp1 + "");
			this.Sp2 = Double.parseDouble(s[1]);
			this.Rp = Double.parseDouble(s[2]);
			this.Ss = Double.parseDouble(s[3]);
			this.Bp1 = Double.parseDouble(s[4]);
			this.Bp2 = Double.parseDouble(s[5]);
			this.B1 = Double.parseDouble(s[6]);
			this.B2 = Double.parseDouble(s[7]);
			this.Ft = Double.parseDouble(s[8]);
			this.Hp = Double.parseDouble(s[9]);
			this.Lp = Double.parseDouble(s[10]);
			this.Np = Double.parseDouble(s[11]);
			this.ps = Double.parseDouble(s[12]);
			this.op = Double.parseDouble(s[13]);
			this.SB_Hour = Integer.parseInt(s[14].substring(0, 2));
			this.SB_Minute = Integer.parseInt(s[14].substring(3, 5));
			this.EB_Hour = Integer.parseInt(s[15].substring(0, 2));
			this.EB_Minute = Integer.parseInt(s[15].substring(3, 5));
			this.EhS_Hour = Integer.parseInt(s[16].substring(0, 2));
			this.EhS_Minute = Integer.parseInt(s[16].substring(3, 5));
			this.EhE_Hour = Integer.parseInt(s[17].substring(0, 2));
			this.EhE_Miunte = Integer.parseInt(s[17].substring(3, 5));
			this.NhS_Hour = Integer.parseInt(s[18].substring(0, 2));
			this.NhS_Minute = Integer.parseInt(s[18].substring(3, 5));
			this.NhE_Hour = Integer.parseInt(s[19].substring(0, 2));
			this.NhE_Miunte = Integer.parseInt(s[19].substring(3, 5));

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		this.all_distance = all_distance;
		this.interval_distance = interval_distance;
	}

	public Taxijifei(Context context, String url, double delay_time,
			int total_delay_time) {
		// TODO Auto-generated constructor stub
		try {
			// 获取文件
			InputStream in = context.getResources().getAssets().open(url);
			// 将文件的inputstream转换为reader
			InputStreamReader isr = new InputStreamReader(in);
			reader = new LineNumberReader(isr);
			String tempString;
			StringBuffer sb = new StringBuffer("");

			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString + ',');
			}
			String[] s = sb.toString().split(",");
			this.Sp1 = Double.parseDouble(s[0]);
			System.out.println(this.Sp1 + "");
			this.Sp2 = Double.parseDouble(s[1]);
			this.Rp = Double.parseDouble(s[2]);
			this.Ss = Double.parseDouble(s[3]);
			this.Bp1 = Double.parseDouble(s[4]);
			this.Bp2 = Double.parseDouble(s[5]);
			this.B1 = Double.parseDouble(s[6]);
			this.B2 = Double.parseDouble(s[7]);
			this.Ft = Double.parseDouble(s[8]);
			this.Hp = Double.parseDouble(s[9]);
			this.Lp = Double.parseDouble(s[10]);
			this.Np = Double.parseDouble(s[11]);
			this.ps = Double.parseDouble(s[12]);
			this.op = Double.parseDouble(s[13]);
			this.SB_Hour = Integer.parseInt(s[14].substring(0, 2));
			this.SB_Minute = Integer.parseInt(s[14].substring(3, 5));
			this.EB_Hour = Integer.parseInt(s[15].substring(0, 2));
			this.EB_Minute = Integer.parseInt(s[15].substring(3, 5));
			this.EhS_Hour = Integer.parseInt(s[16].substring(0, 2));
			this.EhS_Minute = Integer.parseInt(s[16].substring(3, 5));
			this.EhE_Hour = Integer.parseInt(s[17].substring(0, 2));
			this.EhE_Miunte = Integer.parseInt(s[17].substring(3, 5));
			this.NhS_Hour = Integer.parseInt(s[18].substring(0, 2));
			this.NhS_Minute = Integer.parseInt(s[18].substring(3, 5));
			this.NhE_Hour = Integer.parseInt(s[19].substring(0, 2));
			this.NhE_Miunte = Integer.parseInt(s[19].substring(3, 5));

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		this.delay_time = delay_time;
		this.total_delay_time = total_delay_time;
	}

	public Taxijifei(Context context, String url) {
		try {
			// 获取文件
			InputStream in = context.getResources().getAssets().open(url);
			// 将文件的inputstream转换为reader
			InputStreamReader isr = new InputStreamReader(in);
			reader = new LineNumberReader(isr);
			String tempString;
			StringBuffer sb = new StringBuffer("");

			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString + ',');
			}
			String[] s = sb.toString().split(",");
			this.Sp1 = Double.parseDouble(s[0]);
			System.out.println(this.Sp1 + "");
			this.Sp2 = Double.parseDouble(s[1]);
			this.Rp = Double.parseDouble(s[2]);
			this.Ss = Double.parseDouble(s[3]);
			this.Bp1 = Double.parseDouble(s[4]);
			this.Bp2 = Double.parseDouble(s[5]);
			this.B1 = Double.parseDouble(s[6]);
			this.B2 = Double.parseDouble(s[7]);
			this.Ft = Double.parseDouble(s[8]);
			this.Hp = Double.parseDouble(s[9]);
			this.Lp = Double.parseDouble(s[10]);
			this.Np = Double.parseDouble(s[11]);
			this.ps = Double.parseDouble(s[12]);
			this.op = Double.parseDouble(s[13]);
			this.SB_Hour = Integer.parseInt(s[14].substring(0, 2));
			this.SB_Minute = Integer.parseInt(s[14].substring(3, 5));
			this.EB_Hour = Integer.parseInt(s[15].substring(0, 2));
			this.EB_Minute = Integer.parseInt(s[15].substring(3, 5));
			this.EhS_Hour = Integer.parseInt(s[16].substring(0, 2));
			this.EhS_Minute = Integer.parseInt(s[16].substring(3, 5));
			this.EhE_Hour = Integer.parseInt(s[17].substring(0, 2));
			this.EhE_Miunte = Integer.parseInt(s[17].substring(3, 5));
			this.NhS_Hour = Integer.parseInt(s[18].substring(0, 2));
			this.NhS_Minute = Integer.parseInt(s[18].substring(3, 5));
			this.NhE_Hour = Integer.parseInt(s[19].substring(0, 2));
			this.NhE_Miunte = Integer.parseInt(s[19].substring(3, 5));

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public double Getqibujia() {
		if (isDay()) {
			return this.Sp1;
		}
		return this.Sp2;
	}

	public double Getfujiafei() {
		return op;
	}

	public double interval_jifei() {
		if (all_distance < Ss) {
			return 0;
		} else if (all_distance < B1 && all_distance > Ss) {
			return interval_distance / 1000 * Rp;
		} else if (all_distance > B1 && all_distance < B2) {
			return interval_distance / 1000 * Rp * (1 + Bp1);
		}
		return interval_distance / 1000 * Rp * (1 + Bp2);
	}

	public double Night_interval_jifei() {
		if (all_distance < Ss) {
			return 0;
		} else if (all_distance < B1 && all_distance > Ss) {
			return interval_distance / 1000 * Rp;
		} else if (all_distance > B1 && all_distance < B2) {
			return interval_distance / 1000 * Rp * (1 + Bp1);
		}
		return interval_distance / 1000 * Rp * (1 + Bp2);
	}

	public double jifei() {
		if (isDay()) {
			return interval_jifei();
		}
		return Night_interval_jifei() * (1 + ps);
	}

	// 判断是否为白天

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

	public double delay_jifei() {

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

}
