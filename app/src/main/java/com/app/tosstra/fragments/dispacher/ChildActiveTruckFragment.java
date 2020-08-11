package com.app.tosstra.fragments.dispacher;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.tosstra.activities.AddANewJobActivity;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.adapters.ChildActieTruckAdapter;
import com.app.tosstra.R;
import com.app.tosstra.interfaces.PassDriverIds;
import com.app.tosstra.interfaces.RefreshDriverList;
import com.app.tosstra.models.AllDrivers;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChildActiveTruckFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rvAllDriver;
    private ChildActieTruckAdapter rvAllDriverAdapter;
    FloatingActionButton fab;
    private String dispacher_id;
    private SwipeRefreshLayout swiperefresh;
    public static List<String> new_interestList = new ArrayList<>();
    TextView tvSelected;
    Dialog dialog;
    private TextView tvEmptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_truck_child_active, container, false);
        initVw(view);
        allDriverAPI(refreshDriverList, "onCreate");
        return view;
    }


  /*  @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            allDriverAPI(refreshDriverList, "onCreate");        }
    }*/


    private void initVw(View view) {
        tvEmptyView=view.findViewById(R.id.empty_view);
        swiperefresh = view.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(this);
        rvAllDriver = view.findViewById(R.id.recyclerview);
        tvSelected=view.findViewById(R.id.tvSelected);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        if(new_interestList!=null){
            new_interestList.clear();
            tvSelected.setText("Total " + "0" + " Selected");
        }
    }

    @Override
    public void onClick(View v) {
        if (new_interestList != null)
            if (new_interestList.size() == 0) {
                CommonUtils.showSmallToast(getContext(), "Please select at least one driver");
            } else {
                Intent i = new Intent(getContext(), AddANewJobActivity.class);
                i.putExtra("f_type","act");
                startActivityForResult(i,2);
            }

    }

    PassDriverIds passDriverIds = new PassDriverIds() {
        @Override
        public void selectedDriverIdList(List<String> interestList) {
            new_interestList = interestList;
            String s= String.valueOf(new_interestList.size());
            tvSelected.setText("Total "+s+" Selected");
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if(data!=null){
                String sen=data.getStringExtra("refresh_allTruck");
                if(sen.equalsIgnoreCase("1")){
                    allDriverAPI(refreshDriverList, "onCreate");
                    if (new_interestList != null) {
                        new_interestList.clear();
                        tvSelected.setText("Total " + "0" + " Selected");
                    }
                }
            }
        }
    }

    private void allDriverAPI(final RefreshDriverList refreshDriverList, String key) {
        if(key.equalsIgnoreCase("onCreate")){
             dialog = AppUtil.showProgress(getActivity());
        }
        Interface service = CommonUtils.retroInit();
        Call<AllDrivers> call = service.getAllDrivers(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<AllDrivers>() {
            @Override
            public void onResponse(Call<AllDrivers> call, Response<AllDrivers> response) {
                AllDrivers data = response.body();

                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    swiperefresh.setRefreshing(false);
                    dialog.dismiss();
                    Collections.reverse(data.getData());
                    rvAllDriver.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvAllDriverAdapter = new ChildActieTruckAdapter(getActivity(), data, refreshDriverList, passDriverIds);
                    rvAllDriver.setAdapter(rvAllDriverAdapter);
                    tvEmptyView.setVisibility(View.GONE);
                    rvAllDriver.setVisibility(View.VISIBLE);
                } else {
                    dialog.dismiss();
                    tvEmptyView.setVisibility(View.VISIBLE);
                    rvAllDriver.setVisibility(View.GONE);
                    swiperefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<AllDrivers> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
                swiperefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        allDriverAPI(refreshDriverList,"swipeToRefresh");
        if(new_interestList!=null){
            new_interestList.clear();
            tvSelected.setText("Total " + "0" + " Selected");
        }
    }

    RefreshDriverList refreshDriverList = new RefreshDriverList() {
        @Override
        public void favClick(String driver_id) {
            allDriverAPI(refreshDriverList, "fav");
        }
    };

}
