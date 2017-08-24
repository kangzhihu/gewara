package com.gewara.support;

/**
 * 用来调试及定位问题
 */
public class TraceErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TraceErrorException(String msg){
		super(msg);
	}
	public TraceErrorException(String msg, Throwable e){
		super(msg, e);
	}
}
