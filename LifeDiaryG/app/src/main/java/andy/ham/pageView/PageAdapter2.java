package andy.ham.pageView;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by PATIR on 2018/3/22.
 */

public class PageAdapter2 extends PagerAdapter {
    private ArrayList<View> viewLists;
    private String rows;
    private String num;

    public PageAdapter2(ArrayList<View> viewLists,String rows,String num) {
        this.viewLists = viewLists;
        this.rows=rows;
        this.num=num;
        Log.e("tag","aaa"+rows+"bbb"+num);
    }

    @Override
    public int getCount()
    {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == viewLists.get((int)Integer.parseInt(object.toString()));
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        container.addView(viewLists.get(position));
        return position;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView(viewLists.get(position));
    }



    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return rows;
            case 1:
                return num;

            default:
                return "";
        }
    }
}

