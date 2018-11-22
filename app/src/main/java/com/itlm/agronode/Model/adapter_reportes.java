package com.itlm.agronode.Model;

import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itlm.agronode.R;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class adapter_reportes extends RecyclerView.Adapter<adapter_reportes.ReportesviewHolder> implements View.OnClickListener{

    ArrayList<Reporte> reportes;
    private View.OnClickListener listener;

    public adapter_reportes(ArrayList<Reporte> reportes) {
        this.reportes = reportes;
    }

    @Override
    public ReportesviewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_reportes, viewGroup, false);
        ReportesviewHolder holder = new ReportesviewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReportesviewHolder reportesviewHolder, int i) {
        Reporte reporte= reportes.get(i);
        reportesviewHolder.txtNombre.setText(reporte.getTitulo());
        if(reporte.getEstado().equals("Quema")){
            reportesviewHolder.ivIcono.setImageResource(R.drawable.fire);
        }
        if(reporte.getEstado().equals("Fuga")){
            reportesviewHolder.ivIcono.setImageResource(R.drawable.agua);
        }
    }

    @Override
    public int getItemCount() {
        return reportes.size();
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
    public static class ReportesviewHolder extends RecyclerView.ViewHolder{

        TextView txtNombre;
        ImageView ivIcono;

        public ReportesviewHolder(View itemView) {
            super(itemView);
            txtNombre= (TextView)itemView.findViewById(R.id.lista_resportes_nombre);
            ivIcono= (ImageView) itemView.findViewById(R.id.lista_reportes_icono);
        }
    }
}
