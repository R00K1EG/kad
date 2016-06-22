package tox;

import security.MD5;

/**
 * Created by guo on 2016/4/20.
 */
public class ToxGlobalFunc {

	/*
	 * kad, calculate the bucket of self;
	 * 
	 * @param id1: the target or selected node;
	 * 
	 * @param id2: the id of self;
	 */
	public static int getKofBucket(String id1, String id2) {
		byte[] tmp = MD5.hexStringToBytes(id1);
		byte[] self = MD5.hexStringToBytes(id2);
		int k = 0;
		int i = 0;
		for (i = 0; i < tmp.length; i++) {
			int dis = tmp[i] ^ self[i];
			if (dis == 0) {
				k = 0;
				continue;
			} else {
				if (dis >= 128) {
					k = 1;
					break;
				}
				if (dis >= 64) {
					k = 2;
					break;
				}
				if (dis >= 32) {
					k = 3;
					break;
				}
				if (dis >= 16) {
					k = 4;
					break;
				}
				if (dis >= 8) {
					k = 5;
					break;
				}
				if (dis >= 4) {
					k = 6;
					break;
				}
				if (dis >= 2) {
					k = 7;
					break;
				}
				if (dis >= 1) {
					k = 8;
					break;
				}
			}

		}
		k = k + i * 8;
		return 160 - k;
	}

	/**
	 * check they are in one net;
	 */
	public static boolean isInLAN(String ip1, String ip2, String nip, String sip) {
		if (ip1 == null || ip1.equals(""))
			return false;
		if (ip2 == null || ip2.equals(""))
			return false;
		if (sip == null || sip.equals(""))
			return false;
		if (nip == null || nip.equals(""))
			return false;
		String ip1t = ip1.substring(0, ip1.lastIndexOf("."));
		String ip2t = ip2.substring(0, ip2.lastIndexOf("."));
		if (ip1t.equals(ip2t)) {
			if (nip.equals(sip))
				return true;
			else
				return false;
		} else
			return false;
	}
}
