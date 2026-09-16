package com.poco.simulator;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    LinearLayout root, content;
    TextView status, coins, temp, fps, cpu;
    ProgressBar performance;
    int money = 250, cpuLv=1, gpuLv=1, coolingLv=1, batteryLv=1, perfLv=1;
    int toolCooldown = 0;
    Handler h = new Handler();

    TextView txt(String s, int size, boolean bold) {
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size);
        t.setTextColor(Color.WHITE); t.setTypeface(null,bold?Typeface.BOLD:Typeface.NORMAL);
        t.setPadding(16,10,16,10); return t;
    }
    Button btn(String s) { Button b=new Button(this); b.setText(s); b.setTextSize(15); return b; }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b); home(); tick();
    }

    void base(String title) {
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(14,16,14,14); root.setBackgroundColor(Color.rgb(18,18,22));
        TextView t=txt(title,26,true); t.setGravity(Gravity.CENTER);
        root.addView(t);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL);
        ScrollView sv=new ScrollView(this); sv.addView(content); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    void home() {
        base("POCO X3 PRO");
        status=txt("◉ SIMULATOR  •  v0.2",13,false); status.setGravity(Gravity.CENTER); content.addView(status);
        coins=txt("💰 Монеты: "+money,19,true); content.addView(coins);
        temp=txt("🌡️ Температура: 34.0 °C",17,true); content.addView(temp);
        cpu=txt("⚡ CPU: 42%",17,true); content.addView(cpu);
        fps=txt("🎮 FPS: 60",17,true); content.addView(fps);
        performance=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        performance.setMax(100); performance.setProgress(75+perfLv*3); content.addView(performance);

        TextView apps=txt("ПРИЛОЖЕНИЯ",17,true); content.addView(apps);
        Button game=btn("🎮  Игровой режим"), upgrades=btn("🛠️  Улучшения"),
               device=btn("📱  Состояние устройства"), tool=btn("💥  Tool");
        content.addView(game); content.addView(upgrades); content.addView(device); content.addView(tool);
        game.setOnClickListener(v->game());
        upgrades.setOnClickListener(v->upgrades());
        device.setOnClickListener(v->device());
        tool.setOnClickListener(v->tool());
    }

    void game() {
        base("🎮 ИГРОВОЙ РЕЖИМ");
        content.addView(txt("ПОЧТИ КАК НА НАСТОЯЩЕМ ТЕЛЕФОНЕ",16,true));
        TextView info=txt("FPS: 60\nCPU: 55%\nGPU: 58%\nТемпература: 36.5 °C\n\nРежим производительности активен.",18,false);
        content.addView(info);
        Button earn=btn("▶ Играть  +25 монет");
        content.addView(earn);
        earn.setOnClickListener(v->{money+=25; home();});
        Button back=btn("← На главный экран"); content.addView(back); back.setOnClickListener(v->home());
    }

    void upgrades() {
        base("🛠️ УЛУЧШЕНИЯ");
        coins=txt("💰 Монеты: "+money,19,true); content.addView(coins);
        upgrade("⚡ CPU",cpuLv,100,()->cpuLv++);
        upgrade("🎮 GPU",gpuLv,120,()->gpuLv++);
        upgrade("❄️ Охлаждение",coolingLv,110,()->coolingLv++);
        upgrade("🔋 Батарея",batteryLv,90,()->batteryLv++);
        upgrade("🚀 Производительность",perfLv,150,()->perfLv++);
        Button back=btn("← На главный экран"); content.addView(back); back.setOnClickListener(v->home());
    }

    interface Action { void run(); }
    void upgrade(String name,int lv,int price,Action a) {
        Button b=btn(name+"  Lv."+lv+"   •   "+price+" 💰"); content.addView(b);
        b.setOnClickListener(v->{
            if(money>=price){ money-=price; a.run(); upgrades(); }
            else Toast.makeText(this,"Недостаточно монет!",Toast.LENGTH_SHORT).show();
        });
    }

    void device() {
        base("📱 СОСТОЯНИЕ УСТРОЙСТВА");
        content.addView(txt("POCO X3 PRO\n\nRAM: 6 GB\nНакопитель: 128 GB\nCPU: Snapdragon 860 (симуляция)\nGPU: Adreno (симуляция)\nБатарея: "+(78+batteryLv*2)+"%\nТемпература: 34–39 °C\n\nУровни:\nCPU "+cpuLv+"  •  GPU "+gpuLv+"\nОхлаждение "+coolingLv+"  •  Батарея "+batteryLv+"\nПроизводительность "+perfLv,18,false));
        Button back=btn("← На главный экран"); content.addView(back); back.setOnClickListener(v->home());
    }

    void tool() {
        base("💥 TOOL");
        content.addView(txt("Кидай Tool — при попадании происходит взрыв.\nПосле использования он восстанавливается через 1 секунду.",17,false));
        Button throwBtn=btn(toolCooldown==0?"💥 БРОСИТЬ TOOL":"⏳ ВОССТАНОВЛЕНИЕ: "+toolCooldown+" сек");
        content.addView(throwBtn);
        throwBtn.setEnabled(toolCooldown==0);
        throwBtn.setOnClickListener(v->{
            toolCooldown=1;
            throwBtn.setText("💥 BOOM!  •  восстановление 1 сек");
            throwBtn.setEnabled(false);
            h.postDelayed(()->{toolCooldown=0; tool();},1000);
        });
        Button back=btn("← На главный экран"); content.addView(back); back.setOnClickListener(v->home());
    }

    void tick() {
        h.postDelayed(new Runnable(){ public void run(){
            if(temp!=null) temp.setText(String.format("🌡️ Температура: %.1f °C",34.0+Math.random()*3.5));
            if(cpu!=null) cpu.setText("⚡ CPU: "+(35+(int)(Math.random()*30))+"%");
            if(fps!=null) fps.setText("🎮 FPS: "+(58+(int)(Math.random()*3)));
            if(coins!=null) coins.setText("💰 Монеты: "+money);
            h.postDelayed(this,1000);
        }},1000);
    }
}
