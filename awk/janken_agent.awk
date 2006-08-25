#! /usr/bin/gawk -f
#=====================================================================
# じゃんけんエージェントプログラム (awk 版)

#=====================================================================
# BEGIN BLOCK
BEGIN {
  #===================================================================
  # Settings
  agent_name = "gawk";
  janken_hand[0] = "無効";
  janken_hand[1] = "グー";
  janken_hand[2] = "チョキ";
  janken_hand[3] = "パー";
  close_count = 1;
  if ( ARGV[1] == "" ) {
    server_url = "localhost";
  } else {
    server_url = ARGV[1];
  }
  if ( ARGV[2] == "" ) {
    server_port = "12346";
  } else {
    server_port = ARGV[2];
  }
  #read_text( "meros.txt" );
  srand();
  read_text( "meros.od" );
  #===================================================================
  # Initialization for TCP/IP Connection
  ORS = RS = "\r\n";
  co_url = "/inet/tcp/0/" server_url "/" server_port;
  #===================================================================
  # Initiation of Janken Coordinator and Agent
  for (;;) {
    if ( close_count == 1 ) {
      system( "sleep 1" );    # for awk on Unix
      #sleep( 5 );            # for xgawk
      initiation();
    }
    #=================================================================
    # If receive READY, send READY
    be_ready();
    #=================================================================
    # Janken Battle Main Program
    if ( close_count == 0 ) {
      battle( iteration );
    }
  }
  #==================================================================
  # Closing Section (No Need!)
  #close( co_url );
  exit;
}
#=====================================================================

#=====================================================================
function initiation() {
  close_count = 0;
  print "Sending HELLO";
  print "HELLO" |& co_url;
  co_url |& getline;
  print "Recieving " $0;
  if ( $0 ~ /INITIATE/ ) {
    session_id = $2;
    capacity = 1;
    srand();
    print "Sending INITIATE " session_id " " agent_name " " capacity;
    print "INITIATE " session_id " " agent_name " " capacity |& co_url;
  }
}
#=====================================================================
function be_ready() {
  co_url |& getline;
  print "Recieving " $0;
  if ( $0 ~ /READY/ ) {
    round_id = $3;
    iteration = $4;
    rule_id = $5;
    print "***** 第" \
          round_id " ラウンド " \
          iteration " 本勝負 " \
          rule_id " ルール *****";
    print "Sending READY " session_id " " round_id;
    print "READY " session_id " " round_id |& co_url;
  } else if ( $0 ~ /CLOSE/ ) {
    print "CLOSE " session_id |& co_url;
    close_count = 1;
    print "***** 終了 *****";
    close( co_url );
    stat_result();
  }
}
#=====================================================================
# Choice My Hand
# 1: グー
# 2: チョキ
# 3: パー
#=====================================================================
# 乱数によるランダム発生
function gen_hand() {
#  while ( hand == int( rand() * 3 + 1 ) ) {
#    dummy = int( rand() * 3 + 1 );
#  }
#  return dummy;
  return int( rand() * 3 + 1 );
}
#=====================================================================
# MPFR による pi からの取得
#function gen_hand() {
#  pi_count++;
#  MPFR_PRECISION = 35000;
#  return ( substr( mpfr_const_pi(), pi_count, 1 ) % 3 ) + 1;
#}
#=====================================================================
# Wichmann-Hill の乱数
#function gen_hand() {
#  ix = 171 * ( ix % 177 ) -  2 * ( iz / 177 );
#  iy = 172 * ( iy % 176 ) - 35 * ( iz / 176 );
#  iz = 170 * ( iz % 178 ) - 63 * ( iz / 178 );
#  if ( ix < 0 ) ix += 30269;
#  if ( iy < 0 ) iy += 20307;
#  if ( iz < 0 ) iz += 30323;
#  r = ix / 30296 + iy / 30307 + iz / 30323;
#  while ( r >= 1 ) r = r - 1;
#  return int( r * 3 + 1 );
#}
#=====================================================================
# テキストの読み込み
function read_text( str ) {
  #read_cmd = "od " str;
  #while ( ( read_cmd | getline ) > 0 ) {
  while ( getline < str > 0 ) {
    if ( $0 ~ /^[0-9]/ ) {
      for ( i = 2; i <= NF; i++ ) {
        j++;
        meros_hand[j] = ( $i + 0 ) % 3 + 1;
        if ( meros_hand[j-1] == meros_hand[j] ) {
          delete meros_hand[j];
          j--;
        }
      }
    }
  }
  close( read_cmd );
}
#=====================================================================
# 読んだテキストから手を選ぶ
#function gen_hand( i ) {
#  return meros_hand[i];
#}
#=====================================================================
function battle( it ) {
  for ( i = 1;;) {
    i++;
    print "***** " i " 回目のじゃんけん *****";
    co_url |& getline;
    print "Recieving " $0;
    if ( $0 ~ /MATCH/ ) {
      break;
    }
    #hand = gen_hand( i );
    if ( meros_hand[i] ~ /[123]/ && win_count + 10 >= lost_count ) {
      hand = meros_hand[i];
    } else {
      hand = gen_hand();
    }
    print "Sending MOVE " session_id " " round_id " " hand;
    print "MOVE " session_id " " round_id " " hand |& co_url;
    co_url |& getline;
    print "Recieving " $0;
    if ( $0 ~ /RESULT/ ) {
      enemy_hand = $4;
      judge( hand, enemy_hand );
    }
  }
}
#=====================================================================
function judge( m, e ) {
  if ( e == 0 ) {
    print "***** 無効な手を検知しました *****";
  } else if ( m == e ) {
    print "***** 相手が " janken_hand[e] " で自分が " janken_hand[m] \
          " なので、あいこ *****";
    drow_count++;
  } else if ( ( m == 1 && e == 2 ) || \
              ( m == 2 && e == 3 ) || \
              ( m == 3 && e == 1 ) ) {
    print "***** 相手が " janken_hand[e] " で自分が " janken_hand[m] \
          " なので、勝ち *****";
    win_count++;
  } else if ( ( m == 1 && e == 3 ) || \
              ( m == 2 && e == 1 ) || \
              ( m == 3 && e == 2 ) ) {
    print "***** 相手が " janken_hand[e] " で自分が " janken_hand[m] \
          " なので、負け *****";
    lost_count++;
  }
  stat_result();
}
#=====================================================================
function stat_result() {
  print "=====> " win_count " 勝、" lost_count " 敗、" drow_count " 引き分け";
}
