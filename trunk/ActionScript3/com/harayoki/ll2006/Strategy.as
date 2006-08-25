package com.harayoki.ll2006{
	import com.harayoki.Util.Log;
	import com.harayoki.ll2006.*;
	public class Strategy{
		internal var _matchResults:Object;
		internal var _myHands:Array;
		internal var _matchCount:int = 0;
		internal var _gcpCount:int = 0;/**/
		internal var _lastMove:int;/*自分の最後の手*/
		internal var _roundNum:int = 0;
		internal var _oppositeMoves:Array;/*対戦相手の手の傾向*/
		internal static var _gcpNames:Array = ["謎","グー","チョキ","パー"];/*表示用名前設定*/
		internal static var NAZO:int = 0;/*無効な手のID*/
		internal static var GOO:int = 1;/*グーのID*/
		internal static var CHOKI:int = 2;/*チョキのID*/
		internal static var PAA:int = 3;/*パーのID*/
		internal static var AIKO:int = 0;/*作業用*/
		internal static var KATI:int = 1;/*作業用*/
		internal static var MAKE:int = 2;/*作業用*/
		public function Strategy(){
			_oppositeMoves = new Array();
			clearStat();
			_addLog("strategy instantiated.")
		}
		//ログ追加
		internal function _addLog(s:String):void{
			Log.getInstance().add(s);
		}
		/**
		 * 対戦情報を返す
		 */
		public function getStats():String{
			var o:Object = _matchResults;
			var s:String = "round"+_roundNum+" "+_matchCount+"対戦 "+o.kati+"勝 "+o.make+"敗 "+o.aiko+"分け 無効："+o.mukou;
			s += " 自分の手 グー"+_myHands[1]+" チョキ"+_myHands[2]+" パー"+_myHands[3]+" 無効"+_myHands[0];
			return s;
		}
		/**
		 * 対戦情報をクリア
		 */
		public function clearStat():void{
			_matchCount = 0;
			_matchResults = {kati:0,aiko:0,make:0,mukou:0};
			_myHands = [0,0,0,0];
		}
		/**
		 * ラウンドが変わった(相手が変わった？)時に呼び出す
		 */
		public function changeRound():void{
			_roundNum++;
			_oppositeMoves.push(new Array());/*ラウンドごとに手を記録*/			
		}
		//相手の手を記録
		internal function _addOppositeMove(move:int):void{
			_oppositeMoves[_oppositeMoves.length-1].push(move);
		}
		//相手の手を読み出し 存在しない時は-1を返す
		internal function _getOppositeMove(round:int,index:Number):int{
			var res:int = -1;
			try{
				res = _oppositeMoves[round][index];
			}catch(e:Error){
				res = -1;
			}
			return res;
		}
		//手のID番号を名前に変換
		internal function _getMoveNameById(num:int):String{
			var s:String = _gcpNames[num];
			return s;
		}
		/**
		 * 対戦回数を得る
		 */
		public function getMatchCount():int{
			return _matchCount;
		}
		/**
		 * 次の手を得る
		 */
		public function getMyMove():int{
			_gcpCount++;
			var res:int = _selectMove();
			_addLog("自分の手 "+res+"="+_getMoveNameById(res));
			_myHands[res]++;
			_lastMove = res;
			return res;
		}
		//手の選択 ここを継承
		internal function _selectMove():int{
			var res:int = 1 + (Math.floor(Math.random()*_gcpCount)) % 3;
			return res;
		}
		/**
		 * 対戦結果の記録
		 */
		public function match(move:int):void{
			if(_roundNum==0) changeRound();
			_matchCount++;
			_addOppositeMove(move);
			var o:Object = _matchResults;
			var s:String = _matchCount+"手目 自分の手:"+_getMoveNameById(_lastMove)+" 相手の手:"+_getMoveNameById(move);
			if(move==NAZO){
				_addLog(s+" 相手が無効な手を出した ");
				o.mukou++;
				return;
			}else{
				var stat:int = AIKO;
				switch(_lastMove){
					case GOO:
						if(move==CHOKI) stat = KATI;
						if(move==PAA)   stat = MAKE;
						break;
					case CHOKI:
						if(move==GOO)   stat = MAKE;
						if(move==PAA)   stat = KATI;
						break;
					case PAA:
						if(move==GOO)   stat = KATI;
						if(move==CHOKI) stat = MAKE;
						break;
					default:
						break;
				}
				switch(stat){
					case KATI:
						_addLog(s+" 勝ち!");
						o.kati++;
					break;
					case MAKE:
						_addLog(s+" 負け!");
						o.make++;
					break;
					case AIKO:
						_addLog(s+" あいこ!");
						o.aiko++;
					break;
				}
			}
		}
	}
}