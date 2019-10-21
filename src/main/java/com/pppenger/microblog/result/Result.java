package com.pppenger.microblog.result;

public class Result<T> {
	
	private int code;
	private String msg;
	private T data;
	
	/**
	 *  成功时候的调用
	 * */
	public static  <T> Result<T> success(T data){
		Result result=new Result<T>();
		result.setCode(0);
		result.setMsg("success");
		result.setData(data);
		return result;
	}

	public static  Result success(){
		return success(null);
	}
	
	/**
	 *  失败时候的调用
	 * */
	public static  Result error(CodeMsg codeMsg){
		Result result=new Result();
		result.setCode(codeMsg.getCode());
		result.setMsg(codeMsg.getMsg());
		return result;
	}

	private Result() {
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
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
