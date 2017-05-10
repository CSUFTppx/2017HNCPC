package com.csuft.ppx.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.csuft.ppx.R;
import com.csuft.ppx.adpter.ParkAdapter;
import com.csuft.ppx.bean.BeanSearch;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ling on 2017/5/2.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener{
    private List<BeanSearch> searches;
    private List<BeanSearch> searchesRecord;
    private ParkAdapter parkAdapter;
    private EditText search_edit;
    private ImageView back;
    private ImageView close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        ListView searchList = (ListView)findViewById(R.id.search_list);
        searches = new ArrayList<BeanSearch>();
        searchesRecord = new ArrayList<BeanSearch>();
        initSearch();
        parkAdapter = new ParkAdapter(SearchActivity.this, R.layout.item_search,searches);
        searchList.setAdapter(parkAdapter);

        //给edit设置改变事件
        search_edit = (EditText)findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                refreshSearch(search_edit.getText().toString());
            }
        });

        //返回按钮
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        close = (ImageView)findViewById(R.id.close);
        close.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                SearchActivity.this.finish();
                break;
            case R.id.close:
                search_edit.setText("");
                break;
        }
    }

    public void refreshSearch(String str){
        searches.clear();
        if(!str.equals("")){
            for(BeanSearch beanSearch:searchesRecord){
                if(beanSearch.getNames().contains(str)){
                    searches.add(beanSearch);
                }
            }
        }else{
            for(BeanSearch beanSearch:searchesRecord){
                searches.add(beanSearch);
            }
        }
        parkAdapter.notifyDataSetChanged();
    }
    public void initSearch(){
        for (int i=0;i<3;i++){
            BeanSearch search = new BeanSearch("中南林业科技大学"+i,"岳麓36号"+i);
            searches.add(search);
            searchesRecord.add(search);
        }
    }
}
