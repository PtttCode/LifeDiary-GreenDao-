package andy.ham.pageView;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import andy.ham.LifeDiary;
import andy.ham.R;
import andy.ham.SaveFileService;

/**
 * Created by PATIR on 2018/6/20.
 */

public class MainAcitivity extends AppCompatActivity{
    /*private ViewPager vpager_one;
  private ArrayList<View> aList;
  private PageAdapter mAdapter;*/
    private ViewPager vpager_two;
    private ArrayList<View> aList;
    private PageAdapter2 mAdapter;
    private TextView totalnum;
    private TextView totalrow;
    private String rows;
    private String num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int row= LifeDiary.getRows();
        rows=Integer.toString(row);
        int n= SaveFileService.getUserInfo(this);
        num=Integer.toString(n);
        setContentView(R.layout.activity_two);
        init();

    }


    public void init()
    {
        vpager_two =  findViewById(R.id.vpager_two);
        aList = new ArrayList<View>();
        LayoutInflater li = getLayoutInflater();
        aList.add(li.inflate(R.layout.page1,null,false));
        aList.add(li.inflate(R.layout.page2,null,false));
        mAdapter = new PageAdapter2(aList,rows,num);
        vpager_two.setAdapter(mAdapter);
    }
}
