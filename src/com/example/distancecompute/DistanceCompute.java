package com.example.distancecompute;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class DistanceCompute {
	public double getDistance(double lat1, double lng1, double lat2,
			double lng2) {
		LatLng p1LL = new LatLng(lat1, lng1);
		LatLng p2LL = new LatLng(lat2, lng2);
		double distance = DistanceUtil.getDistance(p1LL, p2LL);
		return distance / 1000;
	}
}
