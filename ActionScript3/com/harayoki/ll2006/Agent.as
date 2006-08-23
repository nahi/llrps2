package com.harayoki.ll2006{
	import flash.net.Socket;
	import flash.errors.IOError;
	import flash.events.Event;
	import flash.events.SecurityErrorEvent;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;	
	import com.harayoki.Util.Log;
	import flash.system.Security;
	import flash.utils.setTimeout;
	public class Agent{
		private var _sock:Socket;
		private var _host:String;
		private var _port:int;
		private var _log:Log;
		private var _protocol:IProtocol;
		private var _state:Number = 1;
		private var _destructed:Boolean = false;
		public var user:String = "harayoki";
		public var pass:String = "harahara01";
		public var charSet:String = "us-ascii";
		public var separater:String = "\r\n";
		public var reqireLoadPolicyFile:Boolean = true;
		public var reconnectTime:Number = -1;/*再接続までの秒数*/
		private var _bNoReconnect:Boolean = false;
		public function Agent(protocol:IProtocol){
			_sock = new Socket();
			_sock.addEventListener(Event.CONNECT,connectHandler);
			_sock.addEventListener(Event.CLOSE,closeHandler);
			_sock.addEventListener(ProgressEvent.SOCKET_DATA,dataHandler);
			_sock.addEventListener(IOErrorEvent.IO_ERROR,ioErrorHandler);
			_sock.addEventListener(SecurityErrorEvent.SECURITY_ERROR,securityErrorHandler);
			_protocol = protocol;
			_log = Log.getInstance();
			_log.add("agent instantiated");
		}
		public function destruct():void{
			_destructed = true;
			/*_sock.removeEventListener(Event.CONNECT,connectHandler);
			_sock.removeEventListener(Event.CLOSE,closeHandler);
			_sock.removeEventListener(ProgressEvent.SOCKET_DATA,dataHandler);
			_sock.removeEventListener(IOErrorEvent.IO_ERROR,ioErrorHandler);
			_sock.removeEventListener(SecurityErrorEvent.SECURITY_ERROR,securityErrorHandler);*/
			if(_sock.connected) _sock.close();
			_log.add("agent destructed");
		}
		public function get protocol():IProtocol{
			return _protocol;
		}
		public function connect(host:String="",port:int=-1):void{
			if(host!="") _host = host;
			if(port!=-1) _port = port;
			_log.add("connecting to "+_host+"("+_port+")");
			_bNoReconnect = false;
			if(reqireLoadPolicyFile) Security.loadPolicyFile("http://"+host+"/crossdomain.xml");//!
			_sock.connect(_host,_port);	
			_protocol.init(this);
		}
		public function close():void{
			_log.add("closing... ");
			_bNoReconnect = true;
			_sock.close();
		}
		public function get connected():Boolean{
			return _sock.connected;
		}
		private function _reconenct():void{
			if(_destructed) return;
			if(_bNoReconnect){
				//ユーザによる切断、再接続はしない
			}else if(reconnectTime<0 || isNaN(reconnectTime)){
				_log.add("再接続設定なし");
			}else{
				_log.add("再接続まで"+reconnectTime+"m秒");
				var connectAgain:Function = function():void{
					if(_destructed) return;
					_log.add("再接続開始…");	
					connect();
				}
				var iid:Number = flash.utils.setTimeout(connectAgain,reconnectTime);
			}
		}
		////////// message //////////
		internal function sendMessage(s:String,logMessage:String=""):void{
			if(logMessage=="") logMessage = s;
			_log.add("sendMessage \""+logMessage+"\"");
			s+=separater;
			try{
				_sock.writeMultiByte(s,charSet);
				_sock.flush();
			}catch(e:IOError){
				_log.add("IOError "+e);			
			}
		}
		////////// handlers //////////
		private function dataHandler(ev:Event):void{
			var lines:String = _sock.readMultiByte(_sock.bytesAvailable,charSet);
			var a:Array = lines.split(separater);
			var s:String;
			for(var i:int=0;i<a.length;i++){
				s = String(a[i]);
				if(s=="") continue;
				_log.add("get socket data \""+s+"\"");
				try{
					_protocol.handleData(s);
				}catch(e:Error){
					_log.add(e.toString());
				}
			}
		}
		//接続した
		private function connectHandler(ev:Event):void{
			if(_destructed) return;
			_log.add("connected ");
			_protocol.connectHandler();
		}
		//接続が切れた
		private function closeHandler(ev:Event):void{
			if(_destructed) return;
			_log.add("closed ");
			_protocol.closeHandler();
			_reconenct();
		}
		//IOエラーが起きた
		private function ioErrorHandler(ev:IOErrorEvent):void{
			if(_destructed) return;
			_log.add("IOError");
			_log.add(ev.text);
			_reconenct();
		}
		//セキュリティエラーが起きた
		private function securityErrorHandler(ev:SecurityErrorEvent):void{
			if(_destructed) return;
			_log.add("SecuriryError");
			_log.add(ev.text);
			//_reconenct();
		}
	}
}
