package cambridge.hack.alarmbike.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class ListAdapter<T> extends BaseAdapter {

    private List<T> entries;
    private int R_layout_IdView; 
    private Context context;

    public ListAdapter(Context context, int R_layout_IdView, List<T> entradas) {
        super();
        this.context = context;
        this.entries = entradas;
        this.R_layout_IdView = R_layout_IdView; 
    }

    public void addEntry(T entrada){ entries.add(entrada);}

    @Override
    public View getView(int posicion, View view, ViewGroup pariente)
    {
        if (view == null) 
        {
        	LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, pariente,false);
        }
        onEntrada (entries.get(posicion), view);
        return view; 
    }

    @Override
    public int getCount() 
    {
        return entries.size();
    }

    @Override
    public T getItem(int posicion)
    {
        return entries.get(posicion);
    }

    @Override
    public long getItemId(int posicion)
    {
        return posicion;
    }

    public abstract void onEntrada (T entrada, View view);
}
