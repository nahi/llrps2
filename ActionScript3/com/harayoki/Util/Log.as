package com.harayoki.Util{
	import flash.events.EventDispatcher;
	import flash.events.Event;
	public class Log extends EventDispatcher{
		private static var _instance:Log = null;
		public static function getInstance():Log{
			if(_instance == null){
				_instance = new Log();
			}
			return _instance;
		}
		public static var UPDATE:String = "update"; 
		private var _data:Array;
		private var _viewList:Array;
		public function Log(){//as3はコンストラクタをprivateにできない？
			_data = new Array();
			LogLine.showDate = false;
			LogLine.showId = true;
		}
		public function add(s:String):void{
			_data.push(new LogLine(s));
			var ev:Event = new Event(UPDATE);
			dispatchEvent(ev);
		}
		public function getAll(sep:String="\n"):String{
			return _data.join(sep)+sep;
		}
		public function getTail(num:Number=1,sep:String="\n"):String{
			var l:int = _data.length;
			var a:Array = _data.slice(l-num);
			return a.join(sep)+sep;
		}
	}
}
class LogLine{
	private static var _lastid:int = 0;
	public static var showId:Boolean = true;
	public static var showDate:Boolean = true;
	public var date:Date;
	public var data:String;
	public var id:int;
	public function LogLine(s:String){
		id = ++_lastid;
		date = new Date();
		data = s;
	}
	public function toString():String{
		var s:String = "";
		if(showId) s += "["+id+"]";
		if(showDate) s+=date;
		if(s!="") s += " : ";
		s += data;
		return s;
	}
}
