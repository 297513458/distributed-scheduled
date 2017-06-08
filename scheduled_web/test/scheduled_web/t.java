package scheduled_web;

import java.io.FileInputStream;
import java.io.InputStream;

import org.csource.common.FastDFSUtils;

public class t {
	public static void main(String[] args) throws Exception {
		InputStream is =new FileInputStream("C:\\Users\\Administrator\\Desktop\\诺诺服务首页优化.docx");
		System.err.println(FastDFSUtils.uploadFile(is, "c:\\sb.docx"));
	}
}