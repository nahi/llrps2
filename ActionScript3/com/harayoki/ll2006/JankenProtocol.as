package com.harayoki.ll2006{
	import com.harayoki.ll2006.JankenState;
	import com.harayoki.Util.Log;
	import flash.display.Sprite;
	public class JankenProtocol implements IProtocol{
		private var _agent:Agent;
		private var _session_id:String;
		private var _capatity:String = "1";
		private var _agent_name:String ="actionScript3";
		private var _round_id:String;
		private var _iteration:int;
		private var _rule_id:String;
		public var strategy:Strategy;
		public function JankenProtocol(name:String=""){
			if(name!="") _agent_name = name;
		}
		public function init(agent:Agent):void{
			_agent = agent;
		}
		public function connectHandler():void{
			_sendHello();
			if(!strategy) strategy = new Strategy();
		}
		public function closeHandler():void{
		}
		public function handleData(s:String):void{
			var datas:Array = s.split(" ");
			_checkData(datas[0],datas.slice(1));
		}
		private function _addLog(s:String):void{
			Log.getInstance().add("["+_agent_name+"] "+s);
		}
		private function _checkData(s:String,args:Array):void{
			switch(s){
				case "HELLO":
					break;
				case "INITIATE":
					_session_id = args[0];
					_addLog("session_id : "+_session_id);
					_addLog("◆◆◆◆ セッション開始 ◆◆◆◆");
					_sendInitiation();
					break;
				case "READY":
					//_session_id = args[0];
					_round_id = args[1];
					_iteration = Number(args[2]);
					_rule_id = args[3];
					_addLog("round_id : "+_round_id);
					_addLog("iteration : "+_iteration);
					_addLog("rule_id : "+_rule_id);
					_addLog("◆◆◆◆ ラウンド"+_round_id+"開始 ◆◆◆◆");
					_addLog("全部で約"+_iteration+"回  じゃんけんする");
					_sendReady();
					strategy.changeRound();
					break;
				case "CALL":
					//_session_id = args[0];
					//_round_id = args[1];
					_sendMove();
					break;
				case "RESULT":
					//_session_id = args[0];
					//_round_id = args[1];
					var move:int = parseInt(args[2]);
					_iteration--;
					strategy.match(move);
					//後_iteration回 の じゃんけん
					break;
				case "MATCH":
					//_session_id = args[0];
					//_round_id = args[1];
					_addLog("◆◆◆◆ ラウンド"+_round_id+"終了 ◆◆◆◆");
					break;
				case "CLOSE":
					_addLog("切断");
					_addLog("◆◆◆◆ セッション終了 ◆◆◆◆");
					break;
				default :
					break;
			}			
		}
		private function _throwDataError(s:String,s2:String):void{
			throw(new Error("UNEXPECTED SERVER RESPONCE \n expected "+s+"\nbut "+s2));
			//_agent.close();
		}
		private function _sendHello():void{
			_agent.sendMessage("HELLO");
		}
		private function _sendInitiation():void{
			_agent.sendMessage("INITIATE "+_session_id+" "+_agent_name+" "+_capatity);
		}
		private function _sendReady():void{
			_agent.sendMessage("READY "+_session_id+" "+_round_id);
		}
		private function _sendMove():void{
			var move:int = strategy.getMyMove();
			_agent.sendMessage("MOVE "+_session_id+" "+_round_id+" "+move);
		}
	}
}
