package com.htxx.core.common;

public class RSAUtils{
	static final String privateStr = "MIICXQIBAAKBgQDlOJu6TyygqxfWT7eLtGDwajtNFOb9I5XRb6khyfD1Yt3YiCgQWMNW649887VGJiGr/L5i2osbl8C9+WJTeucF+S76xFxdU6jE0NQ+Z+zEdhUTooNRaY5nZiu5PgDB0ED/ZKBUSLKL7eibMxZtMlUDHjm4gwQco1KRMDSmXSMkDwIDAQABAoGAfY9LpnuWK5Bs50UVep5c93SJdUi82u7yMx4iHFMc/Z2hfenfYEzu+57fI4fvxTQ//5DbzRR/XKb8ulNv6+CHyPF31xk7YOBfkGI8qjLoq06V+FyBfDSwL8KbLyeHm7KUZnLNQbk8yGLzB3iYKkRHlmUanQGaNMIJziWOkN+N9dECQQD0ONYRNZeuM8zd8XJTSdcIX4a3gy3GGCJxOzv16XHxD03GW6UNLmfPwenKu+cdrQeaqEixrCejXdAFz/7+BSMpAkEA8EaSOeP5Xr3ZrbiKzi6TGMwHMvC7HdJxaBJbVRfApFrE0/mPwmP5rN7QwjrMY+0+AbXcm8mRQyQ1+IGEembsdwJBAN6az8Rv7QnD/YBvi52POIlRSSIMV7SwWvSK4WSMnGb1ZBbhgdg57DXaspcwHsFV7hByQ5BvMtIduHcT14ECfcECQATeaTgjFnqE/lQ22Rk0eGaYO80cc643BXVGafNfd9fcvwBMnk0iGX0XRsOozVt5AzilpsLBYuApa66NcVHJpCECQQDTjI2AQhFc1yRnCU/YgDnSpJVm1nASoRUnU8Jfm3Ozuku7JUXcVpt08DFSceCEX9unCuMcT72rAQlLpdZir876";

public static void main(String[] args) {
		try {
//			KeyFactory kf = KeyFactory.getInstance("RSA");
			String password ="0+iAjBXPEE6fHrmwjB3cowmyNUhBYvE3aGAJtvKfnPr1dIPykuQPUYaxbK2RsJ7fxt8cVQ4Lb07Tr7SKHeWOodvpsaxBZCyhxWh05+c0wAZAQPrzBq+Ap5aehfjO33m+t6h0D9nPnNhGE9XGv4oLtNPD5T+DKABp/XpnBvuRpnQ=";
//			RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(Base64Util.decode(privateStr)));  
//			RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());  
//			PrivateKey rsaPriKey = kf.generatePrivate(rsaPrivKeySpec);
//			Cipher cipher = Cipher.getInstance("RSA");
//			cipher.init(Cipher.DECRYPT_MODE, rsaPriKey);
//			byte[] deTestByte = cipher.doFinal(Base64Util.decode(password));
			System.out.println(RSAUtil.decrypt(password, privateStr));
			System.out.println(RSAUtil.encrypt("123456", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmKqA0cCBkMYAcHaNkKHWef2iCgC2poC+MjtOi3G9eFdm0bqYFHDOtj5rvsWYG2+v0AhYCGteIxBm0DJCtP1v4DSGEe41s6CHEh4TQVN1Gp0usw9VtaSccees1WhS29nLwvBMcyeourAcAoKzdP3b3002OYGFEFoBcRlMMYFhmpwIDAQAB"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
