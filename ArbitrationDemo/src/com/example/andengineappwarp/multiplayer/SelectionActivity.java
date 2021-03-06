package com.example.andengineappwarp.multiplayer;

import java.util.Enumeration;
import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.andengineappwarp.multiplayer.handler.ResponseHandler;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;


public class SelectionActivity extends Activity {

	private int selectedMonster = -1;
	private WarpClient theClient;
	private ProgressDialog progressDialog;
	private String roomId = "";
	private ImageView redMonster;
	private ImageView greenMonster;
	private ImageView blueMonster;
	private ImageView yellowMonster;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selection_activity);
		selectedMonster = -1;
		redMonster = (ImageView)findViewById(R.id.imageView1);
		greenMonster = (ImageView)findViewById(R.id.imageView2);
		blueMonster = (ImageView)findViewById(R.id.imageView3);
		yellowMonster = (ImageView)findViewById(R.id.imageView4);
		roomId = getIntent().getStringExtra("roomId");
		init();
		loadRoomData();
	}
	
	private void init(){
        try {
            theClient = WarpClient.getInstance();
        } catch (Exception ex) {
        	Utils.showToastAlert(this, "Exception in Initilization");
        }
    }
	
	private void loadRoomData(){
		ResponseHandler.getInstance().setResultActivity(this);
		progressDialog =  ProgressDialog.show(this, "", "loading room data...");
		theClient.subscribeRoom(roomId);
		theClient.getLiveRoomInfo(roomId);
	}
	
	public void onMonsterClicked(View view){
		switch (view.getId()) {
			case R.id.imageView1:
				selectedMonster = 1;
			break;
			case R.id.imageView2:
				selectedMonster = 2;
			break;
			case R.id.imageView3:
				selectedMonster = 3;
			break;
			case R.id.imageView4:
				selectedMonster = 4;
			break;
		}
		if(selectedMonster!=-1){
			joinGame(selectedMonster);
		}
	}
	public void onStart(){
		super.onStart();
		
	}
	public void onStop(){
		super.onStop();
		Log.d("onStop", "Selection Activity");
	}
	private void joinGame(int selectedIndex){
		if(selectedMonster!=-1){
			joinRoom(selectedMonster);
		}else{
			Utils.showToastAlert(this, getApplicationContext().getString(R.string.alertSelect));
		}
	}
	
	private void joinRoom(int selectedMonster){
		progressDialog = ProgressDialog.show(this, "", "joining room...");
		theClient.joinRoom(roomId);
	}
	public void onRoomJoined(boolean success){
		progressDialog.dismiss();
		if(success){
			Hashtable<String, Object> table = new Hashtable<String, Object>();
			Log.d("Utils.userName", Utils.userName);
			if(selectedMonster==1){
				table.put("red", Utils.userName);
			}else if(selectedMonster==2){
				table.put("green", Utils.userName);
			}else if(selectedMonster==3){
				table.put("blue", Utils.userName);
			}else if(selectedMonster==4){
				table.put("yellow", Utils.userName);
			}
			progressDialog = ProgressDialog.show(this, "", "locking property...");
			theClient.lockProperties(table);
		}
	}
	
	public void onPropertyLocked(boolean success){
		progressDialog.dismiss();
		if(success){
			finish();
			Intent intent = new Intent(this, AndEngineTutorialActivity.class);
			intent.putExtra("roomId", roomId);
			startActivity(intent);
		}
	}
	
	public void handleLockInfo(Hashtable<String, Object> properties, Hashtable<String, String> lockProperties){
//		Log.d("properties", properties+"");
//		Log.d("lockProperties", lockProperties+"");
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
		if(properties!=null && lockProperties!=null){
			Enumeration<String> keyEnum = properties.keys();
			while(keyEnum.hasMoreElements()){
				String key = keyEnum.nextElement();
				String owner = lockProperties.get(key);
				if(owner!=null && owner.toString().length()>0){
					handleMonster(key, false);
				}else{
					handleMonster(key, true);
				}
			}
		}
		
	}
	private void handleMonster(String name, boolean setVisible){
		ImageView selected = null;
		if(name.equals("red")){
			selected = redMonster;
		}if(name.equals("green")){
			selected = greenMonster;
		}if(name.equals("blue")){
			selected = blueMonster;
		}if(name.equals("yellow")){
			selected = yellowMonster;
		}
		if(selected!=null){
			if(setVisible){
				selected.setVisibility(View.VISIBLE);
			}else{
				selected.setVisibility(View.GONE);
			}
		}
	}

}
