package com.poco.simulator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.content.Context;
import java.util.*;

public class MainActivity extends Activity {
    GameView game;
    @Override public void onCreate(Bundle b) { super.onCreate(b); game = new GameView(this); setContentView(game); }

    static class GameView extends View {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
        Random rnd = new Random();
        float px=0, pz=8, yaw=0;
        float touchX, touchY;
        boolean moving=false;
        int role=0; // 0 lobby, 1 innocent, 2 murderer, 3 sheriff
        int phase=0; // lobby/game
        long roundEnd=0;
        int coins=0;
        ArrayList<Box> boxes=new ArrayList<>();
        ArrayList<Bot> bots=new ArrayList<>();
        long last;

        GameView(Context c){ super(c); p.setStrokeWidth(2); text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD)); setFocusable(true); setupLobby(); last=System.currentTimeMillis(); }
        void setupLobby(){ boxes.clear(); bots.clear(); for(int x=-6;x<=6;x+=3) boxes.add(new Box(x,0,0,2,2,2)); for(int i=0;i<5;i++) bots.add(new Bot(rnd.nextFloat()*10-5,rnd.nextFloat()*10-2)); }
        void setupMap(){ boxes.clear(); bots.clear();
            // walls, tables and rooms
            boxes.add(new Box(-7,0,0,1,5,18)); boxes.add(new Box(7,0,0,1,5,18));
            boxes.add(new Box(0,0,-9,14,5,1)); boxes.add(new Box(0,0,9,14,5,1));
            boxes.add(new Box(-3,0,-2,4,2,2)); boxes.add(new Box(3,0,2,4,2,2));
            boxes.add(new Box(0,0,5,2,3,2)); boxes.add(new Box(-4,0,6,2,3,2));
            for(int i=0;i<7;i++) bots.add(new Bot(rnd.nextFloat()*10-5,rnd.nextFloat()*12-6));
        }
        void startRound(){ phase=1; role=1+rnd.nextInt(3); px=0; pz=6; yaw=0; roundEnd=System.currentTimeMillis()+120000; setupMap(); invalidate(); }

        @Override protected void onDraw(Canvas c){ super.onDraw(c); long now=System.currentTimeMillis(); float dt=Math.min(0.05f,(now-last)/1000f); last=now;
            c.drawColor(Color.rgb(22,24,29));
            if(phase==0) drawLobby(c); else { update(dt); draw3D(c); drawHud(c); }
            postInvalidateDelayed(16);
        }
        void drawLobby(Canvas c){
            p.setColor(Color.rgb(42,45,53)); c.drawRect(0,0,getWidth(),getHeight(),p);
            text.setTextAlign(Paint.Align.CENTER); text.setColor(Color.WHITE); text.setTextSize(34); c.drawText("MYSTERY NIGHT",getWidth()/2,90,text);
            text.setTextSize(17); text.setColor(Color.LTGRAY); c.drawText("3D MURDER MYSTERY",getWidth()/2,120,text);
            text.setTextSize(18); text.setColor(Color.WHITE); c.drawText("ЛОББИ",getWidth()/2,165,text);
            for(int i=0;i<5;i++){ float x=80+i*75; drawMiniAvatar(c,x,270+(i%2)*35); }
            p.setColor(Color.rgb(45,120,75)); c.drawRoundRect(getWidth()/2-140,420,getWidth()/2+140,490,18,18,p);
            text.setColor(Color.WHITE); text.setTextSize(22); c.drawText("▶  НАЧАТЬ РАУНД",getWidth()/2,465,text);
            text.setTextSize(14); text.setColor(Color.LTGRAY); c.drawText("Оригинальная 3D игра • v0.1",getWidth()/2,540,text);
        }
        void drawMiniAvatar(Canvas c,float x,float y){ p.setColor(Color.rgb(210,175,130)); c.drawCircle(x,y-25,14,p); p.setColor(Color.rgb(55,80,130)); c.drawRect(x-15,y-10,x+15,y+25,p); }

        void update(float dt){
            if(System.currentTimeMillis()>roundEnd){ phase=0; setupLobby(); return; }
            if(moving){ float speed=3.2f*dt; px += (float)Math.sin(yaw)*speed; pz += (float)Math.cos(yaw)*speed; }
            px=Math.max(-5.8f,Math.min(5.8f,px)); pz=Math.max(-7.8f,Math.min(7.8f,pz));
            for(Bot b:bots){ b.a+=dt*(0.4f+rnd.nextFloat()*0.4f); b.x+=Math.sin(b.a)*dt*.4; b.z+=Math.cos(b.a)*dt*.4; }
        }

        void draw3D(Canvas c){
            int w=getWidth(), h=getHeight();
            p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(90,130,170)); c.drawRect(0,0,w,h*.48f,p);
            p.setColor(Color.rgb(65,65,70)); c.drawRect(0,h*.48f,w,h,p);
            // grid floor
            p.setColor(Color.rgb(85,85,90)); p.setStrokeWidth(1);
            for(int z=-12;z<=12;z++) line3(c,-10,z,10,z);
            for(int x=-10;x<=10;x++) line3(c,x,-12,x,12);
            // boxes
            for(Box b:boxes) drawBox(c,b);
            for(Bot b:bots) drawBot(c,b);
            // crosshair
            p.setColor(Color.WHITE); p.setStrokeWidth(3); c.drawLine(w/2-9,h/2,w/2+9,h/2,p); c.drawLine(w/2,h/2-9,w/2,h/2+9,p);
        }
        float[] proj(float x,float y,float z){
            float dx=x-px, dz=z-pz; float s=(float)Math.sin(yaw), co=(float)Math.cos(yaw);
            float cx=dx*co-dz*s, cz=dx*s+dz*co; if(cz<.15f) return null;
            float f=getWidth()*.72f; return new float[]{getWidth()/2+cx*f/cz, getHeight()*.49f-y*f/cz, cz};
        }
        void line3(Canvas c,float x1,float z1,float x2,float z2){ float[] a=proj(x1,0,z1),b=proj(x2,0,z2); if(a!=null&&b!=null){ c.drawLine(a[0],a[1],b[0],b[1],p); } }
        void drawBox(Canvas c,Box b){
            float[] q=proj(b.x-b.w/2,b.h,b.z-b.d/2); float[] r=proj(b.x+b.w/2,0,b.z+b.d/2); if(q==null||r==null)return;
            p.setColor(Color.rgb(125,128,138)); c.drawRect(q[0],q[1],r[0],r[1],p); p.setColor(Color.rgb(170,170,180)); c.drawRect(q[0],q[1],r[0],q[1]+4,p);
        }
        void drawBot(Canvas c,Bot b){ float[] feet=proj(b.x,0,b.z), head=proj(b.x,1.7f,b.z); if(feet==null||head==null)return; float size=Math.max(5,38/feet[2]);
            p.setColor(Color.rgb(205,170,125)); c.drawCircle(head[0],head[1],size*.45f,p); p.setColor(Color.rgb(55,90,145)); c.drawRect(head[0]-size*.55f,head[1]+size*.4f,head[0]+size*.55f,feet[1],p);
        }
        void drawHud(Canvas c){
            int w=getWidth(),h=getHeight(); p.setColor(0xAA111318); c.drawRect(0,0,w,74,p);
            text.setTextAlign(Paint.Align.LEFT); text.setTextSize(17); text.setColor(Color.WHITE);
            String roleName=role==2?"🔪 УБИЙЦА":role==3?"⭐ ШЕРИФ":"👤 МИРНЫЙ";
            c.drawText(roleName,18,28,text); long sec=Math.max(0,(roundEnd-System.currentTimeMillis())/1000); c.drawText("⏱ "+sec+"s",18,55,text);
            text.setTextAlign(Paint.Align.RIGHT); c.drawText("💰 "+coins,getWidth()-18,28,text); c.drawText("PLAYERS: "+(bots.size()+1),getWidth()-18,55,text);
            // movement area
            p.setColor(0x77444444); c.drawCircle(82,h-92,55,p); p.setColor(0x99AAAAAA); c.drawCircle(82,h-92,22,p);
            p.setColor(0x88444444); c.drawCircle(w-75,h-95,48,p); text.setTextAlign(Paint.Align.CENTER); text.setColor(Color.WHITE); text.setTextSize(13);
            c.drawText(role==2?"ATTACK":"ACTION",w-75,h-91,text); c.drawText("DRAG = LOOK",w/2,h-16,text);
        }
        @Override public boolean onTouchEvent(android.view.MotionEvent e){
            float x=e.getX(), y=e.getY();
            if(phase==0){ if(e.getAction()==MotionEvent.ACTION_UP && y>390&&y<530){ startRound(); return true; } return true; }
            if(e.getAction()==MotionEvent.ACTION_DOWN){ touchX=x; touchY=y; moving=(x<180 && y>getHeight()-190); return true; }
            if(e.getAction()==MotionEvent.ACTION_MOVE){ if(x>180){ yaw += (x-touchX)*0.012f; touchX=x; } else if(y>getHeight()-190) moving=true; return true; }
            if(e.getAction()==MotionEvent.ACTION_UP){
                if(x<180 && y>getHeight()-190) moving=false;
                if(x>getWidth()-160 && y>getHeight()-180 && role==2){ coins+=10; }
                return true;
            } return true;
        }
        static class Box { float x,z,w,h,d; Box(float x,float y,float z,float w,float h,float d){this.x=x;this.z=z;this.w=w;this.h=h;this.d=d;} }
        static class Bot { double x,z,a; Bot(double x,double z){this.x=x;this.z=z;a=Math.random()*6.28;} }
    }
}
