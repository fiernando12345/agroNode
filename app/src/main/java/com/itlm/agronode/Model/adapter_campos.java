package com.itlm.agronode.Model;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itlm.agronode.R;

import java.util.ArrayList;

public class adapter_campos extends RecyclerView.Adapter<adapter_campos.CamposviewHolder> implements View.OnClickListener{

    ArrayList<Campo> campos;
    private View.OnClickListener listener;

    public adapter_campos(ArrayList<Campo> campos) {
        this.campos = campos;
    }

    @Override
    public CamposviewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_campos, viewGroup, false);
        CamposviewHolder holder = new CamposviewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(CamposviewHolder camposviewHolder, int i) {
        Campo campo= campos.get(i);
        camposviewHolder.txtNombre.setText(campo.getNombre());
    }

    @Override
    public int getItemCount() {
        return campos.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener= listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }
    public static class CamposviewHolder extends RecyclerView.ViewHolder{

        TextView txtNombre;

        public CamposviewHolder(View itemView) {
            super(itemView);
            txtNombre= (TextView)itemView.findViewById(R.id.lista_campos_nombre);
        }
    }
}
