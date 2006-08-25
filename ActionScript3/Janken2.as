package {
	import flash.display.Sprite;
	import com.harayoki.ll2006.*;
	import com.harayoki.Util.Log;
	import flash.events.Event;
	import flash.text.TextField;
	import flash.display.Stage;
	import flash.display.StageAlign;
	import flash.display.StageQuality;
	import flash.display.StageScaleMode;
	import flash.text.TextFieldType;
	import flash.display.SimpleButton;
	import flash.filters.*;
	import flash.text.TextFormat;
	import flash.text.TextFormatAlign;
	import flash.events.MouseEvent;
	import flash.system.Security;
	public class Janken2 extends Sprite{
		private var agents:Array;
		private var host:String = "192.168.1.2";
		private var port:int = 12346;
		private var conNum:int = 1;/*同時接続数*/
		public var reconnectTime:Number = 100;/*再接続までの秒数*/
		private var _strategy:Strategy;
		public function Janken2(){
			initStage();
			initLog();
			_strategy = new StrategyUseSound(40);
			//_strategy = new Strategy();
		}
		private var _hostTf:TextField;
		private var _conNumTf:TextField;
		private var _portTf:TextField;
		private var _logTf:TextField;
		private var _reconnectTimeTf:TextField;
		private var _statTf:TextField;
		private var _connectBt:SimpleButton;
		private var _closeBt:SimpleButton;
		private var _clearBt:SimpleButton;
		private var _clearStatBt:SimpleButton;
		private var _shadowFilter:DropShadowFilter = new DropShadowFilter(4,45,0x000011,0.3,6,6);
		private var _bevelFilter:BevelFilter = new BevelFilter(5,90,0x0000ff,0.2,0x000000,0.0,0,10);
		private var _micLevelView:Sprite;
		private var _currentMicViewLevel:Number = 0;
		private var _micLevelViewColor:uint = 0x0044cc;
		/**
		 * ステージの初期化
		 * UI作成(コンポーネントはあえて使っていません)
		 */
		private function initStage():void{
			//ステージ基本設定
			trace("Security.sandboxType "+Security.sandboxType);
			stage.align = StageAlign.TOP_LEFT;
			stage.quality = StageQuality.HIGH;
			stage.scaleMode = StageScaleMode.NO_SCALE;
			stage.frameRate = 200;
			//タイトル
			var titleTf:TextField = createTextField(10,10,300,20,0,false);
			titleTf.htmlText = '<font size="14">Janken2 client</font>';
			titleTf.filters = [new GlowFilter(0x00ffff,0.8,6,6),_shadowFilter];
			addChild(titleTf);
			//host入力テキストフィールド
			_hostTf = createTextField(10,30,145,20,0x0000ff,true,0xffffff,0x0000ff);
			_hostTf.type = TextFieldType.INPUT;
			_hostTf.text = host;
			_hostTf.filters = [_bevelFilter,_shadowFilter];
			addChild(_hostTf);
			//ポート番号入力テキストフィールド
			_portTf = createTextField(160,30,50,20,0x0000ff,true,0xffffff,0x0000ff);
			_portTf.type = TextFieldType.INPUT;
			_portTf.text = String(port);
			_portTf.filters = [_bevelFilter,_shadowFilter];
			addChild(_portTf);
			//接続数入力テキストフィールド
			_conNumTf = createTextField(215,30,30,20,0x0000ff,true,0xffffff,0x0000ff);
			_conNumTf.type = TextFieldType.INPUT;
			_conNumTf.text = String(conNum);
			_conNumTf.filters = [_bevelFilter,_shadowFilter];
			addChild(_conNumTf);
			//再接続m秒数テキストフィールド
			_reconnectTimeTf = createTextField(250,30,30,20,0x0000ff,true,0xffffff,0x0000ff);
			_reconnectTimeTf.type = TextFieldType.INPUT;
			_reconnectTimeTf.text = String(reconnectTime);
			_reconnectTimeTf.filters = [_bevelFilter,_shadowFilter];
			addChild(_reconnectTimeTf);
			//結果表示テキストフィールド
			_statTf = createTextField(10,60,480,20,0x000000,true,0xffffff,0x0000ff);
			_statTf.type = TextFieldType.DYNAMIC;
			_statTf.text = "";
			var tf:TextFormat = _statTf.getTextFormat();
			tf.size = 3;
			_statTf.setTextFormat(tf);
			_statTf.filters = [_bevelFilter,_shadowFilter];
			addChild(_statTf);
			
			//connectボタン
			_connectBt = createButton("conenct",290,30,50,20,0x0000ff,0xddddee,0x0000ff,0xeeeeff,0x0000ff);
			_connectBt.filters = [_shadowFilter];
			addChild(_connectBt);
			_connectBt.addEventListener(MouseEvent.CLICK,connectButtonClickHandler);
			//closeボタン
			_closeBt = createButton("close",340,30,50,20,0x0000ff,0xddddee,0x0000ff,0xeeeeff,0x0000ff);
			_closeBt.filters = [_shadowFilter];
			addChild(_closeBt);
			_closeBt.addEventListener(MouseEvent.CLICK,closeButtonClickHandler);
			//clearボタン
			_clearBt = createButton("clear",390,30,50,20,0x0000ff,0xddddee,0x0000ff,0xeeeeff,0x0000ff);
			_clearBt.filters = [_shadowFilter];
			addChild(_clearBt);
			_clearBt.addEventListener(MouseEvent.CLICK,clearButtonClickHandler);
			//clearstatボタン
			_clearStatBt = createButton("clearStat",440,30,50,20,0x0000ff,0xddddee,0x0000ff,0xeeeeff,0x0000ff);
			_clearStatBt.filters = [_shadowFilter];
			addChild(_clearStatBt);
			_clearStatBt.addEventListener(MouseEvent.CLICK,clearStatButtonClickHandler);
			
			//mic音量バー
			_micLevelView = new Sprite();
			_micLevelView.x = 100;
			_micLevelView.y = 20;
			_micLevelView.filters = [new GlowFilter(0x00ffff,0.8,6,6),_shadowFilter];
			var updateMicView:Function = function(ev:Event):void{
				showMicLevel();
			}
			_micLevelView.addEventListener(Event.ENTER_FRAME,updateMicView);
			addChild(_micLevelView);
		}
		private function connectButtonClickHandler(ev:Event):void{
			run();
		}
		private function closeButtonClickHandler(ev:Event):void{
			end();
		}
		private function clearButtonClickHandler(ev:Event):void{
			_logTf.text = "";
		}
		private function clearStatButtonClickHandler(ev:Event):void{
			_statTf.text = "";
			_strategy.clearStat();
		}
		/**
		 * サーバに接続
		 */
		public function run():void{
			end();
			host = _hostTf.text;
			port = Number(_portTf.text);
			conNum = Number(_conNumTf.text);
			agents = new Array();
			for(var i:int=0;i<conNum;i++){
				var protocol:JankenProtocol = new JankenProtocol("as3Agent_"+i);
				protocol.strategy = _strategy;
				var agent:Agent = new Agent(protocol);
				agent.reconnectTime = Number(_reconnectTimeTf.text);
				agent.reqireLoadPolicyFile = true;
				//agent.charSet = "iso-2022-jp";////JISコード
				agents.push(agent);
				try{
					agent.connect(host,port);
				}catch(e:Error){
					Log.getInstance().add(e.toString());
				}
			}
			addEventListener(Event.ENTER_FRAME,showStat);
		}
		private function showStat(ev:Event):void{
			var stat:String = _strategy.getStats();
			_statTf.text = stat;
		}
		/**
		 * サーバから切断
		 */
		public function end():void{
			for(var i:String in agents){
				var agent:Agent = agents[i];
				agent.destruct();
			}
			agents = null;
			removeEventListener(Event.ENTER_FRAME,showStat);
		}
		/**
		 * ログ表示の初期化
		 */
		private function initLog():void{
			Log.getInstance().addEventListener(Log.UPDATE,logupdateHandler);
			_logTf = createTextField(10,85,480,320,0x010101,true,0xfafafa,0x0000ff);
			_logTf.filters = [_bevelFilter,_shadowFilter];
			addChild(_logTf);
			var s:String = Log.getInstance().getAll();
			_logTf.appendText(s);
			_logTf.scrollV = _logTf.maxScrollV;
		}
		/**
		 * ログ更新通知
		 */
		private function logupdateHandler(e:Event):void{
			var s1:String = Log.getInstance().getTail(20);
			_logTf.text = s1;
			_logTf.scrollV = _logTf.maxScrollV;
			var s2:String = Log.getInstance().getTail(1);
			trace(s2);
		}
		/**
		 * マイクの音量を表示
		 */
		private function showMicLevel():void{
			_micLevelView.graphics.clear();
			if(_strategy is StrategyUseSound){
				var level:Number = StrategyUseSound(_strategy).getMicLevel();
				if(level<0) level = 0;
				_currentMicViewLevel += (level - _currentMicViewLevel)*0.4;
				_micLevelView.graphics.beginFill(_micLevelViewColor,1.0);
				_micLevelView.graphics.drawRect(0,0,_currentMicViewLevel*3,4);
			}
		}
		/**
		 * テキストフィールドを作成
		 */
		private function createTextField(x:Number,y:Number,w:Number,h:Number,col:uint,useBack:Boolean,backCol:uint=0xffffff,borderCol:uint=0x000000):TextField{
			var tf:TextField = new TextField();
			tf.x = x;
			tf.y = y;
			tf.width = w;
			tf.height = h;
			if(useBack){
				tf.background = true;
				tf.backgroundColor = backCol;	
				tf.border = true;
				tf.borderColor = borderCol;
			}
			tf.textColor = col;
			tf.selectable = true;
			tf.type = TextFieldType.DYNAMIC;
			return tf;
		}
		/**
		 * ボタンを作成
		 */
		private function createButton(s:String,x:Number,y:Number,w:Number,h:Number,col:uint=0x000000,backCol:uint=0xffffff,borderCol:uint=0x000000,backCol2:uint=0xf0f0f0,borderCol2:uint=0x000000):SimpleButton{
			var bebelFilter:BevelFilter = new BevelFilter(2,45,0xffffff,0.5,0x333333,0.5);
			var tf1:TextField = createTextField(0,0,w,h,col,true,backCol,borderCol);
			tf1.text = s;
			var tf2:TextField = createTextField(0,0,w,h,col,true,backCol2,borderCol2);
			tf2.text = s;
			tf2.getTextFormat().align = TextFormatAlign.CENTER;
			var tft:TextFormat = tf1.getTextFormat()
			tft.align = TextFormatAlign.CENTER;
			tf1.setTextFormat(tft);
			tf2.setTextFormat(tft);
			tf1.filters = [bebelFilter];
			tf2.filters = [bebelFilter];
			var sp1:Sprite = new Sprite();
			sp1.addChild(tf1);
			var sp2:Sprite = new Sprite();
			sp2.addChild(tf2);
			var bt:SimpleButton = new SimpleButton(sp1,sp2,sp1,sp1);
			bt.x = x;
			bt.y = y;
			return bt;
		}
	}
}
