package app.paste_it.models.holders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.paste_it.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TextHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_text_message)
        public TextView tvMessage;
    public View rootView;

        public TextHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.rootView=itemView;
        }


    public void collapse(){
        rootView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        tvMessage.setVisibility(View.GONE);
    }

        public void setMessage(String message){
            rootView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            tvMessage.setText(message);
            tvMessage.setTextColor(ContextCompat.getColor(tvMessage.getContext(),android.R.color.darker_gray));
            tvMessage.setVisibility(View.VISIBLE);
        }

    public void setLightEmptyMessage(String message) {
        rootView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tvMessage.setText(message);
        tvMessage.setTextColor(ContextCompat.getColor(tvMessage.getContext(), R.color.colorAccent));
        tvMessage.setVisibility(View.VISIBLE);
    }

    public void setLightMessage(String message) {
        rootView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvMessage.setText(message);
        tvMessage.setTextColor(ContextCompat.getColor(tvMessage.getContext(),R.color.colorAccent));
        tvMessage.setVisibility(View.VISIBLE);
    }
}