package app.util.map;

import java.util.ArrayList;
import java.util.List;

public class MapCal {
	
	
	public List cal(double latitude,double longtitude){
		
		List list=new ArrayList();
		 double r=6371393;
		//γ��32�ȵ���һ�־�����ڶ�����
	        double a=(2*Math.PI*r*Math.cos((8.0/45)*Math.PI))/(360);
	      //γ��110�ȵ���һ�־�����ڶ�����
	       double b=(2*Math.PI*r)/(360);
		//x�᷽�����
		double x=0;
		//y�᷽�����
		double y=0;
		x=(Math.abs(latitude-32)/360.0)*2*Math.PI*r;
		y=(Math.abs(longtitude-110)/360.0)*2*Math.PI*Math.cos(32.0/180*Math.PI)*r;
		double x1=x%300;
		double y1=y%300;
		//System.out.println(x1);
		//System.out.println(y1);
		//System.out.println(300/b);0.002697798402001681+32.998185408740625
		//System.out.println(300/a);0.003181185612265157+111.99778456450252
		
		double latitude1=0;
		double latitude2=0;
		if(latitude>32){
			latitude1=latitude-x1/b;
			latitude2=latitude1+0.002697798402001681;
		}else{
		latitude1=latitude+x1/b;
		latitude2=latitude1-0.002697798402001681;
		}
		double longtitude1=0;
		double longtitude2=0;
		if(longtitude>110)
		{ longtitude1=longtitude-y1/a;
		longtitude2=longtitude1+0.003181185612265157;
		}
		else{longtitude1=longtitude+y1/a;
		longtitude2=longtitude1-0.003181185612265157;  
		}
		list.add(latitude1);
		list.add(longtitude1);
		list.add(latitude2);
		list.add(longtitude2);
		return list;
	}
	
	public static void main(String[] args) {
		MapCal a=new MapCal();
		List d=a.cal(33, 112);
		System.out.println(d.get(0)+","+d.get(1));
		System.out.println(d.get(2)+","+d.get(3));
		List b=a.cal(33, 111.997688888);
		System.out.println(b.get(0)+","+b.get(1));
		System.out.println(b.get(2)+","+b.get(3));
}}
