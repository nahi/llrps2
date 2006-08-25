package com.harayoki.ll2006{
	import flash.media.Microphone;
	import flash.system.Security;
	import flash.events.ActivityEvent;
	import flash.events.Event;
	import flash.events.StatusEvent;
	import flash.utils.setInterval;
	public class StrategyUseSound extends Strategy{
		private var _mic:Microphone;
		private var _hands:Array;
		private var _maxActivity:Number = 50;
		public function StrategyUseSound(changeHandsActivity:Number = 50){
			_maxActivity = changeHandsActivity;
			_hands = [GOO,CHOKI,PAA];
			_mic = Microphone.getMicrophone(0);
			Security.showSettings("2");
			_mic.setLoopBack(true);
			if(_mic ==null || _mic.muted){
				_addLog("マイクを繋げてください マイクが使用できない場合、ランダムな手を返します");
			}else{
				_mic.setUseEchoSuppression(true);
				_mic.addEventListener(ActivityEvent.ACTIVITY,activityHandler);
				_mic.addEventListener(StatusEvent.STATUS,statusHandler);
			}
		}
		public function getMicLevel():Number{
			return _mic.activityLevel;
		}
		//手の選択 ここを継承
		override internal function _selectMove():int{
			var res:int;
			if(_mic==null || _mic.muted){
				res = 1 + (Math.floor(Math.random()*_gcpCount)) % 3;
			}else{
				//音の大きさによって手を変える
				var v:Number = _mic.activityLevel;
				if(isNaN(v)) v = 0;
				if(v<0) v = 0;
				if(v>=_maxActivity) v = _maxActivity -1;
				var num:Number = Math.floor(3*v/_maxActivity);
				res = _hands[num];
				//音が大きいとじゃんけんの手の配列が変わる
				if(_mic.activityLevel>_maxActivity) _shuffleHand();
			}
			return res;
		}
		private function activityHandler(ev:Event):void{
			_addLog("activityHandler "+ev);
		}
		private function statusHandler(ev:Event):void{
			_addLog("statusHandler "+ev);
		}
		//手の配列を変更
		private function _shuffleHand():void{
			_addLog("shuffle Hand!");
			var i:int = _hands.shift();
			_hands.push(i);
		}
	}
}
