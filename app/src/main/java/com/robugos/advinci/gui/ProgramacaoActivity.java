package com.robugos.advinci.gui;

import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.robugos.advinci.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProgramacaoActivity extends AppCompatActivity {

    private static String userId;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programacao);

        //Intent intent = getIntent();
        //userId = intent.getExtras().getString("uid");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public String getUserId(){
        return this.userId;
    }

    public void setUserId(String user){
        this.userId = user;
    }

    protected void onActivityStart(){

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1) {
            if (resultCode == RESULT_OK) {
                setUserId(data.getExtras().getString("uid"));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                //listaEventos.clear();
                //new GetEventos().execute();
                return true;

            case R.id.action_order_nome:
                sortListByNome();
                return true;

            case R.id.action_order_rating:
                sortListByRating();
                return true;

            case R.id.action_order_data:
                sortListByData();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void sortListByRating() {
        /*Collections.sort(listaEventos, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                Float nota1 = Float.parseFloat(lhs.get("nota").toString());
                Float nota2 = Float.parseFloat(rhs.get("nota").toString());
                if(null != lhs.get("nota") && null != rhs.get("nota")){
                    return nota2.compareTo(nota1);
                }else if(null != lhs.get("nota")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        adapter.notifyDataSetChanged();*/
    }

    private void sortListByData() {
        /*Collections.sort(listaEventos, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                Date data1 = stringToDate(lhs.get("data"));
                Date data2 = stringToDate(rhs.get("data"));
                if(null != lhs.get("data") && null != rhs.get("data")){
                    return data1.compareTo(data2);
                }else if(null != lhs.get("data")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        adapter.notifyDataSetChanged();*/
    }

    private void sortListByNome() {
        /*Collections.sort(listaEventos, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                String nome1 = lhs.get("nome").toString();
                String nome2 = rhs.get("nome").toString();
                if(null != lhs.get("nome") && null != rhs.get("nome")){
                    return nome1.compareTo(nome2);
                }else if(null != lhs.get("nome")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        adapter.notifyDataSetChanged();*/
    }

    public Date stringToDate(String data){
        String dia = data.substring(0,11);
        String hora = data.substring(14,16)+":"+data.substring(17,19);
        //System.out.println(dia);
        //System.out.println(hora);
        data = dia+hora;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TabProgramacaoRec tab1 = new TabProgramacaoRec();
                    return tab1;
                case 1:
                    TabProgramacaoAll tab2 = new TabProgramacaoAll();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Recomendados";
                case 1:
                    return "Todos";
            }
            return null;
        }
    }
}
