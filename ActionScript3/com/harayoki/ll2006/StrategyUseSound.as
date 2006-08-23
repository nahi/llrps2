package com.harayoki.ll2006{
	import flash.media.Microphone;
	import flash.system.Security;
	import flash.events.ActivityEvent;
	import flash.events.Event;
	import flash.events.StatusEvent;
	public class StrategyUseSound extends Strategy{
		private var _mic:Microphone;
		public function StrategyUseSound(){
			_mic = Microphone.getMicrophone(0);
			Security.showSettings("2");
			_mic.setLoopBack(true);
			if(_mic ==null){
				_addLog("マイクを繋げてください マイクが使用できない場合、ランダムな手を返します");
			}else{
				_mic.setUseEchoSuppression(true);
				_mic.addEventListener(ActivityEvent.ACTIVITY,activityHandler);
				_mic.addEventListener(StatusEvent.STATUS,statusHandler);
			}
		}
		//手の選択 ここを継承
		override internal function _selectMove():int{
			var res:int;
			if(_mic==null){
				res = 1 + (Math.floor(Math.random()*_gcpCount)) % 3;
			}else{
				_addLog(_mic.gain+"");
				res = 1;
			}
			return res;
		}
		private function activityHandler(ev:Event):void{
			//_addLog("activityHandler "+ev);
		}
		private function statusHandler(ev:Event):void{
			//_addLog("statusHandler "+ev);
		}
	}
}
