package com.winapp.sapNetco.adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.winapp.sapNetco.fragments.CategoriesTemp2TabFragments;
import com.winapp.sapNetco.model.AllCategories;

import java.util.ArrayList;

public class ViewPagerTemp2Adapter extends FragmentStatePagerAdapter {
    private int noOfItems;
    private ArrayList<AllCategories> allCategoriesList;


    public ViewPagerTemp2Adapter(FragmentManager fm, int noOfItems, ArrayList<AllCategories> allCategoriesList) {
        super(fm);
        this.noOfItems = noOfItems;
        this.allCategoriesList=allCategoriesList;
    }

    @Override
    public Fragment getItem(int position) {
        Log.w("DefinedCatagoryCode:",allCategoriesList.get(position).getCategoryCode());
        return CategoriesTemp2TabFragments.newInstance(allCategoriesList.get(position).getCategoryCode());
    }

    @Override
    public int getCount() {
        return noOfItems;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return allCategoriesList.get(position).getDescription();
    }
}