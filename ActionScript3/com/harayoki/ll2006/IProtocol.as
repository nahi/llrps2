package com.harayoki.ll2006{
	public interface IProtocol{
		function init(agent:Agent):void;
		function connectHandler():void;
		function closeHandler():void;
		function handleData(s:String):void;
	}
}
