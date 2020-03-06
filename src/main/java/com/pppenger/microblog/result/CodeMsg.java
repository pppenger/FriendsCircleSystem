package com.pppenger.microblog.result;

public class CodeMsg {

	private int code;
	private String msg;


	//登录注册模块
	public static CodeMsg REGISTER_PASSWORD_ERROR=new CodeMsg(500001,"两次密码不相同！");

	//通用的错误码
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");

	public static CodeMsg SERVER_ERROR2 = new CodeMsg(500100, "服务端异常：%s");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
	public static CodeMsg UPOLAD_SIZE_TOO_BIG = new CodeMsg(500102, "图片太大了哦！");

	//权限模块
	public static CodeMsg HAVE_NOT_AUTHORITY = new CodeMsg(500201, "权限异常：您不是微博拥有者，无操作权限");
	public static CodeMsg HAVE_NOT_SCORE = new CodeMsg(500202, "权限异常：您的评分过低，无权进行此操作");
	public static CodeMsg CHANGED_USERNAME = new CodeMsg(500202, "权限异常：您访问的内容不是您用户本身的");

	//收藏模块

	public static CodeMsg COLLECTION_REPEAT = new CodeMsg(500301, "收藏夹名字不允许重复哦！");
	public static CodeMsg HAD_COLLECTION= new CodeMsg(500302, "您已经收藏过该文章了！");
//	//登录模块 5002XX
//	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
//	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
//	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
//	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
//	public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号不存在");
//	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
//
//
//	//商品模块 5003XX
//
//
//	//订单模块 5004XX
//	public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");
//
//	//秒杀模块 5005XX
//	public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
//	public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "不能重复秒杀");
//	public static CodeMsg MIAOSHA_FAIL = new CodeMsg(500502, "秒杀失败");




	private CodeMsg( ) {
	}

	private CodeMsg( int code,String msg ) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + "]";
	}


}
